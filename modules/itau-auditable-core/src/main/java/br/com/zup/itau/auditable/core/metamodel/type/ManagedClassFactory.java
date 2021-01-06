package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.common.reflection.ReflectionUtil;
import br.com.zup.itau.auditable.core.metamodel.annotation.DiffIgnore;
import br.com.zup.itau.auditable.core.metamodel.clazz.ClientsClassDefinition;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.scanner.ClassScan;

import java.util.List;

import static br.com.zup.itau.auditable.common.collections.Lists.positiveFilter;

/**
 * @author bartosz walacik
 */
class ManagedClassFactory {
    private final TypeMapper typeMapper;

    public ManagedClassFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    ManagedClass create(ClientsClassDefinition def, ClassScan scan) {
        List<ItauAuditableProperty> allProperties = convert(scan.getProperties());

        ManagedPropertiesFilter managedPropertiesFilter =
                new ManagedPropertiesFilter(def.getBaseJavaClass(), allProperties, def.getPropertiesFilter());

        return create(def.getBaseJavaClass(), allProperties, managedPropertiesFilter);
    }

    ManagedClass createFromPrototype(Class<?> baseJavaClass, ClassScan scan, ManagedPropertiesFilter prototypePropertiesFilter) {
        List<ItauAuditableProperty> allProperties = convert(scan.getProperties());
        return create(baseJavaClass, allProperties, prototypePropertiesFilter);
    }

    private ManagedClass create(Class<?> baseJavaClass, List<ItauAuditableProperty> allProperties, ManagedPropertiesFilter propertiesFilter){

        List<ItauAuditableProperty> filtered = propertiesFilter.filterProperties(allProperties);

        filtered = filterIgnoredType(filtered, baseJavaClass);

        return new ManagedClass(baseJavaClass, filtered,
                positiveFilter(allProperties, p -> p.looksLikeId()), propertiesFilter);
    }

    private List<ItauAuditableProperty> convert(List<Property> properties) {
        return Lists.transform(properties,  p -> {
            if (typeMapper.contains(p.getGenericType())) {
                final ItauAuditableType javersType = typeMapper.getItauAuditableType(p.getGenericType());
                return new ItauAuditableProperty(() -> javersType, p);
            }
            return new ItauAuditableProperty(() -> typeMapper.getItauAuditableType(p.getGenericType()), p);
        });
    }

    private List<ItauAuditableProperty> filterIgnoredType(List<ItauAuditableProperty> properties, final Class<?> currentClass){

        return Lists.negativeFilter(properties, property -> {
            if (property.getRawType() == currentClass){
                return false;
            }
            //prevents stackoverflow
            if (typeMapper.contains(property.getRawType()) ||
                typeMapper.contains(property.getGenericType())) {
                return typeMapper.getItauAuditableType(property.getRawType()) instanceof IgnoredType;
            }

            return ReflectionUtil.isAnnotationPresentInHierarchy(property.getRawType(), DiffIgnore.class);
        });
    }
}
