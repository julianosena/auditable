package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.string.ToStringBuilder;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId;
import br.com.zup.itau.auditable.core.metamodel.type.EntityType;

/**
 * @author bartosz.walacik
 */
class VoOwnerFilter extends Filter {

    private final EntityType ownerEntity;
    private final String path;

    public VoOwnerFilter(EntityType ownerEntity, String path) {
        this.ownerEntity = ownerEntity;
        this.path = path;
    }

    public EntityType getOwnerEntity() {
        return ownerEntity;
    }

    public String getPath() {
        return path;
    }

    @Override
    boolean matches(GlobalId globalId) {
        if (!(globalId instanceof ValueObjectId)) {
          return false;
        }

        ValueObjectId valueObjectId = (ValueObjectId) globalId;

        return valueObjectId.getOwnerId().getTypeName().equals(ownerEntity.getName())
            &&valueObjectId.getFragment().equals(path);
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "ownerEntity", ownerEntity,
                "path", path);
    }
}
