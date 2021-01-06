package br.com.zup.itau.auditable.core.metamodel.type;

import java.util.List;
import java.util.Optional;

/**
 * @see br.com.zup.itau.auditable.core.metamodel.annotation.ShallowReference
 * @author bartosz.walacik
 */
public class ShallowReferenceType extends EntityType {
    ShallowReferenceType(ManagedClass entity, List<ItauAuditableProperty> idProperties, Optional<String> typeName) {
        super(entity.createShallowReference(), idProperties, typeName);
    }

    @Override
    EntityType spawn(ManagedClass managedClass, Optional<String> typeName) {
        return new ShallowReferenceType(managedClass, getIdProperties(), typeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ShallowReferenceType)) {return false;}

        ShallowReferenceType that = (ShallowReferenceType) o;
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
