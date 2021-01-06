package br.com.zup.itau.auditable.core.examples.typeNames;

import br.com.zup.itau.auditable.core.metamodel.annotation.Id;
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName;

/**
 * @author bartosz.walacik
 */
@TypeName("br.com.zup.itau.auditable.core.examples.typeNames.OldEntity")
public class NewEntity {
    @Id
    private int id;

    private int value;

    private int newValue;

    @Id
    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getNewValue() {
        return newValue;
    }
}
