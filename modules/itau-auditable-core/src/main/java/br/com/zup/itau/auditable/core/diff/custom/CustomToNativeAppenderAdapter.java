package br.com.zup.itau.auditable.core.diff.custom;

import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.appenders.PropertyChangeAppender;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChange;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;

/**
 * @author bartosz walacik
 */
public class CustomToNativeAppenderAdapter<T, C extends PropertyChange> implements PropertyChangeAppender<C> {
    private final CustomPropertyComparator<T, C> delegate;
    private final Class<T> propertyJavaClass;

    public CustomToNativeAppenderAdapter(CustomPropertyComparator<T, C> delegate, Class<T> propertyJavaClass) {
        this.delegate = delegate;
        this.propertyJavaClass = propertyJavaClass;
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType.getBaseJavaType().equals(propertyJavaClass);
    }

    @Override
    public C calculateChanges(NodePair pair, ItauAuditableProperty property) {
        T leftValue = (T)pair.getLeftPropertyValue(property);
        T rightValue = (T)pair.getRightPropertyValue(property);

        return delegate.compare(leftValue, rightValue, pair.createPropertyChangeMetadata(property), property).orElse(null);
    }

    @Override
    public int priority() {
        return HIGH_PRIORITY;
    }
}
