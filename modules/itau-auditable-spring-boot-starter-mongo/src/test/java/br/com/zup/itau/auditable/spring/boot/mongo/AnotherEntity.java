package br.com.zup.itau.auditable.spring.boot.mongo;

import br.com.zup.itau.auditable.core.metamodel.annotation.Entity;
import br.com.zup.itau.auditable.core.metamodel.annotation.Id;
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName;

@Entity
@TypeName("AnotherEntity")
public class AnotherEntity {
    @Id
    public int getId() {
        return 0;
    }
}
