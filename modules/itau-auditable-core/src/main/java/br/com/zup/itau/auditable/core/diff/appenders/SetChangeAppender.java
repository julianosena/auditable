package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.common.collections.Sets;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.SetChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ValueAdded;
import br.com.zup.itau.auditable.core.diff.changetype.container.ValueRemoved;
import br.com.zup.itau.auditable.core.metamodel.type.*;

import java.util.*;

/**
 * @author pawel szymczyk
 */
class SetChangeAppender extends CorePropertyChangeAppender<SetChange> {
    private final TypeMapper typeMapper;

    SetChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType instanceof SetType;
    }

    @Override
    protected SetChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ItauAuditableProperty property) {
        Set leftSet = wrapValuesIfNeeded(toSet(leftValue), property);
        Set rightSet = wrapValuesIfNeeded(toSet(rightValue), property);

        List<ContainerElementChange> entryChanges = calculateDiff(leftSet, rightSet);
        if (!entryChanges.isEmpty()) {
            CollectionType setType = property.getType();
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "Set", property);
            return new SetChange(pair.createPropertyChangeMetadata(property), entryChanges);
        } else {
            return null;
        }
    }

    private Set wrapValuesIfNeeded(Set set, ItauAuditableProperty property) {
        return HashWrapper.wrapValuesIfNeeded(set, typeMapper.getContainerItemType(property));
    }

    private Set toSet(Object collection) {
        if (collection instanceof Set) {
            return (Set) collection;
        }
        return new HashSet((Collection)collection);
    }

    private List<ContainerElementChange> calculateDiff(Set leftSet, Set rightSet) {
        if (Objects.equals(leftSet, rightSet)) {
            return Collections.emptyList();
        }

        List<ContainerElementChange> changes = new ArrayList<>();

        Sets.difference(leftSet, rightSet).forEach(valueOrId -> changes.add(new ValueRemoved(valueOrId)));

        Sets.difference(rightSet, leftSet).forEach(valueOrId -> changes.add(new ValueAdded(valueOrId)));

        return changes;
    }
}
