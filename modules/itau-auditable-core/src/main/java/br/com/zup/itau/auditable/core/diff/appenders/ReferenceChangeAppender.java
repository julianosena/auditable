package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.ReferenceChange;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;

import java.util.Objects;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
class ReferenceChangeAppender implements PropertyChangeAppender<ReferenceChange> {

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType instanceof ManagedType;
    }

    @Override
    public ReferenceChange calculateChanges(NodePair pair, ItauAuditableProperty property) {
        GlobalId leftId = pair.getLeftReference(property);
        GlobalId rightId = pair.getRightReference(property);

        if (Objects.equals(leftId, rightId)) {
            return null;
        }

        return new ReferenceChange(pair.createPropertyChangeMetadata(property), leftId, rightId,
                pair.getLeftPropertyValue(property),
                pair.getRightPropertyValue(property));
    }
}
