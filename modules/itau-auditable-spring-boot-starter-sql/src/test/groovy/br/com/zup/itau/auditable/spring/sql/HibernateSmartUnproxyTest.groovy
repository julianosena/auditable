package br.com.zup.itau.auditable.spring.sql

import org.hibernate.Hibernate
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.spring.boot.DummyEntity
import br.com.zup.itau.auditable.spring.boot.ShallowEntity
import br.com.zup.itau.auditable.spring.boot.ShallowEntityRepository
import br.com.zup.itau.auditable.spring.boot.TestApplication
import br.com.zup.itau.auditable.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class HibernateSmartUnproxyTest extends Specification {
    @Autowired
    ItauAuditable itauAuditable

    @Autowired
    DummyEntityRepository dummyEntityRepository

    @Autowired
    ShallowEntityRepository shallowEntityRepository

    def "should not initialize proxy of Shallow reference"() {
        given:
        def shallowEntity = shallowEntityRepository.save(ShallowEntity.random())
        def proxy = shallowEntityRepository.getOne(shallowEntity.id)

        println "proxy.isInitialized: " + Hibernate.isInitialized(proxy)
        println "proxy.class: " + proxy.class
        println "proxy.id: " + proxy.id
        println "proxy.persistenClass: " + proxy.getHibernateLazyInitializer().getPersistentClass()
        println "proxy.isInitialized: " + Hibernate.isInitialized(proxy)
        println 'I am happy :)'

        assert !Hibernate.isInitialized(proxy)

        when:
        def entity = DummyEntity.random()
        entity.setShallowEntity(proxy)
        entity = dummyEntityRepository.save(entity)

        then:
        def entitySnapshot = itauAuditable.getLatestSnapshot(entity.id, DummyEntity).get()
        InstanceId shallowRef = entitySnapshot.getPropertyValue("shallowEntity")
        shallowRef.typeName == ShallowEntity.class.name
        shallowRef.cdoId == shallowEntity.id

        assert !Hibernate.isInitialized(proxy)
    }
}
