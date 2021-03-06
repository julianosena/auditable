package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * Type for JSON representation. Generic version of {@link ClassType#getRawDehydratedType()}
 *
 * @author bartosz.walacik
 */
class DehydratedTypeFactory {
    private static Class GLOBAL_ID_ARRAY_TYPE = new GlobalId[]{}.getClass();

    private TypeMapper mapper;

    DehydratedTypeFactory(TypeMapper mapper) {
        this.mapper = mapper;
    }

    //recursive
    public Type build(Type givenType) {
        if (givenType instanceof TypeVariable) {
            return Object.class;
        }
        final ClassType itauAuditableType = mapper.getItauAuditableClassType(givenType);

        //for Generics, we have list of type arguments to dehydrate
        if (itauAuditableType.isGenericType()) {
            List<Type> actualDehydratedTypeArguments = extractAndDehydrateTypeArguments(itauAuditableType);
            return new ParametrizedDehydratedType(itauAuditableType.getBaseJavaClass(), actualDehydratedTypeArguments);
        }

        if (itauAuditableType instanceof ArrayType){
            Type dehydratedItemType = build( itauAuditableType.getConcreteClassTypeArguments().get(0) );
            if (dehydratedItemType == GlobalId.class){
                return GLOBAL_ID_ARRAY_TYPE;
            }
            return givenType;
        }

        return itauAuditableType.getRawDehydratedType();
    }

    private List<Type> extractAndDehydrateTypeArguments(ItauAuditableType genericType){
        return Lists.transform(genericType.getConcreteClassTypeArguments(), typeArgument -> build(typeArgument));
    }
}
