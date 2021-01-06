package br.com.zup.itau.auditable.core.metamodel.scanner;

import br.com.zup.itau.auditable.core.metamodel.type.*;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author bartosz.walacik
 */
class TypeFromAnnotation {
    private final Optional<Class<? extends ItauAuditableType>> itauAuditableType;

    TypeFromAnnotation(Class<? extends Annotation> itauAuditableTypeAnnotation) {
        if (itauAuditableTypeAnnotation == ItauAuditableAnnotationsNameSpace.VALUE_ANN) {
            itauAuditableType = Optional.of(ValueType.class);
        } else
        if (itauAuditableTypeAnnotation == ItauAuditableAnnotationsNameSpace.VALUE_OBJECT_ANN) {
            itauAuditableType = Optional.of(ValueObjectType.class);
        } else
        if (itauAuditableTypeAnnotation == ItauAuditableAnnotationsNameSpace.ENTITY_ANN) {
            itauAuditableType = Optional.of(EntityType.class);
        } else
        if (itauAuditableTypeAnnotation == ItauAuditableAnnotationsNameSpace.DIFF_IGNORE_ANN) {
            itauAuditableType = Optional.of(IgnoredType.class);
        } else
        if (itauAuditableTypeAnnotation == ItauAuditableAnnotationsNameSpace.SHALLOW_REFERENCE_ANN) {
            itauAuditableType = Optional.of(ShallowReferenceType.class);
        } else {
            itauAuditableType = Optional.empty();
        }
    }

    TypeFromAnnotation(boolean hasEntity, boolean hasValueObject, boolean hasValue) {
        if (hasEntity){
            itauAuditableType = Optional.of(EntityType.class);
        } else
        if (hasValueObject) {
            itauAuditableType = Optional.of(ValueObjectType.class);
        } else
        if (hasValue) {
            itauAuditableType = Optional.of(ValueType.class);
        } else {
            itauAuditableType = Optional.empty();
        }
    }

    boolean isValue() {
        return itauAuditableType.map(it -> it == ValueType.class).orElse(false);
    }

    boolean isValueObject() {
        return itauAuditableType.map(it -> it == ValueObjectType.class).orElse(false);
    }

    boolean isEntity() {
        return itauAuditableType.map(it -> it == EntityType.class).orElse(false);
    }

    boolean isIgnored() {
        return itauAuditableType.map(it -> it == IgnoredType.class).orElse(false);
    }

    boolean isShallowReference() {
        return itauAuditableType.map(it -> it == ShallowReferenceType.class).orElse(false);
    }
}
