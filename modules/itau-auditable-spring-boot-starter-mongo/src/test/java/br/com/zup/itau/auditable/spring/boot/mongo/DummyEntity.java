package br.com.zup.itau.auditable.spring.boot.mongo;

import br.com.zup.itau.auditable.core.metamodel.annotation.Id;

/**
 * @author pawelszymczyk
 */
public class DummyEntity {

    private final int id;

    public DummyEntity(int id) {
        this.id = id;
    }

    @Id
    public int getId() {
        return id;
    }
}
