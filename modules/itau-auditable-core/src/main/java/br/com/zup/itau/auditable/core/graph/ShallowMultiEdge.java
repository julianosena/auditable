package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

import java.util.Collections;
import java.util.List;

class ShallowMultiEdge extends AbstractMultiEdge {
    private final Object dehydratedPropertyValue;

    ShallowMultiEdge(ItauAuditableProperty property, Object dehydratedPropertyValue) {
        super(property);
        this.dehydratedPropertyValue = dehydratedPropertyValue;
    }

    @Override
    List<LiveNode> getReferences() {
        return Collections.emptyList();
    }

    @Override
    Object getDehydratedPropertyValue() {
        return dehydratedPropertyValue;
    }
}
