package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.Diff
import spock.lang.Specification

class CaseDeserializeDiffFromJson extends Specification {

    def "should deserialize from json"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
        def diff = itauAuditable.compare(new Item(1234), new Item(5678))
        def json = itauAuditable.getJsonConverter().toJson(diff)

        when:
        def itauAuditableDiff = itauAuditable.getJsonConverter().fromJson(json, Diff)

        then:
        println itauAuditableDiff.toString()
        itauAuditableDiff.toString() == diff.toString()
    }

    class Item {
        Integer id

        Item(Integer id) {
            this.id = id
        }
    }
}
