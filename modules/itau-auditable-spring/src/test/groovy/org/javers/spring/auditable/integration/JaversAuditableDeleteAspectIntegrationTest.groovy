package br.com.zup.itau.auditable.spring.auditable.integration

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.spring.model.DummyObject
import br.com.zup.itau.auditable.spring.repository.DummyAuditedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [TestApplicationConfig])
class ItauAuditableDeleteAspectIntegrationTest extends Specification {

    @Autowired
    ItauAuditable itauAuditable

    @Autowired
    DummyAuditedRepository repository

    def "should commit single argument when method is annotated with @ItauAuditableDelete"() {
        given: "one arg test"
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.delete(o)

        then:
        def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should commit few arguments when method is annotated with @ItauAuditableDelete"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when: "many args test"
        repository.saveTwo(o1, o2)
        repository.deleteTwo(o1, o2)

        then:
        def snapshots1 = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(o1.id, DummyObject).build())
        def snapshots2 = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(o2.id, DummyObject).build())

        [snapshots1, snapshots2].each { snapshots ->
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }
    }

    def "should commit with properties provided by CommitPropertiesProvider when method is annotated with @ItauAuditableDelete"(){
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.delete(o)

        then:
        def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[0].commitMetadata.properties["key"] == "ok"
        snapshots[1].initial
    }

    def "should commit iterable argument when method is annotated with @ItauAuditableDelete"() {
        given:
        def objects = [new DummyObject(), new DummyObject()]

        when: "iterable arg test"
        repository.saveAll(objects)
        repository.deleteAll(objects)

        then:
        objects.each {
            itauAuditable.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build()).size() == 1

            def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build())
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }
    }

    def "should commit delete by Id when a method is annotated with @ItauAuditableDelete"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.deleteById(o.id)

        then:
        def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should commit by Ids iterable when a method is annotated with @ItauAuditableDelete"() {
        given:
        def objects = [new DummyObject(), new DummyObject()]

        when: "iterable arg test"
        repository.saveAll(objects)
        repository.deleteAllById(objects.collect { it.id })

        then:
        objects.each {
            itauAuditable.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build()).size() == 1

            def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build())
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }
    }

    def "should throw the exception if no entity parameter is given when deleting by Id using @ItauAuditableDelete"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.deleteByIdNoClass(o.id)

        then:
        ItauAuditableException e = thrown()
        println e
        e.code == ItauAuditableExceptionCode.WRONG_USAGE_OF_JAVERS_AUDITABLE_DELETE
    }
}
