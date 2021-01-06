package br.com.zup.itau.auditable.spring.model

import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by gessnerfl on 21.02.15.
 */
@Document
class DummyObject {
    @Id
    String id
    String name

    DummyObject() {
        this.id = UUID.randomUUID().toString()
    }

    DummyObject(String name) {
        this.name = name
        this.id = UUID.randomUUID().toString()
    }
}
