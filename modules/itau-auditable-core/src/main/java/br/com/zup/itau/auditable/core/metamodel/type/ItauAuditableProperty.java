package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.core.metamodel.property.Property;

import java.util.function.Supplier;

import static br.com.zup.itau.auditable.common.string.ToStringBuilder.typeName;

/**
 * Class property with ItauAuditableType
 *
 * @author bartosz.walacik
 */
public class ItauAuditableProperty extends Property {
    /**
     * Supplier prevents stack overflow exception when building ItauAuditableType
     */
    private final Supplier<ItauAuditableType> propertyType;

    public ItauAuditableProperty(Supplier<ItauAuditableType> propertyType, Property property) {
        super(property.getMember(),  property.hasTransientAnn(), property.hasShallowReferenceAnn(), property.getName(), property.isHasIncludedAnn());
        this.propertyType = propertyType;
    }

    public <T extends ItauAuditableType> T getType() {
        return (T) propertyType.get();
    }

    public boolean isEntityType() {
        return getType() instanceof EntityType;
    }

    public boolean isValueObjectType() {
        return getType() instanceof ValueObjectType;
    }

    public boolean isPrimitiveOrValueType() {
        return getType() instanceof PrimitiveOrValueType;
    }

    public boolean isCustomType() {
        return getType() instanceof CustomType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItauAuditableProperty that = (ItauAuditableProperty) o;
        return super.equals(that) && this.getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isShallowReference(){
        return (hasShallowReferenceAnn()
            || getType() instanceof ShallowReferenceType);
    }

    @Override
    public String toString() {
        return getMember().memberType() + " " +
                getType().getClass().getSimpleName() + ":" +
                typeName(getMember().getGenericResolvedType()) + " " +
                getName() + (getMember().memberType().equals("Getter") ? "()" : "")+
                ", declared in " + getDeclaringClass().getSimpleName();
    }
}
