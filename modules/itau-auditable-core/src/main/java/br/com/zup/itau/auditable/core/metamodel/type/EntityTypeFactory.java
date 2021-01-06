package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.metamodel.clazz.EntityDefinition;
import br.com.zup.itau.auditable.core.metamodel.scanner.ClassScan;

import java.util.List;

/**
 * @author bartosz.walacik
 */
class EntityTypeFactory {
    private final ManagedClassFactory managedClassFactory;

    EntityTypeFactory(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;
    }

    EntityType createEntity(EntityDefinition definition, ClassScan scan) {
        ManagedClass managedClass = managedClassFactory.create(definition, scan);

        List<ItauAuditableProperty> idProperties;
        if (definition.hasExplicitId()) {
            idProperties = managedClass.getProperties(definition.getIdPropertyNames());
        } else {
            idProperties = findDefaultIdProperties(managedClass, definition.isShallowReference());
        }

        if (definition.isShallowReference()) {
            return new ShallowReferenceType(managedClass, idProperties, definition.getTypeName());
        } else {
            return new EntityType(managedClass, idProperties, definition.getTypeName());
        }
    }

    /**
     * @throws ItauAuditableException ENTITY_WITHOUT_ID
     */
    private List<ItauAuditableProperty> findDefaultIdProperties(ManagedClass managedClass, boolean isShallowReference) {
        if (managedClass.getLooksLikeId().isEmpty()) {
            ItauAuditableExceptionCode code = isShallowReference ?
                    ItauAuditableExceptionCode.SHALLOW_REF_ENTITY_WITHOUT_ID :
                    ItauAuditableExceptionCode.ENTITY_WITHOUT_ID;
            throw new ItauAuditableException(code, managedClass.getBaseJavaClass().getName());
        }
        return managedClass.getLooksLikeId();
    }
}
