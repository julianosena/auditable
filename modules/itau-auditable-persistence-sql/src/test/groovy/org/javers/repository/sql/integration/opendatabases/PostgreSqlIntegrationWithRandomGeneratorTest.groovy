package br.com.zup.itau.auditable.repository.sql.integration.opendatabases

class PostgreSqlIntegrationWithRandomGeneratorTest extends PostgreSqlIntegrationTest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
