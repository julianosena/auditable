package br.com.zup.itau.auditable.spring.boot.sql;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable;
import br.com.zup.itau.auditable.spring.boot.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author pawelszymczyk
 */
@ItauAuditableSpringDataAuditable
public interface DummyEntityRepository extends JpaRepository<DummyEntity, Integer> {
}
