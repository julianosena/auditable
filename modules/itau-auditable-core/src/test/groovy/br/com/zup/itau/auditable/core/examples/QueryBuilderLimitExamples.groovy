package br.com.zup.itau.auditable.core.examples


import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.examples.model.Address
import br.com.zup.itau.auditable.core.examples.model.Employee
import spock.lang.Specification

import java.time.ZonedDateTime

import static br.com.zup.itau.auditable.core.examples.model.Position.Specialist
import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId

class QueryBuilderLimitExamples extends Specification {

    def "snapshot limit with findChanges"() {
        given:
        def javers = ItauAuditableBuilder.javers().build()

        def bob = new Employee("Bob", 9_000, "ScrumMaster")
        javers.commit("me", bob)

        bob.salary += 1_000
        bob.position = Specialist
        bob.age = 21
        bob.lastPromotionDate = ZonedDateTime.now()
        javers.commit("me", bob)

        when:
        def changes = javers.findChanges(byInstanceId("Bob", Employee)
                .limit(2).build())

        print(changes.prettyPrint())

        then:
        changes.size() == 4
    }

    def "snapshot limit with findShadows and findShadowsAndStream"() {
        given:
        def javers = ItauAuditableBuilder.javers().build()

        def bob = new Employee("Bob", 9_000, "ScrumMaster")
        bob.primaryAddress = new Address("London")
        javers.commit("me", bob) // 2 snapshots are persisted

        bob.salary += 1_000
        bob.primaryAddress.city = "New York"
        javers.commit("me", bob) // 3 snapshots are persisted

        when : "findShadows() -- result is incomplete"
        def shadows = javers.findShadows(byInstanceId("Bob", Employee)
                .limit(2).build())

        shadows.each {println(it)}

        then:
        shadows.size() == 1

        when : "findShadows() -- result is complete"
        shadows = javers.findShadows(byInstanceId("Bob", Employee)
                .limit(4).build())

        shadows.each {println(it)}

        then:
        shadows.size() == 2

        when : "findShadowsAndStream() -- result is complete"
        shadows = javers.findShadowsAndStream(byInstanceId("Bob", Employee)
                .limit(2).build())
                .toArray() // casting to array loads the whole stream

        shadows.each {println(it)}

        then:
        shadows.size() == 2
    }
}
