package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.collections.Sets;
import br.com.zup.itau.auditable.common.string.ToStringBuilder;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ClassFilter extends Filter {
    private final Set<ManagedType> managedTypes;

    public ClassFilter(Set<ManagedType> managedTypes) {
        Validate.argumentIsNotNull(managedTypes);
        this.managedTypes =  managedTypes;
    }

    Set<ManagedType> getManagedTypes() {
        return managedTypes;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "types",
                Sets.transform(managedTypes, t -> t.getName()));
    }

    @Override
    boolean matches(GlobalId globalId) {
        return managedTypes.stream().anyMatch(id -> id.getName().equals(globalId.getTypeName()));
    }
}
