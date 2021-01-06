package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.annotation.ItauAuditable
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableDelete
import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.stereotype.Repository

/**
 * @author bartosz walacik
 */
@Repository
class DummyAuditedRepository {

    @ItauAuditable
    void save(DummyObject obj){
      //... omitted
    }

    @ItauAuditable
    void saveAndFail(DummyObject obj) {
        throw new RuntimeException()
    }

    @ItauAuditable
    void saveTwo(DummyObject obj, obj2){
        //... omitted
    }

    @ItauAuditable
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query) {
        //... omitted
        null
    }

    @ItauAuditableDelete
    void delete(DummyObject obj) {
        //... omitted
    }

    @ItauAuditableDelete(entity = DummyObject)
    void deleteById(String id) {
    }

    @ItauAuditableDelete(entity = DummyObject.class)
    void deleteAllById(Iterable<String> ids) {
    }

    @ItauAuditableDelete
    void deleteByIdNoClass(String id) {
    }


    @ItauAuditableDelete
    void deleteTwo(DummyObject obj, obj2) {
        //... omitted
    }

    @ItauAuditableDelete
    void deleteAll(Iterable<DummyObject> objetcs) {
        //... omitted
    }

}
