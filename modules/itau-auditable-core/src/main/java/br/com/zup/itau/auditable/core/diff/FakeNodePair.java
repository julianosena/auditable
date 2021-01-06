package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.common.collections.Defaults;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeType;
import br.com.zup.itau.auditable.core.graph.ObjectNode;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class FakeNodePair implements NodePair {

    private final ObjectNode right;
    private final Optional<CommitMetadata> commitMetadata;

    public FakeNodePair(ObjectNode right, Optional<CommitMetadata> commitMetadata) {
        this.right = right;
        this.commitMetadata = commitMetadata;
    }

    @Override
    public ManagedType getManagedType() {
        return right.getManagedType();
    }

    @Override
    public boolean isNullOnBothSides(Property property) {
        return right.getPropertyValue(property) == null;
    }

    @Override
    public GlobalId getGlobalId() {
        return right.getGlobalId();
    }

    @Override
    public ObjectNode getRight() {
        return right;
    }

    @Override
    public ObjectNode getLeft() {
        throw new ItauAuditableException(ItauAuditableExceptionCode.NOT_IMPLEMENTED, "FakeNodePair.getLeft()");
    }

    @Override
    public Object getLeftDehydratedPropertyValueAndSanitize(ItauAuditableProperty property) {
        return sanitize( Defaults.defaultValue(property.getGenericType()), property.getType());
    }

    @Override
    public List<ItauAuditableProperty> getProperties() {
        return getManagedType().getProperties();
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public Object getRightPropertyValue(Property property) {
        return right.getPropertyValue(property);
    }
    @Override
    public GlobalId getRightReference(Property property) {
         return right.getReference(property);
    }

    @Override
    public GlobalId getLeftReference(Property property) {
        return null;
    }

    @Override
    public List<GlobalId> getRightReferences(ItauAuditableProperty property) {
        return right.getReferences(property);
    }

    @Override
    public List<GlobalId> getLeftReferences(ItauAuditableProperty property) {
        return Collections.emptyList();
    }

    @Override
    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    @Override
    public PropertyChangeType getChangeType(ItauAuditableProperty property) {
        return PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }
}