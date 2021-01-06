package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.common.reflection.ItauAuditableMember;
import br.com.zup.itau.auditable.core.metamodel.object.UnboundedValueObjectId;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.scanner.ClassScanner;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;
import br.com.zup.itau.auditable.core.metamodel.type.ValueObjectType;

/**
 * @author pawelszymczyk
 */
public class CollectionsCdoFactory {

    private final ClassScanner classScanner;
    private final TailoredItauAuditableMemberFactory memberGenericTypeInjector;
    private final TypeMapper typeMapper;

    public CollectionsCdoFactory(ClassScanner classScanner, TailoredItauAuditableMemberFactory memberGenericTypeInjector, TypeMapper typeMapper) {
        this.classScanner = classScanner;
        this.memberGenericTypeInjector = memberGenericTypeInjector;
        this.typeMapper = typeMapper;
    }

    public LiveCdo createCdo(final CollectionWrapper wrapper, final Class<?> clazz) {
        Property primaryProperty = classScanner.scan(wrapper.getClass()).getProperties().get(0);
        ItauAuditableMember itauAuditableMember = memberGenericTypeInjector.create(primaryProperty, clazz);

        Property fixedProperty = new Property(itauAuditableMember);
        ItauAuditableProperty fixedJProperty = new ItauAuditableProperty(() -> typeMapper.getPropertyType(fixedProperty), fixedProperty);

        ValueObjectType valueObject = new ValueObjectType(wrapper.getClass(), Lists.asList(fixedJProperty));
        return new LiveCdoWrapper(wrapper, new UnboundedValueObjectId(valueObject.getName()), valueObject);
    }
}
