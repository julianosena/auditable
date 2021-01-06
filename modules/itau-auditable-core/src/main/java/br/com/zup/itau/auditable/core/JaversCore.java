package br.com.zup.itau.auditable.core;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.changelog.ChangeListTraverser;
import br.com.zup.itau.auditable.core.changelog.ChangeProcessor;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.core.commit.CommitFactory;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.DiffFactory;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChange;
import br.com.zup.itau.auditable.core.json.JsonConverter;
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalIdFactory;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.*;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;
import br.com.zup.itau.auditable.repository.jql.GlobalIdDTO;
import br.com.zup.itau.auditable.repository.jql.JqlQuery;
import br.com.zup.itau.auditable.repository.jql.QueryRunner;
import br.com.zup.itau.auditable.shadow.Shadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentIsNotNull;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;
import static br.com.zup.itau.auditable.repository.jql.InstanceIdDTO.instanceId;

/**
 * JaVers instance
 *
 * @author bartosz walacik
 */
class ItauAuditableCore implements ItauAuditable {
    private static final Logger logger = LoggerFactory.getLogger(ItauAuditable.class);

    private final DiffFactory diffFactory;
    private final TypeMapper typeMapper;
    private final JsonConverter jsonConverter;
    private final CommitFactory commitFactory;
    private final ItauAuditableExtendedRepository repository;
    private final QueryRunner queryRunner;
    private final GlobalIdFactory globalIdFactory;
    private final ItauAuditableCoreConfiguration configuration;

    ItauAuditableCore(DiffFactory diffFactory, TypeMapper typeMapper, JsonConverter jsonConverter, CommitFactory commitFactory, ItauAuditableExtendedRepository repository, QueryRunner queryRunner, GlobalIdFactory globalIdFactory, ItauAuditableCoreConfiguration itauAuditableCoreConfiguration) {
        this.diffFactory = diffFactory;
        this.typeMapper = typeMapper;
        this.jsonConverter = jsonConverter;
        this.commitFactory = commitFactory;
        this.repository = repository;
        this.queryRunner = queryRunner;
        this.globalIdFactory = globalIdFactory;
        this.configuration = itauAuditableCoreConfiguration;
    }

    @Override
    public Commit commit(String author, Object currentVersion) {
        return commit(author, currentVersion, Collections.emptyMap());
    }

    public CompletableFuture<Commit> commitAsync(String author, Object currentVersion, Executor executor) {
        return commitAsync(author, currentVersion, Collections.emptyMap(), executor);
    }

    @Override
    public Commit commit(String author, Object currentVersion, Map<String, String> commitProperties) {
        long start = System.currentTimeMillis();

        argumentIsNotNull(author);
        argumentIsNotNull(commitProperties);
        argumentIsNotNull(currentVersion);
        assertItauAuditableTypeNotValueTypeOrPrimitiveType(currentVersion);

        Commit commit = commitFactory.create(author, commitProperties, currentVersion);
        long stopCreate = System.currentTimeMillis();

        persist(commit);
        long stop = System.currentTimeMillis();

        logger.info(commit.toString()+", done in "+ (stop-start)+ " millis (diff:{}, persist:{})",(stopCreate-start), (stop-stopCreate));
        return commit;
    }

    private void assertItauAuditableTypeNotValueTypeOrPrimitiveType(Object currentVersion) {
        ItauAuditableType jType = typeMapper.getItauAuditableType(currentVersion.getClass());
        if (jType instanceof ValueType || jType instanceof PrimitiveType){
            throw new ItauAuditableException(COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED,
                jType.getClass().getSimpleName(), currentVersion.getClass().getSimpleName());
        }
    }

    private Commit persist(Commit commit) {
        if (commit.getSnapshots().isEmpty()) {
            logger.info("Skipping persisting empty commit: {}", commit.toString());
        } else {
            repository.persist(commit);
        }
        return commit;
    }

    @Override
    public CompletableFuture<Commit> commitAsync(String author, Object currentVersion, Map<String, String> commitProperties,
                                                 Executor executor) {
        long start = System.currentTimeMillis();

        argumentsAreNotNull(author, commitProperties, currentVersion);
        assertItauAuditableTypeNotValueTypeOrPrimitiveType(currentVersion);

        CompletableFuture<Commit> commit =
                supplyAsync(() -> commitFactory.create(author, commitProperties, currentVersion), executor)
                .thenApply(it -> new CommitWithTimestamp(it, System.currentTimeMillis()))
                .thenApplyAsync(it -> {
                    persist(it.getCommit());
                    return it;
                }, executor)
                .thenApply(it -> logCommitMessage(start, it));
        return commit;
    }

