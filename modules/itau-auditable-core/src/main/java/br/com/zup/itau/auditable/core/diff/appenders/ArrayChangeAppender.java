package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.common.collections.Arrays;
import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.container.ArrayChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryChange;
import br.com.zup.itau.auditable.core.metamodel.type.*;

import java.util.List;
import java.util.Map;

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppender implements PropertyChangeAppender<ArrayChange>{
    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    ArrayChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return  propertyType instanceof ArrayType;
    }

    @Override
    public ArrayChange calculateChanges(NodePair pair, ItauAuditableProperty property) {

        Map leftMap =  Arrays.asMap(pair.getLeftDehydratedPropertyValueAndSanitize(property));
        Map rightMap = Arrays.asMap(pair.getRightDehydratedPropertyValueAndSanitize(property));

        ArrayType arrayType = property.getType();
        MapContentType mapContentType = typeMapper.getMapContentType(arrayType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(leftMap, rightMap, mapContentType);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new ArrayChange(pair.createPropertyChangeMetadata(property), elementChanges);
        }
        else {
            return null;
        }
    }
}
