package br.com.zup.itau.auditable.spring.boot;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

@ItauAuditableSpringDataAuditable
public interface ShallowEntityRepository extends JpaRepository<ShallowEntity, Integer> {
}
