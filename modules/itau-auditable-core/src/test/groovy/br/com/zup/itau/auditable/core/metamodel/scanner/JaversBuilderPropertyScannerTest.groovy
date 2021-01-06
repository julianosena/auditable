package br.com.zup.itau.auditable.core.metamodel.scanner

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.javers

/**
 * @author bartosz.walacik
 */
class ItauAuditableBuilderPropertyScannerTest extends Specification{
    def "should load default properties file"() {
        given:
        ItauAuditableBuilder javersBuilder = javers()

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    def "should contain FieldBasedPropertyScanner when Field style"() {
        given:
        ItauAuditableBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    def "should contain BeanBasedPropertyScanner when Bean style"() {
        given:
        ItauAuditableBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof BeanBasedPropertyScanner
    }
}
