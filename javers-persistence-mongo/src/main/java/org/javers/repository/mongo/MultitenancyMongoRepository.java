package org.javers.repository.mongo;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javers.common.string.RegexEscape;
import org.javers.common.validation.Validate;
import org.javers.core.CommitIdGenerator;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.repository.api.*;
import org.javers.repository.mongo.model.MongoHeadId;

import java.util.*;
import java.util.stream.Collectors;

import static org.javers.common.collections.Lists.toImmutableList;
import static org.javers.common.validation.Validate.conditionFulfilled;
import static org.javers.repository.mongo.DocumentConverter.fromDocument;
import static org.javers.repository.mongo.DocumentConverter.toDocument;
import static org.javers.repository.mongo.MongoDialect.DOCUMENT_DB;
import static org.javers.repository.mongo.MongoDialect.MONGO_DB;
import static org.javers.repository.mongo.MongoSchemaManager.*;

public class MultitenancyMongoRepository implements JaversRepository, ConfigurationAware {
    private final static int DEFAULT_CACHE_SIZE = 5000;
    private final static double COMMIT_ID_PRECISION = 0.005;

    private static final int DESC = -1;
    private final MultitenancyMongoSchemaManager mongoSchemaManager;
    private JsonConverter jsonConverter;
    private JaversCoreConfiguration coreConfiguration;
    private final MapKeyDotReplacer mapKeyDotReplacer = new MapKeyDotReplacer();
    private final LatestSnapshotCache cache;
    private MongoDialect mongoDialect;

    public MultitenancyMongoRepository(MongoClient mongoClient, String databaseNameDefault) {
        this(mongoClient, databaseNameDefault, DEFAULT_CACHE_SIZE, MONGO_DB);
    }

    /**
     * MongoRepository compatible with Amazon DocumentDB.
     * <br/>
     *
     * Compound index on <code>commitProperties</code> isn't created.
     * <br/><br/>
     *
     * See <a href="http://docs.aws.amazon.com/documentdb/latest/developerguide/functional-differences.html">functional differences</a>.
     */
    public static MultitenancyMongoRepository mongoRepositoryWithDocumentDBCompatibility(MongoClient mongoClient, String databaseNameDefault, int cacheSize) {
        return new MultitenancyMongoRepository(mongoClient, databaseNameDefault, cacheSize, DOCUMENT_DB);
    }

    /**
     * @param cacheSize Size of the latest snapshots cache, default is 5000. Set 0 to disable.
     */
    public MultitenancyMongoRepository(MongoClient mongoClient, String databaseNameDefault, int cacheSize) {
        this(mongoClient, databaseNameDefault, cacheSize, MONGO_DB);
    }

    MultitenancyMongoRepository(MongoClient mongoClient, String databaseNameDefault, int cacheSize, MongoDialect dialect) {
        Validate.argumentsAreNotNull(mongoClient, databaseNameDefault, dialect);
        this.mongoDialect = dialect;
        this.mongoSchemaManager = new MultitenancyMongoSchemaManager(mongoClient, databaseNameDefault);
        cache = new LatestSnapshotCache(cacheSize, input -> getLatest(createIdQuery(input)));
    }

    @Override
    public void persist(Commit commit) {
        persistSnapshots(commit);
        persistHeadId(commit);
    }

    void clean(){
        snapshotsCollection().deleteMany(new Document());
        headCollection().deleteMany(new Document());
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        Bson query;
        if (queryParams.isAggregate()){
            query = createIdQueryWithAggregate(globalId);
        } else {
            query = createIdQuery(globalId);
        }
        return queryForSnapshots(query, Optional.of(queryParams));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return cache.getLatest(globalId);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return queryForSnapshots(new BasicDBObject(), Optional.of(queryParams));
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return snapshotIdentifiers.isEmpty() ? Collections.<CdoSnapshot>emptyList() :
            queryForSnapshots(createSnapshotIdentifiersQuery(snapshotIdentifiers), Optional.<QueryParams>empty());
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        BasicDBObject query = new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ownerEntity.getName());
        query.append(GLOBAL_ID_FRAGMENT, path);

