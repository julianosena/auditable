package br.com.zup.itau.auditable.hibernate.integration.config;

import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.repository.sql.ConnectionProvider;
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.SpringSecurityAuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAspect;
import br.com.zup.itau.auditable.spring.auditable.aspect.springdata.ItauAuditableSpringDataRepositoryAspect;
import br.com.zup.itau.auditable.spring.jpa.JpaHibernateConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HibernateConfig {
    public static final String H2_URL = "jdbc:h2:mem:test";
    /**
     * Integrates {@link ItauAuditableSqlRepository} with Spring {@link JpaTransactionManager}
     */
    @Bean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("br.com.zup.itau.auditable.hibernate.entity", "br.com.zup.itau.auditable.spring.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(H2_URL + ";DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    @Bean
    public ItauAuditableAspect itauAuditableAuditableAspect(ItauAuditable itauAuditable) {
        return new ItauAuditableAspect(itauAuditable, authorProvider(), commitPropertiesProvider());
    }

    @Bean
    public ItauAuditableSpringDataRepositoryAspect itauAuditableSpringDataAspect(ItauAuditable itauAuditable) {
        return new ItauAuditableSpringDataRepositoryAspect(itauAuditable, authorProvider(), commitPropertiesProvider());
    }

    /**
     * Required by Repository auto-audit aspect. <br/><br/>
     * <p>
     * Returns mock implementation for testing.
     * <br/>
     * Provide real implementation,
     * when using Spring Security you can use
     * {@link SpringSecurityAuthorProvider}.
     */
    @Bean
    public AuthorProvider authorProvider() {
        return new AuthorProvider() {
            @Override
            public String provide() {
                return "unknown";
            }
        };
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        final Map<String, String> rv = new HashMap<>();
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provide() {
                return Maps.of("key", "ok_2");
            }
        };
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.current_session_context_class", "thread");
        properties.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        return properties;
    }
}
