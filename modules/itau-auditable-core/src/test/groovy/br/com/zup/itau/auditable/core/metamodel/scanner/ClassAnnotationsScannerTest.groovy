package br.com.zup.itau.auditable.core.metamodel.scanner

import br.com.zup.itau.auditable.core.metamodel.clazz.*
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz walacik
 */
class ClassAnnotationsScannerTest extends Specification {

    def scanner = new ClassAnnotationsScanner(new AnnotationNamesProvider())

    @Unroll
    def "should map #annotation.name to Entity"() {

        when:
        def result = scanner.scan(classToScan)

        then:
        result.entity

        where:
        annotation << [javax.persistence.Entity,
                       javax.persistence.MappedSuperclass,
                       br.com.zup.itau.auditable.core.metamodel.annotation.Entity]
        classToScan << [JpaEntity, JpaMappedSuperclass, ItauAuditableEntity]
    }

    @Unroll
    def "should map #annotation.name to ValueObject"() {

        when:
        def result = scanner.scan(classToScan)

        then:
        result.valueObject

        where:
        annotation << [javax.persistence.Embeddable,
                       br.com.zup.itau.auditable.core.metamodel.annotation.ValueObject]
        classToScan << [JpaEmbeddable, ItauAuditableValueObject]
    }

    @Unroll
    def "should map #annotation.name to Value"() {

        when:
        def result = scanner.scan(classToScan)

        then:
        result.value

        where:
        annotation << [br.com.zup.itau.auditable.core.metamodel.annotation.Value]
        classToScan << [ItauAuditableValue]
    }

}
