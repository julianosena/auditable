package br.com.zup.itau.auditable.core.metamodel.type;

import java.lang.reflect.Type;

/**
 * All properties with IgnoredType are ignored by Itaú Auditable engine
 *
 * @author bartosz.walacik
 */
public class IgnoredType extends ClassType {

    public IgnoredType(Type baseJavaType) {
        super(baseJavaType);
    }
}
