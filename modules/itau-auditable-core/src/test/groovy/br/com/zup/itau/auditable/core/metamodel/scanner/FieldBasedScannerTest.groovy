package br.com.zup.itau.auditable.core.metamodel.scanner

import br.com.zup.itau.auditable.core.model.DummyUser

import static PropertyScanAssert.assertThat

/**
 * @author pawel szymczyk
 */
class FieldBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = new FieldBasedPropertyScanner(new AnnotationNamesProvider())
    }

    def "should ignore transient field"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasntGotProperty("someTransientField")
    }
}
