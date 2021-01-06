package br.com.zup.itau.auditable.spring.jpa

import groovy.sql.Sql
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.hibernate.entity.Person
import br.com.zup.itau.auditable.hibernate.entity.PersonCrudRepository
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static br.com.zup.itau.auditable.hibernate.integration.config.HibernateConfig.H2_URL

@ContextConfiguration(classes = CacheEvictSpringConfig)
class CacheEvictTest extends Specification{

    @Autowired
    ItauAuditable javers

    @Autowired
    ItauAuditableSqlRepository javersSqlRepository

    @Autowired
    PersonCrudRepository repository

    @Autowired
    ErrorThrowingService errorThrowingService

    def setup() {
        def sql = Sql.newInstance(H2_URL, "org.h2.Driver")
        sql.execute("DELETE jv_snapshot")
    }

    def "should evict GlobalId PK Cache after rollback"(){
      given:
      def person = new Person(id:"kaz")

      when:
      repository.save(person)
      person.name = "kaz"
      errorThrowingService.saveAndThrow(person)

      then:
      def ex = thrown(RuntimeException)
      ex.message == "rollback"
      javers.findSnapshots(QueryBuilder.anyDomainObject().build()).size() == 1
      javersSqlRepository.globalIdPkCacheSize == 0
    }
}
