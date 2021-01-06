package br.com.zup.itau.auditable.spring.auditable.integration

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAspectAsync
import br.com.zup.itau.auditable.spring.model.DummyObject
import br.com.zup.itau.auditable.spring.repository.DummyAuditedAsyncRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId

@ContextConfiguration(classes = [TestApplicationConfig])
class ItauAuditableAspectAsyncIntegrationTest extends Specification {

    @Autowired
    ItauAuditable itauAuditable

    @Autowired
    ItauAuditableAspectAsync itauAuditableAuditableAspectAsync

    @Autowired
    DummyAuditedAsyncRepository repository

    @Autowired
    ItauAuditableAspectAsync aspectAsync

    def "should asynchronously commit a method's argument when annotated with @ItauAuditableAsync"() {
        given:
        def o = new DummyObject()

        assert !itauAuditableAuditableAspectAsync.lastAsyncCommit.isPresent()

        when:
        repository.save(o)
        println "lastAsyncCommit: " + itauAuditableAuditableAspectAsync.lastAsyncCommit.get()

        // should be tested with this assertion:
        // !itauAuditableAuditableAspectAsync.lastAsyncCommit.get().isDone()
        // but it failes occasionally

        and:
        waitForCommit([o])

        then:
        def snapshot = itauAuditable.findSnapshots(byInstanceId(o.id, DummyObject).build())[0]
        itauAuditableAuditableAspectAsync.lastAsyncCommit.get().isDone()

        snapshot.globalId.cdoId == o.id
        snapshot.commitMetadata.properties["key"] == "ok"

    }

    def "should asynchronously commit two method's arguments when annotated with @ItauAuditableAsync"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when:
        repository.saveTwo(o1, o2)
        println "lastAsyncCommit: " + itauAuditableAuditableAspectAsync.lastAsyncCommit.get()

        // should be tested with this assertion:
        // !itauAuditableAuditableAspectAsync.lastAsyncCommit.get().isDone()
        // but it failes occasionally

        and:
        waitForCommit([o1, o2])

        then:
        itauAuditable.findSnapshots(byInstanceId(o1.id, DummyObject).build()).size() == 1
        itauAuditable.findSnapshots(byInstanceId(o2.id, DummyObject).build()).size() == 1
        itauAuditableAuditableAspectAsync.lastAsyncCommit.get().isDone()
    }

    def "should asynchronously commit an iterable argument when method is annotated with @ItauAuditableAsync"() {
        given:
        List objects = (1..20).collect{new DummyObject()}

        when:
        repository.saveAll(objects)
        println "lastAsyncCommit: " + itauAuditableAuditableAspectAsync.lastAsyncCommit.get()

        then:
        !itauAuditableAuditableAspectAsync.lastAsyncCommit.get().isDone()

        when:
        waitForCommit(objects)

        then:
        itauAuditableAuditableAspectAsync.lastAsyncCommit.get().isDone()
        (objects).each {o ->
            assert itauAuditable.findSnapshots(byInstanceId(o.id, DummyObject).build()).size() == 1
        }
    }

    void waitForCommit(List objects) {
        println "waitForCommit... "
        long start = new Date().time
        for (int i=0; i<50; i++) {

            def sizes = objects.collect{o ->
                def query = QueryBuilder.byInstanceId(o.id, DummyObject).build()
                itauAuditable.findSnapshots(query).size()
            }
            println("sizes : " + sizes)

            if (sizes.sum() >= objects.size()) {
                long stop = new Date().time
                println "awaited " + (stop - start) + " millis"
                break
            }

            println("$i - wait 50ms ...")
            sleep(50)
        }
    }
}
