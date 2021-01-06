package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.metamodel.type.CollectionType;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.Collection;
import java.util.List;

class CollectionAsListChangeAppender extends ListToMapAppenderAdapter  {

    CollectionAsListChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        super(mapChangeAppender, typeMapper);
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType.getClass() == CollectionType.class;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ItauAuditableProperty property) {
        List leftList = Lists.immutableListOf((Collection)leftValue);
        List rightList = Lists.immutableListOf((Collection)rightValue);

        return super.calculateChangesInList(leftList, rightList, pair, property);
    }
}
