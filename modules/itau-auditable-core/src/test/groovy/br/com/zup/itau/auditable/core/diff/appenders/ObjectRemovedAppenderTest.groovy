package br.com.zup.itau.auditable.core.diff.appenders

import br.com.zup.itau.auditable.core.diff.GraphPair
import br.com.zup.itau.auditable.core.model.DummyUser

import static br.com.zup.itau.auditable.core.diff.ChangeAssert.assertThat
import static br.com.zup.itau.auditable.core.model.DummyUser.dummyUser

/**
 * @author Maciej Zasada
 */
class ObjectRemovedAppenderTest extends AbstractDiffAppendersTest {

    def "should append ObjectRemoved to diff"() {
        given:
        def cdoLeft = dummyUser("removed")
        def cdoRight = dummyUser("1")
        def left =  buildLiveGraph(cdoLeft)
        def right = buildLiveGraph(cdoRight)

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 1
        assertThat(changes[0])
                    .isObjectRemoved()
                    .hasCdoId("removed")
                    .hasEntityTypeOf(DummyUser)
                    .hasAffectedCdo(cdoLeft)
    }

    def "should append 2 ObjectRemoved to diff"() {
        given:
        def left =  buildLiveGraph(dummyUser("removed").withDetails(5))
        def right = buildLiveGraph(dummyUser("1"))

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 2
        assertThat(changes[0]).isObjectRemoved()
        assertThat(changes[1]).isObjectRemoved()
    }

    def "should do nothing when graph has same node set"() {
        given:
        def left =  buildLiveGraph(dummyUser("1"))
        def right = buildLiveGraph(dummyUser("1"))

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 0
    }

}
