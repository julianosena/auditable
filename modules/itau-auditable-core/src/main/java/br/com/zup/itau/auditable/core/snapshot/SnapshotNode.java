package br.com.zup.itau.auditable.core.snapshot;

import br.com.zup.itau.auditable.core.graph.ObjectNode;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.EnumerableType;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

import java.util.Collections;
import java.util.List;

class SnapshotNode extends ObjectNode<CdoSnapshot> {

    public SnapshotNode(CdoSnapshot cdo) {
        super(cdo);
    }

    @Override
    public GlobalId getReference(Property property){

        Object propertyValue = getPropertyValue(property);
        if (propertyValue instanceof GlobalId) {
            return (GlobalId)propertyValue;
        } else {
            //when user's class is refactored, a property can have different type
            return null;
        }
    }

    @Override
    protected Object getDehydratedPropertyValue(String property) {
        return getCdo().getPropertyValue(property);
    }

    @Override
    public Object getDehydratedPropertyValue(ItauAuditableProperty property) {
        return getCdo().getPropertyValue(property);
    }

    @Override
    public List<GlobalId> getReferences(ItauAuditableProperty property) {
        if (property.getType() instanceof EnumerableType) {
            Object propertyValue = getPropertyValue(property);
            EnumerableType enumerableType = property.getType();
            return enumerableType.filterToList(propertyValue, GlobalId.class);
        }
        else {
            return Collections.emptyList();
        }
    }
}
