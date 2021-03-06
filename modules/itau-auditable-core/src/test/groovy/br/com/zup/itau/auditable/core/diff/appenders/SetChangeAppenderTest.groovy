package br.com.zup.itau.auditable.core.diff.appenders

import br.com.zup.itau.auditable.core.diff.RealNodePair
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import spock.lang.Shared
import spock.lang.Unroll

import java.time.LocalDate

import static br.com.zup.itau.auditable.core.diff.appenders.ContainerChangeAssert.getAssertThat

/**
 * @author pawel szymczyk
 */
class SetChangeAppenderTest extends AbstractDiffAppendersTest {

    @Shared
    SetChangeAppender setChangeAppender

    @Shared
    String commonFieldName

    @Shared
    String dateFieldName

    def setupSpec() {
        setChangeAppender = setChangeAppender()
        commonFieldName = "stringSet"
        dateFieldName = "setOfDates"
    }

    @Unroll
    def "should append #changesCount changes when left field is #leftField and right field is #rightField"() {

        when:
        def leftNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": leftField))
        def rightNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": rightField))

        def change = setChangeAppender.calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, commonFieldName))

        then:
        change.changes.size() == changesCount
        change.changes.each {
            assert it.index == null
        }

        where:
        leftField            | rightField           || changesCount
        null                 | ["1", "2"]           || 2
        []                   | ["1", "2"]           || 2
        ["1", "2"]           | ["1", "2", "3", "4"] || 2
        ["1", "2"]           | ["2", "1", "3"]      || 1
        ["1", "2"]           | []                   || 2
        ["1", "2", "3", "4"] | ["1"]                || 3
        ["2", "1", "3"]      | ["1", "2"]           || 1
    }

    @Unroll
    def "should not append changes when left field #leftField and right field #rightField are equal"() {

        when:
        def leftNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": leftField))
        def rightNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": rightField))

        def change = setChangeAppender.calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, commonFieldName))

        then:
        change == null

        where:
        leftField  | rightField
        []         | []
        ["1", "2"] | ["1", "2"]
        ["1", "2"] | ["2", "1"]
    }

    def "should append ValueAdded in field of Values"() {
        given:
        def leftCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 1, 1)])
        def rightCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 5, 5), new LocalDate(2001, 1, 1)])

        when:
        def change = setChangeAppender
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, dateFieldName))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueAdded(new LocalDate(2001, 5, 5))

    }

    def "should append ValueRemoved in field of Values"() {
        given:
        def leftCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 5, 5), new LocalDate(2001, 1, 1)])
        def rightCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 1, 1)])

        when:
        def change = setChangeAppender
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, dateFieldName))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueRemoved(new LocalDate(2001, 5, 5))

    }

}