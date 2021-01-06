package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.annotation.ShallowReference
import br.com.zup.itau.auditable.core.metamodel.clazz.EntityDefinitionBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
public class OpBoxCaseClassExtractionError extends Specification {
    interface ParamType {
        String getName()
    }

    class BooleanParamType extends ParamType {
        String name = "name"
        String getName() {
            name
        }
    }

    @ShallowReference
    class ParamPrototype<T extends ParamType> {
        @Id String name
        T type
        Optional<Object> defaultValue
    }

    class DataSource {
        String name
        ParamPrototype<? extends ParamType> paramPrototype
    }

    def "should not fail when spawning ShallowReferenceType from prototype"(){
        given:
        def javers = ItauAuditableBuilder.javers().build();
        def d1 = new DataSource(name: "name", paramPrototype: new ParamPrototype(name:"p1"))
        def d2 = new DataSource(name: "name", paramPrototype: new ParamPrototype(name:"p2"))

        when:
        def diff = javers.compare(d1, d2)

        then:
        diff.changes.size() == 1
    }

    def "should allow defining ShallowReference in ItauAuditableBuilder"(){
        given:
        def javers = ItauAuditableBuilder.javers()
            .registerEntity(EntityDefinitionBuilder.entityDefinition(DataSource).withShallowReference().withIdPropertyName("name").build())
            .build()

        expect:
        javers.getTypeMapping(DataSource).idProperty.name == "name"
        javers.getTypeMapping(DataSource).properties.size() == 0
    }

    def "should allow defining EntityType in ItauAuditableBuilder"(){
        given:
        def javers = ItauAuditableBuilder.javers()
                .registerEntity(EntityDefinitionBuilder.entityDefinition(DataSource).withIdPropertyName("name").build())
                .build()

        expect:
        javers.getTypeMapping(DataSource).idProperty.name == "name"
        javers.getTypeMapping(DataSource).properties.size() == 2
    }
}
