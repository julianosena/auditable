package org.javers.repository.sql.schema;

import org.javers.core.AuditableContextHolder;

public class MultitenancyDBObjectName {

    private final String SCHEMA_TABLE_SEP = ".";

    private final String localName;

    MultitenancyDBObjectName(String localName) {
        this.localName = localName;
    }

    public String localName() {
        return localName;
    }

    public String nameWithSchema() {
        String schemaDatabaseName = AuditableContextHolder.getContext().getDatabaseSchemaName();
        return "\"" +schemaDatabaseName + "\"" + SCHEMA_TABLE_SEP + localName;
    }

    @Override
    public String toString() {
        return nameWithSchema();
    }
}