        return queryForSnapshots(query, Optional.of(queryParams));
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        Bson query = createManagedTypeQuery(givenClasses, queryParams.isAggregate());
        return queryForSnapshots(query, Optional.of(queryParams));
    }

    @Override
    public CommitId getHeadId() {
        Document headId = headCollection().find().first();

        if (headId == null) {
            return null;
        }

        return new MongoHeadId(headId).toCommitId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void setConfiguration(JaversCoreConfiguration coreConfiguration) {
        this.coreConfiguration = coreConfiguration;
    }

    @Override
    public void ensureSchema() {
        mongoSchemaManager.ensureSchema(mongoDialect);
    }

    private Bson createIdQuery(GlobalId id) {
        return new BasicDBObject(GLOBAL_ID_KEY, id.value());
    }

    private Bson createIdQueryWithAggregate(GlobalId id) {
        return Filters.or(createIdQuery(id), prefixQuery(GLOBAL_ID_KEY, id.value() + "#"));
    }

    private Bson createVersionQuery(Long version) {
        return new BasicDBObject(SNAPSHOT_VERSION, version);
    }

    private Bson createSnapshotIdentifiersQuery(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        List<Bson> descFilters = snapshotIdentifiers.stream().map(
            snapshotIdentifier -> Filters.and(
                createIdQuery(snapshotIdentifier.getGlobalId()),
                createVersionQuery(snapshotIdentifier.getVersion())
        )).collect(toImmutableList());
        return Filters.or(descFilters);
    }

    private Bson createManagedTypeQuery(Set<ManagedType> managedTypes, boolean aggregate) {
        List<Bson> classFilters = managedTypes.stream().map( managedType -> {
                if (managedType instanceof ValueObjectType) {
                    return createValueObjectTypeQuery(managedType);
                } else {
                    return createEntityTypeQuery(aggregate, managedType);
                }
            }).collect(toImmutableList());
        return Filters.or(classFilters);
    }

    private Bson createValueObjectTypeQuery(ManagedType managedType) {
        return new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, managedType.getName());
    }

    private Bson createEntityTypeQuery(boolean aggregate, ManagedType managedType) {
        Bson entityTypeQuery = prefixQuery(GLOBAL_ID_KEY, managedType.getName() + "/");
        if (!aggregate) {
            entityTypeQuery = Filters.and(entityTypeQuery, Filters.exists(GLOBAL_ID_ENTITY));
        }
        return entityTypeQuery;
    }

    private CdoSnapshot readFromDBObject(Document dbObject) {
        return jsonConverter.fromJson(fromDocument(mapKeyDotReplacer.back(dbObject)), CdoSnapshot.class);
    }

    private Document writeToDBObject(CdoSnapshot snapshot){
        conditionFulfilled(jsonConverter != null, "MongoRepository: jsonConverter is null");
        Document dbObject = toDocument((JsonObject)jsonConverter.toJsonElement(snapshot));
        dbObject = mapKeyDotReplacer.replaceInSnapshotState(dbObject);
        dbObject.append(GLOBAL_ID_KEY,snapshot.getGlobalId().value());
        return dbObject;
    }

    private MongoCollection<Document> snapshotsCollection() {
        return mongoSchemaManager.snapshotsCollection();
    }

    private MongoCollection<Document> headCollection() {
        return mongoSchemaManager.headCollection();
    }

    private void persistSnapshots(Commit commit) {
        MongoCollection<Document> collection = snapshotsCollection();
        commit.getSnapshots().forEach(snapshot -> {
            collection.insertOne(writeToDBObject(snapshot));
            cache.put(snapshot);
        });
    }

    private void persistHeadId(Commit commit) {
        MongoCollection<Document> headIdCollection = headCollection();

        Document oldHead = headIdCollection.find().first();
        MongoHeadId newHeadId = new MongoHeadId(commit.getId());

        if (oldHead == null) {
            headIdCollection.insertOne(newHeadId.toDocument());
        } else {
            headIdCollection.updateOne(objectIdFiler(oldHead), newHeadId.getUpdateCommand());
        }
    }

    private Bson objectIdFiler(Document document) {
        return Filters.eq(OBJECT_ID, document.getObjectId("_id"));
    }

    private MongoCursor<Document> getMongoSnapshotsCursor(Bson query, Optional<QueryParams> queryParams) {
        FindIterable<Document> findIterable = snapshotsCollection()
            .find(applyQueryParams(query, queryParams));

        if (coreConfiguration.getCommitIdGenerator() == CommitIdGenerator.SYNCHRONIZED_SEQUENCE) {
            findIterable.sort(new Document(COMMIT_ID, DESC));
        }
        else {
            findIterable.sort(new Document(COMMIT_DATE_INSTANT, DESC));
        }

        return applyQueryParams(findIterable, queryParams).iterator();
    }

    private Bson applyQueryParams(Bson query, Optional<QueryParams> queryParams) {
        if (queryParams.isPresent()) {
            QueryParams params = queryParams.get();

            if (params.from().isPresent()) {
                query = Filters.and(query, Filters.gte(COMMIT_DATE, UtilTypeCoreAdapters.serialize(params.from().get())));
            }
            if (params.fromInstant().isPresent()) {
                query = Filters.and(query, Filters.gte(COMMIT_DATE_INSTANT, UtilTypeCoreAdapters.serialize(params.fromInstant().get())));
            }
            if (params.to().isPresent()) {
                query =  Filters.and(query, Filters.lte(COMMIT_DATE, UtilTypeCoreAdapters.serialize(params.to().get())));
            }
            if (params.toInstant().isPresent()) {
                query = Filters.and(query, Filters.lte(COMMIT_DATE_INSTANT, UtilTypeCoreAdapters.serialize(params.toInstant().get())));
            }
            if (params.toCommitId().isPresent()) {
                query = Filters.and(query, Filters.lte(COMMIT_ID, params.toCommitId().get().valueAsNumber().doubleValue() + COMMIT_ID_PRECISION));
            }
            if (params.commitIds().size() > 0) {
                query = Filters.or(params.commitIds().stream()
                        .map(it -> commitIdFilter(it)).collect(Collectors.toList()));
            }
            if (params.version().isPresent()) {
                query = Filters.and(query, createVersionQuery(params.version().get()));
            }
            if (params.author().isPresent()) {
                query = Filters.and(query, new BasicDBObject(COMMIT_AUTHOR, params.author().get()));
            }
            if (!params.commitProperties().isEmpty()) {
                query = addCommitPropertiesFilter(query, params.commitProperties());
            }
            if (params.changedProperties().size() > 0) {
                query = Filters.and(query, Filters.or(params.changedProperties().stream()
                        .map(it -> new BasicDBObject(CHANGED_PROPERTIES, it)).collect(Collectors.toList())));
            }
            if (params.snapshotType().isPresent()) {
                query = Filters.and(query, new BasicDBObject(SNAPSHOT_TYPE, params.snapshotType().get().name()));
            }

        }
        return query;
    }

    private Bson commitIdFilter(CommitId commitId) {
        if (commitId.getMinorId() > 0) {
            commitId.valueAsNumber().doubleValue();
            return Filters.and(
                    Filters.gte(COMMIT_ID, commitId.valueAsNumber().doubleValue() - COMMIT_ID_PRECISION),
                    Filters.lte(COMMIT_ID, commitId.valueAsNumber().doubleValue() + COMMIT_ID_PRECISION)
            );
        }
        return Filters.eq(COMMIT_ID, commitId.getMajorId());
    }

    private FindIterable<Document> applyQueryParams(FindIterable<Document> findIterable, Optional<QueryParams> queryParams) {
        if (queryParams.isPresent()) {
            QueryParams params = queryParams.get();
            findIterable = findIterable
                .limit(params.limit())
                .skip(params.skip());
        }
        return  findIterable;
    }

    private Bson addCommitPropertiesFilter(Bson query, Map<String, String> commitProperties) {
        List<Bson> propertyFilters = commitProperties.entrySet().stream().map( commitProperty ->
            new BasicDBObject(COMMIT_PROPERTIES,
                new BasicDBObject("$elemMatch",
                        new BasicDBObject("key", commitProperty.getKey()).append(
                                          "value", commitProperty.getValue())))
        ).collect(toImmutableList());
        return Filters.and(query, Filters.and(propertyFilters.toArray(new Bson[]{})));
    }

    private Optional<CdoSnapshot> getLatest(Bson idQuery) {
        QueryParams queryParams = QueryParamsBuilder.withLimit(1).build();
        MongoCursor<Document> mongoLatest = getMongoSnapshotsCursor(idQuery, Optional.of(queryParams));

        return getOne(mongoLatest).map(d -> readFromDBObject(d));
    }

    private List<CdoSnapshot> queryForSnapshots(Bson query, Optional<QueryParams> queryParams) {
        List<CdoSnapshot> snapshots = new ArrayList<>();
        try (MongoCursor<Document> mongoSnapshots = getMongoSnapshotsCursor(query, queryParams)) {
            while (mongoSnapshots.hasNext()) {
                Document dbObject = mongoSnapshots.next();
                snapshots.add(readFromDBObject(dbObject));
            }
            return snapshots;
        }
    }

    private static <T> Optional<T> getOne(MongoCursor<T> mongoCursor){
        try{
            if (!mongoCursor.hasNext()) {
                return Optional.empty();
            }
            return Optional.of(mongoCursor.next());
        }
        finally {
            mongoCursor.close();
        }
    }

    //enables index range scan
    private static Bson prefixQuery(String fieldName, String prefix){
        return Filters.regex(fieldName, "^" + RegexEscape.escape(prefix) + ".*");
    }
}