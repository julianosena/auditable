package br.com.zup.itau.auditable.hibernate.integration

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.hibernate.integration.config.HibernateConfig
import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository
import br.com.zup.itau.auditable.repository.sql.SqlRepositoryBuilder
import br.com.zup.itau.auditable.spring.jpa.JpaHibernateConnectionProvider
import br.com.zup.itau.auditable.spring.jpa.TransactionalItauAuditableBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = ["br.com.zup.itau.auditable.hibernate.entity"])
@Import(HibernateConfig)
class ItauAuditableBeanHibernateProxyConfig {

    /**
     * Creates Itaú Auditable instance with {@link ItauAuditableSqlRepository}
     */
    @Bean
    ItauAuditable itauAuditable(JpaHibernateConnectionProvider jpaHibernateConnectionProvider,
                         PlatformTransactionManager txManager) {

        ItauAuditableSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaHibernateConnectionProvider)
                .withDialect(DialectName.H2)
                .build()

        return TransactionalItauAuditableBuilder
                .itauAuditable()
                .withTxManager(txManager)
                .registerItauAuditableRepository(sqlRepository)
                .withObjectAccessHook(new HibernateUnproxyObjectAccessHook())
                .withMappingStyle(MappingStyle.BEAN)
                .build()
    }

}
