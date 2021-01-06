package br.com.zup.itau.auditable.repository.sql.integration.docker

import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepositoryE2ETest
import org.testcontainers.containers.MSSQLServerContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

@Testcontainers
class MSSqlDockerIntegrationTest extends ItauAuditableSqlRepositoryE2ETest {

    @Shared
    public MSSQLServerContainer mssqlserver = new MSSQLServerContainer();

    Connection createConnection() {
       String url = mssqlserver.jdbcUrl
       String user = mssqlserver.username
       String pass = mssqlserver.password

       DriverManager.getConnection(url, user, pass)
    }

    DialectName getDialect() {
        DialectName.MSSQL
    }

    String getSchema() {
        return null
    }
}
