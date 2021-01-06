package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeType;
import br.com.zup.itau.auditable.core.graph.ObjectNode;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.property.MissingProperty;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * holds two versions of the same {@link ObjectNode}
 *
 * @author bartosz walacik
 */
public class RealNodePair implements NodePair {
    private final ObjectNode left;
    private final ObjectNode right;
    private final Optional<CommitMetadata> commitMetadata;

    RealNodePair(ObjectNode left, ObjectNode right) {
        this(left, right, Optional.empty());
    }

    public RealNodePair(ObjectNode left, ObjectNode right, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(left, right, commitMetadata);
        Validate.argumentCheck(left.getGlobalId().equals(right.getGlobalId()), "left & right should refer to the same Cdo");
        this.left = left;
        this.right = right;
        this.commitMetadata = commitMetadata;
    }

    @Override
    public ManagedType getManagedType() {
        return right.getManagedType();
    }

    @Override
    public boolean isNullOnBothSides(Property property) {
        return left.getPropertyValue(property) == null &&
                right.getPropertyValue(property) == null;
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return left.getPropertyValue(property);
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
        return left.getReference(property);
    }

    @Override
    public List<GlobalId> getRightReferences(ItauAuditableProperty property) {
        return right.getReferences(property);
    }

    @Override
    public List<GlobalId> getLeftReferences(ItauAuditableProperty property) {
        return left.getReferences(property);
    }

    @Override
    public ObjectNode getRight() {
        return right;
    }

    @Override
    public ObjectNode getLeft() {
        return left;
    }

    boolean sameClass() {
        return right.getManagedType().getBaseJavaType() == left.getManagedType().getBaseJavaType();
    }

    @Override
    public List<ItauAuditableProperty> getProperties() {
        if (sameClass()) {
            return getManagedType().getProperties();
        }
        else {
            return Collections.unmodifiableList(getPropertiesFromBothSides());
        }
    }

    private List<ItauAuditableProperty> getPropertiesFromBothSides() {
        Set<String> leftNames = left.getManagedType().getProperties().stream()
                .map(it -> it.getName()).collect(Collectors.toSet());


        return Stream.concat(left.getManagedType().getProperties().stream(),
                              right.getManagedType().getProperties().stream().filter(it -> !leftNames.contains(it.getName())))
                       .collect(Collectors.toList());
    }

    @Override
    public GlobalId getGlobalId() {
        return left.getGlobalId();
    }

    @Override
    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    public PropertyChangeType getChangeType(ItauAuditableProperty property) {
        if (getLeft().getManagedType().getBaseJavaClass() == getRight().getManagedType().getBaseJavaClass()) {
            return PropertyChangeType.PROPERTY_VALUE_CHANGED;
        }

        if (getLeftPropertyValue(property) == MissingProperty.INSTANCE) {
            return PropertyChangeType.PROPERTY_ADDED;
        }

        if (getRightPropertyValue(property) == MissingProperty.INSTANCE) {
            return PropertyChangeType.PROPERTY_REMOVED;
        }

        return PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }
}