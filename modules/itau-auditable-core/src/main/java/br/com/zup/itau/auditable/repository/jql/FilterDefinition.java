package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.collections.Sets;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.string.ToStringBuilder;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalIdFactory;
import br.com.zup.itau.auditable.core.metamodel.type.EntityType;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.Set;

abstract class FilterDefinition {

    abstract Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper);

    static class IdFilterDefinition extends FilterDefinition {
        private final GlobalIdDTO globalIdDTO;

        IdFilterDefinition(GlobalIdDTO globalIdDTO) {
            this.globalIdDTO = globalIdDTO;
        }

        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new IdFilter(globalIdFactory.createFromDto(globalIdDTO));
        }
    }

    static class IdAndTypeNameFilterDefinition extends FilterDefinition {
        private final Object localId;
        private final String typeName;

        IdAndTypeNameFilterDefinition(Object localId, String typeName) {
            Validate.argumentsAreNotNull(localId, typeName);
            this.localId = localId;
            this.typeName = typeName;
        }

        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new IdFilter(globalIdFactory.createInstanceId(localId, typeName));
        }
    }

    static class ClassFilterDefinition extends FilterDefinition {
        private final Set<Class> requiredClasses;

        ClassFilterDefinition(Set<Class> requiredClasses) {
            this.requiredClasses = requiredClasses;
        }

        @Override
        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new ClassFilter(Sets.transform(requiredClasses, javaClass -> typeMapper.getItauAuditableManagedType(javaClass)));
        }
    }

    static class InstanceFilterDefinition extends  FilterDefinition {
        private final Object instance;

        InstanceFilterDefinition(Object instance) {
            this.instance = instance;
        }

        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            try {
                return new IdFilter(globalIdFactory.createId(instance));
            } catch (ItauAuditableException e) {
                if (e.getCode() == ItauAuditableExceptionCode.MANAGED_CLASS_MAPPING_ERROR) {
                    throw new ItauAuditableException(ItauAuditableExceptionCode.MALFORMED_JQL,
                        "object passed to byInstance(Object) query should be instance of Entity or ValueObject, got "+typeMapper.getItauAuditableType(instance.getClass()) + " - " +ToStringBuilder.format(instance)+".\nDid you mean byInstanceId(Object localId, Class entityClass)?");
                }
                else {
                    throw e;
                }
            }
        }
    }

    static class VoOwnerFilterDefinition extends FilterDefinition {
        private final Class ownerEntityClass;
        private final String path;

        VoOwnerFilterDefinition(Class ownerEntityClass, String path) {
            this.ownerEntityClass = ownerEntityClass;
            this.path = path;
        }

        @Override
        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            ManagedType mType = typeMapper.getItauAuditableManagedType(ownerEntityClass);

            if (! (mType instanceof EntityType)) {
                throw new ItauAuditableException(
                        ItauAuditableExceptionCode.MALFORMED_JQL, "queryForChanges: ownerEntityClass {'"+ownerEntityClass.getName()+"'} should be an Entity");
            }

            return new VoOwnerFilter((EntityType)mType, path);
        }
    }

    static class AnyDomainObjectFilterDefinition extends FilterDefinition {
        @Override
        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new AnyDomainObjectFilter();
        }
    }
}
