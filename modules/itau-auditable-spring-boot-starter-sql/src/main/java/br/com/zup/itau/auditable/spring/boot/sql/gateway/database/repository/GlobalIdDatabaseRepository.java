package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.repository;

import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.GlobalIdDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalIdDatabaseRepository extends JpaRepository<GlobalIdDatabase, Long> {

    List<GlobalIdDatabase> findAllByLocalIdAndTypeName(Long id, String typeName);

}
