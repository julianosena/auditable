package br.com.zup.itau.auditable.core.snapshot;

import br.com.zup.itau.auditable.core.graph.ObjectGraph;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;

import java.util.Set;

/**
 * @author bartosz walacik
 */
class SnapshotGraph extends ObjectGraph<CdoSnapshot> {
    SnapshotGraph(Set<SnapshotNode> snapshots) {
        super((Set)snapshots);
    }
}
