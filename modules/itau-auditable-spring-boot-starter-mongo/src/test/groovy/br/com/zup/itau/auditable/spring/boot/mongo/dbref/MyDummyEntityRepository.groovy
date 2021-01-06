package br.com.zup.itau.auditable.spring.boot.mongo.dbref

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData
import org.springframework.data.repository.CrudRepository

@ItauAuditableSpringData
interface MyDummyEntityRepository extends CrudRepository<MyDummyEntity, String> {
}