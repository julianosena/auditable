package br.com.zup.itau.auditable.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.diff.Change
import br.com.zup.itau.auditable.core.diff.appenders.MapChangeAssert
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryAdded
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryRemoved
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryValueChange
import br.com.zup.itau.auditable.core.diff.changetype.map.MapChange
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import java.time.LocalDate
import java.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly
import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssemblyTypeSafe
import static br.com.zup.itau.auditable.core.json.builder.ChangeTestBuilder.mapChange
import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class MapChangeTypeAdapterTest extends Specification {

    @Shared
    def jsonConverter = ItauAuditableTestBuilder.itauAuditableTestAssembly().jsonConverter

    def "should serialize polymorfic MapChange type-safely when switched on" () {
        when:
        def jsonConverterTypeSafe = itauAuditableTestAssemblyTypeSafe().jsonConverter
        def entryChanges = [new EntryAdded("some",new LocalDate(2001,1,1)),
                            new EntryRemoved("some",new LocalDate(2002,1,1)),
                            new EntryValueChange(new LocalDate(2003,1,1), new LocalDate(2004,1,1), new LocalDate(2005,1,1))]

        MapChange change = mapChange(new SnapshotEntity(id:1),"polymorficMap",entryChanges)


        String jsonText = jsonConverterTypeSafe.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.entryChanges.size() == 3
        with(json.entryChanges[0]){
            entryChangeType == "EntryAdded"
            key == "some"
            value.typeAlias == "LocalDate"
            value.value == "2001-01-01"
        }
        with(json.entryChanges[1]){
            entryChangeType == "EntryRemoved"
            key == "some"
            value.typeAlias == "LocalDate"
            value.value == "2002-01-01"
        }
        with(json.entryChanges[2]){
            entryChangeType == "EntryValueChange"
            key.typeAlias  == "LocalDate"
            key.value      == "2003-01-01"
            leftValue.typeAlias == "LocalDate"
            leftValue.value     == "2004-01-01"
            rightValue.typeAlias == "LocalDate"
            rightValue.value     == "2005-01-01"
        }
    }

    def "should serialize MapChange.EntryValueChange with Values using custom TypeAdapter" () {
        given:
        def entryChanges = [new EntryValueChange(new LocalDate(2001,1,1), 1.12,1.13)]
        def change = mapChange(new SnapshotEntity(id:1),"mapOfValues",entryChanges)

        when:
        def jsonText = jsonConverter.toJson(change)

        then:
            def json = new JsonSlurper().parseText(jsonText)
            with(json.entryChanges[0]){
                entryChangeType == "EntryValueChange"
                key == "2001-01-01"
                leftValue == 1.12
                rightValue == 1.13
            }

    }

    def "should deserialize MapChange.EntryValueChange with Values using custom TypeAdapter" () {
        given:
            def json = new JsonBuilder()
            json{
                        changeType "MapChange"
                        globalId {
                            entity "br.com.zup.itau.auditable.core.model.SnapshotEntity"
                            cdoId 1
                        }
                        commitMetadata {
                            author "kazik"
                            commitDate "2001-12-01T22:23:03"
                            id "1.0"
                        }
                        property "mapOfValues"
                        entryChanges ([
                                {
                                    entryChangeType "EntryValueChange"
                                    key "2001-01-01"
                                    leftValue 1.12
                                    rightValue 1.13
                                }
                        ])

                }

        when:
        def change  = jsonConverter.fromJson(json.toString(), Change)

        then:
        MapChangeAssert.assertThat(change)
                       .hasEntryValueChange(new LocalDate(2001,1,1), 1.12, 1.13)
    }
    def "should serialize MapChange.EntryValueChange with references" () {
        given:
        def itauAuditableTestBuilder = itauAuditableTestAssembly()
        def itauAuditable = itauAuditableTestBuilder.itauAuditable()
        def jsonConverter = itauAuditable.getJsonConverter()

        def keyId = itauAuditableTestBuilder.instanceId(1,SnapshotEntity)
        def leftReference  = itauAuditableTestBuilder.instanceId(2,SnapshotEntity)
        def rightReference = itauAuditableTestBuilder.instanceId(3,SnapshotEntity)
        def entryChanges = [new EntryValueChange(keyId, leftReference, rightReference)]

        def change = mapChange(new SnapshotEntity(id:1),"mapOfEntities",entryChanges)

        when:
        def jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        with(json.entryChanges[0]){
            entryChangeType == "EntryValueChange"
            key.entity  == "br.com.zup.itau.auditable.core.model.SnapshotEntity"
            key.cdoId   == 1
            leftValue.entity == "br.com.zup.itau.auditable.core.model.SnapshotEntity"
            leftValue.cdoId  == 2
            rightValue.entity == "br.com.zup.itau.auditable.core.model.SnapshotEntity"
            rightValue.cdoId  == 3
        }
    }

    def "should deserialize MapChange.EntryValueChange with references" () {
        given:
            def json = new JsonBuilder()
            json  {
                        changeType "MapChange"
                        globalId {
                            entity "br.com.zup.itau.auditable.core.model.SnapshotEntity"
                            cdoId 1
                        }
                        commitMetadata {
                            author "kazik"
                            commitDate "2001-12-01T22:23:03"
                            id "1.0"
                        }
                        property "mapOfEntities"
                        entryChanges ([
                                {
                                    entryChangeType "EntryValueChange"
                                    key{
                                        entity "br.com.zup.itau.auditable.core.model.SnapshotEntity"
                                        cdoId 2
                                    }
                                    leftValue {
                                        entity "br.com.zup.itau.auditable.core.model.SnapshotEntity"
                                        cdoId 3
                                    }
                                    rightValue {
                                        entity "br.com.zup.itau.auditable.core.model.SnapshotEntity"
                                        cdoId 4
                                    }
                                }
                        ])

                    }

        when:
        def change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        MapChangeAssert.assertThat(change)
                       .hasEntryValueChange(instanceId(2,SnapshotEntity),
                                            instanceId(3,SnapshotEntity),
                                            instanceId(4,SnapshotEntity))
    }

    def "should deserialize MapChange with primitives" () {
        given:
        def json = new JsonBuilder()
        json
                {
                    changeType "MapChange"
                    globalId {
                        entity "br.com.zup.itau.auditable.core.model.SnapshotEntity"
                        cdoId 1
                    }
                    commitMetadata {
                        author "kazik"
                        commitDate "2001-12-01T22:23:03"
                        id "1.0"
                    }
                    property "mapOfPrimitives"
                    entryChanges ([
                            {
                                entryChangeType "EntryAdded"
                                key "some1"
                                value 1
                            },
                            {
                                entryChangeType "EntryRemoved"
                                key "some2"
                                value 2
                            },
                            {
                                entryChangeType "EntryValueChange"
                                key "some3"
                                leftValue 3
                                rightValue 4
                            }
                    ])

                }

        when:
        def change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        change.affectedGlobalId == instanceId(1,SnapshotEntity)
        change.commitMetadata.get().author == "kazik"
        change.commitMetadata.get().id.majorId == 1
        change.commitMetadata.get().commitDate == LocalDateTime.of(2001,12,01,22,23,03)
        change.propertyName == "mapOfPrimitives"
        MapChangeAssert.assertThat(change)
                       .hasEntryAdded("some1",1)
                       .hasEntryRemoved("some2",2)
                       .hasEntryValueChange("some3",3,4)
    }

    def "should serialize MapChange with primitives" () {
        given:
        def entryChanges = [new EntryAdded("some",1),
                            new EntryRemoved("some",2),
                            new EntryValueChange("mod",3,4)]

        def change = mapChange(DummyUser.dummyUser("kaz"),"valueMap",entryChanges)

        when:
        def jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "valueMap"
        json.changeType == "MapChange"
        json.globalId
        json.entryChanges.size() == 3
        with(json.entryChanges[0]){
            entryChangeType == "EntryAdded"
            key == "some"
            value == 1
        }
        with(json.entryChanges[1]){
            entryChangeType == "EntryRemoved"
            key == "some"
            value == 2
        }
        with(json.entryChanges[2]){
            entryChangeType == "EntryValueChange"
            key == "mod"
            leftValue == 3
            rightValue == 4
        }
    }
}
