package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.SetChange;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.core.metamodel.type.ListAsSetType;

/**
 * @author Sergey Kobyshev
 */
public class ListAsSetChangeAppender implements PropertyChangeAppender<ListChange> {

    private final SetChangeAppender setChangeAppender;

    ListAsSetChangeAppender(SetChangeAppender setChangeAppender) {
        this.setChangeAppender = setChangeAppender;
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType instanceof ListAsSetType;
    }

    @Override
    public ListChange calculateChanges(NodePair pair, ItauAuditableProperty property) {
        SetChange setChange = setChangeAppender.calculateChanges(pair, property);

        if (setChange != null) {
            return new ListChange(pair.createPropertyChangeMetadata(property), setChange.getChanges());
        }
        return null;
    }
}
