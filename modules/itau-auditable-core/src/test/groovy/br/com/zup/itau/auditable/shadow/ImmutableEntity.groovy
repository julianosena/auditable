package br.com.zup.itau.auditable.shadow

import br.com.zup.itau.auditable.core.metamodel.annotation.Id

class ImmutableEntity {
    @Id
    private final int id

    private final ImmutableEntity entityRef

    ImmutableEntity(int id) {
        this(id, null)
    }

    ImmutableEntity(int id, ImmutableEntity entityRef) {
        this.id = id
        this.entityRef = entityRef
    }

    @Id
    int getId() {
        id
    }

    ImmutableEntity getReference() {
        entityRef
    }
}
