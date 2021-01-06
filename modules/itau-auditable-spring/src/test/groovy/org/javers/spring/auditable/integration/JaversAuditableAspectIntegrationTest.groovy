package br.com.zup.itau.auditable.spring.auditable.integration

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.spring.model.DummyObject
import br.com.zup.itau.auditable.spring.repository.DummyAuditedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [TestApplicationConfig])
class ItauAuditableAuditableAspectIntegrationTest extends Specification {

    @Autowired
    ItauAuditable javers

    @Autowired
    DummyAuditedRepository repository

    def "should commit a method's argument when annotated with @ItauAuditableAuditable"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 1
    }

    def "should not commit method args when it didn't exit normally"() {
        given:
        def o = new DummyObject()

        when:
        try {
            repository.saveAndFail(o)
        } catch (Exception e) {}

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 0
    }

    def "should commit method's arguments when annotated with @ItauAuditableAuditable"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when: "many args test"
        repository.saveTwo(o1, o2)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o1.id, DummyObject).build()).size() == 1
        javers.findSnapshots(QueryBuilder.byInstanceId(o2.id, DummyObject).build()).size() == 1
    }

    def "should commit with properties provided by CommitPropertiesProvider"(){
        given:
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())[0]
        snapshot.commitMetadata.properties["key"] == "ok"
    }

    def "should commit iterable argument when method is annotated with @ItauAuditableAuditable"() {
        given:
        def objects = [new DummyObject(), new DummyObject()]

        when: "iterable arg test"
        repository.saveAll(objects)

        then:
        objects.each {
            javers.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build()).size() == 1
        }
    }

    def "should not advice a method from a Repository when no annotation"() {
        given:
        def o = new DummyObject()

        when:
        repository.find(o)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 0
    }
}
