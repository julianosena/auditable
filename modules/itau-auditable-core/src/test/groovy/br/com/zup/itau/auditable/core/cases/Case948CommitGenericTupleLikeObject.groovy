package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.commit.Commit
import spock.lang.Specification

class Case948CommitGenericTupleLikeObject extends Specification {
    class Pair<L, R> {
        L left
        R right

        Pair(L left, R right) {
            this.left = left
            this.right = right
        }

        @Override
        String toString() {
            return "Pair [left=" + left + ", right=" + right + "]"
        }
    }

    def "should track changes when committing generic-tuple-like object"() {
        given:
        ItauAuditable javers = ItauAuditableBuilder.javers().build()

        def obj = new Pair(1L, "foo")

        when:
        Commit commit = javers.commit("jay", obj)

        obj.right = "bar"

        commit = javers.commit("jay", obj)

        println "commit.changes" + commit.changes.prettyPrint()

        then:
        commit.getChanges().size() == 1
    }
}

