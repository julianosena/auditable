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
    ItauAuditable itauAuditable

    @Autowired
    ItauAuditableMongoProperties itauAuditableProperties

    @Autowired
    AuthorProvider provider

    def "shoudUseDbNameFromMongoStarter"(){
        expect:
        itauAuditable.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo"
    }

    def "shouldReadConfigurationFromYml"() {
        expect:
        itauAuditableProperties.algorithm == "levenshtein_distance"
        itauAuditableProperties.mappingStyle == "bean"
        !itauAuditableProperties.newObjectSnapshot
        !itauAuditableProperties.prettyPrint
        itauAuditableProperties.typeSafeValues
        itauAuditableProperties.commitIdGenerator == "random"
        itauAuditableProperties.documentDbCompatibilityEnabled == true
        itauAuditableProperties.objectAccessHook == "br.com.zup.itau.auditable.spring.boot.mongo.DummyDBRefUnproxyObjectAccessHook"
        itauAuditableProperties.snapshotsCacheSize == 100
    }

    def "shouldReadBeanMappingStyleFromYml"() {
        expect:
        itauAuditable.getTypeMapping(DummyEntity) instanceof EntityType
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
