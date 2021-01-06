package br.com.zup.itau.auditable.core.examples

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class RefactoringExample extends Specification {

    @TypeName("Person")
    class Person {
        @Id
        int id

        String name

        Address address
    }

    @TypeName("Person")
    class PersonRefactored {
        @Id
        int id

        String name

        String city
    }

    def '''should allow Entity class name change
           when both old and new class use @TypeName annotation'''()
    {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
        itauAuditable.commit('author', new Person(id:1, name:'Bob'))

        when: '''Refactoring happens here, Person.class is removed,
                 new PersonRefactored.class appears'''
        itauAuditable.commit('author', new PersonRefactored(id:1, name:'Uncle Bob', city:'London'))

        def changes =
            itauAuditable.findChanges( QueryBuilder.byInstanceId(1, PersonRefactored.class).build() )

        then: 'two ValueChanges are expected'
        assert changes.size() == 2

        with(changes.find{it.propertyName == "name"}){
            assert left == 'Bob'
            assert right == 'Uncle Bob'
        }

        with(changes.find{it.propertyName == "city"}){
            assert left == null
            assert right == 'London'
        }

        changes.each { assert it.affectedGlobalId.value() == 'Person/1' }

        println changes.prettyPrint()
    }

    @TypeName("br.com.zup.itau.auditable.core.examples.PersonSimple")
    class PersonRetrofitted {
        @Id
        int id

        String name
    }

    def '''should allow Entity class name change
           when old class forgot to use @TypeName annotation'''()
    {
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
      itauAuditable.commit('author', new PersonSimple(id:1, name:'Bob'))

      when:
      itauAuditable.commit('author', new PersonRetrofitted(id:1, name:'Uncle Bob'))

      def changes =
          itauAuditable.findChanges( QueryBuilder.byInstanceId(1,PersonRetrofitted.class).build() )

      then: 'one ValueChange is expected'
      assert changes.size() == 1
      with(changes[0]){
          assert left == 'Bob'
          assert right == 'Uncle Bob'
          assert affectedGlobalId.value() == 'br.com.zup.itau.auditable.core.examples.PersonSimple/1'
      }
      println changes[0]
    }

    abstract class Address {
        boolean verified

        Address(boolean verified) {
            this.verified = verified
        }
    }

    class EmailAddress extends Address {
        String email

        EmailAddress(String email, boolean verified) {
            super(verified)
            this.email = email
        }
    }

    class HomeAddress extends Address {
        String city
        String street

        HomeAddress(String city, String street, boolean verified) {
            super(verified)
            this.city = city
            this.street = street
        }
    }

    def 'should be very relaxed about ValueObject types'(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
      itauAuditable.commit('author', new Person(id:1, address:new EmailAddress('me@example.com', false)))
      itauAuditable.commit('author', new Person(id:1, address:new HomeAddress ('London','Green 50', true)))
      itauAuditable.commit('author', new Person(id:1, address:new HomeAddress ('London','Green 55', true)))

      when:
      def changes =
          itauAuditable.findChanges( QueryBuilder.byValueObjectId(1, Person.class, 'address').build() )

      changes.each { println it }

      then: 'four ValueChanges are expected'
      assert changes.size() == 5
      assert changes.collect{ it.propertyName } as Set == ['street','verified','city', 'email'] as Set
    }
}
