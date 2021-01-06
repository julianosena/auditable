package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.reflection.ReflectionUtil;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.core.metamodel.clazz.ClientsClassDefinition;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static br.com.zup.itau.auditable.common.reflection.ReflectionUtil.extractClass;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentIsNotNull;

/**
 * Maps Java types into ItauAuditable types
 *
 * @author bartosz walacik
 */
public class TypeMapper {
    static final Logger logger = LoggerFactory.getLogger("br.com.zup.itau.auditable.TypeMapper");
    static final ValueType OBJECT_TYPE = new ValueType(Object.class);

    private final TypeMapperEngine engine = new TypeMapperEngine();
    private final TypeFactory typeFactory;

    private final DehydratedTypeFactory dehydratedTypeFactory = new DehydratedTypeFactory(this);

    public TypeMapper(ClassScanner classScanner, ItauAuditableCoreConfiguration javersCoreConfiguration, DynamicMappingStrategy dynamicMappingStrategy) {
        //Pico doesn't support cycles, so manual construction
        TypeFactory typeFactory = new TypeFactory(classScanner, this, dynamicMappingStrategy);

        engine.registerCoreTypes(javersCoreConfiguration.getListCompareAlgorithm());
        this.typeFactory = typeFactory;
    }

    /**
     * For TypeMapperConcurrentTest only,
     * no better idea how to writhe this test without additional constructor
     */
    @Deprecated
    protected TypeMapper(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    public MapContentType getMapContentType(KeyValueType mapType){
        ItauAuditableType keyType = getItauAuditableType(mapType.getKeyType());
        ItauAuditableType valueType = getItauAuditableType(mapType.getValueType());
        return new MapContentType(keyType, valueType);
    }

    /**
     * only for change appenders
     */
    public MapContentType getMapContentType(ContainerType containerType){
        ItauAuditableType keyType = getItauAuditableType(Integer.class);
        ItauAuditableType valueType = getItauAuditableType(containerType.getItemType());
        return new MapContentType(keyType, valueType);
    }

    public ItauAuditableType getContainerItemType(ItauAuditableProperty property) {
        ContainerType containerType = property.getType();
        return getItauAuditableType(containerType.getItemType());
    }

    /**
     * is Set, List or Array of ManagedClasses
     */
    public boolean isContainerOfManagedTypes(ItauAuditableType javersType){
        if (! (javersType instanceof ContainerType)) {
            return false;
        }

        return getItauAuditableType(((ContainerType)javersType).getItemType()) instanceof ManagedType;
    }

    /**
     * is Map (or Multimap) with ManagedClass on Key or Value position
     */
    public boolean isKeyValueTypeWithManagedTypes(ItauAuditableType enumerableType) {
        if (enumerableType instanceof KeyValueType){
            KeyValueType mapType = (KeyValueType)enumerableType;

            ItauAuditableType keyType = getItauAuditableType(mapType.getKeyType());
            ItauAuditableType valueType = getItauAuditableType(mapType.getValueType());

            return keyType instanceof ManagedType || valueType instanceof ManagedType;
        } else{
            return false;
        }
    }

    public boolean isManagedType(ItauAuditableType javersType){
        return javersType instanceof ManagedType;
    }

    public boolean isEnumerableOfManagedTypes(ItauAuditableType javersType){
        return isContainerOfManagedTypes(javersType) || isKeyValueTypeWithManagedTypes(javersType);
    }

    /**
     * Returns mapped type, spawns a new one from a prototype,
     * or infers a new one using default mapping.
     */
    public ItauAuditableType getItauAuditableType(Type javaType) {
        argumentIsNotNull(javaType);

        if (javaType == Object.class) {
            return OBJECT_TYPE;
        }

        return engine.computeIfAbsent(javaType, j -> typeFactory.infer(j, findPrototype(j)));
    }

    public boolean isShallowReferenceType(Type javaType) {
        return getItauAuditableType(javaType) instanceof ShallowReferenceType;
    }

    public ClassType getItauAuditableClassType(Type javaType) {
        argumentIsNotNull(javaType);
        ItauAuditableType jType = getItauAuditableType(javaType);

        if (jType instanceof ClassType) {
            return (ClassType) jType;
        }

        throw new ItauAuditableException(ItauAuditableExceptionCode.CLASS_MAPPING_ERROR,
                    javaType,
                    jType.getClass().getSimpleName(),
                    ClassType.class.getSimpleName());
    }

    /**
     * @throws ItauAuditableException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public ManagedType getItauAuditableManagedType(GlobalId globalId){
        return getItauAuditableManagedType(engine.getClassByTypeName(globalId.getTypeName()), ManagedType.class);
    }

    /**
     * @throws ItauAuditableException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public <T extends ManagedType> T getItauAuditableManagedType(String typeName, Class<T> expectedType) {
        return getItauAuditableManagedType(engine.getClassByTypeName(typeName), expectedType);
    }

    /**
     * for tests only
     */
    private <T extends ManagedType> T getItauAuditableManagedType(String typeName) {
        return (T)getItauAuditableManagedType(engine.getClassByTypeName(typeName), ManagedType.class);
    }

    /**
     * @throws ItauAuditableException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public <T extends ManagedType> T getItauAuditableManagedType(DuckType duckType, Class<T> expectedType) {
        return getItauAuditableManagedType(engine.getClassByDuckType(duckType), expectedType);
    }

    /**
     * If given javaClass is mapped to ManagedType, returns its ItauAuditableType
     *
     * @throws ItauAuditableException MANAGED_CLASS_MAPPING_ERROR
     */
    public ManagedType getItauAuditableManagedType(Class javaType) {
        return getItauAuditableManagedType(javaType, ManagedType.class);
    }

    /**
     * If given javaClass is mapped to expected ManagedType, returns its ItauAuditableType
     *
     * @throws ItauAuditableException MANAGED_CLASS_MAPPING_ERROR
     */
    public <T extends ManagedType> T getItauAuditableManagedType(Class javaClass, Class<T> expectedType) {
        ItauAuditableType mType = getItauAuditableType(javaClass);

        if (expectedType.isAssignableFrom(mType.getClass())) {
            return (T) mType;
        } else {
            throw new ItauAuditableException(ItauAuditableExceptionCode.MANAGED_CLASS_MAPPING_ERROR,
                    javaClass,
                    mType.getClass().getSimpleName(),
                    expectedType.getSimpleName());
        }
    }

    public <T extends ManagedType> Optional<T> getItauAuditableManagedTypeMaybe(String typeName, Class<T> expectedType) {
        return getItauAuditableManagedTypeMaybe(new DuckType(typeName), expectedType);
    }

    public <T extends ManagedType> Optional<T> getItauAuditableManagedTypeMaybe(DuckType duckType, Class<T> expectedType) {
        try {
            return Optional.of(getItauAuditableManagedType(duckType, expectedType));
        } catch (ItauAuditableException e) {
            if (ItauAuditableExceptionCode.TYPE_NAME_NOT_FOUND == e.getCode()) {
                return Optional.empty();
            }
            if (ItauAuditableExceptionCode.MANAGED_CLASS_MAPPING_ERROR == e.getCode()) {
                return Optional.empty();
            }
            throw e;
        }
    }

    public <T extends ItauAuditableType> T getPropertyType(Property property){
        argumentIsNotNull(property);
        try {
            return (T) getItauAuditableType(property.getGenericType());
        }catch (ItauAuditableException e) {
            logger.error("Can't calculate ItauAuditableType for property: {}", property);
            throw e;
        }
    }

    public void registerClientsClass(ClientsClassDefinition def) {
        ItauAuditableType newType = typeFactory.create(def);

        logger.debug("javersType of '{}' " + "mapped explicitly to {}",
                def.getBaseJavaClass().getSimpleName(), newType.getClass().getSimpleName());

        engine.registerExplicitType(newType);
    }

    /**
     * Dehydrated type for JSON representation
     */
    public Type getDehydratedType(Type type) {
        return dehydratedTypeFactory.build(type);
    }

    public void addPluginTypes(Collection<ItauAuditableType> jTypes) {
        Validate.argumentIsNotNull(jTypes);
        for (ItauAuditableType t : jTypes) {
            engine.registerExplicitType(t);
        }
    }

    boolean contains(Type javaType){
        return engine.contains(javaType);
    }

    private Optional<ItauAuditableType> findPrototype(Type javaType) {
        if (javaType instanceof TypeVariable) {
            return Optional.empty();
        }

        Class javaClass = extractClass(javaType);

        //this is due too spoiled Java Array reflection API
        if (javaClass.isArray()) {
            return Optional.of(getItauAuditableType(Object[].class));
        }

        ItauAuditableType selfClassType = engine.get(javaClass);
        if (selfClassType != null && javaClass != javaType){
            return  Optional.of(selfClassType); //returns rawType for ParametrizedTypes
        }

        List<Type> hierarchy = ReflectionUtil.calculateHierarchyDistance(javaClass);

        for (Type parent : hierarchy) {
            ItauAuditableType jType = engine.get(parent);
            if (jType != null && jType.canBePrototype()) {
                logger.debug("proto for {} -> {}", javaType, jType);
                return Optional.of(jType);
            }
        }

        return Optional.empty();
    }
}
