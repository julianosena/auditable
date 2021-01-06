package br.com.zup.itau.auditable.spring.auditable.integration

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAuditableAspectAsync
import br.com.zup.itau.auditable.spring.model.DummyObject
import br.com.zup.itau.auditable.spring.repository.DummyAuditedAsyncRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId

@ContextConfiguration(classes = [TestApplicationConfig])
class ItauAuditableAuditableAspectAsyncIntegrationTest extends Specification {

    @Autowired
    ItauAuditable javers

    @Autowired
    ItauAuditableAuditableAspectAsync javersAuditableAspectAsync

    @Autowired
    DummyAuditedAsyncRepository repository

    @Autowired
    ItauAuditableAuditableAspectAsync aspectAsync

    def "should asynchronously commit a method's argument when annotated with @ItauAuditableAuditableAsync"() {
        given:
        def o = new DummyObject()

        assert !javersAuditableAspectAsync.lastAsyncCommit.isPresent()

        when:
        repository.save(o)
        println "lastAsyncCommit: " + javersAuditableAspectAsync.lastAsyncCommit.get()

        // should be tested with this assertion:
        // !javersAuditableAspectAsync.lastAsyncCommit.get().isDone()
        // but it failes occasionally

        and:
        waitForCommit([o])

        then:
        def snapshot = javers.findSnapshots(byInstanceId(o.id, DummyObject).build())[0]
        javersAuditableAspectAsync.lastAsyncCommit.get().isDone()

        snapshot.globalId.cdoId == o.id
        snapshot.commitMetadata.properties["key"] == "ok"

    }

    def "should asynchronously commit two method's arguments when annotated with @ItauAuditableAuditableAsync"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when:
        repository.saveTwo(o1, o2)
        println "lastAsyncCommit: " + javersAuditableAspectAsync.lastAsyncCommit.get()

        // should be tested with this assertion:
        // !javersAuditableAspectAsync.lastAsyncCommit.get().isDone()
        // but it failes occasionally

        and:
        waitForCommit([o1, o2])

        then:
        javers.findSnapshots(byInstanceId(o1.id, DummyObject).build()).size() == 1
        javers.findSnapshots(byInstanceId(o2.id, DummyObject).build()).size() == 1
        javersAuditableAspectAsync.lastAsyncCommit.get().isDone()
    }

    def "should asynchronously commit an iterable argument when method is annotated with @ItauAuditableAuditableAsync"() {
        given:
        List objects = (1..20).collect{new DummyObject()}

        when:
        repository.saveAll(objects)
        println "lastAsyncCommit: " + javersAuditableAspectAsync.lastAsyncCommit.get()

        then:
        !javersAuditableAspectAsync.lastAsyncCommit.get().isDone()

        when:
        waitForCommit(objects)

        then:
        javersAuditableAspectAsync.lastAsyncCommit.get().isDone()
        (objects).each {o ->
            assert javers.findSnapshots(byInstanceId(o.id, DummyObject).build()).size() == 1
        }
    }

    void waitForCommit(List objects) {
        println "waitForCommit... "
        long start = new Date().time
        for (int i=0; i<50; i++) {

            def sizes = objects.collect{o ->
                def query = QueryBuilder.byInstanceId(o.id, DummyObject).build()
                javers.findSnapshots(query).size()
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
