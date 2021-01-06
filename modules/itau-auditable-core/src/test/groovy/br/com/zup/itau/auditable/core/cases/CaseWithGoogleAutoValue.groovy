package br.com.zup.itau.auditable.core.cases

import groovy.transform.PackageScope
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import spock.lang.Specification
import spock.lang.Unroll

/**
 * https://github.com/itauAuditable/itauAuditable/issues/548
 */
class CaseWithGoogleAutoValue extends Specification {

    @PackageScope
    abstract class AbstractEntity {
        @Id
        abstract int getId()

        abstract Number getValue()
    }

    @PackageScope
    class ConcreteEntity extends AbstractEntity {
        int id
        int value

        @Override
        int getId() {
            id
        }

        @Override
        BigDecimal getValue() {
            value
        }
    }

    @Unroll
    def "should map #entity.simpleName with abstract @IdGetter as EntityType"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().withMappingStyle(MappingStyle.BEAN).build()

        when:
        def jType = itauAuditable.getTypeMapping(entity)

        then:
        jType instanceof EntityType
        jType.idProperty.name == "id"
        def properties = jType.getProperties{true}
        properties.size() == 2
        properties.each { assert it.member.declaringClass == entity}

        where:
        entity << [AbstractEntity, ConcreteEntity]
    }

    def "should compare package protected Entity with abstract @IdGetter"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().withMappingStyle(MappingStyle.BEAN).build()

        expect:
        itauAuditable.compare(new ConcreteEntity(id:1, value: 1),
                       new ConcreteEntity(id:1, value: 1))
                .changes.size() == 0
    }
}
