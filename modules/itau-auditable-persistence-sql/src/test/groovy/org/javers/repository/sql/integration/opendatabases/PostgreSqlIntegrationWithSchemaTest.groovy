package br.com.zup.itau.auditable.repository.sql.integration.opendatabases

/**
 * @author Ian Agius
 */
class PostgreSqlIntegrationWithSchemaTest extends PostgreSqlIntegrationTest {

    @Override
    String getSchema() {
        return "j_some"
    }
}