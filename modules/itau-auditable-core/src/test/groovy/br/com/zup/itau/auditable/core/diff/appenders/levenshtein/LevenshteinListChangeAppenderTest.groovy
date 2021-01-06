package br.com.zup.itau.auditable.core.diff.appenders.levenshtein

import br.com.zup.itau.auditable.core.diff.appenders.AbstractDiffAppendersTest
import br.com.zup.itau.auditable.core.diff.appenders.ContainerChangeAssert
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import spock.lang.Unroll
import static br.com.zup.itau.auditable.core.diff.appenders.ContainerChangeAssert.assertThat
import static br.com.zup.itau.auditable.core.model.DummyUser.dummyUser

class LevenshteinListChangeAppenderTest extends AbstractDiffAppendersTest {

    def "should recognise that entity lists as equal"() {

        when:
        def leftNode =  new SnapshotEntity(id:1, listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def rightNode = new SnapshotEntity(id:1, listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        !change
    }

    def "should compare entity lists using GlobalId"() {

        when:
        def added = new SnapshotEntity(id:3)
        def leftNode =  new SnapshotEntity(id:1, listOfEntities: [new SnapshotEntity(id:2)])
        def rightNode = new SnapshotEntity(id:1, listOfEntities: [new SnapshotEntity(id:2), added])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        change.changes.size() == 1
        def addedValue = change.changes[0].addedValue
        addedValue instanceof InstanceId
        addedValue.cdoId == 3
    }

    @Unroll
    def "should find #changeDesc element at the beginning of the list"() {
        given:
        def leftNode =  dummyUser().withIntegerList(leftList)
        def rightNode = dummyUser().withIntegerList(rightList)

        when:
        def change = levenshteinListChangeAppender().calculateChanges(
                     realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change.changes.size() == 1
        assertion.call(change)

        where:
        leftList  | rightList | changeDesc || assertion
        [1, 2, 3] | [2, 3]    | "removed"  || {it -> ContainerChangeAssert.assertThat(it).hasValueRemoved(0, 1)}
        [2, 3]    | [1, 2, 3] | "added"    || {it -> ContainerChangeAssert.assertThat(it).hasValueAdded(0, 1)}
        [1, 2, 3] | [9, 2, 3] | "changed"  || {it -> ContainerChangeAssert.assertThat(it).hasValueChange(0, 1, 9)}
    }

    def "should recognise that integer lists as equal"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 3])
        def rightNode = dummyUser().withIntegerList([1, 2, 3])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        !change
    }

    def "should find added element at the end of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 3])
        def rightNode = dummyUser().withIntegerList([1, 2, 3, 4])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueAdded(3, 4)
    }

    def "should find added element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 3])
        def rightNode = dummyUser().withIntegerList([1, 2, 4, 3])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueAdded(2, 4)
    }

    def "should find removed element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 4, 3])
        def rightNode = dummyUser().withIntegerList([1, 2, 3])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueRemoved(2, 4)
    }

    def "should find removed element at the end of the list"() {

        when:
        def leftNode =   dummyUser().withIntegerList([1, 2, 3])
        def rightNode =  dummyUser().withIntegerList([1, 2])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueRemoved(2, 3)
    }

    def "should find changed element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 4, 3])
        def rightNode = dummyUser().withIntegerList([1, 2, 5, 3])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))


        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueChange(2, 4, 5)
    }

    def "should find changed element at the end of the list"() {
        when:
        def leftNode = dummyUser().withIntegerList([0, 1, 2, 4])
        def rightNode = dummyUser().withIntegerList([0, 1, 2, 5])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasValueChange(3, 4, 5)
    }

    def "should not fail on nulls"(){
      given:
      def leftNode = dummyUser().withIntegerList(null)
      def rightNode = dummyUser().withIntegerList([5])

      when:
      def change = levenshteinListChangeAppender().calculateChanges(
              realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

      then:
      ContainerChangeAssert.assertThat(change)
              .hasSize(1)
              .hasValueAdded(0, 5)
    }

    def "should find changed and added element"() {
        when:
        def leftNode = dummyUser().withIntegerList([0, 1, 2, 4])
        def rightNode = dummyUser().withIntegerList([0, 1, 3, 5, 6])

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(3)
                .hasValueChange(2, 2, 3)
                .hasValueChange(3, 4, 5)
                .hasValueAdded(4, 6)
    }
}
