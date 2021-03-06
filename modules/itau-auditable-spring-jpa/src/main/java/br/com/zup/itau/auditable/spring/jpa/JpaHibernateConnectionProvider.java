package br.com.zup.itau.auditable.spring.jpa;

import org.hibernate.engine.spi.SessionImplementor;
import br.com.zup.itau.auditable.repository.sql.ConnectionProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;

/**
 * @author bartosz walacik
 */
public class JpaHibernateConnectionProvider implements ConnectionProvider {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Connection getConnection() {

        SessionImplementor session =  entityManager.unwrap(SessionImplementor.class);

        return session.connection();
    }

}
