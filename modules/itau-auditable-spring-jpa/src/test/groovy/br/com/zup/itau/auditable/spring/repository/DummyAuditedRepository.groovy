package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditable
import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.stereotype.Repository

@Repository
class DummyAuditedRepository {

    @ItauAuditableAuditable
    void save(DummyObject obj){
      //... omitted
    }
}
