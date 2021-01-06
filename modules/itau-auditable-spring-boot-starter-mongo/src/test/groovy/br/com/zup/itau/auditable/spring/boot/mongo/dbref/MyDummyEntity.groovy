package br.com.zup.itau.auditable.spring.boot.mongo.dbref

import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

class MyDummyEntity {
    private String id
    private String name

    @DBRef(lazy = true)
    private MyDummyRefEntity refEntity

    @Id
    String getId() {
        return id
    }

    MyDummyRefEntity getRefEntity() {
        return refEntity
    }

    String getName() {
        return name
    }
}