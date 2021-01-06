package br.com.zup.itau.auditable.core.examples.typeNames;

import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName;

/**
 * @author bartosz.walacik
 */
@TypeName("br.com.zup.itau.auditable.core.examples.typeNames.OldValueObject")
public class NewNamedValueObject extends AbstractValueObject {
    private int newField;

    public NewNamedValueObject(int someValue, int newField) {
        super(someValue);
        this.newField = newField;
    }
}
