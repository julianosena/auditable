package br.com.zup.itau.auditable.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
class ItauAuditableFieldFactory {

    private final Class methodSource;

    public ItauAuditableFieldFactory(Class methodSource) {
        this.methodSource = methodSource;
    }

    public List<ItauAuditableField> getAllFields(){
        List<ItauAuditableField> fields = new ArrayList<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = methodSource;
        while (clazz != null && clazz != Object.class)  {
            context.addTypeSubstitutions(clazz);

            for (Field f : clazz.getDeclaredFields()){
                fields.add(createJField(f,context));
            }

            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    private ItauAuditableField createJField(Field rawField, TypeResolvingContext context){
        Type actualType = context.getSubstitution(rawField.getGenericType());
        return new ItauAuditableField(rawField, actualType);
    }
}
