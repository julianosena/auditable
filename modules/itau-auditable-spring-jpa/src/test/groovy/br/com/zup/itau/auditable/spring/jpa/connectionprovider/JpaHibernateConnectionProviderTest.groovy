package br.com.zup.itau.auditable.spring.jpa.connectionprovider

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.spring.example.ItauAuditableSpringJpaApplicationConfig
import br.com.zup.itau.auditable.spring.model.DummyObject
import br.com.zup.itau.auditable.spring.repository.DummyAuditedJpaRepository
import br.com.zup.itau.auditable.spring.repository.DummyAuditedRepository
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class JpaHibernateConnectionProviderTest extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    ItauAuditable itauAuditable

    @Shared
    def repository


    def setupSpec() {
        context = new AnnotationConfigApplicationContext(ItauAuditableSpringJpaApplicationConfig)
        itauAuditable = context.getBean(ItauAuditable)
    }

    @Unroll
    def "should use transactional JpaHibernateConnectionProvider with #repositortKind Repository to commit and read objects"() {
        given:
        repository = context.getBean(repositoryClass)
        def o = new DummyObject("some")

        when:
        repository.save(o)
        def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())

        then:
        true
        snapshots.size() == 1

        where:
        repositortKind <<  ["ordinal","spring-data-crud"]
        repositoryClass << [DummyAuditedRepository, DummyAuditedJpaRepository]
    }

}
