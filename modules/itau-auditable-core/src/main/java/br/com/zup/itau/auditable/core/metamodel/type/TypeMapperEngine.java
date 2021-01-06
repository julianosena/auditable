package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.collections.Primitives;
import br.com.zup.itau.auditable.common.collections.WellKnownValueTypes;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.reflection.ReflectionUtil;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;

/**
 * thread-safe, mutable state of ItauAuditableTypes mapping
 */
class TypeMapperEngine {

    private final Map<String, ItauAuditableType> mappedTypes = new ConcurrentHashMap<>();
    private final Map<DuckType, Class> mappedTypeNames = new ConcurrentHashMap<>();

    private void putIfAbsent(Type javaType, final ItauAuditableType jType) {
        Validate.argumentsAreNotNull(javaType, jType);
        if (contains(javaType)) {
            return;
        }

        addFullMapping(javaType, jType);
    }

    private void putWithOverwrite(Type javaType, final ItauAuditableType jType) {
        Validate.argumentsAreNotNull(javaType, jType);
        addFullMapping(javaType, jType);
    }

    void registerCoreTypes(ListCompareAlgorithm listCompareAlgorithm){
        //primitives & boxes
        for (Class primitiveOrBox : Primitives.getPrimitiveAndBoxTypes()) {
            registerCoreType(new PrimitiveType(primitiveOrBox));
        }

        registerCoreType(new PrimitiveType(Enum.class));

        //array
        registerCoreType(new ArrayType(Object[].class));

        //well known Value types
        for (Class valueType : WellKnownValueTypes.getValueTypes()) {
            registerCoreType(new ValueType(valueType));
        }

        //Collections
        registerCoreType(new CollectionType(Collection.class));
        registerCoreType(new SetType(Set.class));
        if (listCompareAlgorithm == ListCompareAlgorithm.AS_SET) {
            registerCoreType(new ListAsSetType(List.class));
        } else {
            registerCoreType(new ListType(List.class));
        }
        registerCoreType(new OptionalType());

        //& Maps
        registerCoreType(new MapType(Map.class));
    }

    void registerExplicitType(ItauAuditableType itauAuditableType) {
        putWithOverwrite(itauAuditableType.getBaseJavaType(), itauAuditableType);
    }

    private void registerCoreType(ItauAuditableType jType) {
        putIfAbsent(jType.getBaseJavaType(), jType);
    }

    ItauAuditableType computeIfAbsent(Type javaType, Function<Type, ItauAuditableType> computeFunction) {
        ItauAuditableType itauAuditableType = get(javaType);
        if (itauAuditableType != null) {
            return itauAuditableType;
        }

        synchronized (javaType) {
            //map.contains double check
            ItauAuditableType mappedType = get(javaType);
            if (mappedType != null) {
                return mappedType;
            }

            ItauAuditableType newType = computeFunction.apply(javaType);
            addFullMapping(javaType, newType);
            return newType;
        }
    }

    private void addFullMapping(Type javaType, ItauAuditableType newType){
        Validate.argumentsAreNotNull(javaType, newType);

        mappedTypes.put(javaType.toString(), newType);

        if (newType instanceof ManagedType){
            ManagedType managedType = (ManagedType)newType;
            mappedTypeNames.put(new DuckType(managedType.getName()), ReflectionUtil.extractClass(javaType));
            mappedTypeNames.put(new DuckType(managedType), ReflectionUtil.extractClass(javaType));
        }
    }

    ItauAuditableType get(Type javaType) {
        return mappedTypes.get(javaType.toString());
    }

    boolean contains(Type javaType) {
        return get(javaType) != null;
    }

    /**
     * @throws ItauAuditableException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    Class getClassByTypeName(String typeName) {
        return getClassByDuckType(new DuckType(typeName));
    }

    /**
     * @throws ItauAuditableException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    Class getClassByDuckType(DuckType duckType) {
        argumentsAreNotNull(duckType);

        Class javaType = mappedTypeNames.get(duckType);
        if (javaType != null){
            return javaType;
        }

        synchronized (duckType.getTypeName()) {
            Optional<? extends Class> classForName = parseClass(duckType.getTypeName());
            if (classForName.isPresent()) {
                mappedTypeNames.put(duckType, classForName.get());
                return classForName.get();
            }
        }

        //try to fallback to bare typeName when properties doesn't match
        if (!duckType.isBare()){
            return getClassByDuckType(duckType.bareCopy());
        }

        throw new ItauAuditableException(ItauAuditableExceptionCode.TYPE_NAME_NOT_FOUND, duckType.getTypeName());
    }

    private Optional<? extends Class> parseClass(String qualifiedName){
        try {
            return Optional.of( this.getClass().forName(qualifiedName) );
        }
        catch (ClassNotFoundException e){
            return Optional.empty();
        }
    }

}
