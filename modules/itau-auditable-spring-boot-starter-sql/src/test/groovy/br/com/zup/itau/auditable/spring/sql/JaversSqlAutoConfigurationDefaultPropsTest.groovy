package br.com.zup.itau.auditable.spring.sql


import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider
import br.com.zup.itau.auditable.spring.auditable.SpringSecurityAuthorProvider
import br.com.zup.itau.auditable.spring.boot.TestApplication
import br.com.zup.itau.auditable.spring.boot.sql.ItauAuditableSqlProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
class ItauAuditableSqlAutoConfigurationDefaultPropsTest extends Specification {

    @Autowired
    DialectName dialectName

    @Autowired
    ItauAuditableSqlProperties itauAuditableProperties

    @Autowired
    AuthorProvider provider

    def "should provide default props"() {
        expect:
        itauAuditableProperties.getAlgorithm() == "simple"
        itauAuditableProperties.getMappingStyle() == "field"
        !itauAuditableProperties.isNewObjectSnapshot()
        itauAuditableProperties.isPrettyPrint()
        !itauAuditableProperties.isTypeSafeValues()
        itauAuditableProperties.packagesToScan == ""
        dialectName == DialectName.H2
        itauAuditableProperties.sqlSchema == null
        itauAuditableProperties.sqlSchemaManagementEnabled
        itauAuditableProperties.commitIdGenerator == "synchronized_sequence"
        itauAuditableProperties.prettyPrintDateFormats.localDateTime == "dd MMM yyyy, HH:mm:ss"
        itauAuditableProperties.prettyPrintDateFormats.zonedDateTime == "dd MMM yyyy, HH:mm:ssZ"
        itauAuditableProperties.prettyPrintDateFormats.localDate == "dd MMM yyyy"
        itauAuditableProperties.prettyPrintDateFormats.localTime == "HH:mm:ss"
        !itauAuditableProperties.sqlGlobalIdCacheDisabled
        itauAuditableProperties.objectAccessHook == "br.com.zup.itau.auditable.hibernate.integration.HibernateUnproxyObjectAccessHook"
        itauAuditableProperties.sqlGlobalIdTableName == null
        itauAuditableProperties.sqlCommitTableName == null
        itauAuditableProperties.sqlSnapshotTableName == null
        itauAuditableProperties.sqlCommitPropertyTableName == null
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
