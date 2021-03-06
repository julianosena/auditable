package br.com.zup.itau.auditable.repository.sql

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Ignore
import spock.lang.Specification

import java.math.RoundingMode
import java.sql.Connection
import java.sql.DriverManager

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.itauAuditable

/**
 * @author bartosz walacik
 */
@Ignore
class SqlMigrationTest extends Specification{

    Connection dbConnection
    ItauAuditable itauAuditable

    static def n = 1000
    static def updates = 200

    def start

    def setup() {
        start = System.currentTimeMillis()

        //dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/itauAuditable", "itauAuditable", "itauAuditable")

        //dbConnection = DriverManager.getConnection("jdbc:mysql://192.168.99.100:32774/itauAuditable_db", "itauAuditable", "itauAuditable");

        dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.99.100:49161:xe", "itauAuditable", "itauAuditable");

        //dbConnection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=polly", "polly", "polly");

        dbConnection.setAutoCommit(false)

        def connectionProvider = { dbConnection } as ConnectionProvider

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.ORACLE).build()
        itauAuditable = itauAuditable().registerItauAuditableRepository(sqlRepository).build()
    }

    def cleanup() {
        stop(start, n)
    }

    def "should init database - insert and updates - old schema"() {
        given:
        clearTables()

        when:
        n.times {
            def root = MigrationEntity.produce(it * 100, 9)
            itauAuditable.commit("author", root)

            root.change()
            itauAuditable.commit("author", root)

            dbConnection.commit()
        }

        then:
        true
    }

    def "should do schema migration - new schema "(){
        when:
        dbConnection.commit()

        then:
        true
    }

    def "should query after migration"() {
        expect:
        itauAuditable.findSnapshots(QueryBuilder.byClass(MigrationEntity).limit(n*100).build()).size() == 2 * n * 10
        itauAuditable.findSnapshots(QueryBuilder.byClass(MigrationValueObject).limit(n*100).build()).size() == 2 * n * 10
        itauAuditable.findSnapshots(QueryBuilder.byClass(AnotherValueObject).limit(n*100).build()).size() == 2 * n * 10

        itauAuditable.findSnapshots(QueryBuilder.byValueObject(MigrationEntity, 'vo').limit(n*n).build()).size() == 2 * n * 10
        itauAuditable.findSnapshots(QueryBuilder.byValueObject(MigrationEntity, 'anotherVo').limit(n*n).build()).size() == 2 * n * 10

        itauAuditable.findSnapshots(QueryBuilder.byInstanceId(100, MigrationEntity).build()).size() == 2

        itauAuditable.findSnapshots(QueryBuilder.byValueObjectId(100, MigrationEntity, 'vo').build()).size() == 2
        itauAuditable.findSnapshots(QueryBuilder.byValueObjectId(100, MigrationEntity, 'anotherVo').build()).size() == 2
    }

    def "should do inserts & updates after migration"() {
        when:
        updates.times {
            def root = MigrationEntity.produce(it * 100 + n * 100, 9)
            itauAuditable.commit("author", root)
            root.change()
            itauAuditable.commit("author", root)

            dbConnection.commit()
        }

        then:
        itauAuditable.findSnapshots(QueryBuilder.byClass(MigrationEntity).limit(n*100).build()).size() == 2 * n * 10 + 2 * updates * 10
        itauAuditable.findSnapshots(QueryBuilder.byClass(MigrationValueObject).limit(n*100).build()).size() == 2 * n * 10 + 2 * updates * 10
        itauAuditable.findSnapshots(QueryBuilder.byClass(AnotherValueObject).limit(n*100).build()).size() == 2 * n * 10 + 2 * updates * 10
    }

    def stop(long start, int times){
        def stop = System.currentTimeMillis()

        def opAvg = (stop-start)/times

        println "total time: "+ round(stop-start)+" ms"
        println "op avg:     "+ round(opAvg)+" ms"
    }

    String round(def what){
        new BigDecimal(what).setScale(2,RoundingMode.HALF_UP).toString()
    }

    def clearTables(){
        execute("delete  from audit_snapshot")
        execute("delete  from audit_commit")
        execute("delete  from audit_global_id")
    }

    def execute(String sql){
        def stmt = dbConnection.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }
}
