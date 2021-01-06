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
    ItauAuditableSqlProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "should provide default props"() {
        expect:
        javersProperties.getAlgorithm() == "simple"
        javersProperties.getMappingStyle() == "field"
        !javersProperties.isNewObjectSnapshot()
        javersProperties.isPrettyPrint()
        !javersProperties.isTypeSafeValues()
        javersProperties.packagesToScan == ""
        dialectName == DialectName.H2
        javersProperties.sqlSchema == null
        javersProperties.sqlSchemaManagementEnabled
        javersProperties.commitIdGenerator == "synchronized_sequence"
        javersProperties.prettyPrintDateFormats.localDateTime == "dd MMM yyyy, HH:mm:ss"
        javersProperties.prettyPrintDateFormats.zonedDateTime == "dd MMM yyyy, HH:mm:ssZ"
        javersProperties.prettyPrintDateFormats.localDate == "dd MMM yyyy"
        javersProperties.prettyPrintDateFormats.localTime == "HH:mm:ss"
        !javersProperties.sqlGlobalIdCacheDisabled
        javersProperties.objectAccessHook == "br.com.zup.itau.auditable.hibernate.integration.HibernateUnproxyObjectAccessHook"
        javersProperties.sqlGlobalIdTableName == null
        javersProperties.sqlCommitTableName == null
        javersProperties.sqlSnapshotTableName == null
        javersProperties.sqlCommitPropertyTableName == null
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
