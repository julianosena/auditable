package br.com.zup.itau.auditable.spring.boot.mongo

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class ItauAuditableMongoStarterIntegrationTest extends Specification{

    @Autowired
    ItauAuditable javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build default javers instance with auto-audit aspect" () {
        when:
        def dummyEntity = dummyEntityRepository.save(new DummyEntity(UUID.randomUUID().hashCode()))
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(dummyEntity.id, DummyEntity).build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["dummyEntityId"] == dummyEntity.id + ""
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should scan given packages for classes with @TypeName"() {
        expect:
        javers.getTypeMapping("AnotherEntity") instanceof EntityType
    }
}