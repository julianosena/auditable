package br.com.zup.itau.auditable.spring.boot.mongo.dbref

import br.com.zup.itau.auditable.core.metamodel.annotation.Id

class MyDummyRefEntity {

    private  String id

    private String name

    @Id
    String getId() {
        return id
    }

    String getName() {
        return name
    }
}