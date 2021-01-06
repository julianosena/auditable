package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyNoAuditedCrudRepository extends CrudRepository<DummyObject, String> {
}