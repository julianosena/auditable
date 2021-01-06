package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/76
 *
 * To resolve this issue we need to support types like List<List<String>>
 *
 * @author bartosz walacik
 */
class NestedListsTest extends Specification{

    def "should support lists with nested item type"() {
        given:
        def javers = ItauAuditableBuilder.javers().build()
        def cdo = new EntityWithNestedList(id:1,listWithGenericItem: [Optional.of("a")])

        when:
        javers.commit("me@here.com", cdo)
        cdo.setListWithGenericItem([Optional.empty()])

        javers.commit("me@here.com", cdo)

        then:
        def changes = javers.findChanges(QueryBuilder.byInstanceId(1, EntityWithNestedList).build())
        ListChange change = changes[0]
        with(change.changes[0]) {
            index == 0
            leftValue.get() == "a"
            !rightValue.present
        }
    }

    def "should support nested lists"() {
        given:
        def javers = ItauAuditableBuilder.javers().build()
        def cdo = new EntityWithNestedList(id:1,nestedList: [["A", "B", "C"], ["D", ".", "F"]])

        when:
        javers.commit("me@here.com", cdo)
        cdo.setNestedList([["A", "B", "C"], ["D", "E", "F"]])

        javers.commit("me@here.com", cdo)

        then:
        def changes = javers.findChanges(QueryBuilder.byInstanceId(1, EntityWithNestedList.class).build())

        ListChange change = changes[0]
        with(change.changes[0]){
            index == 1
            leftValue == ["D", ".", "F"]
            rightValue == ["D", "E", "F"]
        }
    }
}
