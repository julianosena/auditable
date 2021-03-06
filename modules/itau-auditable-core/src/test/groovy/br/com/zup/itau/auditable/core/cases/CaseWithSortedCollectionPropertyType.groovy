package br.com.zup.itau.auditable.core.cases

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.changetype.container.SetChange
import br.com.zup.itau.auditable.core.diff.changetype.map.MapChange
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * see https://github.com/itauAuditable/itauAuditable/issues/888
 */
class CaseWithSortedCollectionPropertyType extends Specification {

    @Shared
    def itauAuditable = ItauAuditableBuilder
            .itauAuditable()
            .registerValueObject(DummyValueObject)
            .build()

    @Unroll
    def "should detect new element added to Set where leftSet is #leftSet"() {
        given:
        def a = new DummyEntity(id:1, valueObjectSet: leftSet)

        def b = new DummyEntity(id:1, valueObjectSet: [new DummyValueObject("bar")] as Set)

        when:
        def diff = itauAuditable.compare(a, b)

        then:
        diff.getChangesByType(SetChange).size() == 1

        where:
        leftSet << [ [] as Set, null ]
    }

    @Unroll
    def "should detect new element added to SortedSet where leftSet is #leftSet"() {
        given:
        def a = new DummyEntity(id:1, valueObjectSortedSet: leftSet)

        def b = new DummyEntity(id:1, valueObjectSortedSet: new TreeSet() + new DummyValueObject("bar"))

        when:
        def diff = itauAuditable.compare(a, b)

        then:
        diff.getChangesByType(SetChange).size() == 1

        where:
        leftSet << [new TreeSet(), null]
    }

    def "should detect new element added to Map"() {
        given:
        def a = new DummyEntity(id:1, valueObjectMap:[:])

        def b = new DummyEntity(id:1, valueObjectMap:["3": new DummyValueObject("bar")])

        when:
        def diff = itauAuditable.compare(a, b)

        then:
        diff.getChangesByType(MapChange)[0].entryAddedChanges.size() == 1
    }

    def "should detect new element added to SortedMap"() {
        given:
        def a = new DummyEntity(id:1)

        def sortedMap = new TreeMap<>()
        sortedMap.put("3", new DummyValueObject("bar"))
        def b = new DummyEntity(id:1, valueObjectSortedMap:sortedMap)

        when:
        def diff = itauAuditable.compare(a, b)

        then:
        diff.getChangesByType(MapChange)[0].entryAddedChanges.size() == 1
    }
}

class DummyEntity {
    @Id
    int id
    Set<DummyValueObject> valueObjectSet
    SortedSet<DummyValueObject> valueObjectSortedSet
    Map<String, DummyValueObject> valueObjectMap
    SortedMap<String, DummyValueObject> valueObjectSortedMap
}

@ToString
@TupleConstructor
class DummyValueObject implements Comparable<DummyValueObject> {
    final String name

    @Override
    int compareTo(DummyValueObject that) {
        return name <=> that.name
    }
}
