package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * https://github.com/itauAuditable/itauAuditable/issues/76
 *
 * To resolve this issue we need to support types like List<List<String>>
 *
 * @author bartosz walacik
 */
class NestedListsTest extends Specification{

    def "should support lists with nested item type"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
        def cdo = new EntityWithNestedList(id:1,listWithGenericItem: [Optional.of("a")])

        when:
        itauAuditable.commit("me@here.com", cdo)
        cdo.setListWithGenericItem([Optional.empty()])

        itauAuditable.commit("me@here.com", cdo)

        then:
        def changes = itauAuditable.findChanges(QueryBuilder.byInstanceId(1, EntityWithNestedList).build())
        ListChange change = changes[0]
        with(change.changes[0]) {
            index == 0
            leftValue.get() == "a"
            !rightValue.present
        }
    }

    def "should support nested lists"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
        def cdo = new EntityWithNestedList(id:1,nestedList: [["A", "B", "C"], ["D", ".", "F"]])

        when:
        itauAuditable.commit("me@here.com", cdo)
        cdo.setNestedList([["A", "B", "C"], ["D", "E", "F"]])

        itauAuditable.commit("me@here.com", cdo)

        then:
        def changes = itauAuditable.findChanges(QueryBuilder.byInstanceId(1, EntityWithNestedList.class).build())

        ListChange change = changes[0]
        with(change.changes[0]){
            index == 1
            leftValue == ["D", ".", "F"]
            rightValue == ["D", "E", "F"]
        }
    }
}
