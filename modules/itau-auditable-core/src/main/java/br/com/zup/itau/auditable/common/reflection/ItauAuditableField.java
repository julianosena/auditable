package br.com.zup.itau.auditable.common.reflection;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static br.com.zup.itau.auditable.common.string.ToStringBuilder.typeName;

/**
 * @author bartosz walacik
 */
public class ItauAuditableField extends ItauAuditableMember<Field> {

    protected ItauAuditableField(Field rawField, Type resolvedReturnType) {
        super(rawField, resolvedReturnType);
    }

    @Override
    protected Type getRawGenericType() {
        return getRawMember().getGenericType();
    }

    @Override
    public Class<?> getRawType() {
        return getRawMember().getType();
    }

    @Override
    public Object getEvenIfPrivate(Object onObject) {
        try {
            return getRawMember().get(onObject);
        } catch (IllegalArgumentException ie) {
            return getOnMissingProperty(onObject);
        } catch (IllegalAccessException e) {
            throw new ItauAuditableException(ItauAuditableExceptionCode.PROPERTY_ACCESS_ERROR,
                  this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public void setEvenIfPrivate(Object onObject, Object value) {
        try {
            getRawMember().set(onObject, value);
        } catch (IllegalArgumentException ie){
            String valueType = value == null ? "null" : value.getClass().getName();
            throw new ItauAuditableException(ItauAuditableExceptionCode.PROPERTY_SETTING_ERROR, valueType, this, ie.getClass().getName() + " - " + ie.getMessage());
        } catch (IllegalAccessException e) {
            throw new ItauAuditableException(ItauAuditableExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public String memberType() {
        return "Field";
    }
}
