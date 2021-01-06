package br.com.zup.itau.auditable.core.metamodel.clazz;

import br.com.zup.itau.auditable.core.metamodel.annotation.Id;

/**
 * @author bartosz walacik
 */
@br.com.zup.itau.auditable.core.metamodel.annotation.Entity
public class ItauAuditableEntity {
    @Id
    private int id;
}
