package br.com.zup.itau.auditable.core.snapshot;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds SnapshotGraph from latest snapshots loaded from javersRepository
 */
public class SnapshotGraphFactory {
    private final ItauAuditableExtendedRepository javersRepository;

    SnapshotGraphFactory(ItauAuditableExtendedRepository javersRepository) {
        this.javersRepository = javersRepository;
    }

    public SnapshotGraph createLatest(Set<GlobalId> globalIds){
        Validate.argumentIsNotNull(globalIds);

        Set<SnapshotNode> snapshotNodes = javersRepository.getLatest(globalIds)
                .stream()
                .map(SnapshotNode::new)
                .collect(Collectors.toSet());

        return new SnapshotGraph(snapshotNodes);
    }
}
