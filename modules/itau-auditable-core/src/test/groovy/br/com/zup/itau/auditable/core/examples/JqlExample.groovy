package br.com.zup.itau.auditable.core.examples

import br.com.zup.itau.auditable.core.FakeDateProvider
import br.com.zup.itau.auditable.core.Changes
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.commit.CommitId
import br.com.zup.itau.auditable.core.diff.changetype.NewObject
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange
import br.com.zup.itau.auditable.core.examples.model.Address
import br.com.zup.itau.auditable.core.examples.model.Employee
import br.com.zup.itau.auditable.core.examples.model.Person
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.model.DummyAddress
import br.com.zup.itau.auditable.core.model.DummyUserDetails
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author bartosz.walacik
 */
class JqlExample extends Specification {

    class Entity {
        @Id int id
        Entity ref
    }

    def "should query for Shadows with different scopes, lightweight example, multiple commits"(){
      given: 'In this scenario, our 4 entities are committed in 3 commits'
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        // E1 -> E2 -> E3 -> E4
        def e4 = new Entity(id:4)
        def e3 = new Entity(id:3, ref:e4)
        def e2 = new Entity(id:2, ref:e3)
        def e1 = new Entity(id:1, ref:e2)

        itauAuditable.commit("author", e4) // commit 1.0 with e4 snapshot
        itauAuditable.commit("author", e3) // commit 2.0 with e3 snapshot
        itauAuditable.commit("author", e1) // commit 3.0 with snapshots of e1 and e2

      when: 'shallow scope query'
        def shadows = itauAuditable.findShadows(QueryBuilder.byInstanceId(1, Entity).build())
        def shadowE1 = shadows.get(0).get()

      then: 'only e1 is loaded'
        shadowE1 instanceof Entity
        shadowE1.id == 1
        shadowE1.ref == null

      when: 'commit-deep scope query'
        shadows = itauAuditable.findShadows(QueryBuilder.byInstanceId(1, Entity)
                        .withScopeCommitDeep().build())
        shadowE1 = shadows.get(0).get()

      then: 'only e1 and e2 are loaded, both was committed in commit 3.0'
        shadowE1.id == 1
        shadowE1.ref.id == 2
        shadowE1.ref.ref == null

      when: 'deep+1 scope query'
        shadows = itauAuditable.findShadows(QueryBuilder.byInstanceId(1, Entity)
                        .withScopeDeepPlus(1).build())
        shadowE1 = shadows.get(0).get()

      then: 'only e1 and e2 are loaded'
        shadowE1.id == 1
        shadowE1.ref.id == 2
        shadowE1.ref.ref == null

      when: 'deep+3 scope query'
        shadows = itauAuditable.findShadows(QueryBuilder.byInstanceId(1, Entity)
                        .withScopeDeepPlus(3).build())
        shadowE1 = shadows.get(0).get()

      then: 'all object are loaded'
        shadowE1.id == 1
        shadowE1.ref.id == 2
        shadowE1.ref.ref.id == 3
        shadowE1.ref.ref.ref.id == 4
    }

    def "should query for Shadows with different scopes, lightweight example, single commit"(){
        given: 'In this scenario, all entities are committed in the first commit'
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        // E1 -> E2 -> E3 -> E4
        def e4 = new Entity(id:4)
        def e3 = new Entity(id:3, ref:e4)
        def e2 = new Entity(id:2, ref:e3)
        def e1 = new Entity(id:1, ref:e2)

        itauAuditable.commit("author", e1) // commit 1.0 with snapshots of e1, e2, e3 and e4

        when: 'shallow scope query'
        def shadows = itauAuditable.findShadows(QueryBuilder.byInstanceId(1, Entity)
                .build())
        def shadowE1 = shadows.get(0).get()

        then: 'only e1 is loaded'
        shadowE1 instanceof Entity
        shadowE1.id == 1
        shadowE1.ref == null

        when: 'commit-deep scope query'
        shadows = itauAuditable.findShadows(QueryBuilder.byInstanceId(1, Entity)
                .withScopeCommitDeep().build())
        shadowE1 = shadows.get(0).get()

        then: 'all object are loaded'
        shadowE1.id == 1
        shadowE1.ref.id == 2
        shadowE1.ref.ref.id == 3
        shadowE1.ref.ref.ref.id == 4
    }

