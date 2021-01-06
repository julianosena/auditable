package br.com.zup.itau.auditable.core.metamodel.object;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Global ID of Client's domain object (CDO)
 */
public abstract class GlobalId implements Serializable, Comparable<GlobalId> {

    private final String typeName;

    GlobalId(String typeName) {
        Validate.argumentIsNotNull(typeName);
        this.typeName = typeName;
    }

    /**
     * <pre>
     * For ex.:
     * br.com.zup.itau.auditable.core.model.SnapshotEntity/1
     * br.com.zup.itau.auditable.core.model.SnapshotEntity/2#setOfValueObjects
     * </pre>
     */
    public abstract String value();

    @Override
    public String toString(){
        return this.value();
    }

    public boolean isTypeOf(ManagedType managedType){
        return getTypeName().equals(managedType.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if ( !(o instanceof GlobalId) ) {return false;}

        return value().equals(((GlobalId) o).value());
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    public String getTypeName() {
        return typeName;
    }

    public GlobalId masterObjectId() {
        return this;
    }

    String getTypeNameShort() {
        String[] split = getTypeName().split("\\.");
        if (split.length >=2) {
            return "..." + split[split.length-1];
        }
        return getTypeName();
    }

    @Override
    public int compareTo(GlobalId o) {
        return Comparator.comparing(GlobalId::value).compare(this, o);
    }
}
