package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.core.metamodel.type.ListType;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class SimpleListChangeAppender extends ListToMapAppenderAdapter {

    SimpleListChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        super(mapChangeAppender, typeMapper);
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ItauAuditableProperty property) {
        List leftList = (List) leftValue;
        List rightList = (List) rightValue;

        return super.calculateChangesInList(leftList, rightList, pair, property);
    }
}
