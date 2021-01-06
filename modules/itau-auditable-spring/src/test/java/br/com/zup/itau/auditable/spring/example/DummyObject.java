package br.com.zup.itau.auditable.spring.example;

import br.com.zup.itau.auditable.core.metamodel.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class DummyObject {
    @Id
    String id;
    String name;

    public Object getName() {
        return name;
    }
}
