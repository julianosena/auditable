package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.string.PrettyPrintBuilder;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * @author bartosz walacik
 */
public abstract class ManagedType extends ClassType {
    private final ManagedClass managedClass;

    ManagedType(ManagedClass managedClass) {
        this(managedClass, Optional.empty());
    }

    ManagedType(ManagedClass managedClass, Optional<String> typeName) {
        super(managedClass.getBaseJavaClass(), typeName);
        this.managedClass = managedClass;
    }

    abstract ManagedType spawn(ManagedClass managedClass, Optional<String> typeName);

    @Override
    protected Type getRawDehydratedType() {
        return GlobalId.class;
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addMultiField("managedProperties", managedClass.getManagedProperties());
    }

    /**
     * @throws ItauAuditableException PROPERTY_NOT_FOUND
     */
    public ItauAuditableProperty getProperty(String propertyName) {
        return managedClass.getProperty(propertyName);
    }

    public Optional<ItauAuditableProperty> findProperty(String propertyName) {
        return managedClass.hasProperty(propertyName) ?
                Optional.of(managedClass.getProperty(propertyName)) :
                Optional.empty();
    }

    public List<ItauAuditableProperty> getProperties(Predicate<ItauAuditableProperty> query) {
        return managedClass.getManagedProperties(query);
    }

    /**
     * unmodifiable list
     */
    public List<ItauAuditableProperty> getProperties() {
        return managedClass.getManagedProperties();
    }

    public void forEachProperty(Consumer<ItauAuditableProperty> consumer) {
        managedClass.forEachProperty(consumer);
    }

    public Set<String> getPropertyNames(){
        return managedClass.getPropertyNames();
    }

    ManagedClass getManagedClass() {
        return managedClass;
    }
}
