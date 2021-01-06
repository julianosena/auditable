package br.com.zup.itau.auditable.core.metamodel.type;

/**
 * @author bartosz walacik
 */
public class MapContentType {
    private final ItauAuditableType keyType;
    private final ItauAuditableType valueType;

    public MapContentType(ItauAuditableType keyType, ItauAuditableType valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public ItauAuditableType getKeyType() {
        return keyType;
    }

    public ItauAuditableType getValueType() {
        return valueType;
    }
}
