package br.com.zup.itau.auditable.spring.boot.mongo.dbref

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable
import org.springframework.data.repository.CrudRepository

@ItauAuditableSpringDataAuditable
interface MyDummyEntityRepository extends CrudRepository<MyDummyEntity, String> {
}