package br.com.zup.itau.auditable.core.cases

import groovy.json.JsonSlurper
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.Diff
import br.com.zup.itau.auditable.core.model.DummyParameterEntry
import spock.lang.Specification

import java.time.LocalDate

import static br.com.zup.itau.auditable.core.model.DummyUser.Sex.FEMALE

/**
 * @author bartosz walacik
 */
class ItauAuditableSmartparamIntegrationTest extends Specification{

    def "should serialize parameter entry"() {
        given:
        ItauAuditable javers = ItauAuditableBuilder.javers()
                                     .withTypeSafeValues(true)
                                     .build()

        def entry1 = new DummyParameterEntry(["util": LocalDate.of(2014,01,10)])
        def entry2 = new DummyParameterEntry(["util": LocalDate.of(2014,01,12),
                                              "rate":new BigDecimal(10),
                                              "int" :1,
                                              "String":"str",
                                              "enum":FEMALE])

        when:
        Diff diff = javers.compare(entry1, entry2)
        String jsonText = javers.jsonConverter.toJson(diff)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        def mapChange = json.changes[0]

        mapChange.changeType == "MapChange"
        mapChange.globalId.valueObject == "br.com.zup.itau.auditable.core.model.DummyParameterEntry"
        mapChange.property == "levels"
        mapChange.entryChanges.size() == 5

        with(mapChange.entryChanges.find{it -> it.key == "String"}) {
            entryChangeType == "EntryAdded"
            value == "str"
        }

        with(mapChange.entryChanges.find{it -> it.key == "util"}) {
            entryChangeType == "EntryValueChange"
            leftValue.typeAlias == "LocalDate"
            leftValue.value == "2014-01-10"
            rightValue.value == "2014-01-12"
            rightValue.typeAlias == "LocalDate"
        }

        with(mapChange.entryChanges.find{it -> it.key == "enum"}) {
            entryChangeType == "EntryAdded"
            value.typeAlias == "Sex"
            value.value == FEMALE.name()
        }

        with(mapChange.entryChanges.find{it -> it.key == "int"}) {
            entryChangeType == "EntryAdded"
            key == "int"
            value == 1
        }

        with(mapChange.entryChanges.find{it -> it.key == "rate"}) {
            entryChangeType == "EntryAdded"
            value.typeAlias == "BigDecimal"
            value.value == 10
        }
    }
}
