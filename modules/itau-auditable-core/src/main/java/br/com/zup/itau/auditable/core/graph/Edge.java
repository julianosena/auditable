package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

import java.util.List;

/**
 * Relation between (Entity) instances
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
abstract class Edge {
    private final ItauAuditableProperty property;

    Edge(ItauAuditableProperty property) {
        Validate.argumentIsNotNull(property);
        this.property = property;
    }

    ItauAuditableProperty getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Edge that = (Edge) obj;
        return property.equals(that.property);
    }

    @Override
    public int hashCode() {
        return property.hashCode();
    }

    abstract List<LiveNode> getReferences();

    abstract Object getDehydratedPropertyValue();
}
