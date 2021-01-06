package br.com.zup.itau.auditable.spring.boot.mongo;

import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import org.springframework.data.repository.CrudRepository;

@ItauAuditableSpringData
public interface DummyEntityRepository extends CrudRepository<DummyEntity, Integer>{
}
