package br.com.zup.itau.auditable.spring.boot;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

}
