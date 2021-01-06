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
    ItauAuditableSqlProperties itauAuditableProperties

    @Autowired
    AuthorProvider provider

    def "should read configuration from yml" () {
        expect:
        itauAuditableProperties.getAlgorithm() == "levenshtein_distance"
        itauAuditableProperties.getMappingStyle() == "bean"
        !itauAuditableProperties.isNewObjectSnapshot()
        !itauAuditableProperties.isPrettyPrint()
        itauAuditableProperties.isTypeSafeValues()
        dialectName == DialectName.H2
        itauAuditableProperties.sqlSchema == "test"
        itauAuditableProperties.sqlSchemaManagementEnabled
        itauAuditableProperties.getCommitIdGenerator() == "random"
        itauAuditableProperties.packagesToScan == "my.company.domain.person, my.company.domain.finance"
        itauAuditableProperties.prettyPrintDateFormats.localDateTime == "dd-mm-yyyy"
        itauAuditableProperties.prettyPrintDateFormats.zonedDateTime == "dd-mm-yyyy HH mm ss Z"
        itauAuditableProperties.prettyPrintDateFormats.localDate == "dd-mm-yyyy"
        itauAuditableProperties.prettyPrintDateFormats.localTime == "HH mm ss"
        itauAuditableProperties.sqlGlobalIdCacheDisabled
        itauAuditableProperties.objectAccessHook == "br.com.zup.itau.auditable.spring.boot.DummySqlObjectAccessHook"
        itauAuditableProperties.sqlGlobalIdTableName == "cust_jv_global_id"
        itauAuditableProperties.sqlCommitTableName == "cust_jv_commit"
        itauAuditableProperties.sqlSnapshotTableName == "cust_jv_snapshot"
        itauAuditableProperties.sqlCommitPropertyTableName == "cust_jv_commit_property"
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath" () {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