    private Commit logCommitMessage(long start, CommitWithTimestamp it) {
        long stop = System.currentTimeMillis();
        Commit persistedCommit = it.getCommit();
        Long creationTime = it.getTimestamp();
        logger.info(persistedCommit.toString()+", done asynchronously in "+ (stop-start)+ " millis (diff:{}, persist:{})",(creationTime-start), (stop-creationTime));
        return persistedCommit;
    }

    @Override
    public Commit commitShallowDelete(String author, Object deleted) {
        return commitShallowDelete(author, deleted, Collections.<String, String>emptyMap());
    }

    @Override
    public Commit commitShallowDelete(String author, Object deleted, Map<String, String> properties) {
        argumentsAreNotNull(author, properties, deleted);

        Commit commit = commitFactory.createTerminal(author, properties, deleted);

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    @Override
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId) {
        return commitShallowDeleteById(author, globalId, Collections.<String, String>emptyMap());
    }


    @Override
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId, Map<String, String> properties) {
        argumentsAreNotNull(author, properties, globalId);

        Commit commit = commitFactory.createTerminalByGlobalId(author, properties, globalIdFactory.createFromDto(globalId));

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    @Override
    public Diff compare(Object oldVersion, Object currentVersion) {
        return diffFactory.compare(oldVersion, currentVersion);
    }

    @Override
    public Diff initial(Object newDomainObject) {
        Validate.argumentIsNotNull(newDomainObject);
        return diffFactory.initial(newDomainObject);
    }

    @Override
    public <T> List<Shadow<T>> findShadows(JqlQuery query) {
        Validate.argumentIsNotNull(query);
        return (List)queryRunner.queryForShadows(query);
    }

    @Override
    public <T> Stream<Shadow<T>> findShadowsAndStream(JqlQuery query) {
        Validate.argumentIsNotNull(query);
        return (Stream)queryRunner.queryForShadowsStream(query);
    }

    @Override
    public List<CdoSnapshot> findSnapshots(JqlQuery query){
        Validate.argumentIsNotNull(query);
        return queryRunner.queryForSnapshots(query);
    }

    @Override
    public Changes findChanges(JqlQuery query){
        Validate.argumentIsNotNull(query);
        return new Changes(queryRunner.queryForChanges(query), configuration.getPrettyValuePrinter());
    }

    @Override
    public Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entity) {
        Validate.argumentsAreNotNull(localId, entity);
        return queryRunner.runQueryForLatestSnapshot(instanceId(localId, entity));
    }

    @Override
    public Optional<CdoSnapshot> getHistoricalSnapshot(Object localId, Class entity, LocalDateTime effectiveDate) {
        Validate.argumentsAreNotNull(localId, entity, effectiveDate);
        return repository.getHistorical(globalIdFactory.createInstanceId(localId, entity), effectiveDate);
    }

    @Override
    public JsonConverter getJsonConverter() {
        return jsonConverter;
    }

    @Override
    public <T> T processChangeList(List<Change> changes, ChangeProcessor<T> changeProcessor){
        argumentsAreNotNull(changes, changeProcessor);

        ChangeListTraverser.traverse(changes, changeProcessor);
        return changeProcessor.result();
    }

    @Override
    public <T extends ItauAuditableType> T getTypeMapping(Type clientsType) {
        return (T) typeMapper.getItauAuditableType(clientsType);
    }

    /**
     * @see TypeName
     * @since 2.3
     */
    public <T extends ManagedType> T getTypeMapping(String typeName) {
        return (T) typeMapper.getItauAuditableManagedType(typeName, ManagedType.class);
    }

    @Override
    public <T> Diff compareCollections(Collection<T> oldVersion, Collection<T> currentVersion, Class<T> itemClass) {
        return diffFactory.compareCollections(oldVersion, currentVersion, itemClass);
    }

    @Override
    public Property getProperty(PropertyChange propertyChange) {
        ManagedType managedType = typeMapper.getItauAuditableManagedType(propertyChange.getAffectedGlobalId());
        return managedType.getProperty(propertyChange.getPropertyName());
    }

    private static class CommitWithTimestamp {
        private Commit commit;
        private Long timestamp;

        CommitWithTimestamp(Commit commit, Long timestamp) {
            this.commit = commit;
            this.timestamp = timestamp;
        }

        Commit getCommit() {
            return commit;
        }

        Long getTimestamp() {
            return timestamp;
        }
    }
}
