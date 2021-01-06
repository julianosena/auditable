package br.com.zup.itau.auditable.spring.boot;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import org.springframework.data.jpa.repository.JpaRepository;

@ItauAuditableSpringData
public interface ShallowEntityRepository extends JpaRepository<ShallowEntity, Integer> {
}
