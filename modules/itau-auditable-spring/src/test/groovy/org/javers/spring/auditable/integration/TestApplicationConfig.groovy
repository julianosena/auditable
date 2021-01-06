package br.com.zup.itau.auditable.spring.auditable.integration

import com.mongodb.client.MongoClient
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider
import br.com.zup.itau.auditable.spring.example.ItauAuditableSpringMongoApplicationConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@ComponentScan(basePackages = "br.com.zup.itau.auditable.spring.repository")
@EnableMongoRepositories(["br.com.zup.itau.auditable.spring.repository"])
@EnableAspectJAutoProxy
class TestApplicationConfig extends ItauAuditableSpringMongoApplicationConfig {

    @Autowired
    EmbeddedMongoFactory.EmbeddedMongo embeddedMongo

    @Bean
    @Override
    MongoClient mongo() {
        embeddedMongo.client
    }

    @Bean(destroyMethod = "stop")
    EmbeddedMongoFactory.EmbeddedMongo embeddedMongo() {
        EmbeddedMongoFactory.create()
    }

    @Bean
    CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            Map<String, String> provideForCommittedObject(Object domainObject) {
                return ["key":"ok"]
            }
        }
    }
}
