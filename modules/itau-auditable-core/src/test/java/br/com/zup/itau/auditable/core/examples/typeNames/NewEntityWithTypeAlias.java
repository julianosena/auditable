package br.com.zup.itau.auditable.core.examples.typeNames;

import br.com.zup.itau.auditable.core.metamodel.annotation.Entity;
import br.com.zup.itau.auditable.core.metamodel.annotation.Id;
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName;

import java.math.BigDecimal;

/**
 * @author bartosz.walacik
 */
@TypeName("myName")
@Entity
public class NewEntityWithTypeAlias {
    @Id
    private BigDecimal id;

    private int val;

    private NewValueObjectWithTypeAlias valueObject;

    @Id
    public BigDecimal getId() {
        return id;
    }

    public int getVal() {
        return val;
    }

    public NewValueObjectWithTypeAlias getValueObject() {
        return valueObject;
    }
}
