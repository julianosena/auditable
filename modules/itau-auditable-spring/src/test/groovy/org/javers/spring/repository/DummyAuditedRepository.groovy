package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditable
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditableDelete
import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.stereotype.Repository

/**
 * @author bartosz walacik
 */
@Repository
class DummyAuditedRepository {

    @ItauAuditableAuditable
    void save(DummyObject obj){
      //... omitted
    }

    @ItauAuditableAuditable
    void saveAndFail(DummyObject obj) {
        throw new RuntimeException()
    }

    @ItauAuditableAuditable
    void saveTwo(DummyObject obj, obj2){
        //... omitted
    }

    @ItauAuditableAuditable
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query) {
        //... omitted
        null
    }

    @ItauAuditableAuditableDelete
    void delete(DummyObject obj) {
        //... omitted
    }

    @ItauAuditableAuditableDelete(entity = DummyObject)
    void deleteById(String id) {
    }

    @ItauAuditableAuditableDelete(entity = DummyObject.class)
    void deleteAllById(Iterable<String> ids) {
    }

    @ItauAuditableAuditableDelete
    void deleteByIdNoClass(String id) {
    }


    @ItauAuditableAuditableDelete
    void deleteTwo(DummyObject obj, obj2) {
        //... omitted
    }

    @ItauAuditableAuditableDelete
    void deleteAll(Iterable<DummyObject> objetcs) {
        //... omitted
    }

}
