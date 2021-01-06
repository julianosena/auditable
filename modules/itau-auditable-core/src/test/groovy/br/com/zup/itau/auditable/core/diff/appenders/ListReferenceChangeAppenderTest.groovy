package br.com.zup.itau.auditable.core.diff.appenders

import br.com.zup.itau.auditable.core.model.DummyAddress
import br.com.zup.itau.auditable.core.model.SnapshotEntity

import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.valueObjectId
import static br.com.zup.itau.auditable.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class ListReferenceChangeAppenderTest extends AbstractDiffAppendersTest {

    def "should append ElementReferenceChange in List of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                  .hasSize(1)
                  .hasValueChange(1,instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should NOT append ElementReferenceChange in List of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London","Street")])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        !change
    }

    def "should append ReferenceAdded in List of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                  .hasSize(1)
                  .hasValueAdded(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceRemoved in List of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2)])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                  .hasSize(1)
                  .hasValueRemoved(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in List of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        ContainerChangeAssert.assertThat(change)
                  .hasSize(1)
                  .hasValueAdded(1, valueObjectId(5, SnapshotEntity, "listOfValueObjects/1"))
    }

    def "should append ReferenceRemoved in List of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London")])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        ContainerChangeAssert.assertThat(change)
                  .hasSize(1)
                  .hasValueRemoved(1, valueObjectId(5, SnapshotEntity, "listOfValueObjects/1"))
    }

}
