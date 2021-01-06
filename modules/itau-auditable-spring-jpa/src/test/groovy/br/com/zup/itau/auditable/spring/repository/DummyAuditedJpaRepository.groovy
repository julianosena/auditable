package br.com.zup.itau.auditable.spring.repository

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData
import br.com.zup.itau.auditable.spring.model.DummyObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@ItauAuditableSpringData
interface DummyAuditedJpaRepository extends JpaRepository<DummyObject, String> {
}