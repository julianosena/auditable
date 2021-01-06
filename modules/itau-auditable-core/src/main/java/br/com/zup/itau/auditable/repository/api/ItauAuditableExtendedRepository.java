package br.com.zup.itau.auditable.repository.api;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.core.commit.CommitId;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChange;
import br.com.zup.itau.auditable.core.json.JsonConverter;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId;
import br.com.zup.itau.auditable.core.metamodel.type.EntityType;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import br.com.zup.itau.auditable.core.snapshot.SnapshotDiffer;

import java.time.LocalDateTime;
import java.util.*;

import static br.com.zup.itau.auditable.common.validation.Validate.argumentIsNotNull;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;

/**
 * @author bartosz walacik
 */
public class ItauAuditableExtendedRepository implements ItauAuditableRepository {
    private final ItauAuditableRepository delegate;
    private final SnapshotDiffer snapshotDiffer;
    private final PreviousSnapshotsCalculator previousSnapshotsCalculator;

    public ItauAuditableExtendedRepository(ItauAuditableRepository delegate, SnapshotDiffer snapshotDiffer) {
        this.delegate = delegate;
        this.snapshotDiffer = snapshotDiffer;
        previousSnapshotsCalculator = new PreviousSnapshotsCalculator(input -> getSnapshots(input));
    }

    public List<Change> getChangeHistory(GlobalId globalId, QueryParams queryParams) {
        argumentsAreNotNull(globalId, queryParams);

        List<CdoSnapshot> snapshots = getStateHistory(globalId, queryParams);
        List<Change> changes = getChangesIntroducedBySnapshots(snapshots, queryParams.newObjectChanges());

        return filterChangesByPropertyNames(changes, queryParams);
    }

    public List<Change> getChangeHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        argumentsAreNotNull(givenClasses, queryParams);

        List<CdoSnapshot> snapshots = getStateHistory(givenClasses, queryParams);
        List<Change> changes = getChangesIntroducedBySnapshots(snapshots, queryParams.newObjectChanges());
        return filterChangesByPropertyNames(changes, queryParams);
    }

    public List<Change> getValueObjectChangeHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        argumentsAreNotNull(ownerEntity, path, queryParams);

        List<CdoSnapshot> snapshots = getValueObjectStateHistory(ownerEntity, path, queryParams);
        return getChangesIntroducedBySnapshots(snapshots, queryParams.newObjectChanges());
    }

    public List<Change> getChanges(boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(queryParams);

        List<CdoSnapshot> snapshots = getSnapshots(queryParams);
        return getChangesIntroducedBySnapshots(snapshots, queryParams.newObjectChanges());
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        argumentsAreNotNull(globalId, queryParams);

        List<CdoSnapshot> snapshots = delegate.getStateHistory(globalId, queryParams);

        if (globalId instanceof InstanceId && queryParams.isAggregate()) {
            return loadMasterEntitySnapshotIfNecessary((InstanceId) globalId, snapshots);
        } else {
            return snapshots;
        }
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        argumentsAreNotNull(ownerEntity, path, queryParams);

        return delegate.getValueObjectStateHistory(ownerEntity, path, queryParams);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        argumentIsNotNull(globalId);

        return delegate.getLatest(globalId);
    }

    @Override
    public List<CdoSnapshot> getLatest(Collection<GlobalId> globalIds) {
        argumentIsNotNull(globalIds);

        return delegate.getLatest(globalIds);
    }

    /**
     * last snapshot with commitId <= given timePoint
     */
    public List<CdoSnapshot> getHistoricals(GlobalId globalId, CommitId timePoint, boolean withChildValueObjects, int limit) {
        argumentsAreNotNull(globalId, timePoint);

        return delegate.getStateHistory(globalId, QueryParamsBuilder
                        .withLimit(limit)
                        .withChildValueObjects(withChildValueObjects)
                        .toCommitId(timePoint).build());
    }

    /**
     * last snapshot with commitId <= given date
     */
    public Optional<CdoSnapshot> getHistorical(GlobalId globalId, LocalDateTime timePoint) {
        argumentsAreNotNull(globalId, timePoint);

        return delegate.getStateHistory(globalId, QueryParamsBuilder.withLimit(1).to(timePoint).build())
                .stream().findFirst();
    }

    public List<CdoSnapshot> getHistoricals(GlobalId globalId, LocalDateTime timePoint, boolean withChildValueObjects, int limit) {
        argumentsAreNotNull(globalId, timePoint);

        return delegate.getStateHistory(globalId, QueryParamsBuilder
                        .withLimit(limit)
                        .withChildValueObjects(withChildValueObjects)
                        .to(timePoint).build());

    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        argumentsAreNotNull(queryParams);

        return delegate.getSnapshots(queryParams);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        argumentIsNotNull(snapshotIdentifiers);

        return delegate.getSnapshots(snapshotIdentifiers);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        return delegate.getStateHistory(givenClasses, queryParams);
    }

    @Override
    public void persist(Commit commit) {
        delegate.persist(commit);
    }

    @Override
    public CommitId getHeadId() {
        return delegate.getHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
    }

    @Override
    public void ensureSchema() {
        delegate.ensureSchema();
    }

    private List<Change> filterChangesByPropertyNames(List<Change> changes, final QueryParams queryParams) {
        if (queryParams.changedProperties().size() == 0){
            return changes;
        }

        return Lists.positiveFilter(changes, input -> input instanceof PropertyChange &&
                queryParams.changedProperties().contains(((PropertyChange) input).getPropertyName()));
    }

    private List<CdoSnapshot> skipInitial(List<CdoSnapshot> snapshots) {
        return Lists.negativeFilter(snapshots, snapshot -> snapshot.isInitial());
    }

    private List<Change> getChangesIntroducedBySnapshots(List<CdoSnapshot> snapshots, boolean newObjectChanges) {
        return snapshotDiffer.calculateDiffs(newObjectChanges ? snapshots : skipInitial(snapshots), previousSnapshotsCalculator.calculate(snapshots));
    }

    //required for the corner case, when valueObject snapshots consume all the limit
    private List<CdoSnapshot> loadMasterEntitySnapshotIfNecessary(InstanceId instanceId, List<CdoSnapshot> alreadyLoaded) {
        if (alreadyLoaded.isEmpty()) {
            return alreadyLoaded;
        }

        if (alreadyLoaded.stream().filter(s -> s.getGlobalId().equals(instanceId)).findFirst().isPresent()) {
            return alreadyLoaded;
        }

        return getLatest(instanceId).map(it -> {
            List<CdoSnapshot> enhanced = new ArrayList(alreadyLoaded);
            enhanced.add(it);
            return java.util.Collections.unmodifiableList(enhanced);
        }).orElse(alreadyLoaded);
    }
}
