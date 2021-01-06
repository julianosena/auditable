package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAsync
import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.stereotype.Repository

/**
 * @author bartosz walacik
 */
@Repository
class DummyAuditedAsyncRepository {

    @ItauAuditableAsync
    void save(DummyObject obj){
      //... omitted
    }

    @ItauAuditableAsync
    void saveAndFail(DummyObject obj) {
        throw new RuntimeException()
    }

    @ItauAuditableAsync
    void saveTwo(DummyObject obj, obj2){
        //... omitted
    }

    @ItauAuditableAsync
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query){
        //... omitted
        null
    }

}
