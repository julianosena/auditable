package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

import java.util.Collections;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class ShallowSingleEdge extends AbstractSingleEdge {
    private final GlobalId reference;

    ShallowSingleEdge(ItauAuditableProperty property, GlobalId referenced) {
        super(property);
        Validate.argumentIsNotNull(referenced);
        this.reference = referenced;
    }

    @Override
    GlobalId getReference() {
        return reference;
    }

    @Override
    List<LiveNode> getReferences() {
        return Collections.emptyList();
    }
}