    def "should query for Changes made on any object"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
        def bob = new Employee(name: "bob",
                               salary: 1000,
                               primaryAddress: new Address("London"))
        itauAuditable.commit("author", bob)       // initial commit

        bob.salary = 1200                  // changes
        bob.primaryAddress.city = "Paris"  //
        itauAuditable.commit("author", bob)       // second commit

        when:
        Changes changes = itauAuditable.findChanges( QueryBuilder.anyDomainObject().build() )

        then:
        assert changes.size() == 2
        ValueChange salaryChange = changes.find{it.propertyName == "salary"}
        ValueChange cityChange = changes.find{it.propertyName == "city"}
        assert salaryChange.left ==  1000
        assert salaryChange.right == 1200
        assert cityChange.left ==  "London"
        assert cityChange.right == "Paris"

        println changes.prettyPrint()
    }

    def "should query for Shadows of an object"() {
      given:
          def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
          def bob = new Employee(name: "bob",
                                 salary: 1000,
                                 primaryAddress: new Address("London"))
          itauAuditable.commit("author", bob)       // initial commit

          bob.salary = 1200                  // changes
          bob.primaryAddress.city = "Paris"  //
          itauAuditable.commit("author", bob)       // second commit

      when:
          def shadows = itauAuditable.findShadows(QueryBuilder.byInstance(bob).build())

      then:
          assert shadows.size() == 2

          Employee bobNew = shadows[0].get()     // Employee shadows are instances
          Employee bobOld = shadows[1].get()     // of Employee.class

          bobNew.salary == 1200
          bobOld.salary == 1000
          bobNew.primaryAddress.city == "Paris"  // Employee shadows are linked
          bobOld.primaryAddress.city == "London" // to Address Shadows

          shadows[0].commitMetadata.id.majorId == 2
          shadows[1].commitMetadata.id.majorId == 1
    }

    def "should query for Shadows with different scopes"(){
      given:
          def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

          //    /-> John -> Steve
          // Bob
          //    \-> #address
          def steve = new Employee(name: 'steve')
          def john = new Employee(name: 'john', boss: steve)
          def bob  = new Employee(name: 'bob', boss: john, primaryAddress: new Address('London'))

          itauAuditable.commit('author', steve)  // commit 1.0 with snapshot of Steve
          itauAuditable.commit('author', bob)    // commit 2.0 with snapshots of Bob, Bob#address and John
          bob.salary = 1200               // the change
          itauAuditable.commit('author', bob)    // commit 3.0 with snapshot of Bob

      when: 'shallow scope query'
          def shadows = itauAuditable.findShadows(QueryBuilder.byInstance(bob).build())
          Employee bobShadow = shadows[0].get()  //get the latest version of Bob

      then:
          assert shadows.size() == 2             //we have 2 shadows of Bob
          assert bobShadow.name == 'bob'
          // referenced entities are outside the query scope so they are nulled
          assert bobShadow.boss == null
          // child Value Objects are always in scope
          assert bobShadow.primaryAddress.city == 'London'

      when: 'commit-deep scope query'
          shadows = itauAuditable.findShadows(QueryBuilder.byInstance(bob)
                          .withScopeCommitDeep().build())
          bobShadow = shadows[0].get()
      then:
          assert bobShadow.boss.name == 'john' // John is inside the query scope, so his
                                               // shadow is loaded and linked to Bob
          assert bobShadow.boss.boss == null   // Steve is still outside the scope
          assert bobShadow.primaryAddress.city == 'London'

      when: 'deep+2 scope query'
          shadows = itauAuditable.findShadows(QueryBuilder.byInstance(bob)
                          .withScopeDeepPlus(2).build())
          bobShadow = shadows[0].get()

      then: 'all objects are loaded'
          assert bobShadow.boss.name == 'john'
          assert bobShadow.boss.boss.name == 'steve' // Steve is loaded thanks to deep+2 scope
          assert bobShadow.primaryAddress.city == 'London'
    }

    def "should query for Snapshots of an object"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
        def bob = new Employee(name: "bob",
                               salary: 1000,
                               age: 29,
                               boss: new Employee("john"))
        itauAuditable.commit("author", bob)       // initial commit

        bob.salary = 1200                  // changes
        bob.age = 30                       //
        itauAuditable.commit("author", bob)       // second commit

        when:
        def snapshots = itauAuditable.findSnapshots( QueryBuilder.byInstance(bob).build() )

        then:
        assert snapshots.size() == 2

        assert snapshots[0].commitMetadata.id.majorId == 2
        assert snapshots[0].changed == ["salary", "age"]
        assert snapshots[0].getPropertyValue("salary") == 1200
        assert snapshots[0].getPropertyValue("age") == 30
        // references are dehydrated
        assert snapshots[0].getPropertyValue("boss").value() == "Employee/john"

        assert snapshots[1].commitMetadata.id.majorId == 1
        assert snapshots[1].getPropertyValue("salary") == 1000
        assert snapshots[1].getPropertyValue("age") == 29
        assert snapshots[1].getPropertyValue("boss").value() == "Employee/john"
    }


    def "should query for Entity changes by instance Id"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("author", new Employee(name:"bob", age:30, salary:1000) )
        itauAuditable.commit("author", new Employee(name:"bob", age:31, salary:1200) )
        itauAuditable.commit("author", new Employee(name:"john",age:25) )

        when:
        Changes changes = itauAuditable.findChanges( QueryBuilder.byInstanceId("bob", Employee.class).build() )

        then:
        println changes.prettyPrint()
        assert changes.size() == 2
    }

    def "should query for ValueObject changes by owning Entity instance and class"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("author", new Employee(name:"bob",  postalAddress:  new Address(city:"Paris")))
        itauAuditable.commit("author", new Employee(name:"bob",  primaryAddress: new Address(city:"London")))
        itauAuditable.commit("author", new Employee(name:"bob",  primaryAddress: new Address(city:"Paris")))
        itauAuditable.commit("author", new Employee(name:"lucy", primaryAddress: new Address(city:"New York")))
        itauAuditable.commit("author", new Employee(name:"lucy", primaryAddress: new Address(city:"Washington")))

        when:
        println "query for ValueObject changes by owning Entity instance Id"
        Changes changes = itauAuditable
            .findChanges( QueryBuilder.byValueObjectId("bob",Employee.class,"primaryAddress").build())

        then:
        println changes.prettyPrint()
        assert changes.size() == 1

        when:
        println "query for ValueObject changes by owning Entity class"
        changes = itauAuditable
            .findChanges( QueryBuilder.byValueObject(Employee.class,"primaryAddress").build())

        then:
        println changes.prettyPrint()
        assert changes.size() == 2
    }

    def "should query for ValueObject changes when stored in a List"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("author",
                new Person(login: "bob", addresses: [new Address(city: "London"), new Address(city: "Luton")]))
        itauAuditable.commit("author",
                new Person(login: "bob", addresses: [new Address(city: "Paris"), new Address(city: "Luton")]))

        when:
        Changes changes = itauAuditable
                .findChanges(QueryBuilder.byValueObjectId("bob",Person.class, "addresses/0").build())

        then:
        println changes.prettyPrint()
        assert changes.size() == 1
    }

    def "should query for ValueObject changes when stored as Map values"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("author", new Person(login: "bob", addressMap: ["HOME":new Address(city: "Paris")]))
        itauAuditable.commit("author", new Person(login: "bob", addressMap: ["HOME":new Address(city: "London")]))

        when:
        Changes changes = itauAuditable
            .findChanges(QueryBuilder.byValueObjectId("bob", Person.class, "addressMap/HOME").build())

        then:
        println changes.prettyPrint()
        assert changes.size() == 1
    }

    def "should query for Object changes by its class"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("me", new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city: "London")))
        itauAuditable.commit("me", new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city: "Paris")))
        itauAuditable.commit("me", new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Rome")))
        itauAuditable.commit("me", new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Palma")))
        itauAuditable.commit("me", new SnapshotEntity(id:2, intProperty:2))

        when:
        Changes changes = itauAuditable.findChanges( QueryBuilder.byClass(DummyAddress.class).build() )

        then:
        println changes.prettyPrint()
        assert changes.size() == 2
    }

    def "should query for any domain object changes"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("author", new Employee(name:"bob", age:30) )
        itauAuditable.commit("author", new Employee(name:"bob", age:31) )
        itauAuditable.commit("author", new DummyUserDetails(id:1, someValue:"old") )
        itauAuditable.commit("author", new DummyUserDetails(id:1, someValue:"new") )

        when:
        Changes changes = itauAuditable.findChanges( QueryBuilder.anyDomainObject().build() )

        then:
        println changes.prettyPrint()
        assert changes.size() == 2
    }

    def "should query for changes (and snapshots) with property filter"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("me", new Employee(name:"bob", age:30, salary:1000) )
        itauAuditable.commit("me", new Employee(name:"bob", age:31, salary:1100) )
        itauAuditable.commit("me", new Employee(name:"bob", age:31, salary:1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class)
                .withChangedProperty("salary").build()
        Changes changes = itauAuditable.findChanges(query)

        then:
        println changes.prettyPrint()
        assert changes.size() == 2
        assert itauAuditable.findSnapshots(query).size() == 3
    }

    def "should query for changes (and snapshots) with properties filter"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit("me", new Employee(name:"bob", age:30, salary:1000) )
        itauAuditable.commit("me", new Employee(name:"bob", age:31, salary:1100) )
        itauAuditable.commit("me", new Employee(name:"bob", age:31, salary:1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class)
                .withChangedPropertyIn("salary", "age").build()
        Changes changes = itauAuditable.findChanges(query)

        then:
        println changes.prettyPrint()
        assert changes.size() == 3
        assert itauAuditable.findSnapshots(query).size() == 3
    }

    def "should query for changes (and snapshots) with limit filter"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit( "me", new Employee(name:"bob", salary: 900) )
        itauAuditable.commit( "me", new Employee(name:"bob", salary: 1000) )
        itauAuditable.commit( "me", new Employee(name:"bob", salary: 1100) )
        itauAuditable.commit( "me", new Employee(name:"bob", salary: 1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).limit(2).build()
        Changes changes = itauAuditable.findChanges(query)

        then:
        println changes.prettyPrint()
        assert changes.size() == 2
        assert itauAuditable.findSnapshots(query).size() == 2
    }

    def "should query for changes (and snapshots) with skip filter"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit( "me", new Employee(name:"bob", age:29, salary: 900) )
        itauAuditable.commit( "me", new Employee(name:"bob", age:30, salary: 1000) )
        itauAuditable.commit( "me", new Employee(name:"bob", age:31, salary: 1100) )
        itauAuditable.commit( "me", new Employee(name:"bob", age:32, salary: 1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).skip(1).build()
        Changes changes = itauAuditable.findChanges( query )

        then:
        println changes.prettyPrint()
        assert changes.size() == 4
        assert itauAuditable.findSnapshots(query).size() == 3
    }

    def "should query for changes (and snapshots) with author filter"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit( "Jim", new Employee(name:"bob", age:29, salary: 900) )
        itauAuditable.commit( "Pam", new Employee(name:"bob", age:30, salary: 1000) )
        itauAuditable.commit( "Jim", new Employee(name:"bob", age:31, salary: 1100) )
        itauAuditable.commit( "Pam", new Employee(name:"bob", age:32, salary: 1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).byAuthor("Pam").build()
        Changes changes = itauAuditable.findChanges( query )

        then:
        println changes.prettyPrint()
        assert changes.size() == 4
        assert itauAuditable.findSnapshots(query).size() == 2
    }

    def "should query for changes (and snapshots) with commit property filters"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        def bob = new Employee(name: "bob", position: "Assistant", salary: 900)
        itauAuditable.commit( "author", bob, ["tenant": "ACME", "event": "birthday"] )
        bob.position = "Specialist"
        bob.salary = 1600
        itauAuditable.commit( "author", bob, ["tenant": "ACME", "event": "promotion"] )

        def pam = new Employee(name: "pam", position: "Secretary", salary: 1300)
        itauAuditable.commit( "author", pam, ["tenant": "Dunder Mifflin", "event": "hire"] )
        bob.position = "Saleswoman"
        bob.salary = 1700
        itauAuditable.commit( "author", pam, ["tenant": "Dunder Mifflin", "event": "promotion"] )

        when:
        def query = QueryBuilder.anyDomainObject()
            .withCommitProperty("tenant", "ACME")
            .withCommitProperty("event", "promotion").build()
        Changes changes = itauAuditable.findChanges( query )

        then:
        println changes.prettyPrint()
        assert changes.size() == 2
        assert itauAuditable.findSnapshots(query).size() == 1
    }

    def "should query for changes (and snapshots) with commitDate filter"(){
      given:
      def fakeDateProvider = new FakeDateProvider()
      def itauAuditable = ItauAuditableBuilder.itauAuditable().withDateTimeProvider(fakeDateProvider).build()

      (0..5).each{ i ->
          def now = ZonedDateTime.of(2015+i,01,1,0,0,0,0, ZoneId.of("UTC"))
          fakeDateProvider.set( now )
          def bob = new Employee(name:"bob", age:20+i)
          itauAuditable.commit("author", bob)
          println "comitting bob on $now"
      }

      when:
      def query = QueryBuilder.byInstanceId("bob", Employee.class)
              .from(new LocalDate(2016,01,1))
              .to  (new LocalDate(2018,01,1)).build()
      Changes changes = itauAuditable.findChanges( query )

      then:
      println changes.prettyPrint()
      assert changes.size() == 3
      assert itauAuditable.findSnapshots(query).size() == 3
    }

    def "should query for changes (and snapshots) with commitId filter"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        (1..3).each {
            itauAuditable.commit("author", new Employee(name:"john", age:20+it))
            itauAuditable.commit("author", new Employee(name:"bob",  age:20+it))
        }

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class )
                .withCommitId( CommitId.valueOf(4) ).build()
        Changes changes = itauAuditable.findChanges(query)

        then:
        println changes.prettyPrint()
        assert changes.size() == 1
        assert changes[0].left == 21
        assert changes[0].right == 22
        assert itauAuditable.findSnapshots(query).size() == 1
    }

    def "should query for changes (and snapshots) with version filter"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        (1..5).each {
            itauAuditable.commit("author", new Employee(name: "john",age: 20+it))
            itauAuditable.commit("author", new Employee(name: "bob", age: 20+it))
        }

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).withVersion(4).build()
        Changes changes = itauAuditable.findChanges( query )

        then:
        println changes.prettyPrint()
        assert changes.size() == 1
        assert changes[0].left == 23
        assert changes[0].right == 24
        assert itauAuditable.findSnapshots(query).size() == 1
    }

    def "should query for changes with NewObject filter"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        itauAuditable.commit( "author", new Employee(name:"bob", age:30, salary: 1000) )
        itauAuditable.commit( "author", new Employee(name:"bob", age:30, salary: 1200) )

        when:
        Changes changes = itauAuditable
            .findChanges( QueryBuilder.byInstanceId("bob", Employee.class)
            .withNewObjectChanges(true).build() )

        then:
        println changes.prettyPrint()
        assert changes.size() == 5
        assert changes[4] instanceof NewObject
    }

    def "should query for changes made on Entity and its ValueObjects by InstanceId and Class"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

      def bob = new Employee(name:"bob", age:30, salary: 1000,
              primaryAddress: new Address(city:"Paris"),
              postalAddress: new Address(city:"Paris"))
      itauAuditable.commit("author", bob)

      bob.age = 31
      bob.primaryAddress.city = "London"
      itauAuditable.commit("author", bob)

      when: "query by instance Id"
      def query = QueryBuilder.byInstanceId("bob", Employee.class).withChildValueObjects().build()
      Changes changes = itauAuditable.findChanges( query )

      then:
      println changes.prettyPrint()
      assert changes.size() == 2

      when: "query by Entity class"
      query = QueryBuilder.byClass(Employee.class).withChildValueObjects().build()
      changes = itauAuditable.findChanges( query )

      then:
      assert changes.size() == 2
    }
}