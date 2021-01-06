package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.EntityType;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import br.com.zup.itau.auditable.core.metamodel.type.ValueObjectType;

import java.util.List;
import java.util.Optional;

import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;

/**
 * Node in client's domain object graph. Reflects one {@link Cdo} or {@link CdoSnapshot}.
 * <p/>
 * Cdo could be an {@link EntityType} or a {@link ValueObjectType}
 * <p/>
 * Implementation should delegate equals() and hashCode() to {@link Cdo}
 *
 * @author bartosz walacik
 */
public abstract class ObjectNode<T extends Cdo> {
    private final T cdo;

    public ObjectNode(T cdo) {
        argumentsAreNotNull(cdo);
        this.cdo = cdo;
    }

    /**
     * @return returns {@link Optional#empty()} for snapshots
     */
    public Optional<Object> wrappedCdo() {
        return cdo.getWrappedCdo();
    }

    /**
     * shortcut to {@link Cdo#getGlobalId()}
     */
    public GlobalId getGlobalId() {
        return cdo.getGlobalId();
    }

    /**
     * returns null if property is not ManagedType
     */
    public abstract GlobalId getReference(Property property);

    /**
     * returns null if property is not Collection of ManagedType
     */
    public abstract List<GlobalId> getReferences(ItauAuditableProperty property);

    protected abstract Object getDehydratedPropertyValue(String propertyName);

    public abstract Object getDehydratedPropertyValue(ItauAuditableProperty property);

    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        return cdo.getPropertyValue(property);
    }

    public boolean isNull(Property property){
        return cdo.isNull(property);
    }

    public ManagedType getManagedType() {
        return cdo.getManagedType();
    }

    public T getCdo() {
        return cdo;
    }

    public int cdoHashCode() {
        return cdo.hashCode();
    }
}