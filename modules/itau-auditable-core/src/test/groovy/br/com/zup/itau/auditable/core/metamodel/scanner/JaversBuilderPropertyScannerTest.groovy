package br.com.zup.itau.auditable.core.metamodel.scanner

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.itauAuditable

/**
 * @author bartosz.walacik
 */
class ItauAuditableBuilderPropertyScannerTest extends Specification{
    def "should load default properties file"() {
        given:
        ItauAuditableBuilder itauAuditableBuilder = itauAuditable()

        when:
        itauAuditableBuilder.build()

        then:
        itauAuditableBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    def "should contain FieldBasedPropertyScanner when Field style"() {
        given:
        ItauAuditableBuilder itauAuditableBuilder = itauAuditable().withMappingStyle(MappingStyle.FIELD)

        when:
        itauAuditableBuilder.build()

        then:
        itauAuditableBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    def "should contain BeanBasedPropertyScanner when Bean style"() {
        given:
        ItauAuditableBuilder itauAuditableBuilder = itauAuditable().withMappingStyle(MappingStyle.BEAN)

        when:
        itauAuditableBuilder.build()

        then:
        itauAuditableBuilder.getContainerComponent(PropertyScanner) instanceof BeanBasedPropertyScanner
    }
}
