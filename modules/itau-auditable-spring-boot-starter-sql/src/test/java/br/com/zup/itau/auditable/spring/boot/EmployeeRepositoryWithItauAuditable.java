package br.com.zup.itau.auditable.spring.boot;

import java.util.UUID;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import org.springframework.data.jpa.repository.JpaRepository;

@ItauAuditableSpringData
public interface EmployeeRepositoryWithItauAuditable extends JpaRepository<EmployeeEntity, UUID> {
}
