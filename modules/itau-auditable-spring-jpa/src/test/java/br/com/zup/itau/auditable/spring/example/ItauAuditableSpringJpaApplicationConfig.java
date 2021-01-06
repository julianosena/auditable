package br.com.zup.itau.auditable.spring.example;

import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.hibernate.integration.HibernateUnproxyObjectAccessHook;
import br.com.zup.itau.auditable.repository.sql.ConnectionProvider;
import br.com.zup.itau.auditable.repository.sql.DialectName;
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository;
import br.com.zup.itau.auditable.repository.sql.SqlRepositoryBuilder;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.SpringSecurityAuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAspect;
import br.com.zup.itau.auditable.spring.auditable.aspect.springdatajpa.ItauAuditableSpringDataJpaRepositoryAspect;
import br.com.zup.itau.auditable.spring.jpa.JpaHibernateConnectionProvider;
import br.com.zup.itau.auditable.spring.jpa.TransactionalItauAuditableBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "br.com.zup.itau.auditable.spring.repository")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaRepositories({"br.com.zup.itau.auditable.spring.repository"})
public class ItauAuditableSpringJpaApplicationConfig {

    //.. JaVers setup ..

    /**
     * Creates JaVers instance with {@link ItauAuditableSqlRepository}
     */
    @Bean
    public ItauAuditable itauAuditable(PlatformTransactionManager txManager) {
        ItauAuditableSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider())
                .withDialect(DialectName.H2)
                .build();

        return TransactionalItauAuditableBuilder
                .itauAuditable()
                .withTxManager(txManager)
                .withObjectAccessHook(new HibernateUnproxyObjectAccessHook())
                .registerItauAuditableRepository(sqlRepository)
                .build();
    }

    /**
     * Enables auto-audit aspect for ordinary repositories.<br/>
     *
     * Use {@link br.com.zup.itau.auditable.spring.annotation.ItauAuditable}
     * to mark data writing methods that you want to audit.
     */
    @Bean
    public ItauAuditableAspect itauAuditableAuditableAspect(ItauAuditable itauAuditable) {
        return new ItauAuditableAspect(itauAuditable, authorProvider(), commitPropertiesProvider());
    }

    /**
     * Enables auto-audit aspect for Spring Data repositories. <br/>
     *
     * Use {@link ItauAuditableSpringData}
     * to annotate CrudRepository, PagingAndSortingRepository or JpaRepository
     * you want to audit.
     */
    @Bean
    public ItauAuditableSpringDataJpaRepositoryAspect itauAuditableSpringDataAspect(ItauAuditable itauAuditable) {
        return new ItauAuditableSpringDataJpaRepositoryAspect(itauAuditable, authorProvider(), commitPropertiesProvider());
    }

    /**
     * Required by auto-audit aspect. <br/><br/>
     *
     * Creates {@link SpringSecurityAuthorProvider} instance,
     * suitable when using Spring Security
     */
    @Bean
    public AuthorProvider authorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    /**
     * Optional for auto-audit aspect. <br/>
     * @see CommitPropertiesProvider
     */
    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provideForCommittedObject(Object domainObject) {
                if (domainObject instanceof DummyObject) {
                    return Maps.of("dummyObject.name", ((DummyObject)domainObject).getName());
                }
                return Collections.emptyMap();
            }
        };
    }

    /**
     * Integrates {@link ItauAuditableSqlRepository} with Spring {@link JpaTransactionManager}
     */
    @Bean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }
    //.. EOF JaVers setup ..


    //.. Spring-JPA-Hibernate setup ..
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("br.com.zup.itau.auditable.spring.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        return properties;
    }
    //.. EOF Spring-JPA-Hibernate setup ..
}
