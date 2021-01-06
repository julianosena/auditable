package br.com.zup.itau.auditable.core.snapshot;

import br.com.zup.itau.auditable.common.collections.Defaults;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.graph.Cdo;
import br.com.zup.itau.auditable.core.graph.LiveNode;
import br.com.zup.itau.auditable.core.metamodel.object.*;
import br.com.zup.itau.auditable.core.metamodel.type.CustomComparableType;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.Objects;

import static br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;
import static br.com.zup.itau.auditable.core.metamodel.object.SnapshotType.*;

/**
 * @author bartosz walacik
 */
public class SnapshotFactory {
    private final TypeMapper typeMapper;

    SnapshotFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public CdoSnapshot createTerminal(GlobalId globalId, CdoSnapshot previous, CommitMetadata commitMetadata) {
        ManagedType managedType = typeMapper.getItauAuditableManagedType(globalId);
        return cdoSnapshot()
                .withGlobalId(globalId)
                .withManagedType(managedType)
                .withCommitMetadata(commitMetadata)
                .withType(TERMINAL)
                .withVersion(previous.getVersion()+1)
                .build();
    }

    CdoSnapshot createInitial(LiveNode liveNode, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(liveNode, commitMetadata)
                .withState(createSnapshotState(liveNode))
                .withType(INITIAL)
                .markAllAsChanged()
                .withVersion(1L)
                .build();
    }

    CdoSnapshot createUpdate(LiveNode liveNode, CdoSnapshot previous, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(liveNode, commitMetadata)
                .withState(createSnapshotState(liveNode))
                .withType(UPDATE)
                .markChanged(previous)
                .withVersion(previous.getVersion()+1)
                .build();
    }

    public CdoSnapshotState createSnapshotStateNoRefs(ManagedType managedType, Object instance) {
        CdoSnapshotStateBuilder stateBuilder = CdoSnapshotStateBuilder.cdoSnapshotState();
        for (ItauAuditableProperty property : managedType.getProperties()) {
            if (typeMapper.isManagedType(property.getType()) ||
                typeMapper.isEnumerableOfManagedTypes(property.getType())) {
                continue;
            }

            Object propertyValue = property.get(instance);
            if (Objects.equals(propertyValue, Defaults.defaultValue(property.getGenericType()))) {
                continue;
            }

            if (property.getType() instanceof CustomComparableType) {
                String propertyValueToString = ((CustomComparableType) property.getType()).valueToString(propertyValue);
                stateBuilder.withPropertyValue(property, propertyValueToString);
            } else {
                stateBuilder.withPropertyValue(property, propertyValue);
            }
        }
        return stateBuilder.build();
    }

    public CdoSnapshotState createSnapshotStateNoRefs(Cdo liveCdo){
        return createSnapshotStateNoRefs(liveCdo.getManagedType(), liveCdo.getWrappedCdo().get());
    }

    public CdoSnapshotState createSnapshotState(LiveNode liveNode){
        CdoSnapshotStateBuilder stateBuilder = CdoSnapshotStateBuilder.cdoSnapshotState();
        for (ItauAuditableProperty property : liveNode.getManagedType().getProperties()) {
            Object dehydratedPropertyValue = liveNode.getDehydratedPropertyValue(property);
            if (Objects.equals(dehydratedPropertyValue, Defaults.defaultValue(property.getGenericType()))) {
                continue;
            }
            if (stateBuilder.contains(property)) {
                throw new ItauAuditableException(ItauAuditableExceptionCode.SNAPSHOT_SERIALIZATION_ERROR, liveNode.getGlobalId().value(), property);
            }
            stateBuilder.withPropertyValue(property, dehydratedPropertyValue);
        }
        return stateBuilder.build();
    }

    private CdoSnapshotBuilder initSnapshotBuilder(LiveNode liveNode, CommitMetadata commitMetadata) {
        return cdoSnapshot()
                .withGlobalId(liveNode.getGlobalId())
                .withCommitMetadata(commitMetadata)
                .withManagedType(liveNode.getManagedType());
    }
}
