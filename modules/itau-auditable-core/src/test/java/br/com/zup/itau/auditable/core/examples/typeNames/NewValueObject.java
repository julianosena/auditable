package br.com.zup.itau.auditable.core.examples.typeNames;

/**
 * @author bartosz.walacik
 */
public class NewValueObject extends AbstractValueObject {
    private int newField;

    public NewValueObject(int someValue, int newField) {
        super(someValue);
        this.newField = newField;
    }
}
