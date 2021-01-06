package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryChange;
import br.com.zup.itau.auditable.core.metamodel.type.CollectionType;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.MapContentType;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.List;

import static br.com.zup.itau.auditable.common.collections.Lists.asMap;

abstract class ListToMapAppenderAdapter extends CorePropertyChangeAppender<ListChange> {
    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    ListToMapAppenderAdapter(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    ListChange calculateChangesInList(List leftList, List rightList, NodePair pair, ItauAuditableProperty property) {
        CollectionType listType = ((ItauAuditableProperty) property).getType();
        MapContentType mapContentType = typeMapper.getMapContentType(listType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(asMap(leftList), asMap(rightList), mapContentType);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            renderNotParametrizedWarningIfNeeded(listType.getItemType(), "item", "List", property);
            return new ListChange(pair.createPropertyChangeMetadata(property), elementChanges);
        }
        else {
            return null;
        }
    }
}
