package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditableAsync
import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.stereotype.Repository

/**
 * @author bartosz walacik
 */
@Repository
class DummyAuditedAsyncRepository {

    @ItauAuditableAuditableAsync
    void save(DummyObject obj){
      //... omitted
    }

    @ItauAuditableAuditableAsync
    void saveAndFail(DummyObject obj) {
        throw new RuntimeException()
    }

    @ItauAuditableAuditableAsync
    void saveTwo(DummyObject obj, obj2){
        //... omitted
    }

    @ItauAuditableAuditableAsync
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query){
        //... omitted
        null
    }

}
