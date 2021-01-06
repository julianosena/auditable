package br.com.zup.itau.auditable.core.examples

import br.com.zup.itau.auditable.core.Changes
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.examples.model.Person
import br.com.zup.itau.auditable.core.examples.model.Position
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot
import br.com.zup.itau.auditable.repository.jql.JqlQuery
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.shadow.Shadow
import spock.lang.Specification

class BasicCommitAndQueryExample extends Specification {

    def "should commit and query from ItauAuditableRepository"() {
        given:
        // prepare Itaú Auditable instance. By default, Itaú Auditable uses InMemoryRepository,
        // it's useful for testing
        ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        Person robert = new Person("bob", "Robert Martin")
        itauAuditable.commit("user", robert)           // persist initial commit

        robert.setName("Robert C.")             // do some changes
        robert.setPosition(Position.Developer)
        itauAuditable.commit("user", robert)           // and persist another commit

        JqlQuery query = QueryBuilder.byInstanceId("bob", Person.class).build()

        when:
        println "Shadows query:"

        List<Shadow<Person>> shadows = itauAuditable.findShadows(query)

        shadows.forEach { println it.get() }

        then: "there should be two Bob's Shadows"
        assert shadows.size == 2

        when:
        println "Snapshots query:"

        List<CdoSnapshot> snapshots = itauAuditable.findSnapshots(query)

        snapshots.forEach { println it }

        then: "there should be two Bob's Shadows"
        assert snapshots.size == 2

        when:
        println "Changes query:"

        Changes changes = itauAuditable.findChanges(query)
        // or the old approach:
        // List<Change> changes = itauAuditable.findChanges(query)

        println changes.prettyPrint()

        then: "there should be two Changes on Bob"
        assert changes.size() == 2
    }
}

