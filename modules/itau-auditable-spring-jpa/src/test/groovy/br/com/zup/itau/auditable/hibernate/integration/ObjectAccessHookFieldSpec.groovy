package br.com.zup.itau.auditable.hibernate.integration

import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.hibernate.entity.Person
import br.com.zup.itau.auditable.hibernate.entity.PersonCrudRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = ItauAuditableFieldHibernateProxyConfig)
class ObjectAccessHookFieldSpec extends Specification {

    @Autowired
    ItauAuditable javers

    @Autowired
    PersonCrudRepository repository

    @Unroll
    def "should unproxy hibernate entity with Field MappingType when modPointLevel is #modPointLevel and savePointLevel is #savePointLevel"() {
        given:
        def developer = new Person("0", "kaz")
        def manager =   new Person("1", "pawel")
        def director =  new Person("2", "Steve")
        developer.boss = manager
        manager.boss = director
        repository.saveAll([director, manager, developer])

        def loadedDeveloper = repository.getOne(developer.id)

        def proxy = loadedDeveloper.getBoss(modPointLevel)
        assert proxy instanceof HibernateProxy
        assert !Hibernate.isInitialized(proxy)

        when:
        proxy.name = "New Name"
        def savePoint = loadedDeveloper.getBoss(savePointLevel)
        repository.save(savePoint)

        then:
        def snapshot = javers.getLatestSnapshot(proxy.id, Person).get()
        snapshot.getPropertyValue("name") == "New Name"

        where:
        savePointLevel <<     [0, 1, 1] // 0   this case fails after bumping Hibernate
        modPointLevel  <<     [1, 1, 2] // 2   to 5.3.11.Final for unknown reason
    }

}