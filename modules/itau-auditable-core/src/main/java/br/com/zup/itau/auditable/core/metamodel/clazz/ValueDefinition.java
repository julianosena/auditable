package br.com.zup.itau.auditable.core.metamodel.clazz;

import br.com.zup.itau.auditable.core.diff.custom.CustomValueComparator;

/**
 * @author bartosz walacik
 */
public class ValueDefinition extends ClientsClassDefinition {
    private CustomValueComparator customValueComparator;

    public ValueDefinition(Class<?> clazz) {
        super(clazz);
    }

    public void setCustomValueComparator(CustomValueComparator customValueComparator) {
        this.customValueComparator = customValueComparator;
    }

    public CustomValueComparator getComparator() {
        return customValueComparator;
    }
}
