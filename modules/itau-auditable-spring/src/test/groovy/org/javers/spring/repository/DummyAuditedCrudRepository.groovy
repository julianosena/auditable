package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData
import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
@ItauAuditableSpringData
interface DummyAuditedCrudRepository extends PagingAndSortingRepository<DummyObject, String> {
}