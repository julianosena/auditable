package br.com.zup.itau.auditable.guava;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.appenders.PropertyChangeAppender;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.SetChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ValueAdded;
import br.com.zup.itau.auditable.core.diff.changetype.container.ValueRemoved;
import br.com.zup.itau.auditable.core.metamodel.object.OwnerContext;
import br.com.zup.itau.auditable.core.metamodel.object.PropertyOwnerContext;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;

import java.util.ArrayList;
import java.util.List;

import static br.com.zup.itau.auditable.core.diff.appenders.CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded;

/**
 * Compares Guava Multisets.
 * <br/>
 *
 * It's automatically registered, if Guava is detected on the classpath.
 *
 * @author akrystian
 */
class MultisetChangeAppender implements PropertyChangeAppender<SetChange> {

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return  propertyType instanceof MultisetType;
    }

    @Override
    public SetChange calculateChanges(NodePair pair, ItauAuditableProperty property) {

        Multiset left = (Multiset) pair.getLeftDehydratedPropertyValueAndSanitize(property);
        Multiset right = (Multiset) pair.getRightDehydratedPropertyValueAndSanitize(property);

        MultisetType multisetType = ((ItauAuditableProperty) property).getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());

        List<ContainerElementChange> entryChanges = calculateEntryChanges(multisetType, left, right, owner);
        if (!entryChanges.isEmpty()){
            renderNotParametrizedWarningIfNeeded(multisetType.getItemType(), "item", "Multiset", property);
            return new SetChange(pair.createPropertyChangeMetadata(property), entryChanges);
        } else {
            return null;
        }
    }

    private List<ContainerElementChange> calculateEntryChanges(MultisetType multisetType, Multiset left, Multiset right,  OwnerContext owner){

        List<ContainerElementChange> changes = new ArrayList<>();
        for (Object globalCdoId : Multisets.difference(left, right)){
            changes.add(new ValueRemoved(globalCdoId));
        }
        Multiset difference = Multisets.difference(right, left);
        for (Object globalCdoId : difference){
            changes.add(new ValueAdded(globalCdoId));
        }
        return changes;
    }
}
