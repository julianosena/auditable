package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.reflection.ItauAuditableGetter;
import br.com.zup.itau.auditable.core.metamodel.property.Property;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
class TailoredItauAuditableMethodFactory extends TailoredItauAuditableMemberFactory {

    @Override
    public ItauAuditableGetter create(final Property primaryProperty, final Class<?> genericItemClass) {
        return new ItauAuditableGetter((Method) primaryProperty.getMember().getRawMember(), null) {
            @Override
            public Type getGenericResolvedType() {
                return parametrizedType(primaryProperty, genericItemClass);
            }

            @Override
            protected Type getRawGenericType() {
                return genericItemClass;
            }
        };
    }
}
