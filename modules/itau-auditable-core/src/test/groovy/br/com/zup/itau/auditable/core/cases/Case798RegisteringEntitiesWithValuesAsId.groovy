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
        ItauAuditableBuilder javersBuilder = ItauAuditableBuilder.javers()

        javersBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)

        // Order changed
        javersBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        javersBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        javersBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        ItauAuditable javers = javersBuilder.build()

        println javers.getTypeMapping(Person.class).prettyPrint()
        println javers.getTypeMapping(Person2.class).prettyPrint()
        println javers.getTypeMapping(Passport.class).prettyPrint()

        EntityType entityType = (EntityType) javers.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)
        
        then:
        javers.getTypeMapping(Person.class).idProperty.type instanceof ValueType
        javers.getTypeMapping(Person2.class).idProperty.type instanceof ValueType
        sebastiansId.value().endsWith("Person/ID-398")
    }

    def "should successfully generate ID when registering entities before referred value"(){
        given:
        Passport sebastiansPassport = new Passport("ID-398")
        Person sebastian = new Person(sebastiansPassport)

        when:
        ItauAuditableBuilder javersBuilder = ItauAuditableBuilder.javers()

        javersBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
        javersBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        javersBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        javersBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        ItauAuditable javers = javersBuilder.build()

        println javers.getTypeMapping(Person.class).prettyPrint()
        println javers.getTypeMapping(Person2.class).prettyPrint()
        println javers.getTypeMapping(Passport.class).prettyPrint()

        EntityType entityType = (EntityType) javers.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)

        then:
        javers.getTypeMapping(Person.class).idProperty.type instanceof ValueType
        javers.getTypeMapping(Person2.class).idProperty.type instanceof ValueType
        sebastiansId.value().endsWith("Person/ID-398")
    }
}
