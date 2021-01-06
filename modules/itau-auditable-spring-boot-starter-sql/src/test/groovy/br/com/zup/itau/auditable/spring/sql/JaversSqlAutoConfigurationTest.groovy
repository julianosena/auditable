package br.com.zup.itau.auditable.spring.sql


import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider
import br.com.zup.itau.auditable.spring.auditable.SpringSecurityAuthorProvider
import br.com.zup.itau.auditable.spring.boot.sql.ItauAuditableSqlProperties
import br.com.zup.itau.auditable.spring.boot.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class ItauAuditableSqlAutoConfigurationTest extends Specification {

    @Autowired
    DialectName dialectName

    @Autowired
    ItauAuditableSqlProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "should read configuration from yml" () {
        expect:
        javersProperties.getAlgorithm() == "levenshtein_distance"
        javersProperties.getMappingStyle() == "bean"
        !javersProperties.isNewObjectSnapshot()
        !javersProperties.isPrettyPrint()
        javersProperties.isTypeSafeValues()
        dialectName == DialectName.H2
        javersProperties.sqlSchema == "test"
        javersProperties.sqlSchemaManagementEnabled
        javersProperties.getCommitIdGenerator() == "random"
        javersProperties.packagesToScan == "my.company.domain.person, my.company.domain.finance"
        javersProperties.prettyPrintDateFormats.localDateTime == "dd-mm-yyyy"
        javersProperties.prettyPrintDateFormats.zonedDateTime == "dd-mm-yyyy HH mm ss Z"
        javersProperties.prettyPrintDateFormats.localDate == "dd-mm-yyyy"
        javersProperties.prettyPrintDateFormats.localTime == "HH mm ss"
        javersProperties.sqlGlobalIdCacheDisabled
        javersProperties.objectAccessHook == "br.com.zup.itau.auditable.spring.boot.DummySqlObjectAccessHook"
        javersProperties.sqlGlobalIdTableName == "cust_jv_global_id"
        javersProperties.sqlCommitTableName == "cust_jv_commit"
        javersProperties.sqlSnapshotTableName == "cust_jv_snapshot"
        javersProperties.sqlCommitPropertyTableName == "cust_jv_commit_property"
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath" () {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
