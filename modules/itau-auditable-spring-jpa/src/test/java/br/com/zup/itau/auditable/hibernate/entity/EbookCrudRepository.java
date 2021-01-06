package br.com.zup.itau.auditable.hibernate.entity;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ItauAuditableSpringDataAuditable
public interface EbookCrudRepository extends JpaRepository<Ebook, String> {
}
