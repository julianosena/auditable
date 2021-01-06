package br.com.zup.itau.auditable.repository.sql.integration.docker

import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepositoryE2ETest
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

@Testcontainers
class MySqlDockerIntegrationTest extends ItauAuditableSqlRepositoryE2ETest {

    @Shared
    public MySQLContainer postgres = new MySQLContainer()

    Connection createConnection() {
       String url = postgres.jdbcUrl
       String user = postgres.username
       String pass = postgres.password

       DriverManager.getConnection(url, user, pass)
    }

    DialectName getDialect() {
        DialectName.MYSQL
    }

    String getSchema() {
        return null
    }
}
