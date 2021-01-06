package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.collections.Primitives;
import br.com.zup.itau.auditable.common.string.PrettyPrintBuilder;
import br.com.zup.itau.auditable.core.diff.custom.CustomValueComparator;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public abstract class PrimitiveOrValueType<T> extends ClassType implements CustomComparableType {
    private final CustomValueComparatorNullSafe<T> valueComparator;

    @Override
    public boolean hasCustomValueComparator() {
        return valueComparator != null;
    }

    PrimitiveOrValueType(Type baseJavaType) {
        this(baseJavaType, null);
    }

    PrimitiveOrValueType(Type baseJavaType, CustomValueComparator<T> comparator) {
        super(baseJavaType);
        this.valueComparator = comparator == null ? null : new CustomValueComparatorNullSafe<>(comparator);
    }

    @Override
    public boolean equals(Object left, Object right) {
        if (valueComparator != null) {
            return valueComparator.equals((T)left, (T)right);
        }
        return super.equals(left, right);
    }

    public boolean isNumber() {
        return Number.class.isAssignableFrom(getBaseJavaClass()) ||
               Primitives.isPrimitiveNumber(getBaseJavaClass());
    }

    public boolean isBoolean() {
        return Boolean.class == getBaseJavaClass() || boolean.class == getBaseJavaClass();
    }

    public boolean isStringy() {
        return String.class == getBaseJavaClass() ||
               CharSequence.class == getBaseJavaClass() ||
               char.class == getBaseJavaClass() ||
               Character.class == getBaseJavaClass();
    }

    public boolean isJsonPrimitive() {
        return isStringy() || isBoolean() || isNumber();
    }

    CustomValueComparator getValueComparator() {
        return valueComparator;
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addField("valueComparator", valueComparator);
    }
}
