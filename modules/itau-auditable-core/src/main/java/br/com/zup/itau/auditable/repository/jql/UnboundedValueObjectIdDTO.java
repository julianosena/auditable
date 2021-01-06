package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.graph.LiveGraphFactory;

/**
 * @author bartosz walacik
 */
public final class UnboundedValueObjectIdDTO extends GlobalIdDTO {
    private final Class  javaClass;

    UnboundedValueObjectIdDTO(Class javaClass) {
        Validate.argumentsAreNotNull(javaClass);
        this.javaClass = javaClass;
    }

    public static UnboundedValueObjectIdDTO unboundedMapId(){
        return new UnboundedValueObjectIdDTO(LiveGraphFactory.getMapWrapperType());
    }

    public static UnboundedValueObjectIdDTO unboundedSetId(){
        return new UnboundedValueObjectIdDTO(LiveGraphFactory.getSetWrapperType());
    }

    public static UnboundedValueObjectIdDTO unboundedListId(){
        return new UnboundedValueObjectIdDTO(LiveGraphFactory.getListWrapperType());
    }

    public static UnboundedValueObjectIdDTO unboundedArrayId(){
        return new UnboundedValueObjectIdDTO(LiveGraphFactory.getArrayWrapperType());
    }

    public static UnboundedValueObjectIdDTO unboundedValueObjectId(Class valueObjectClass) {
        return new UnboundedValueObjectIdDTO(valueObjectClass);
    }

    public Class getVoClass() {
        return javaClass;
    }

    @Override
    public String value() {
        return javaClass.getName()+"/";
    }
}