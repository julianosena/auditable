package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.common.collections.Lists
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeMetadata
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange
import br.com.zup.itau.auditable.core.diff.custom.CustomPropertyComparator
import br.com.zup.itau.auditable.core.metamodel.property.Property
import spock.lang.Specification
import spock.lang.Unroll

/**
 * see https://stackoverflow.com/questions/53418466/using-custompropertycomparator-with-java-util-list
 */
class CustomPropertyComparatorCase extends Specification {

    class Person {
        private String name
        private String ignoreThis
    }

    class Company {
        private String id
        private Collection<Person> clients
        private List<Person> partners
    }

    class PersonComparator implements CustomPropertyComparator<Person, ValueChange> {
        Optional<ValueChange> compare(Person left, Person right, PropertyChangeMetadata metadata, Property property) {
            if (left.name.equals(right.name))
                return Optional.empty()
            return Optional.of(new ValueChange(metadata, left.name, right.name));
        }

        @Override
        boolean equals(Person left, Person right) {
            return left.name.equals(right.name)
        }

        @Override
        String toString(Person value) {
            return value.name
        }
    }

    @Unroll
    def "should use CustomPropertyComparator for raw Collection items with #alg"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable()
              .withListCompareAlgorithm(alg)
              .registerCustomComparator(new  PersonComparator(), Person).build()

      when:
      Company c1 = new Company(id: "1", clients: [new Person(name: "james", ignoreThis: "ignore this")])
      Company c2 = new Company(id: "1", clients: [new Person(name: "james")])
      def diff = itauAuditable.compare(c1, c2)

      then:
      diff.changes.size() == 0

      when:
      c1 = new Company(id: "1", clients: [new Person(name: "james")] )
      c2 = new Company(id: "1", clients: [new Person(name: "james"), new Person(name: "kaz")] )

      diff = itauAuditable.compare(c1, c2)

      then:
      diff.changes.size() == 1

      where:
      alg << Lists.asList(ListCompareAlgorithm.values())
    }

    @Unroll
    def "should use CustomPropertyComparator for List items with #alg"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable()
                .withListCompareAlgorithm(alg)
                .registerCustomComparator(new  PersonComparator(), Person).build()

        when:
        Company c1 = new Company(id: "1", partners: [new Person(name: "james", ignoreThis: "ignore this")])
        Company c2 = new Company(id: "1", partners: [new Person(name: "james")])

        def diff = itauAuditable.compare(c1, c2)

        then:
        diff.changes.size() == 0

        when:
        c1 = new Company(id: "1", partners: [new Person(name: "james")] )
        c2 = new Company(id: "1", partners: [new Person(name: "james"), new Person(name: "kaz")] )

        diff = itauAuditable.compare(c1, c2)

        then:
        diff.changes.size() == 1

        where:
        alg << [ListCompareAlgorithm.SIMPLE, ListCompareAlgorithm.LEVENSHTEIN_DISTANCE]
    }
}
