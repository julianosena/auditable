package br.com.zup.itau.auditable.spring.auditable.integration

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.spring.example.ItauAuditableSpringJpaApplicationConfig
import br.com.zup.itau.auditable.spring.model.DummyObject
import br.com.zup.itau.auditable.spring.repository.DummyAuditedJpaRepository
import br.com.zup.itau.auditable.spring.repository.DummyNoAuditJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId

@ContextConfiguration(classes = [ItauAuditableSpringJpaApplicationConfig])
class ItauAuditableSpringDataJpaAspectIntegrationTest extends Specification {
    @Autowired
    ApplicationContext context

    @Autowired
    ItauAuditable itauAuditable

    @Autowired
    DummyAuditedJpaRepository repository

    @Autowired
    DummyNoAuditJpaRepository noAuditRepository

    def "should commit to Itaú Auditable on audited jpaRepository.save(Object)"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        itauAuditable.findSnapshots(byInstanceId(o.id, DummyObject).build()).size() == 1
    }

    def "should commit to Itaú Auditable on audited jpaRepository.saveAndFlush(object)"() {
        given:
        def o = new DummyObject()

        when:
        repository.saveAndFlush(o)

        then:
        itauAuditable.findSnapshots(br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 1
    }

    def "should commitDelete on audited jpaRepository.delete(object)"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.delete(o)

        then:
        def snapshots = itauAuditable.findSnapshots(byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should not commit when any method is called on not audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        noAuditRepository.save(o)
        noAuditRepository.delete(o)
        noAuditRepository.getOne(o.id)

        then:
        itauAuditable.findSnapshots(byInstanceId(o.id, DummyObject).build()).size() == 0
    }
}
