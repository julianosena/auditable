package br.com.zup.itau.auditable.repository.sql.schema;

import br.com.zup.itau.auditable.core.AuditableContextHolder;

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
