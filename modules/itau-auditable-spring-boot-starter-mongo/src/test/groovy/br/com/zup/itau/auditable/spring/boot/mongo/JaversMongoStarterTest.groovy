package br.com.zup.itau.auditable.spring.boot.mongo

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider
import br.com.zup.itau.auditable.spring.auditable.SpringSecurityAuthorProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class ItauAuditableMongoStarterTest extends Specification{

    @Autowired
    ItauAuditable javers

    @Autowired
    ItauAuditableMongoProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "shoudUseDbNameFromMongoStarter"(){
        expect:
        javers.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo"
    }

    def "shouldReadConfigurationFromYml"() {
        expect:
        javersProperties.algorithm == "levenshtein_distance"
        javersProperties.mappingStyle == "bean"
        !javersProperties.newObjectSnapshot
        !javersProperties.prettyPrint
        javersProperties.typeSafeValues
        javersProperties.commitIdGenerator == "random"
        javersProperties.documentDbCompatibilityEnabled == true
        javersProperties.objectAccessHook == "br.com.zup.itau.auditable.spring.boot.mongo.DummyDBRefUnproxyObjectAccessHook"
        javersProperties.snapshotsCacheSize == 100
    }

    def "shouldReadBeanMappingStyleFromYml"() {
        expect:
        javers.getTypeMapping(DummyEntity) instanceof EntityType
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
