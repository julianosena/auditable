package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeMetadata;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeType;
import br.com.zup.itau.auditable.core.graph.ObjectNode;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.*;

import java.util.List;
import java.util.Optional;

public interface NodePair {
    boolean isNullOnBothSides(Property property);

    GlobalId getGlobalId();

    ObjectNode getRight();

    ObjectNode getLeft();

    List<ItauAuditableProperty> getProperties();

    Object getLeftPropertyValue(Property property);

    Object getRightPropertyValue(Property property);

    GlobalId getRightReference(Property property);

    GlobalId getLeftReference(Property property);

    List<GlobalId> getRightReferences(ItauAuditableProperty property);

    List<GlobalId> getLeftReferences(ItauAuditableProperty property);

    ManagedType getManagedType();

    default Object getRightDehydratedPropertyValueAndSanitize(ItauAuditableProperty property) {
        return sanitize(getRight().getDehydratedPropertyValue(property), property.getType());
    }

    default Object getLeftDehydratedPropertyValueAndSanitize(ItauAuditableProperty property) {
        return sanitize(getLeft().getDehydratedPropertyValue(property), property.getType());
    }

    default Object sanitize(Object value, ItauAuditableType expectedType) {
        //all Enumerables (except Arrays) are sanitized
        if (expectedType instanceof EnumerableType && !(expectedType instanceof ArrayType)) {
            EnumerableType enumerableType = (EnumerableType)expectedType;
            if (value == null || !enumerableType.getEnumerableInterface().isAssignableFrom(value.getClass())) {
                return ((EnumerableType)expectedType).empty();
            }
        }
        return value;
    }

    Optional<CommitMetadata> getCommitMetadata();

    default PropertyChangeMetadata createPropertyChangeMetadata(ItauAuditableProperty property) {
        return new PropertyChangeMetadata(getGlobalId(), property.getName(), getCommitMetadata(), getChangeType(property));
    }

    PropertyChangeType getChangeType(ItauAuditableProperty property);
}
