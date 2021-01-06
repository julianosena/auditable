package br.com.zup.itau.auditable.core.metamodel.property;

public class MissingProperty {
    public static final MissingProperty INSTANCE = new MissingProperty();

    private MissingProperty() {
    }

    @Override
    public String toString() {
        return "MISSING_PROPERTY";
    }
}
