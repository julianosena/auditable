package br.com.zup.itau.auditable.spring.boot.mongo.dbref

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.spring.boot.mongo.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class DBRefUnproxyObjectAccessHookTest extends Specification {

    @Autowired
    MyDummyEntityRepository dummyEntityRepository

    @Autowired
    MyDummyRefEntityRepository dummyRefEntityRepository

    @Autowired
    ItauAuditable itauAuditable

    @Unroll
    def "should unproxy a LazyLoadingProxy of DBRef before #commitKind commit to Itaú Auditable"() {
        given:
        def refEntity = new MyDummyRefEntity(name: "bert")
        refEntity = dummyRefEntityRepository.save(refEntity)

        def author = new MyDummyEntity(refEntity: refEntity, name: "kaz")
        author = dummyEntityRepository.save(author)

        def loaded = dummyEntityRepository.findById(author.getId()).get()
        assert loaded.refEntity instanceof LazyLoadingProxy

        when:
        loaded.name = "mad kaz"
        commit(loaded, itauAuditable, dummyEntityRepository)

        def authorSnapshot = itauAuditable.getLatestSnapshot(author.id, MyDummyEntity)
        def refEntitySnapshot = itauAuditable.getLatestSnapshot(refEntity.id, MyDummyRefEntity)

        then:
        refEntitySnapshot.isPresent()
        authorSnapshot.get().getPropertyValue("name") == "mad kaz"
        authorSnapshot.get().getPropertyValue("refEntity") instanceof InstanceId
        authorSnapshot.get().getPropertyValue("refEntity").value() == MyDummyRefEntity.name + "/" + refEntity.id

        where:
        commitKind << ["direct", "AOP"]
        commit     << [
                {loaded_, itauAuditable_, dummyEntityRepository_ -> itauAuditable_.commit("me", loaded_)},
                {loaded_, itauAuditable_, dummyEntityRepository_ -> dummyEntityRepository_.save(loaded_)}
        ]
    }
}
