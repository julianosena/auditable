package br.com.zup.itau.auditable.guava;

import com.google.common.collect.Multimap;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.appenders.PropertyChangeAppender;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryAdded;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryChange;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryRemoved;
import br.com.zup.itau.auditable.core.diff.changetype.map.MapChange;
import br.com.zup.itau.auditable.core.metamodel.object.OwnerContext;
import br.com.zup.itau.auditable.core.metamodel.object.PropertyOwnerContext;
import br.com.zup.itau.auditable.core.metamodel.type.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.EMPTY_LIST;
import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY;
import static br.com.zup.itau.auditable.core.diff.appenders.CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded;

/**
 * Compares Guava Multimaps.
 * <br/>
 *
 * It's automatically registered, if Guava is detected on a classpath.
 *
 * @author akrystian
 */
class MultimapChangeAppender implements PropertyChangeAppender<MapChange> {

    private final TypeMapper typeMapper;

    MultimapChangeAppender(TypeMapper typeMapper){
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        if (!(propertyType instanceof MultimapType)){
            return false;
        }
        MapContentType mapContentType = typeMapper.getMapContentType((KeyValueType) propertyType);
        if (mapContentType.getKeyType() instanceof ValueObjectType){
            throw new ItauAuditableException(VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY, propertyType);
        }
        return true;
    }

    @Override
    public MapChange calculateChanges(NodePair pair, ItauAuditableProperty property) {

        Multimap left = (Multimap) pair.getLeftDehydratedPropertyValueAndSanitize(property);
        Multimap right = (Multimap) pair.getRightDehydratedPropertyValueAndSanitize(property);

        MultimapType multimapType = ((ItauAuditableProperty) property).getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());

        List<EntryChange> entryChanges = calculateChanges(multimapType, left, right, owner);
        if (!entryChanges.isEmpty()){
            renderNotParametrizedWarningIfNeeded(multimapType.getKeyType(), "key", "Multimap", property);
            renderNotParametrizedWarningIfNeeded(multimapType.getValueType(), "value", "Multimap", property);
            return new MapChange(pair.createPropertyChangeMetadata(property), entryChanges);
        } else {
            return null;
        }
    }

    private List<EntryChange> calculateChanges(MultimapType multimapType, Multimap left, Multimap right, OwnerContext owner){
        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Multimaps.commonKeys(left, right)){
            Collection leftValues = left.get(commonKey);
            Collection rightValues = right.get(commonKey);

            Collection difference = difference(leftValues, rightValues);
            difference.addAll(difference(rightValues, leftValues));
            if (difference.size() > 0){
                calculateValueChanges(changes, commonKey, leftValues, rightValues);
            }
        }
        calculateKeyChanges(left, right, changes);
        return changes;
    }

    /**
     * Difference that handle properly collections with duplicates.
     */
    private static Collection difference(Collection first, Collection second){
        if (first == null) {
            return EMPTY_LIST;
        }

        if (second == null) {
            return first;
        }

        Collection difference = new ArrayList<>(first);
        for (Object current : second){
            difference.remove(current);
        }
        return difference;
    }

    private void calculateKeyChanges(Multimap leftMultimap, Multimap rightMultimap, List<EntryChange> changes){
        for (Object addedKey : Multimaps.keysDifference(rightMultimap, leftMultimap)){
            Collection difference = difference(rightMultimap.get(addedKey), leftMultimap.get(addedKey));
            for (Object addedValue : difference){
                changes.add(new EntryAdded(addedKey, addedValue));
            }
        }
        for (Object removedKey : Multimaps.keysDifference(leftMultimap, rightMultimap)){
            for (Object removedValue : difference(leftMultimap.get(removedKey), rightMultimap.get(removedKey))){
                changes.add(new EntryRemoved(removedKey, removedValue));
            }
        }
    }

    private void calculateValueChanges(List<EntryChange> changes, Object commonKey, Collection leftVal, Collection rightVal){
        if (Objects.equals(leftVal, rightVal)){
            return;
        }
        final Collection valuesRemoved = difference(leftVal, rightVal);
        if(valuesRemoved.size() > 0){
            for (Object addedValue : valuesRemoved){
                changes.add( new EntryRemoved(commonKey, addedValue));
            }
        }
        final Collection valuesAdded = difference(rightVal, leftVal);
        if(valuesAdded.size() > 0){
            for (Object addedValue : valuesAdded){
                changes.add( new EntryAdded(commonKey, addedValue));
            }
        }
    }
}
