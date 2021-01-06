package br.com.zup.itau.auditable.core.json.typeadapter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import br.com.zup.itau.auditable.core.examples.typeNames.NewEntityWithTypeAlias
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.annotation.ValueObject
import br.com.zup.itau.auditable.core.metamodel.clazz.ItauAuditableEntity
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.core.metamodel.object.UnboundedValueObjectId
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId
import br.com.zup.itau.auditable.core.model.*
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.valueObjectId
import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly
import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class GlobalIdTypeAdapterTest extends Specification {

    def class IdHolder{
        GlobalId id
    }

    class DummyWithEntityId {
        @Id EntityAsId entityAsId
        int value
    }

    class EntityAsId {
        @Id
        int id
        int value
    }


    def "should deserialize InstanceId with nested Entity Id -- legacy format"(){
      given:
      def instanceIdLegacyJson = '''
            {
              "entity": "br.com.zup.itau.auditable.core.json.typeadapter.GlobalIdTypeAdapterTest$DummyWithEntityId",
              "cdoId": {
                "id": 1,
                "value": 5
              }
            }
            '''

      when:
      InstanceId instanceId = itauAuditableTestAssembly().jsonConverter.fromJson(instanceIdLegacyJson, InstanceId)

      then:
      println instanceId.value()
      instanceId.value().endsWith("DummyWithEntityId/1")
    }

    class EntityWithVOId {
        @Id ValueObjectAsId id
        int value
    }

    @ValueObject
    class ValueObjectAsId {
        int id
        int value
    }

    def "should deserialize InstanceId with ValueObject Id -- legacy format"(){
        given:
        def instanceIdLegacyJson = '''
            {
              "entity": "br.com.zup.itau.auditable.core.json.typeadapter.GlobalIdTypeAdapterTest$EntityWithVOId",
              "cdoId": {
                "id": 1,
                "value": 5
              }
            }
            '''

        when:
        InstanceId instanceId = itauAuditableTestAssembly().jsonConverter.fromJson(instanceIdLegacyJson, InstanceId)

        then:
        println instanceId.value()
        instanceId.value().endsWith("EntityWithVOId/1,5")
    }


    @Unroll
    def "should deserialize InstanceId with #type cdoId"() {
        when:
        def idHolder = itauAuditableTestAssembly().jsonConverter.fromJson(givenJson, IdHolder)

        then:
        idHolder.id instanceof InstanceId
        idHolder.id == expectedId

        where:
        type << ["String", "Long"]
        givenJson << [
                '{"id":{"entity":"br.com.zup.itau.auditable.core.model.DummyUser","cdoId":"kaz"}}',
                '{"id":{"entity":"br.com.zup.itau.auditable.core.model.DummyUserDetails","cdoId":1}}'
                ]
        expectedId <<[
                instanceId("kaz", DummyUser),
                instanceId(1L, DummyUserDetails)
        ]
    }

    def "should serialize Instance @EmbeddedId using json fields"(){
        given:
        def itauAuditable = itauAuditableTestAssembly()
        def id = itauAuditable.instanceId(new DummyPoint(2,3),DummyEntityWithEmbeddedId)

        when:
        def jsonText = itauAuditable.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId.x == 2
        json.cdoId.y == 3
    }

    @Unroll
    def "should serialize InstanceId with #what name"() {
        given:
        def itauAuditable = itauAuditableTestAssembly()
        def id = itauAuditable.instanceId("kaz",clazz)

        when:
        def jsonText = itauAuditable.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId == "kaz"
        json.entity == expectedName

        where:
        what <<  ["default", "@TypeName"]
        clazz << [ItauAuditableEntity, NewEntityWithTypeAlias]
        expectedName << [ItauAuditableEntity.name, "myName"]
    }

    def "should serialize UnboundedValueObjectId"() {
        given:
        def itauAuditable = itauAuditableTestAssembly()
        def id = itauAuditable.unboundedValueObjectId(DummyAddress)

        when:
        def jsonText = itauAuditable.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.valueObject == "br.com.zup.itau.auditable.core.model.DummyAddress"
    }

    def "should deserialize UnboundedValueObjectId from JSON"() {
        given:
        def json = '{"id":{"valueObject":"br.com.zup.itau.auditable.core.model.DummyAddress","cdoId":"/"}}'
        def itauAuditable = itauAuditableTestAssembly()

        when:
        def idHolder = itauAuditable.jsonConverter.fromJson(json, IdHolder)

        then:
        idHolder.id instanceof UnboundedValueObjectId
        idHolder.id == itauAuditable.unboundedValueObjectId(DummyAddress)
    }

    def "should deserialize InstanceId with @EmbeddedId to original Type"(){
        given:
        def json =
        '''
        { "entity": "br.com.zup.itau.auditable.core.model.DummyEntityWithEmbeddedId",
          "cdoId": {
            "x": 2,
            "y": 3
          }}
        '''
        def itauAuditable = itauAuditableTestAssembly()

        when:
        def id = itauAuditable.jsonConverter.fromJson(json, GlobalId)

        then:
        id instanceof InstanceId
        id.cdoId instanceof DummyPoint
        id.cdoId.x == 2
        id.cdoId.y == 3
    }

    def "should deserialize InstanceId with @TypeName when EntityType is mapped"(){
        given:
        def json = '{ "entity": "myName", "cdoId": 1}'
        def itauAuditable = itauAuditableTestAssembly()
        itauAuditable.typeMapper.getItauAuditableType(NewEntityWithTypeAlias)

        when:
        def id = itauAuditable.jsonConverter.fromJson(json, GlobalId)

        then:
        id instanceof InstanceId
        id.cdoId instanceof BigDecimal
        id.cdoId == 1
    }

    def "should serialize ValueObjectId"() {
        given:
        def itauAuditable = itauAuditableTestAssembly()
        def id = itauAuditable.valueObjectId(5,DummyUserDetails,"dummyAddress")

        when:
        def jsonText = itauAuditable.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.ownerId.entity == "br.com.zup.itau.auditable.core.model.DummyUserDetails"
        json.ownerId.cdoId ==  5
        json.valueObject == "br.com.zup.itau.auditable.core.model.DummyAddress"
        json.fragment == "dummyAddress"
    }

    def "should deserialize ValueObjectId"() {
        given:
        def json = new JsonBuilder()
        json.id {
            fragment "dummyAddress"
            valueObject "br.com.zup.itau.auditable.core.model.DummyAddress"
            ownerId {
                entity "br.com.zup.itau.auditable.core.model.DummyUserDetails"
                cdoId 5
            }
        }

        when:
        def idHolder = itauAuditableTestAssembly().jsonConverter.fromJson(json.toString(), IdHolder)

        then:
        idHolder.id instanceof ValueObjectId
        idHolder.id == valueObjectId(5,DummyUserDetails,"dummyAddress")
    }
}
