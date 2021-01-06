package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.common.reflection.ItauAuditableMember;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.ParametrizedDehydratedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
abstract class TailoredItauAuditableMemberFactory {

     protected abstract ItauAuditableMember create(Property primaryProperty, Class<?> genericItemClass);

     protected ParameterizedType parametrizedType(Property property, Class<?> itemClass) {
          return new ParametrizedDehydratedType(property.getRawType(), Lists.asList((Type) itemClass));
     }
}