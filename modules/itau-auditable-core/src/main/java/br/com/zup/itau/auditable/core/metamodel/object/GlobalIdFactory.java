package br.com.zup.itau.auditable.core.metamodel.object;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.string.ToStringBuilder;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.graph.ObjectAccessHook;
import br.com.zup.itau.auditable.core.graph.ObjectAccessProxy;
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectIdWithHash.ValueObjectIdWithPlaceholder;
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectIdWithHash.ValueObjectIdWithPlaceholderOnParent;
import br.com.zup.itau.auditable.core.metamodel.type.EntityType;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;
import br.com.zup.itau.auditable.core.metamodel.type.ValueObjectType;
import br.com.zup.itau.auditable.repository.jql.GlobalIdDTO;
import br.com.zup.itau.auditable.repository.jql.InstanceIdDTO;
import br.com.zup.itau.auditable.repository.jql.UnboundedValueObjectIdDTO;
import br.com.zup.itau.auditable.repository.jql.ValueObjectIdDTO;

import java.util.Optional;

/**
 * @author bartosz walacik
 */
public class GlobalIdFactory {
    private final TypeMapper typeMapper;
    private ObjectAccessHook objectAccessHook;
    private final GlobalIdPathParser pathParser;

    public GlobalIdFactory(TypeMapper typeMapper, ObjectAccessHook objectAccessHook) {
        this.typeMapper = typeMapper;
        this.objectAccessHook = objectAccessHook;
        this.pathParser = new GlobalIdPathParser(typeMapper);
    }

    public GlobalId createId(Object targetCdo) {
        return createId(targetCdo, null);
    }

    /**
     * @param ownerContext for bounded ValueObjects, optional
     */
    public GlobalId createId(Object targetCdo, OwnerContext ownerContext) {
        Validate.argumentsAreNotNull(targetCdo);

        Optional<ObjectAccessProxy> cdoProxy = objectAccessHook.createAccessor(targetCdo);

        Class<?> targetClass = cdoProxy.map((p) -> p.getTargetClass()).orElse(targetCdo.getClass());
        ManagedType targetManagedType = typeMapper.getItauAuditableManagedType(targetClass);

        if (targetManagedType instanceof EntityType) {
            if (cdoProxy.isPresent() && cdoProxy.get().getLocalId().isPresent()){
                return createInstanceId(cdoProxy.get().getLocalId().get(), targetClass);
            }
            else {
                return ((EntityType) targetManagedType).createIdFromInstance(targetCdo);
            }
        }

        if (targetManagedType instanceof ValueObjectType && !hasOwner(ownerContext)) {
            return new UnboundedValueObjectId(targetManagedType.getName());
        }

        if (targetManagedType instanceof ValueObjectType && hasOwner(ownerContext)) {
            String pathFromRoot = createPathFromRoot(ownerContext.getOwnerId(), ownerContext.getPath());

            if (ownerContext.requiresObjectHasher()) {
                return new ValueObjectIdWithPlaceholder(targetManagedType.getName(),
                        getRootOwnerId(ownerContext),
                        pathFromRoot);
            } else if (ownerContext.getOwnerId() instanceof ValueObjectIdWithHash) {
                return new ValueObjectIdWithPlaceholderOnParent(targetManagedType.getName(),
                        (ValueObjectIdWithHash)ownerContext.getOwnerId(),
                        ownerContext.getPath());
            }
            else {
                return new ValueObjectId(targetManagedType.getName(), getRootOwnerId(ownerContext), pathFromRoot);
            }
        }

        throw new ItauAuditableException(ItauAuditableExceptionCode.NOT_IMPLEMENTED);
    }

    private GlobalId getRootOwnerId(OwnerContext ownerContext) {
        if (ownerContext.getOwnerId() instanceof ValueObjectId){
            return ((ValueObjectId)ownerContext.getOwnerId()).getOwnerId();
        } else{
            return ownerContext.getOwnerId();
        }
    }

    private String createPathFromRoot(GlobalId parentId, String fragment) {
        if (parentId instanceof ValueObjectId){
           return ((ValueObjectId)parentId).getFragment()+"/"+fragment;
        } else{
           return fragment;
        }
    }

    public UnboundedValueObjectId createUnboundedValueObjectId(Class valueObjectClass){
        ValueObjectType valueObject = typeMapper.getItauAuditableManagedType(valueObjectClass, ValueObjectType.class);
        return new UnboundedValueObjectId(valueObject.getName());
    }

    @Deprecated
    public ValueObjectId createValueObjectIdFromPath(GlobalId owner, String fragment){
        ManagedType ownerType = typeMapper.getItauAuditableManagedType(owner);
        ValueObjectType valueObjectType = pathParser.parseChildValueObject(ownerType,fragment);
        return new ValueObjectId(valueObjectType.getName(), owner, fragment);
    }

    public InstanceId createIdFromInstance(Object instance) {
        EntityType entityType = typeMapper.getItauAuditableManagedType(instance.getClass(), EntityType.class);
        return entityType.createIdFromInstance(instance);
    }

    public InstanceId createInstanceId(Object localId, Class entityClass) {
        EntityType entity = typeMapper.getItauAuditableManagedType(entityClass, EntityType.class);
        return entity.createIdFromInstanceId(localId);
    }

    public InstanceId createInstanceId(Object localId, String typeName) {
        Optional<EntityType> entity = typeMapper.getItauAuditableManagedTypeMaybe(typeName, EntityType.class);
        return entity.map(e -> e.createIdFromInstanceId(localId))
                     .orElseGet(() -> new InstanceId(typeName, localId, ToStringBuilder.smartToString(localId)));
    }

    public GlobalId createFromDto(GlobalIdDTO globalIdDTO){
        if (globalIdDTO instanceof InstanceIdDTO){
            InstanceIdDTO idDTO = (InstanceIdDTO) globalIdDTO;
            return createInstanceId(idDTO.getCdoId(), idDTO.getEntity());
        }
        if (globalIdDTO instanceof UnboundedValueObjectIdDTO){
            UnboundedValueObjectIdDTO idDTO = (UnboundedValueObjectIdDTO) globalIdDTO;
            return createUnboundedValueObjectId(idDTO.getVoClass());
        }
        if (globalIdDTO instanceof ValueObjectIdDTO){
            ValueObjectIdDTO idDTO = (ValueObjectIdDTO) globalIdDTO;
            GlobalId ownerId = createFromDto(idDTO.getOwnerIdDTO());
            return createValueObjectIdFromPath(ownerId, idDTO.getPath());
        }
        throw new RuntimeException("type " + globalIdDTO.getClass() + " is not implemented");
    }

    private boolean hasOwner(OwnerContext context) {
        return context != null && context.getOwnerId() != null;
    }
}
