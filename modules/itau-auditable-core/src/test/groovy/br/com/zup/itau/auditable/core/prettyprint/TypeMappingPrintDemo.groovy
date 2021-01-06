package br.com.zup.itau.auditable.core.prettyprint

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.examples.model.Person
import br.com.zup.itau.auditable.core.metamodel.property.Property
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType
import br.com.zup.itau.auditable.core.model.DummyUserDetails
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class TypeMappingPrintDemo extends Specification {

    def "should pretty print JaVers types"() {
        expect:
        def javers = ItauAuditableBuilder.javers().build()

        def t = javers.getTypeMapping(DummyUserDetails)

        println "toString: "+  t.toString()
        println "pretty: " + t.prettyPrint()

        true
    }

    //Java style is deliberated
    def "should allow basic Reflective operations"() {
        expect:
        ItauAuditable javers = ItauAuditableBuilder.javers().build();
        ManagedType jType = javers.getTypeMapping(Person.class);
        Person person = new Person("bob", "Uncle Bob");

        System.out.println("Bob's properties:");
        for (Property p : jType.getProperties()){
            Object value = p.get(person);
            System.out.println( "property:"+ p.getName() +", value:"+value );
        }

        true
    }
}
