package br.com.zup.itau.auditable.spring.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.repository.mongo.MongoRepository;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditable;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditableAsync;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.SpringSecurityAuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAuditableAspect;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAuditableAspectAsync;
import br.com.zup.itau.auditable.spring.auditable.aspect.springdata.ItauAuditableSpringDataAuditableRepositoryAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@ComponentScan(basePackages = "br.com.zup.itau.auditable.spring.repository")
@EnableMongoRepositories({"br.com.zup.itau.auditable.spring.repository"})
@EnableAspectJAutoProxy
public class ItauAuditableSpringMongoApplicationConfig {
    private static final String DATABASE_NAME = "mydatabase";

    /**
     * Creates JaVers instance backed by {@link MongoRepository}
     */
    @Bean
    public ItauAuditable javers() {
        MongoRepository javersMongoRepository =
                new MongoRepository(mongo().getDatabase(DATABASE_NAME));

        return ItauAuditableBuilder.javers()
                .registerItauAuditableRepository(javersMongoRepository)
                .build();
    }

    /**
     * MongoDB setup
     */
    @Bean(name="realMongoClient")
    @ConditionalOnMissingBean
    public MongoClient mongo() {
        return MongoClients.create();
    }

    /**
     * required by Spring Data MongoDB
     */
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), DATABASE_NAME);
    }

    /**
     * Enables auto-audit aspect for ordinary repositories.<br/>
     *
     * Use {@link ItauAuditableAuditable}
     * to mark repository methods that you want to audit.
     */
    @Bean
    public ItauAuditableAuditableAspect javersAuditableAspect() {
        return new ItauAuditableAuditableAspect(javers(), authorProvider(), commitPropertiesProvider());
    }

    /**
     * Enables auto-audit aspect for Spring Data repositories. <br/>
     *
     * Use {@link ItauAuditableSpringDataAuditable}
     * to annotate CrudRepositories you want to audit.
     */
    @Bean
    public ItauAuditableSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        return new ItauAuditableSpringDataAuditableRepositoryAspect(javers(), authorProvider(),
                commitPropertiesProvider());
    }

    /**
     * <b>INCUBATING - ItauAuditable Async API has incubating status.</b>
     * <br/><br/>
     *
     * Enables asynchronous auto-audit aspect for ordinary repositories.<br/>
     *
     * Use {@link ItauAuditableAuditableAsync}
     * to mark repository methods that you want to audit.
     */
    @Bean
    public ItauAuditableAuditableAspectAsync javersAuditableAspectAsync() {
        return new ItauAuditableAuditableAspectAsync(javers(), authorProvider(), commitPropertiesProvider(), javersAsyncAuditExecutor());
    }

    /**
     * <b>INCUBATING - ItauAuditable Async API has incubating status.</b>
     * <br/><br/>
     */
    @Bean
    public ExecutorService javersAsyncAuditExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("ItauAuditableAuditableAsync-%d")
                .build();
        return Executors.newFixedThreadPool(2, threadFactory);
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
}
