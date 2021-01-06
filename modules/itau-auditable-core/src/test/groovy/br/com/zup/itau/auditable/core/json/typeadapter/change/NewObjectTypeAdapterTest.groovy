package br.com.zup.itau.auditable.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import br.com.zup.itau.auditable.core.diff.Change
import br.com.zup.itau.auditable.core.diff.changetype.NewObject
import br.com.zup.itau.auditable.core.json.JsonConverter
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.itauAuditable
import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly
import static br.com.zup.itau.auditable.core.json.builder.ChangeTestBuilder.newObject
import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class NewObjectTypeAdapterTest extends Specification {
    def "should serialize NewObject"() {
        given:
        JsonConverter jsonConverter = itauAuditableTestAssembly().jsonConverter
        def change = newObject(new DummyUser(name:"kaz"))

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == "NewObject"
        json.globalId.entity == "br.com.zup.itau.auditable.core.model.DummyUser"
        json.globalId.cdoId == "kaz"
    }

    def "should deserialize NewObject"() {
        given:
        JsonConverter jsonConverter = itauAuditableTestAssembly().jsonConverter
        def json = new JsonBuilder()
        json {
            changeType "NewObject"
            globalId {
                entity "br.com.zup.itau.auditable.core.model.DummyUser"
                cdoId  "kaz"
            }
        }

        when:
        def change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        change instanceof NewObject
        change.affectedGlobalId == instanceId("kaz",DummyUser)
    }

    def "should serialize Change with CommitMetadata unwrapped from Optional"() {
        given:
        def itauAuditable = itauAuditable().build()
        def dummyUser = new DummyUser(name: "bob")
        itauAuditable.commit("author", dummyUser)
        def changes = itauAuditable
                .findChanges(QueryBuilder.byInstanceId("bob", DummyUser.class)
                .withNewObjectChanges(true).build())
        def change = changes[1]
        when:
        def jsonText = itauAuditable.jsonConverter.toJson(change)

        then:
        change.commitMetadata instanceof Optional
        def json = new JsonSlurper().parseText(jsonText)
        json.commitMetadata.id == 1.00
        json.commitMetadata.author == "author"
        json.changeType == "NewObject"
        json.globalId.entity == "br.com.zup.itau.auditable.core.model.DummyUser"
        json.globalId.cdoId == "bob"

    }
}
