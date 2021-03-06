package br.com.zup.itau.auditable.core.snapshot

import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import br.com.zup.itau.auditable.core.graph.NodeAssert
import spock.lang.Shared
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class SnapshotGraphFactoryTest extends Specification {

    @Shared ItauAuditableTestBuilder itauAuditable
    @Shared SnapshotGraphFactory snapshotGraphFactory

    def setup(){
        itauAuditable = ItauAuditableTestBuilder.itauAuditableTestAssembly()
        snapshotGraphFactory = itauAuditable.getContainerComponent(SnapshotGraphFactory)
    }

    def "should create SnapshotGraph with snapshots of committed objects "() {
        given:
        def oldRef = new SnapshotEntity(id: 2, intProperty:2)
        itauAuditable.itauAuditable().commit("user",oldRef)
        def cdo = new SnapshotEntity(id: 1, entityRef: oldRef)
        def liveGraph = itauAuditable.createLiveGraph(cdo)

        when:
        def snapshotGraph = snapshotGraphFactory.createLatest(liveGraph.globalIds())

        then:
        snapshotGraph.nodes().size() == 1
        NodeAssert.assertThat(snapshotGraph.nodes()[0]).hasGlobalId(instanceId(2,SnapshotEntity))
                  .isSnapshot()
    }
}
