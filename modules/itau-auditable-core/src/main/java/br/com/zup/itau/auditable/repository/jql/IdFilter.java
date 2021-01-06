package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.string.ToStringBuilder;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId;

/**
 * @author bartosz.walacik
 */
class IdFilter extends Filter {
    private final GlobalId globalId;

    IdFilter(GlobalId globalId) {
        Validate.argumentIsNotNull(globalId);
        this.globalId = globalId;
    }

    GlobalId getGlobalId() {
        return globalId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "globalId", globalId);
    }

    boolean isInstanceIdFilter() {
        return globalId instanceof InstanceId;
    }

    @Override
    boolean matches(GlobalId targetId) {
        return globalId.equals(targetId);
    }
}
