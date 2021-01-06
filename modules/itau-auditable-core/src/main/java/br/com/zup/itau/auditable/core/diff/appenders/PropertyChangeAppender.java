package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChange;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;

/**
 * Property-scope comparator,
 * follows Chain-of-responsibility pattern.
 * <br/><br/>
 *
 * Implementation should calculate diff between two property values
 *
 * @author bartosz walacik
 */
public interface PropertyChangeAppender<T extends PropertyChange> {
    int HIGH_PRIORITY = 1;
    int LOW_PRIORITY = 2;

    /**
     * Checks if given property type is supported
     */
    boolean supports(ItauAuditableType propertyType);

    T calculateChanges(NodePair pair, ItauAuditableProperty property);

    default int priority() {
        return LOW_PRIORITY;
    }
}
