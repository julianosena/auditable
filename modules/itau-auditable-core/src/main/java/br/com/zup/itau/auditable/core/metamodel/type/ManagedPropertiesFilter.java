package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.metamodel.clazz.PropertiesFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ManagedPropertiesFilter {
    private static final Logger logger = LoggerFactory.getLogger(ManagedPropertiesFilter.class);
    private final Set<ItauAuditableProperty> includedProperties;
    private final Set<ItauAuditableProperty> ignoredProperties;

    static ManagedPropertiesFilter empty() {
        return new ManagedPropertiesFilter();
    }

    ManagedPropertiesFilter(Class<?> baseJavaClass, List<ItauAuditableProperty> allSourceProperties, PropertiesFilter propertiesFilter) {
        this.includedProperties = filter(allSourceProperties, propertiesFilter.getIncludedProperties(), baseJavaClass);
        this.includedProperties.addAll(allSourceProperties.stream().filter(p -> p.isHasIncludedAnn()).collect(Collectors.toSet()));

        if (this.includedProperties.size() > 0) {
            logger.debug("Included properties have been provided and thus any @Transient or @DiffIgnore annotation will be disregarded for class " + baseJavaClass.getName());
            this.ignoredProperties = Collections.emptySet();
        } else {
            this.ignoredProperties = filter(allSourceProperties, propertiesFilter.getIgnoredProperties(), baseJavaClass);
            this.ignoredProperties.addAll(allSourceProperties.stream().filter(p -> p.hasTransientAnn()).collect(Collectors.toSet()));
        }
    }

    private ManagedPropertiesFilter() {
        this.includedProperties = Collections.emptySet();
        this.ignoredProperties = Collections.emptySet();
    }

    List<ItauAuditableProperty> filterProperties(List<ItauAuditableProperty> allProperties){
        if (hasIncludedProperties()) {
            return new ArrayList<>(includedProperties);
        }

        if (hasIgnoredProperties()) {
            return allProperties.stream().filter(it -> !ignoredProperties.contains(it)).collect(Collectors.toList());
        }

        return allProperties;
    }

    boolean hasIgnoredProperties() {
        return !ignoredProperties.isEmpty();
    }

    boolean hasIncludedProperties() {
        return !includedProperties.isEmpty();
    }

    private Set<ItauAuditableProperty> filter(List<ItauAuditableProperty> allProperties, List<String> propertyNames, Class<?> baseJavaClass) {
        return propertyNames.stream()
            .map(p -> allProperties.stream()
                    .filter(jp -> jp.getName().equals(p))
                    .findFirst()
                    .orElseThrow(() -> new ItauAuditableException(ItauAuditableExceptionCode.PROPERTY_NOT_FOUND, p, baseJavaClass.getName())))
            .collect(Collectors.toSet());
    }
}
