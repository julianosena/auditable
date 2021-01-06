package br.com.zup.itau.auditable.spring.jpa

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.hibernate.integration.config.HibernateConfig
import br.com.zup.itau.auditable.hibernate.integration.config.HibernateConfig
import br.com.zup.itau.auditable.repository.sql.DialectName
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository
import br.com.zup.itau.auditable.repository.sql.SqlRepositoryBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.TransactionManagementConfigurer

import javax.persistence.EntityManagerFactory

@Configuration()
@EnableJpaRepositories(basePackages = ["br.com.zup.itau.auditable.hibernate.entity"])
@EnableTransactionManagement()
@EnableAspectJAutoProxy
@Import(HibernateConfig)
class MultipleTxManagersConfig extends HibernateConfig implements TransactionManagementConfigurer {


    @Override
    PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager()
    }

    @Bean
    PlatformTransactionManager secondTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    ItauAuditableSqlRepository sqlRepository(){
        SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider())
                .withDialect(DialectName.H2)
                .build()
    }

    @Bean
    ItauAuditable itauAuditable(ItauAuditableSqlRepository sqlRepository,
                  @Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
        TransactionalItauAuditableBuilder
                .itauAuditable()
                .withTxManager(transactionManager)
                .registerItauAuditableRepository(sqlRepository)
                .build()
    }
}