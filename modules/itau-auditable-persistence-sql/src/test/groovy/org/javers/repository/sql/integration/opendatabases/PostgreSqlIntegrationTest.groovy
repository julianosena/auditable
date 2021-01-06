package br.com.zup.itau.auditable.repository.sql.integration.opendatabases

import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

class PostgreSqlIntegrationTest extends ItauAuditableSqlRepositoryE2ETest {

    @Override
    Connection createConnection() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/travis_ci_test", "itauAuditable", "itauAuditable");
    }

    @Override
    DialectName getDialect() {
        DialectName.POSTGRES
    }

    @Override
    String getSchema() {
        return null
    }

    @Override
    boolean useRandomCommitIdGenerator() {
        false
    }
}
