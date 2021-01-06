package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.validation.Validate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static br.com.zup.itau.auditable.common.collections.Lists.immutableCopyOf;
import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.PROPERTY_NOT_FOUND;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;

/**
 * Decomposes a class into list of properties.
 * 
 * @author bartosz walacik
 */
class ManagedClass {
    private final Class<?> baseJavaClass;
    private final Map<String, ItauAuditableProperty> propertiesByName;
    private final List<ItauAuditableProperty> managedProperties;
    private final List<ItauAuditableProperty> looksLikeId;
    private final ManagedPropertiesFilter managedPropertiesFilter;

    ManagedClass(Class baseJavaClass, List<ItauAuditableProperty> managedProperties, List<ItauAuditableProperty> looksLikeId, ManagedPropertiesFilter managedPropertiesFilter) {
        argumentsAreNotNull(baseJavaClass, managedProperties, looksLikeId, managedPropertiesFilter);

        this.baseJavaClass = baseJavaClass;

        this.looksLikeId = immutableCopyOf(looksLikeId);
        this.managedPropertiesFilter = managedPropertiesFilter;
        this.managedProperties = immutableCopyOf(managedProperties);

        this.propertiesByName = new HashMap<>();
        managedProperties.forEach(property -> propertiesByName.put(property.getName(),property));
    }

    static ManagedClass unknown() {
        return new ManagedClass(Object.class, Collections.emptyList(), Collections.emptyList(), ManagedPropertiesFilter.empty());
    }

    ManagedClass createShallowReference(){
        return new ManagedClass(baseJavaClass, Collections.emptyList(), getLooksLikeId(), ManagedPropertiesFilter.empty());
    }

    ManagedPropertiesFilter getManagedPropertiesFilter() {
        return managedPropertiesFilter;
    }

    /**
     * Returns all managed properties, unmodifiable list
     */
    List<ItauAuditableProperty> getManagedProperties() {
        return managedProperties;
    }

    List<ItauAuditableProperty> getLooksLikeId() {
        return looksLikeId;
    }

    Set<String> getPropertyNames(){
        return Collections.unmodifiableSet(propertiesByName.keySet());
    }

    /**
     * returns managed properties subset
     */
    List<ItauAuditableProperty> getManagedProperties(Predicate<ItauAuditableProperty> query) {
        return Lists.positiveFilter(managedProperties, query);
    }

    /**
     * finds property by name (managed or withTransientAnn)
     *
     * @throws ItauAuditableException PROPERTY_NOT_FOUND
     */
    ItauAuditableProperty getProperty(String withName) {
        Validate.argumentIsNotNull(withName);
        if (!propertiesByName.containsKey(withName)){
            throw new ItauAuditableException(PROPERTY_NOT_FOUND, withName, baseJavaClass.getName());
        }
        return propertiesByName.get(withName);
    }

    /**
     * @throws ItauAuditableException PROPERTY_NOT_FOUND
     */
    List<ItauAuditableProperty> getProperties(List<String> withNames) {
        Validate.argumentIsNotNull(withNames);
        return withNames.stream().map(n -> getProperty(n)).collect(Collectors.toList());
    }

    boolean hasProperty(String propertyName) {
        return propertiesByName.containsKey(propertyName);
    }

    void forEachProperty(Consumer<ItauAuditableProperty> consumer) {
        managedProperties.forEach(p -> consumer.accept(p));
    }

    Class<?> getBaseJavaClass() {
        return baseJavaClass;
    }
}
