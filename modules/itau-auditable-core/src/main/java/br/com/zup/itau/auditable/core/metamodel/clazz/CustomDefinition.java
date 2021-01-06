package br.com.zup.itau.auditable.core.metamodel.clazz;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.diff.custom.CustomPropertyComparator;
import br.com.zup.itau.auditable.core.metamodel.type.CustomType;

/**
 *  Recipe for {@link CustomType}
 *
 * @author bartosz walacik
 */
public class CustomDefinition<T> extends ClientsClassDefinition {
    private CustomPropertyComparator<T, ?> comparator;

    public CustomDefinition(Class<T> clazz, CustomPropertyComparator<T, ?> comparator) {
        super(clazz);
        Validate.argumentIsNotNull(comparator);
        this.comparator = comparator;
    }

    public CustomPropertyComparator<T, ?> getComparator() {
        return comparator;
    }
}
