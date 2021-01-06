package br.com.zup.itau.auditable.core.examples.typeNames;

import br.com.zup.itau.auditable.core.metamodel.annotation.Id;

/**
 * @author bartosz.walacik
 */
public class EntityWithRefactoredValueObject {
    @Id
    private int id;
    private AbstractValueObject value;
}
