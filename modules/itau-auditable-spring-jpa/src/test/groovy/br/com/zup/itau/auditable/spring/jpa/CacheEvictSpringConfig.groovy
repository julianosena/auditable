package br.com.zup.itau.auditable.spring.jpa

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.hibernate.integration.config.HibernateConfig
import br.com.zup.itau.auditable.hibernate.entity.PersonCrudRepository
import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository
import br.com.zup.itau.auditable.repository.sql.SqlRepositoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration()
@EnableJpaRepositories(["br.com.zup.itau.auditable.hibernate.entity"])
@EnableTransactionManagement
@EnableAspectJAutoProxy
@Import(HibernateConfig)
class CacheEvictSpringConfig extends HibernateConfig {
    @Bean
    ItauAuditableSqlRepository sqlRepository(){
        SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider())
                .withDialect(DialectName.H2)
                .build()
    }

    @Bean
    ItauAuditable javers(ItauAuditableSqlRepository sqlRepository, PlatformTransactionManager txManager) {
        TransactionalItauAuditableBuilder
                .javers()
                .withTxManager(txManager)
                .registerItauAuditableRepository(sqlRepository)
                .build()
    }

    @Bean
    ErrorThrowingService errorThrowingService(PersonCrudRepository repository) {
        new ErrorThrowingService(repository)
    }
}
