package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.Diff
import spock.lang.Specification

class CaseDeserializeDiffFromJson extends Specification {

    def "should deserialize from json"() {
        given:
        def javers = ItauAuditableBuilder.javers().build()
        def diff = javers.compare(new Item(1234), new Item(5678))
        def json = javers.getJsonConverter().toJson(diff)

        when:
        def javersDiff = javers.getJsonConverter().fromJson(json, Diff)

        then:
        println javersDiff.toString()
        javersDiff.toString() == diff.toString()
    }

    class Item {
        Integer id

        Item(Integer id) {
            this.id = id
        }
    }
}
