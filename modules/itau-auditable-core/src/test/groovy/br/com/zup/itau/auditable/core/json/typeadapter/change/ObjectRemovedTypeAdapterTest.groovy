package br.com.zup.itau.auditable.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import br.com.zup.itau.auditable.core.diff.Change
import br.com.zup.itau.auditable.core.diff.changetype.ObjectRemoved
import br.com.zup.itau.auditable.core.json.JsonConverter
import br.com.zup.itau.auditable.core.model.DummyUser
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly
import static br.com.zup.itau.auditable.core.json.builder.ChangeTestBuilder.objectRemoved
import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class ObjectRemovedTypeAdapterTest extends Specification {
    def "should serialize ObjectRemoved"() {
        given:
        JsonConverter jsonConverter = itauAuditableTestAssembly().jsonConverter
        def change = objectRemoved(new DummyUser(name:"kaz"))

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == "ObjectRemoved"
        json.globalId.entity == "br.com.zup.itau.auditable.core.model.DummyUser"
        json.globalId.cdoId == "kaz"
    }

    def "should deserialize ObjectRemoved"() {
        given:
        JsonConverter jsonConverter = itauAuditableTestAssembly().jsonConverter
        def json = new JsonBuilder()
        json {
            changeType "ObjectRemoved"
            globalId {
                entity "br.com.zup.itau.auditable.core.model.DummyUser"
                cdoId  "kaz"
            }
        }

        when:
        def change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        change instanceof ObjectRemoved
        change.affectedGlobalId == instanceId("kaz",DummyUser)
    }
}
