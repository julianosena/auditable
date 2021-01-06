package br.com.zup.itau.auditable.spring.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.repository.mongo.MongoRepository;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditable;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAsync;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.SpringSecurityAuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAspect;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAspectAsync;
import br.com.zup.itau.auditable.spring.auditable.aspect.springdata.ItauAuditableSpringDataRepositoryAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;
import java.util.Map;
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
    public br.com.zup.itau.auditable.core.ItauAuditable itauAuditable() {
        MongoRepository itauAuditableMongoRepository =
                new MongoRepository(mongo().getDatabase(DATABASE_NAME));

        return ItauAuditableBuilder.itauAuditable()
                .registerItauAuditableRepository(itauAuditableMongoRepository)
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
     * Use {@link ItauAuditable}
     * to mark repository methods that you want to audit.
     */
    @Bean
    public ItauAuditableAspect itauAuditableAuditableAspect() {
        return new ItauAuditableAspect(itauAuditable(), authorProvider(), commitPropertiesProvider());
    }

    /**
     * Enables auto-audit aspect for Spring Data repositories. <br/>
     *
     * Use {@link ItauAuditableSpringData}
     * to annotate CrudRepositories you want to audit.
     */
    @Bean
    public ItauAuditableSpringDataRepositoryAspect itauAuditableSpringDataAspect() {
        return new ItauAuditableSpringDataRepositoryAspect(itauAuditable(), authorProvider(),
                commitPropertiesProvider());
    }

    /**
     * <b>INCUBATING - ItauAuditable Async API has incubating status.</b>
     * <br/><br/>
     *
     * Enables asynchronous auto-audit aspect for ordinary repositories.<br/>
     *
     * Use {@link ItauAuditableAsync}
     * to mark repository methods that you want to audit.
     */
    @Bean
    public ItauAuditableAspectAsync itauAuditableAuditableAspectAsync() {
        return new ItauAuditableAspectAsync(itauAuditable(), authorProvider(), commitPropertiesProvider(), itauAuditableAsyncAuditExecutor());
    }

    /**
     * <b>INCUBATING - ItauAuditable Async API has incubating status.</b>
     * <br/><br/>
     */
    @Bean
    public ExecutorService itauAuditableAsyncAuditExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("ItauAuditableAsync-%d")
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
