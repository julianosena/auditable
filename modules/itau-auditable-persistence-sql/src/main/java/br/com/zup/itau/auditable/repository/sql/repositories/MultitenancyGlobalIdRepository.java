package br.com.zup.itau.auditable.repository.sql.repositories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import br.com.zup.itau.auditable.core.json.JsonConverter;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId;
import br.com.zup.itau.auditable.core.metamodel.object.UnboundedValueObjectId;
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId;
import br.com.zup.itau.auditable.repository.sql.SqlRepositoryConfiguration;
import br.com.zup.itau.auditable.repository.sql.schema.MultitenancySchemaNameAware;
import br.com.zup.itau.auditable.repository.sql.schema.MultitenancyTableNameProvider;
import br.com.zup.itau.auditable.repository.sql.session.InsertBuilder;
import br.com.zup.itau.auditable.repository.sql.session.SelectBuilder;
import br.com.zup.itau.auditable.repository.sql.session.Session;

import java.util.Optional;

import static br.com.zup.itau.auditable.repository.sql.schema.FixedSchemaFactory.*;

public class MultitenancyGlobalIdRepository extends MultitenancySchemaNameAware {

    private JsonConverter jsonConverter;
    private final boolean disableCache;

    private final Cache<GlobalId, Long> globalIdPkCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    public MultitenancyGlobalIdRepository(MultitenancyTableNameProvider tableNameProvider, SqlRepositoryConfiguration configuration) {
        super(tableNameProvider);
        this.disableCache = configuration.isGlobalIdCacheDisabled();
    }

    public long getOrInsertId(GlobalId globalId, Session session) {
        Optional<Long> pk = findGlobalIdPk(globalId, session);
        return pk.orElseGet(() -> insert(globalId, session));
    }

    public void evictCache() {
        globalIdPkCache.invalidateAll();
    }

    public int getGlobalIdPkCacheSize() {
        return (int)globalIdPkCache.size();
    }

    /**
     * cached
     */
    public Optional<Long> findGlobalIdPk(GlobalId globalId, Session session) {
        if (disableCache){
            return findGlobalIdPkInDB(globalId, session);
        }

        Long foundPk = globalIdPkCache.getIfPresent(globalId);

        if (foundPk != null){
            return Optional.of(foundPk);
        }

        Optional<Long> fresh = findGlobalIdPkInDB(globalId, session);
        if (fresh.isPresent()){
            globalIdPkCache.put(globalId, fresh.get());
        }

        return fresh;
    }

    private Optional<Long> findGlobalIdPkInDB(GlobalId globalId, Session session) {
        SelectBuilder select =  session.select(GLOBAL_ID_PK)
                .from(getGlobalIdTableNameWithSchema());

        if (globalId instanceof ValueObjectId) {
            final ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            Optional<Long> ownerFk = findGlobalIdPk(valueObjectId.getOwnerId(), session);
            if (!ownerFk.isPresent()){
                return Optional.empty();
            }
            select.and(GLOBAL_ID_FRAGMENT, valueObjectId.getFragment())
                  .and(GLOBAL_ID_OWNER_ID_FK, ownerFk.get())
                  .queryName("find PK of valueObjectId");
        }
        else if (globalId instanceof InstanceId){
            Object cdoId = ((InstanceId)globalId).getCdoId();

            if(cdoId instanceof String){
                select.and(GLOBAL_ID_LOCAL_ID, cdoId.toString())
                        .and(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                        .queryName("find PK of InstanceId");
            } else {
                select.and(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(((InstanceId) globalId).getCdoId()))
                        .and(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                        .queryName("find PK of InstanceId");
            }
        }
        else if (globalId instanceof UnboundedValueObjectId){
            select.and(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                  .queryName("find PK of UnboundedValueObjectId");
        }

        return select.queryForOptionalLong();
    }

    private long insert(GlobalId globalId, Session session) {
        InsertBuilder insert = null;

        if (globalId instanceof ValueObjectId) {
            insert = session.insert("ValueObjectId");
            ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            long ownerFk = getOrInsertId(valueObjectId.getOwnerId(), session);
            insert.value(GLOBAL_ID_FRAGMENT, valueObjectId.getFragment())
                  .value(GLOBAL_ID_OWNER_ID_FK, ownerFk);
        }
        else if (globalId instanceof InstanceId) {
            Object cdoId = ((InstanceId)globalId).getCdoId();

            if(cdoId instanceof String){
                insert = session.insert("InstanceId")
                        .value(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                        .value(GLOBAL_ID_LOCAL_ID, cdoId.toString());
            } else {
                insert = session.insert("InstanceId")
                        .value(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                        .value(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(((InstanceId)globalId).getCdoId()));
            }

        }
        else if (globalId instanceof UnboundedValueObjectId) {
            insert = session.insert("UnboundedValueObjectId")
                    .value(GLOBAL_ID_TYPE_NAME, globalId.getTypeName());
        }

        return insert.into(getGlobalIdTableNameWithSchema())
              .sequence(GLOBAL_ID_PK, getGlobalIdPkSeqName().nameWithSchema())
              .executeAndGetSequence();
    }

    public void setJsonConverter(JsonConverter JSONConverter) {
        this.jsonConverter = JSONConverter;
    }
}
