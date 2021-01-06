package br.com.zup.itau.auditable.core.examples

import br.com.zup.itau.auditable.common.collections.Sets
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeMetadata
import br.com.zup.itau.auditable.core.diff.changetype.container.*
import br.com.zup.itau.auditable.core.diff.custom.CustomPropertyComparator
import br.com.zup.itau.auditable.core.metamodel.property.Property
import spock.lang.Specification

class CustomPropertyComparatorExample extends Specification {

    class ValueObject {
        String value
    }

    /**
     * Compares Strings as character sets
     */
    class FunnyStringComparator implements CustomPropertyComparator<String, SetChange> {
        @Override
        Optional<SetChange> compare(String left, String right, PropertyChangeMetadata metadata, Property property) {
            if (equals(left, right)) {
                return Optional.empty()
            }

            Set leftSet = left.toCharArray().toSet()
            Set rightSet = right.toCharArray().toSet()

            List<ContainerElementChange> changes = []
            Sets.difference(leftSet, rightSet).forEach{c -> changes.add(new ValueRemoved(c))}
            Sets.difference(rightSet, leftSet).forEach{c -> changes.add(new ValueAdded(c))}

            return Optional.of(new SetChange(metadata, changes))
        }

        @Override
        boolean equals(String a, String b) {
            a.toCharArray().toSet() == b.toCharArray().toSet()
        }

        @Override
        String toString(String value) {
            return value;
        }
    }

    def "should use FunnyStringComparator to compare String properties"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable()
                .registerCustomType(String, new FunnyStringComparator()).build()

        when:
        def diff = itauAuditable.compare(new ValueObject(value: "aaa"), new ValueObject(value: "a"))
        println "first diff: "+ diff

        then:
        diff.changes.size() == 0

        when:
        diff = itauAuditable.compare(new ValueObject(value: "aaa"), new ValueObject(value: "b"))
        println "second diff: "+ diff

        then:
        diff.changes.size() == 1
        diff.changes[0] instanceof SetChange
        diff.changes[0].changes.size() == 2 // two item changes in this SetChange
    }
}

