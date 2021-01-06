package br.com.zup.itau.auditable.core.examples.typeNames;

import br.com.zup.itau.auditable.core.metamodel.annotation.Id;

/**
 * @author bartosz.walacik
 */
public class OldEntity {
    @Id
    private int id;

    private int value;

    private int oldValue;

    @Id
    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getOldValue() {
        return oldValue;
    }
}
