package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm
import br.com.zup.itau.auditable.core.metamodel.annotation.DiffIgnore
import br.com.zup.itau.auditable.core.metamodel.clazz.EntityDefinition
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import br.com.zup.itau.auditable.core.metamodel.type.ValueType
import spock.lang.Specification

import javax.persistence.Id

class Case798RegisteringEntitiesWithValuesAsId extends Specification {
    
    class Person {
        @DiffIgnore
        @Id
        private Integer id 
        public Passport passport

        public Person(Passport passport) {
            this.passport = passport
        }
    }
    
    class Passport {
        @DiffIgnore
        @Id
        private Integer id 
        private String passportId
        
        Passport(String passportId) {
            this.passportId = passportId
        }

        @Override
        String toString() {
            return "Passport{" +
                    "passportId='" + passportId + '\'' +
                    '}';
        }
    }
    
    class Person2 {
        @DiffIgnore
        @Id
        private Integer id 
        public Passport passport
        
        public Person2(Passport passport) {
            this.passport = passport
        }
    }

    def "should successfully generate ID when registering entities and referred value in mixed order"(){
        given:
        Passport sebastiansPassport = new Passport("ID-398")
        Person sebastian = new Person(sebastiansPassport)

        when:
        ItauAuditableBuilder itauAuditableBuilder = ItauAuditableBuilder.itauAuditable()

        itauAuditableBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)

        // Order changed
        itauAuditableBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        itauAuditableBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        itauAuditableBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        ItauAuditable itauAuditable = itauAuditableBuilder.build()

        println itauAuditable.getTypeMapping(Person.class).prettyPrint()
        println itauAuditable.getTypeMapping(Person2.class).prettyPrint()
        println itauAuditable.getTypeMapping(Passport.class).prettyPrint()

        EntityType entityType = (EntityType) itauAuditable.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)
        
        then:
        itauAuditable.getTypeMapping(Person.class).idProperty.type instanceof ValueType
        itauAuditable.getTypeMapping(Person2.class).idProperty.type instanceof ValueType
        sebastiansId.value().endsWith("Person/ID-398")
    }

    def "should successfully generate ID when registering entities before referred value"(){
        given:
        Passport sebastiansPassport = new Passport("ID-398")
        Person sebastian = new Person(sebastiansPassport)

        when:
        ItauAuditableBuilder itauAuditableBuilder = ItauAuditableBuilder.itauAuditable()

        itauAuditableBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
        itauAuditableBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        itauAuditableBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        itauAuditableBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        ItauAuditable itauAuditable = itauAuditableBuilder.build()

        println itauAuditable.getTypeMapping(Person.class).prettyPrint()
        println itauAuditable.getTypeMapping(Person2.class).prettyPrint()
        println itauAuditable.getTypeMapping(Passport.class).prettyPrint()

        EntityType entityType = (EntityType) itauAuditable.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)

        then:
        itauAuditable.getTypeMapping(Person.class).idProperty.type instanceof ValueType
        itauAuditable.getTypeMapping(Person2.class).idProperty.type instanceof ValueType
        sebastiansId.value().endsWith("Person/ID-398")
    }
}
