package br.com.zup.itau.auditable.core.commit;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.common.date.DateProvider;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.DiffFactory;
import br.com.zup.itau.auditable.core.graph.Cdo;
import br.com.zup.itau.auditable.core.graph.LiveGraph;
import br.com.zup.itau.auditable.core.graph.LiveGraphFactory;
import br.com.zup.itau.auditable.core.graph.ObjectGraph;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.snapshot.ChangedCdoSnapshotsFactory;
import br.com.zup.itau.auditable.core.snapshot.SnapshotFactory;
import br.com.zup.itau.auditable.core.snapshot.SnapshotGraphFactory;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final DiffFactory diffFactory;
    private final ItauAuditableExtendedRepository javersRepository;
    private final DateProvider dateProvider;
    private final LiveGraphFactory liveGraphFactory;
    private final SnapshotFactory snapshotFactory;
    private final SnapshotGraphFactory snapshotGraphFactory;
    private final ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory;
    private final CommitIdFactory commitIdFactory;

    public CommitFactory(DiffFactory diffFactory, ItauAuditableExtendedRepository javersRepository, DateProvider dateProvider, LiveGraphFactory liveGraphFactory, SnapshotFactory snapshotFactory, SnapshotGraphFactory snapshotGraphFactory, ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory, CommitIdFactory commitIdFactory) {
        this.diffFactory = diffFactory;
        this.javersRepository = javersRepository;
        this.dateProvider = dateProvider;
        this.liveGraphFactory = liveGraphFactory;
        this.snapshotFactory = snapshotFactory;
        this.snapshotGraphFactory = snapshotGraphFactory;
        this.changedCdoSnapshotsFactory = changedCdoSnapshotsFactory;
        this.commitIdFactory = commitIdFactory;
    }

    public Commit createTerminalByGlobalId(String author, Map<String, String> properties, GlobalId removedId){
        argumentsAreNotNull(author, properties, removedId);
        Optional<CdoSnapshot> previousSnapshot = javersRepository.getLatest(removedId);

        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        CdoSnapshot terminalSnapshot = previousSnapshot
                .map(prev -> snapshotFactory.createTerminal(removedId, prev, commitMetadata))
                .orElseThrow(() -> new ItauAuditableException(ItauAuditableExceptionCode.CANT_DELETE_OBJECT_NOT_FOUND, removedId.value()));
        Diff diff = diffFactory.singleTerminal(removedId, commitMetadata);
        return new Commit(commitMetadata, Lists.asList(terminalSnapshot), diff);
    }

    public Commit createTerminal(String author, Map<String, String> properties, Object removed){
        argumentsAreNotNull(author, properties, removed);
        Cdo removedCdo = liveGraphFactory.createCdo(removed);
        return createTerminalByGlobalId(author, properties, removedCdo.getGlobalId());
    }

    public Commit create(String author, Map<String, String> properties, Object currentVersion){
        argumentsAreNotNull(author, currentVersion);
        LiveGraph currentGraph = createLiveGraph(currentVersion);
        return createCommit(author, properties, currentGraph);
    }

    private Commit createCommit(String author, Map<String, String> properties, LiveGraph currentGraph){
        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        ObjectGraph<CdoSnapshot> latestSnapshotGraph = snapshotGraphFactory.createLatest(currentGraph.globalIds());
        List<CdoSnapshot> changedCdoSnapshots =
            changedCdoSnapshotsFactory.create(currentGraph, latestSnapshotGraph.cdos(), commitMetadata);
        Diff diff = diffFactory.create(latestSnapshotGraph, currentGraph, Optional.of(commitMetadata));
        return new Commit(commitMetadata, changedCdoSnapshots, diff);
    }

    private LiveGraph createLiveGraph(Object currentVersion){
        argumentsAreNotNull(currentVersion);
        return liveGraphFactory.createLiveGraph(currentVersion);
    }

    private CommitMetadata newCommitMetadata(String author, Map<String, String> properties){
        ZonedDateTime now = dateProvider.now();
        return new CommitMetadata(author, properties,
                now.toLocalDateTime(), now.toInstant(),
                commitIdFactory.nextId());
    }
}
