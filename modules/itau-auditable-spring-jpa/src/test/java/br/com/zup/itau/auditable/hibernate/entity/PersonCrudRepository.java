package br.com.zup.itau.auditable.hibernate.entity;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ItauAuditableSpringData
public interface PersonCrudRepository extends JpaRepository<Person, String> {
}
