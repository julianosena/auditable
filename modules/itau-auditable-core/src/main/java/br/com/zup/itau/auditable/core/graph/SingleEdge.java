package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

import java.util.List;

/**
 * OneToOne or ManyToOne relation
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
class SingleEdge extends AbstractSingleEdge {
    private final LiveNode referencedNode;

    SingleEdge(ItauAuditableProperty property, LiveNode referencedNode) {
        super(property);
        Validate.argumentsAreNotNull(referencedNode);
        this.referencedNode = referencedNode;
    }

    @Override
    GlobalId getReference() {
        return referencedNode.getGlobalId();
    }

    @Override
    List<LiveNode> getReferences() {
        return Lists.asList(referencedNode);
    }
}
