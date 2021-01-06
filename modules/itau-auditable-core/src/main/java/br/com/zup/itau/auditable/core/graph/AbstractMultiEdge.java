package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

abstract class AbstractMultiEdge extends Edge {
    public AbstractMultiEdge(ItauAuditableProperty property) {
        super(property);
    }
}
