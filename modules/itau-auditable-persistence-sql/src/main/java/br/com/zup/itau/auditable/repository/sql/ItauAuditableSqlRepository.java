package br.com.zup.itau.auditable.repository.sql;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.core.commit.CommitId;
import br.com.zup.itau.auditable.core.json.JsonConverter;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.EntityType;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository;
import br.com.zup.itau.auditable.repository.api.QueryParams;
import br.com.zup.itau.auditable.repository.api.SnapshotIdentifier;
import br.com.zup.itau.auditable.repository.sql.finders.CdoSnapshotFinder;
import br.com.zup.itau.auditable.repository.sql.repositories.CdoSnapshotRepository;
import br.com.zup.itau.auditable.repository.sql.repositories.CommitMetadataRepository;
import br.com.zup.itau.auditable.repository.sql.repositories.GlobalIdRepository;
import br.com.zup.itau.auditable.repository.sql.schema.ItauAuditableSchemaManager;
import br.com.zup.itau.auditable.repository.sql.session.Session;
import br.com.zup.itau.auditable.repository.sql.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.zup.itau.auditable.repository.sql.session.Session.SQL_LOGGER_NAME;

public class ItauAuditableSqlRepository implements ItauAuditableRepository {
    private static final Logger logger = LoggerFactory.getLogger(SQL_LOGGER_NAME);

    private final SessionFactory sessionFactory;
    private final CommitMetadataRepository commitRepository;
    private final GlobalIdRepository globalIdRepository;
    private final CdoSnapshotRepository cdoSnapshotRepository;
    private final CdoSnapshotFinder finder;
    private final ItauAuditableSchemaManager schemaManager;

    private final SqlRepositoryConfiguration sqlRepositoryConfiguration;

    public ItauAuditableSqlRepository(SessionFactory sessionFactory,
                               CommitMetadataRepository commitRepository,
                               GlobalIdRepository globalIdRepository,
                               CdoSnapshotRepository cdoSnapshotRepository,
                               CdoSnapshotFinder finder,
                               ItauAuditableSchemaManager schemaManager,
                               SqlRepositoryConfiguration sqlRepositoryConfiguration) {
        this.sessionFactory = sessionFactory;
        this.commitRepository = commitRepository;
        this.globalIdRepository = globalIdRepository;
        this.cdoSnapshotRepository = cdoSnapshotRepository;
        this.finder = finder;
        this.schemaManager = schemaManager;
        this.sqlRepositoryConfiguration = sqlRepositoryConfiguration;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        try(Session session = sessionFactory.create("get latest snapshot")) {
            return finder.getLatest(globalId, session, true);
        }
    }

    @Override
    public List<CdoSnapshot> getLatest(Collection<GlobalId> globalIds) {
        Validate.argumentIsNotNull(globalIds);
        try(Session session = sessionFactory.create("get latest snapshots")) {
            return globalIds.stream()
                    .map(id -> finder.getLatest(id, session, false))
                    .filter(it -> it.isPresent())
                    .map(it -> it.get())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        try(Session session = sessionFactory.create("find snapshots")) {
            return finder.getSnapshots(queryParams, session);
        }
    }

    @Override
    public void persist(Commit commit) {
        try(Session session = sessionFactory.create("persist commit")) {
            long commitPk = commitRepository.save(commit.getAuthor(), commit.getProperties(), commit.getCommitDate(), commit.getCommitDateInstant(), commit.getId(), session);
            cdoSnapshotRepository.save(commitPk, commit.getSnapshots(), session);
        }
    }

    @Override
    public CommitId getHeadId() {
        try(Session session = sessionFactory.create("get head id")) {
            return commitRepository.getCommitHeadId(session);
        }
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        if (isEmpty(snapshotIdentifiers)) {
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.create("find snapshots by ids")) {
            return finder.getSnapshots(snapshotIdentifiers, session);
        }
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        try(Session session = sessionFactory.create("find snapshots by globalId")) {
            return finder.getStateHistory(globalId, queryParams, session);
        }
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        if (isEmpty(givenClasses)) {
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.create("find snapshots by type")) {
            return finder.getStateHistory(givenClasses, queryParams, session);
        }
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        try(Session session = sessionFactory.create("find VO snapshots by path")) {
            return finder.getVOStateHistory(ownerEntity, path, queryParams, session);
        }
    }

    /**
     * ItauAuditableSqlRepository uses the cache for GlobalId primary keys.
     * This cache is non-transactional.
     * <br/><br/>
     *
     * If a SQL transaction encounters errors and must be rolled back,
     * then cache modifications should be rolled back as well.
     * <br/><br/>
     *
     * Itaú Auditable does this automatically in <code>ItauAuditableTransactionalDecorator</code>
     * from <code>itau-auditable-spring</code> module.
     * If you are using <code>itau-auditable-spring-boot-starter-sql</code>
     * (or directly <code>itau-auditable-spring</code>) you don't need to call this method.
     *
     * @since 2.7.2
     */
    public void evictCache() {
        globalIdRepository.evictCache();
    }

    /**
     * @since 2.7.2
     */
    public int getGlobalIdPkCacheSize(){
        return globalIdRepository.getGlobalIdPkCacheSize();
    }

    /**
     * @since 2.7.2
     */
    public SqlRepositoryConfiguration getConfiguration() {
        return sqlRepositoryConfiguration;
    }

    /**
     * Clears the sequence allocation cache. It can be useful for testing.
     * See https://github.com/itauAuditable/itauAuditable/issues/532
     * @since 3.1.1
     */
    public void evictSequenceAllocationCache() {
        sessionFactory.resetKeyGeneratorCache();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        //TODO centralize to Session?
        globalIdRepository.setJsonConverter(jsonConverter);
        cdoSnapshotRepository.setJsonConverter(jsonConverter);
        finder.setJsonConverter(jsonConverter);
    }

    @Override
    public void ensureSchema() {
        if(sqlRepositoryConfiguration.isSchemaManagementEnabled()) {
            schemaManager.ensureSchema();
        }
    }

    public void ensureMultiTenancySchema() {
        try {
            schemaManager.ensureMultiTenancySchema();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }
}
