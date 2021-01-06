package br.com.zup.itau.auditable.spring.jpa

import groovy.sql.Sql
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.hibernate.entity.PersonCrudRepository
import br.com.zup.itau.auditable.hibernate.entity.Person
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static br.com.zup.itau.auditable.hibernate.integration.config.HibernateConfig.H2_URL

@ContextConfiguration(classes = MultipleTxManagersConfig)
class MultipleTxManagersTest extends Specification {
    @Autowired
    ItauAuditable javers

    @Autowired
    PersonCrudRepository repository

    def setup() {
        def sql = Sql.newInstance(H2_URL, "org.h2.Driver")
        sql.execute("DELETE jv_snapshot")
    }

    def "should not fail when there are more than one transaction manager in the application context"(){
        given:
        def person = new Person(id:"kaz")

        when:
        repository.save(person)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId("kaz", Person).build()).size() == 1
    }
}
