package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.string.ToStringBuilder;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

class AnyDomainObjectFilter extends Filter {

    @Override
    boolean matches(GlobalId globalId) {
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this);
    }
}
