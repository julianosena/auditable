package br.com.zup.itau.auditable.spring.boot.sql;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import br.com.zup.itau.auditable.spring.boot.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author pawelszymczyk
 */
@ItauAuditableSpringData
public interface DummyEntityRepository extends JpaRepository<DummyEntity, Integer> {
}
