package br.com.zup.itau.auditable.repository.sql.schema;

import java.util.Optional;
import br.com.zup.itau.auditable.repository.sql.SqlRepositoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ian Agius
 */
public class TableNameProvider {
    private static final String SNAPSHOT_TABLE_PK_SEQ = "audit_snapshot_pk_seq";
    private static final String COMMIT_PK_SEQ =        "audit_commit_pk_seq";
    private static final String GLOBAL_ID_PK_SEQ =     "audit_global_id_pk_seq";


    private static final String DEFAULT_GLOBAL_ID_TABLE_NAME = "audit_global_id";
    private static final String DEFAULT_SNAPSHOT_TABLE_NAME =   "audit_snapshot";
    private static final String DEFAULT_COMMIT_TABLE_NAME =    "audit_commit";
    private static final String DEFAULT_COMMIT_PROPERTY_TABLE_NAME = "audit_commit_property";

    private static final Logger logger = LoggerFactory.getLogger(TableNameProvider.class);
    private final SqlRepositoryConfiguration configuration;

    public TableNameProvider(SqlRepositoryConfiguration configuration) {
        this.configuration = configuration;
        logger.info("Commit table:           {}", getCommitTableNameWithSchema());
        logger.info("CommitProperty table:   {}", getCommitPropertyTableNameWithSchema());
        logger.info("GlobalId table:         {}", getGlobalIdTableNameWithSchema());
        logger.info("Snapshot table:         {}", getSnapshotTableNameWithSchema());
    }

    public String getGlobalIdTableNameWithSchema() {
        return getGlobalIdTableName().nameWithSchema();
    }

    public String getCommitTableNameWithSchema() {
        return getCommitTableName().nameWithSchema();
    }

    public String getCommitPropertyTableNameWithSchema() {
        return getCommitPropertyTableName().nameWithSchema();
    }

    public String getSnapshotTableNameWithSchema() {
        return getSnapshotTableName().nameWithSchema();
    }

    public DBObjectName getSnapshotTablePkSeqName() {
        return fullDbName(SNAPSHOT_TABLE_PK_SEQ);
    }

    public DBObjectName getGlobalIdPkSeqName() {
        return fullDbName(GLOBAL_ID_PK_SEQ);
    }

    public DBObjectName getCommitPkSeqName() {
        return fullDbName(COMMIT_PK_SEQ);
    }

    /**
     * used only by migration scripts
     */
    @Deprecated
    public String getCdoClassTableNameWithSchema() {
        return fullDbName("audit_cdo_class").nameWithSchema();
    }

    DBObjectName getGlobalIdTableName() {
        return fullDbName(configuration.getGlobalIdTableName().orElse(DEFAULT_GLOBAL_ID_TABLE_NAME));
    }

    DBObjectName getCommitTableName() {
        return fullDbName(configuration.getCommitTableName().orElse(DEFAULT_COMMIT_TABLE_NAME));
    }

    DBObjectName getCommitPropertyTableName() {
        return fullDbName(configuration.getCommitPropertyTableName().orElse(DEFAULT_COMMIT_PROPERTY_TABLE_NAME));
    }

    DBObjectName getSnapshotTableName() {
        return fullDbName(configuration.getSnapshotTableName().orElse(DEFAULT_SNAPSHOT_TABLE_NAME));
    }

    Optional<String> getSchemaName() {
        return configuration.getSchemaNameAsOptional();
    }

    private DBObjectName fullDbName(String name) {
        return new DBObjectName(configuration.getSchemaNameAsOptional(), name);
    }
}
