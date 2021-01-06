package br.com.zup.itau.auditable.spring.boot.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoDatabase;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.repository.mongo.MongoRepository;
import br.com.zup.itau.auditable.spring.auditable.*;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAuditableAspect;
import br.com.zup.itau.auditable.spring.auditable.aspect.springdata.ItauAuditableSpringDataAuditableRepositoryAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Optional;

import static br.com.zup.itau.auditable.repository.mongo.MongoRepository.mongoRepositoryWithDocumentDBCompatibility;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties({ItauAuditableMongoProperties.class})
public class ItauAuditableMongoAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ItauAuditableMongoAutoConfiguration.class);

    @Autowired
    private ItauAuditableMongoProperties javersMongoProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoProperties mongoProperties; //from spring-boot-starter-data-mongodb

    @Autowired
    @Qualifier("javersMongoClientSettings")
    private Optional<MongoClientSettings> mongoClientSettings;

    @Bean(name = "ItauAuditableFromStarter")
    @ConditionalOnMissingBean
    public ItauAuditable javers() {
        logger.info("Starting itau-auditable-spring-boot-starter-mongo ...");

        MongoDatabase mongoDatabase = initItauAuditableMongoDatabase();

        MongoRepository javersRepository = createMongoRepository(mongoDatabase);

        return ItauAuditableBuilder.javers()
                .registerItauAuditableRepository(javersRepository)
                .withProperties(javersMongoProperties)
                .withObjectAccessHook(javersMongoProperties.createObjectAccessHookInstance())
                .build();
    }

    private MongoDatabase initItauAuditableMongoDatabase() {
        if (!javersMongoProperties.isDedicatedMongodbConfigurationEnabled()) {
            MongoDatabase mongoDatabase = getDefaultMongoDatabase();
            logger.info("connecting ItauAuditable to Mongo database '{}' configured in spring.data.mongodb properties",
                        mongoDatabase.getName());
            return mongoDatabase;
        } else {
            MongoDatabase mongoDatabase = ItauAuditableDedicatedMongoFactory
                    .createMongoDatabase(javersMongoProperties, mongoClientSettings);
            logger.info("connecting ItauAuditable to Mongo database '{}' configured in javers.mongodb properties",
                    mongoDatabase.getName());
            return mongoDatabase;
        }
    }

    //from the spring-boot-starter-data-mongodb
    private MongoDatabase getDefaultMongoDatabase() {
        if (getBean(com.mongodb.client.MongoClient.class).isPresent()) {
            return getBean(com.mongodb.client.MongoClient.class).get().getDatabase(mongoProperties.getMongoClientDatabase());
        }
        if (getBean(com.mongodb.MongoClient.class).isPresent()) {
            return getBean(com.mongodb.MongoClient.class).get().getDatabase(mongoProperties.getMongoClientDatabase());
        }
        throw new ItauAuditableException(
                ItauAuditableExceptionCode.RUNTIME_EXCEPTION,
                "Can't fins the default mongoClient bean. It should be crated by the spring-boot-starter-data-mongodb");
    }

    private <T> Optional<T> getBean(Class<T> ofType) {
        try {
            return Optional.of(applicationContext.getBean(ofType));
        } catch (BeansException e) {
            return Optional.empty();
        }
    }

    private MongoRepository createMongoRepository(MongoDatabase mongoDatabase) {
        if (javersMongoProperties.isDocumentDbCompatibilityEnabled()) {
            logger.info("enabling Amazon DocumentDB compatibility");
            return mongoRepositoryWithDocumentDBCompatibility(mongoDatabase, javersMongoProperties.getSnapshotsCacheSize());
        }
        return new MongoRepository(mongoDatabase, javersMongoProperties.getSnapshotsCacheSize());
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

    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    public ItauAuditableAuditableAspect javersAuditableAspect(
            ItauAuditable javers,
            AuthorProvider authorProvider,
            CommitPropertiesProvider commitPropertiesProvider) {
        return new ItauAuditableAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    public ItauAuditableSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(
            ItauAuditable javers,
            AuthorProvider authorProvider,
            CommitPropertiesProvider commitPropertiesProvider) {
        return new ItauAuditableSpringDataAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
