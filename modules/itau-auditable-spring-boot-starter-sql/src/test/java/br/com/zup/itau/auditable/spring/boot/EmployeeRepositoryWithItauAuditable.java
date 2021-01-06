package br.com.zup.itau.auditable.spring.boot;

import java.util.UUID;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

@ItauAuditableSpringDataAuditable
public interface EmployeeRepositoryWithItauAuditable extends JpaRepository<EmployeeEntity, UUID> {
}
