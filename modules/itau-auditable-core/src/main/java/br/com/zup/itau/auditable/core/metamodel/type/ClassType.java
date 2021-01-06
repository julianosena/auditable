package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.reflection.ReflectionUtil;
import br.com.zup.itau.auditable.common.validation.Validate;

import java.lang.reflect.Type;
import java.util.Optional;

import static br.com.zup.itau.auditable.common.validation.Validate.argumentIsNotNull;

/**
 * Class or ParametrizedType
 */
abstract class ClassType extends ItauAuditableType {

    private final Class baseJavaClass;

    ClassType(Type baseJavaType) {
        this(baseJavaType, Optional.empty());
    }

    ClassType(Type baseJavaType, Optional<String> name) {
        this(baseJavaType, name, 0);
    }

    ClassType(Type baseJavaType, Optional<String> name, int expectedArgs) {
        super(baseJavaType, name, expectedArgs);
        Validate.argumentIsNotNull(name);
        this.baseJavaClass = ReflectionUtil.extractClass(baseJavaType);
    }

    @Override
    public boolean canBePrototype() {
        return true;
    }

    @Override
    public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);
        return baseJavaClass.isAssignableFrom(cdo.getClass());
    }

    /**
     * Type for JSON representation.
     *
     * For Values it's simply baseJavaType.
     *
     * For ManagedTypes (references to Entities and ValueObjects) it's GlobalId
     * because Itaú Auditable serializes references in the 'dehydrated' form.
     */
    protected Type getRawDehydratedType() {
        return baseJavaClass;
    }

    public Class getBaseJavaClass() {
        return baseJavaClass;
    }
}
