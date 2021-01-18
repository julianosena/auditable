package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.repository;

import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.GlobalIdDatabase;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.exception.ItauAuditableGatewayException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Component
public class GlobalIdDatabaseRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<GlobalIdDatabase> findAllByLocalIdAndTypeName(String id, String typeName) throws ItauAuditableGatewayException {
        try {
            Session session = em.unwrap(Session.class);
            Query<GlobalIdDatabase> query = session.createQuery("" +
                    "SELECT DISTINCT globalId FROM GlobalIdDatabase AS globalId " +
                    "INNER JOIN FETCH globalId.jvSnapshots AS snapshots " +
                    "INNER JOIN FETCH snapshots.commitDatabase AS commit " +
                    "WHERE globalId.localId = :id AND globalId.typeName = :typeName")
                    .setParameter("id", id)
                    .setParameter("typeName", typeName);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ItauAuditableGatewayException("Ocorreu o seguinte erro ao buscar as auditorias", e);
        }
    }
}
