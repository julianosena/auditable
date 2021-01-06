package br.com.zup.itau.auditable.spring.boot.mongo;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

@ItauAuditableSpringDataAuditable
public interface DummyEntityRepository extends CrudRepository<DummyEntity, Integer>{
}
