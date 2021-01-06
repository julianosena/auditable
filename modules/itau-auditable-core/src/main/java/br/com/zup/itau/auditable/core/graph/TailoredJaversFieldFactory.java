package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.reflection.ItauAuditableField;
import br.com.zup.itau.auditable.core.metamodel.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
class TailoredItauAuditableFieldFactory extends TailoredItauAuditableMemberFactory {

    @Override
    public ItauAuditableField create(final Property primaryProperty, final Class<?> genericItemClass) {
        return new ItauAuditableField((Field) primaryProperty.getMember().getRawMember(), null) {
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
