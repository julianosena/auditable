package br.com.zup.itau.auditable.repository.sql.schema;

import java.util.Optional;

public abstract class MultitenancySchemaNameAware {
    private final MultitenancyTableNameProvider tableNameProvider;

    protected MultitenancySchemaNameAware(MultitenancyTableNameProvider tableNameProvider) {
        this.tableNameProvider = tableNameProvider;
    }

    protected String getCommitTableNameWithSchema() {
        return tableNameProvider.getCommitTableNameWithSchema();
    }

    protected String getSnapshotTableNameWithSchema() {
        return tableNameProvider.getSnapshotTableNameWithSchema();
    }

    protected String getGlobalIdTableNameWithSchema() {
        return tableNameProvider.getGlobalIdTableNameWithSchema();
    }

    protected String getCommitPropertyTableNameWithSchema() {
        return tableNameProvider.getCommitPropertyTableNameWithSchema();
    }

    protected MultitenancyDBObjectName getGlobalIdTableName() {
        return tableNameProvider.getGlobalIdTableName();
    }

    protected MultitenancyDBObjectName getCommitTableName() {
        return tableNameProvider.getCommitTableName();
    }

    protected MultitenancyDBObjectName getCommitPropertyTableName() {
        return tableNameProvider.getCommitPropertyTableName();
    }

    protected MultitenancyDBObjectName getSnapshotTableName() {
        return tableNameProvider.getSnapshotTableName();
    }

    protected MultitenancyDBObjectName getCommitPkSeqName(){
        return tableNameProvider.getCommitPkSeqName();
    }

    protected MultitenancyDBObjectName getSnapshotTablePkSeqName(){
        return tableNameProvider.getSnapshotTablePkSeqName();
    }

    protected MultitenancyDBObjectName getGlobalIdPkSeqName() {
        return tableNameProvider.getGlobalIdPkSeqName();
    }

    protected String getSchemaName() {
        return new StringBuilder("\"")
                .append(tableNameProvider.getSchemaName())
                .append("\"")
                .toString();
    }
}
