package br.com.zup.itau.auditable.repository.sql;

import br.com.zup.itau.auditable.core.AbstractContainerBuilder;
import br.com.zup.itau.auditable.repository.sql.pico.ItauAuditableSqlModule;
import br.com.zup.itau.auditable.repository.sql.session.SessionFactory;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static br.com.zup.itau.auditable.common.string.Strings.isNonEmpty;

public class MultitenancySqlRepositoryBuilder extends AbstractContainerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(MultitenancySqlRepositoryBuilder.class);

    private DialectName dialectName;
    private ConnectionProvider connectionProvider;

    private String schemaName;
    private boolean globalIdCacheDisabled;
    private boolean schemaManagementEnabled = true;

    private String globalIdTableName;
    private String commitTableName;
    private String snapshotTableName;
    private String commitPropertyTableName;

    public MultitenancySqlRepositoryBuilder() {
    }

    public static MultitenancySqlRepositoryBuilder sqlRepository() {
        return new MultitenancySqlRepositoryBuilder();
    }

    public MultitenancySqlRepositoryBuilder withDialect(DialectName dialect) {
        dialectName = dialect;
        return this;
    }

    public MultitenancySqlRepositoryBuilder withConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        return this;
    }

    /**
     * This function sets a schema to be used for creation and updating tables. When passing a schema name make sure
     * that the schema has been created in the database before running Itaú Auditable. If schemaName is null or empty, the default
     * schema is used instead.
     *
     * @since 2.4
     */
    public MultitenancySqlRepositoryBuilder withSchema(String schemaName) {
        if (isNonEmpty(schemaName)) {
            this.schemaName = schemaName;
        }
        return this;
    }

    /**
     * Since 2.7.2, ItauAuditableTransactionalDecorator evicts the cache on transaction rollback,
     * so there are no known reasons to disabling it.
     */
    public MultitenancySqlRepositoryBuilder withGlobalIdCacheDisabled(boolean globalIdCacheDisabled) {
        this.globalIdCacheDisabled = globalIdCacheDisabled;
        return this;
    }

    public MultitenancySqlRepositoryBuilder withSchemaManagementEnabled(boolean schemaManagementEnabled){
        this.schemaManagementEnabled = schemaManagementEnabled;
        return this;
    }

    public MultitenancySqlRepositoryBuilder withGlobalIdTableName(String globalIdTableName) {
        if(isNonEmpty(globalIdTableName)) {
            this.globalIdTableName = globalIdTableName;
        }
        return this;
    }

    public MultitenancySqlRepositoryBuilder withCommitTableName(String commitTableName) {
        if(isNonEmpty(commitTableName)) {
            this.commitTableName = commitTableName;
        }
        return this;
    }

    public MultitenancySqlRepositoryBuilder withSnapshotTableName(String snapshotTableName) {
        if(isNonEmpty(snapshotTableName)) {
            this.snapshotTableName = snapshotTableName;
        }
        return this;
    }

    public MultitenancySqlRepositoryBuilder withCommitPropertyTableName(String commitPropertyTableName) {
        if(isNonEmpty(commitPropertyTableName)) {
            this.commitPropertyTableName = commitPropertyTableName;
        }
        return this;
    }

    public MultitenancyItauAuditableSqlRepository build() {
        logger.info("starting SqlRepository...");
        logger.info("  dialect:                  {}", dialectName);
        logger.info("  schemaManagementEnabled:  {}", schemaManagementEnabled);
        logger.info("  schema name:              {}", schemaName);
        bootContainer();

        SqlRepositoryConfiguration config = new SqlRepositoryConfiguration(globalIdCacheDisabled, schemaName, schemaManagementEnabled, globalIdTableName, commitTableName, snapshotTableName, commitPropertyTableName);
        addComponent(config);

        PolyJDBC polyJDBC = PolyJDBCBuilder.polyJDBC(
                dialectName.getPolyDialect(),
                config.getSchemaName()
        ).usingManagedConnections(() -> connectionProvider.getConnection()).build();

        SessionFactory sessionFactory = new SessionFactory(dialectName, connectionProvider);

        addComponent(polyJDBC);
        addComponent(sessionFactory);

        addModule(new ItauAuditableSqlModule());

        addComponent(dialectName.getPolyDialect());
        addComponent(connectionProvider);
        return getContainerComponent(MultitenancyItauAuditableSqlRepository.class);
    }

    @Override
    protected <T> T getContainerComponent(Class<T> ofClass) {
        return super.getContainerComponent(ofClass);
    }
}
