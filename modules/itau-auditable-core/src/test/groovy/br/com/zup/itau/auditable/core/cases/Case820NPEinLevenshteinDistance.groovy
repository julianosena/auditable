package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/820
 */
class Case820NPEinLevenshteinDistance extends Specification {

    def "should not fail when comparing list to null or empty list"(){
        given:
        ItauAuditable javers = ItauAuditableBuilder.javers().withNewObjectsSnapshot(true)
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build()


        when:
        def diff = javers.compare(null, new Obj(["test"]))

        then:
        diff.changes

        when:
        def diff2 = javers.compareCollections(Collections.emptyList(),
                                              Collections.singletonList(new Obj(["test"])), Obj)

        then:
        diff2.changes
    }

    class Obj {
        private List<String> strings

        Obj(List<String> strings) {
            this.strings = strings
        }
    }
}
