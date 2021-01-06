package br.com.zup.itau.auditable.spring.boot.sql;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.repository.sql.ConnectionProvider;
import br.com.zup.itau.auditable.repository.sql.DialectName;
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository;
import br.com.zup.itau.auditable.repository.sql.SqlRepositoryBuilder;
import br.com.zup.itau.auditable.spring.auditable.*;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAuditableAspect;
import br.com.zup.itau.auditable.spring.auditable.aspect.springdatajpa.ItauAuditableSpringDataJpaAuditableRepositoryAspect;
import br.com.zup.itau.auditable.spring.jpa.JpaHibernateConnectionProvider;
import br.com.zup.itau.auditable.spring.jpa.TransactionalItauAuditableBuilder;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(value = {ItauAuditableSqlProperties.class, JpaProperties.class})
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
public class ItauAuditableSqlAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ItauAuditableSqlAutoConfiguration.class);

    private final DialectMapper dialectMapper = new DialectMapper();

    @Autowired
    private ItauAuditableSqlProperties javersSqlProperties;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public DialectName javersSqlDialectName() {
        SessionFactoryImplementor sessionFactory =
                entityManagerFactory.unwrap(SessionFactoryImplementor.class);

        Dialect hibernateDialect = sessionFactory.getJdbcServices().getDialect();
        logger.info("detected Hibernate dialect: " + hibernateDialect.getClass().getSimpleName());

        return dialectMapper.map(hibernateDialect);
    }

    @Bean(name = "ItauAuditableSqlRepositoryFromStarter")
    @ConditionalOnMissingBean
    public ItauAuditableSqlRepository javersSqlRepository(ConnectionProvider connectionProvider) {
        return SqlRepositoryBuilder
                .sqlRepository()
                .withSchema(javersSqlProperties.getSqlSchema())
                .withConnectionProvider(connectionProvider)
                .withDialect(javersSqlDialectName())
                .withSchemaManagementEnabled(javersSqlProperties.isSqlSchemaManagementEnabled())
                .withGlobalIdCacheDisabled(javersSqlProperties.isSqlGlobalIdCacheDisabled())
                .withGlobalIdTableName(javersSqlProperties.getSqlGlobalIdTableName())
                .withCommitTableName(javersSqlProperties.getSqlCommitTableName())
                .withSnapshotTableName(javersSqlProperties.getSqlSnapshotTableName())
                .withCommitPropertyTableName(javersSqlProperties.getSqlCommitPropertyTableName())
                .build();
    }

    @Bean(name = "ItauAuditableFromStarter")
    @ConditionalOnMissingBean
    public ItauAuditable javers(ItauAuditableSqlRepository sqlRepository, PlatformTransactionManager transactionManager) {
        return TransactionalItauAuditableBuilder
                .javers()
                .withTxManager(transactionManager)
                .registerItauAuditableRepository(sqlRepository)
                .withObjectAccessHook(javersSqlProperties.createObjectAccessHookInstance())
                .withProperties(javersSqlProperties)
                .build();
    }

    @Bean(name = "SpringSecurityAuthorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = {"org.springframework.security.core.context.SecurityContextHolder"})
    public AuthorProvider springSecurityAuthorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    @Bean(name = "MockAuthorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass({"org.springframework.security.core.context.SecurityContextHolder"})
    public AuthorProvider unknownAuthorProvider() {
        return new MockAuthorProvider();
    }

    @Bean(name = "EmptyPropertiesProvider")
    @ConditionalOnMissingBean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new EmptyPropertiesProvider();
    }

    @Bean(name = "JpaHibernateConnectionProvider")
    @ConditionalOnMissingBean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    public ItauAuditableAuditableAspect javersAuditableAspect(ItauAuditable javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new ItauAuditableAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    public ItauAuditableSpringDataJpaAuditableRepositoryAspect javersSpringDataAuditableAspect(ItauAuditable javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new ItauAuditableSpringDataJpaAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
