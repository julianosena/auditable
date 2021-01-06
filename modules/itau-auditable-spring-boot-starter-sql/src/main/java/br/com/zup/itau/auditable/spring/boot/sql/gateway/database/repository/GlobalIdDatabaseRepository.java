package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.repository;

import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.JvGlobalIdDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalIdDatabaseRepository extends JpaRepository<JvGlobalIdDatabase, Long> {

    List<JvGlobalIdDatabase> findAllByLocalIdAndTypeName(Long id, String typeName);

}
