package br.com.zup.itau.auditable.guava

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.gson.reflect.TypeToken
import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.diff.appenders.AbstractDiffAppendersTest
import br.com.zup.itau.auditable.core.diff.appenders.MapChangeAssert
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryAdded
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryRemoved
import br.com.zup.itau.auditable.core.diff.changetype.map.MapChange
import br.com.zup.itau.auditable.core.model.DummyAddress
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import spock.lang.Unroll

import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static br.com.zup.itau.auditable.guava.MultimapBuilder.create
import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.*

/**
 * @author akrystian
 */
class MultimapChangeAppenderTest extends AbstractDiffAppendersTest {

    MultimapChangeAppender multimapChangeAppender() {
        def itauAuditable = ItauAuditableTestBuilder.itauAuditableTestAssembly()
        new MultimapChangeAppender(itauAuditable.typeMapper)
    }

    def "should append changes on Multimaps of primitives "(){
      given:
      def left = new SnapshotEntity(id:1, multiMapOfPrimitives: create(
              ["a" : ["x", "y"],
               "b" : ["u"],
               "d" : ["1"]]
      ))

      def right = new SnapshotEntity(id:1, multiMapOfPrimitives: create(
              ["a" : ["y", "z", "0"],
               "c" : ["u"],
               "d" : ["2"]]
      ))

      when:
      MapChange change = multimapChangeAppender()
              .calculateChanges(realNodePair(left, right), getProperty(SnapshotEntity, "multiMapOfPrimitives"))

      then:
      change.entryAddedChanges as Set == [new EntryAdded("a","z"),
                                          new EntryAdded("a","0"),
                                          new EntryAdded("c","u"),
                                          new EntryAdded("d","2")] as Set

      change.entryRemovedChanges as Set == [new EntryRemoved("a","x"),
                                            new EntryAdded("b","u"),
                                            new EntryAdded("d","1")] as Set

      change.entryValueChanges == []
    }

    @Unroll
    def "should append EntryAdded and EntryRemoved for #what"() {
        when:
        def addChange = multimapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, property))

        def removeChange = multimapChangeAppender()
                .calculateChanges(realNodePair(rightCdo, leftCdo), getProperty(SnapshotEntity, property))

        then:
        MapChangeAssert.assertThat(addChange).hasSize(1)
                .hasEntryAdded(expectedKey, expectedVal)

        MapChangeAssert.assertThat(removeChange).hasSize(1)
                .hasEntryRemoved(expectedKey, expectedVal)

        where:
        what << ["Multimap<Entity,Entity>", "Multimap<Primitive,Entity>", "Multimap<Primitive,ValueObject>"]
        leftCdo << [new SnapshotEntity(id: 1)] * 3
        rightCdo << [
                new SnapshotEntity(id: 1, multiMapEntityToEntity: create((new SnapshotEntity(id: 2)): [new SnapshotEntity(id: 3)])),
                new SnapshotEntity(id: 1, multiMapPrimitiveToEntity: create(a: [new SnapshotEntity(id: 2)])),
                new SnapshotEntity(id: 1, multimapPrimitiveToValueObject: create(a: [new DummyAddress("London")]))
        ]
        property << ["multiMapEntityToEntity", "multiMapPrimitiveToEntity", "multimapPrimitiveToValueObject"]
        expectedKey << [instanceId(2, SnapshotEntity), "a", "a"]
        expectedVal << [instanceId(3, SnapshotEntity),
                        instanceId(2, SnapshotEntity),
                        valueObjectId(1, SnapshotEntity, "multimapPrimitiveToValueObject/a/fe9f8f0d164b49489301b7eaefc00c13")]
    }

    def "should not support Map of ValueObject to ?"() {
        when:
        multimapChangeAppender().supports(getItauAuditableType(new TypeToken<Multimap<DummyAddress, String>>() {}.getType()))

        then:
        def e = thrown(ItauAuditableException)
        e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
    }

    def "should EntryAdded & EntryRemoved when key is changed"() {
        given:
        def leftCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: create((new SnapshotEntity(id: 10)): [new SnapshotEntity(id: 5)]))
        def rightCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: create((new SnapshotEntity(id: 12)): [new SnapshotEntity(id: 5)]))

        when:
        def change = multimapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "multiMapEntityToEntity"))

        then:
        MapChangeAssert.assertThat(change).hasSize(2)
                .hasEntryRemoved(instanceId(10, SnapshotEntity), instanceId(5, SnapshotEntity))
                .hasEntryAdded(instanceId(12, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should handle different multimap implementation"(){
        given:
        def leftCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: ArrayListMultimap.create(
                create((new SnapshotEntity(id: 10)): [new SnapshotEntity(id: 5)]))
        )
        def rightCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: HashMultimap.create(
                create((new SnapshotEntity(id: 12)): [new SnapshotEntity(id: 5)]))
        )
        when:
        def change = multimapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "multiMapEntityToEntity"))

        then:
        MapChangeAssert.assertThat(change).hasSize(2)
                .hasEntryRemoved(instanceId(10, SnapshotEntity), instanceId(5, SnapshotEntity))
                .hasEntryAdded(instanceId(12, SnapshotEntity), instanceId(5, SnapshotEntity))
    }
}
