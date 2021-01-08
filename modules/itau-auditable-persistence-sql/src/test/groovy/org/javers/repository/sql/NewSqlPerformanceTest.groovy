package br.com.zup.itau.auditable.repository.sql

import br.com.zup.itau.auditable.repository.jql.NewPerformanceTest
import spock.lang.Ignore

import java.sql.Connection
import java.sql.DriverManager
import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.itauAuditable

@Ignore
class NewSqlPerformanceTest extends NewPerformanceTest {

    Connection dbConnection

    def setup() {
        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/itauAuditable", "itauAuditable", "itauAuditable")

        //dbConnection = DriverManager.getConnection("jdbc:mysql://192.168.99.100:32774/itauAuditable_db", "itauAuditable", "itauAuditable");
        //dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.99.100:49161:xe", "itauAuditable", "itauAuditable");
        //dbConnection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=polly", "polly", "polly");

        dbConnection.setAutoCommit(false)

        def connectionProvider = { dbConnection } as ConnectionProvider

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.POSTGRES).build()
        itauAuditable = itauAuditable().registerItauAuditableRepository(sqlRepository).build()
    }

    @Override
    void clearDatabase(){
        execute("delete  from audit_commit_property")
        execute("delete  from audit_snapshot")
        execute("delete  from audit_commit")
        execute("delete  from audit_global_id")
    }

    @Override
    void commitDatabase() {
        dbConnection.commit()
    }

    void execute(String sql){
        println (sql)
        def stmt = dbConnection.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }
}
