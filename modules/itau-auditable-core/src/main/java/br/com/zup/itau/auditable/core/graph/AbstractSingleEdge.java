package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

abstract class AbstractSingleEdge extends Edge {
    AbstractSingleEdge(ItauAuditableProperty property) {
        super(property);
    }

    abstract GlobalId getReference();

    public Object getDehydratedPropertyValue() {
        return getReference();
    }
}
