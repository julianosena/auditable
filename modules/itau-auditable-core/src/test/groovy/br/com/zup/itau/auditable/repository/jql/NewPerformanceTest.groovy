package br.com.zup.itau.auditable.repository.jql

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import spock.lang.Ignore
import spock.lang.Specification

import java.math.RoundingMode

abstract class NewPerformanceTest extends Specification {

    ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build()
    def start

    def "should init database - insert and updates"() {
        given:
        def n = 50
        //clearDatabase()
        start()

        when:
        n.times {
            def root = NewPerformanceEntity.produce(10)
            itauAuditable.commit("author", root, [os: "android", country: "pl"])

            root.change()
            itauAuditable.commit("author", root, [os: "a" + it, lang: "pl", country: "de"])

            commitDatabase()
        }

        then:
        stop(n)
    }

    @Ignore
    def "should query - standard queries by Type"() {
        given:
        start()

        when:
        itauAuditable.findSnapshots(QueryBuilder.byClass(NewPerformanceEntity).limit(100).build()).size() == 100
        itauAuditable.findSnapshots(QueryBuilder.byClass(MigrationValueObject).limit(100).build()).size() == 100
        itauAuditable.findSnapshots(QueryBuilder.byClass(AnotherValueObject).limit(100).build()).size() == 100
        itauAuditable.findSnapshots(QueryBuilder.byValueObject(NewPerformanceEntity, 'vo').limit(100).build()).size() == 100
        itauAuditable.findSnapshots(QueryBuilder.byValueObject(NewPerformanceEntity, 'anotherVo').limit(100).build()).size() == 100

        then:
        stop(5)
    }

    @Ignore
    def "should query - standard queries by Id"() {
        given:
        start()

        when:
        def n = 30
        n.times {
            def id = n * 100
            assert itauAuditable.findSnapshots(QueryBuilder.byInstanceId(id, NewPerformanceEntity).build()).size() == 2
            assert itauAuditable.findSnapshots(QueryBuilder.byValueObjectId(id, NewPerformanceEntity, 'vo').build()).size() == 2
            assert itauAuditable.findSnapshots(QueryBuilder.byValueObjectId(id, NewPerformanceEntity, 'anotherVo').build()).size() == 2
        }

        then:
        stop(n * 3)
    }

    @Ignore
    def "should query - new query by property"() {
        given:
        start()

        when:
        int n = 10

        n.times {
            assert itauAuditable.findSnapshots(QueryBuilder.byClass(NewPerformanceEntity)
                    .withCommitProperty("os", "a" + it)
                    .build()).size() == 3
            assert itauAuditable.findSnapshots(QueryBuilder.byClass(AnotherValueObject)
                    .withCommitProperty("lang", "pl")
                    .withCommitProperty("os", "a" + it)
                    .build()).size() == 3
            assert itauAuditable.findSnapshots(QueryBuilder.byValueObject(NewPerformanceEntity, "vo")
                    .withCommitProperty("country", "de")
                    .withCommitProperty("lang", "pl")
                    .withCommitProperty("os", "a" + it)
                    .build()).size() == 3
        }

        then:
        stop(n * 3)
    }

    @Ignore
    def "should query - new Aggregate queries by Id"() {
        given:
        start()

        when:
        def n = 30
        n.times {
            def id = n * 100
            def query = QueryBuilder.byInstanceId(id, NewPerformanceEntity).withChildValueObjects().build()
            assert itauAuditable.findSnapshots(query).size() == 6
        }

        then:
        stop(n * 3 + 5)
    }

    void start() {
        start = System.currentTimeMillis()
    }

    boolean stop(int times) {
        def stop = System.currentTimeMillis()

        def opAvg = (stop - start) / times

        println "total time: " + round(stop - start) + " ms"
        println "op avg:     " + round(opAvg) + " ms"

        true
    }

    String round(def what) {
        new BigDecimal(what).setScale(2, RoundingMode.HALF_UP).toString()
    }

    void clearDatabase() {
    }

    void commitDatabase() {
    }
}
