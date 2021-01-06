package org.javers.spring.jpa;

import org.hibernate.engine.spi.SessionImplementor;
import org.javers.core.AuditableContextHolder;
import org.javers.repository.sql.ConnectionProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.SQLException;

public class MultitenancyJpaHibernateConnectionProvider implements ConnectionProvider {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Connection getConnection() throws SQLException {

        SessionImplementor session = entityManager.unwrap(SessionImplementor.class);

        Connection connection = session.connection();

        final String schemaName = AuditableContextHolder.getContext().getDatabaseSchemaName();
        connection.setSchema(schemaName);

        return connection;
    }

}
