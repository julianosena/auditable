package br.com.zup.itau.auditable.spring.jpa;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository;
import br.com.zup.itau.auditable.repository.sql.MultitenancyItauAuditableSqlRepository;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author bartosz walacik
 */
public final class MultitenancyTransactionalItauAuditableBuilder extends ItauAuditableBuilder {
    private PlatformTransactionManager txManager;

    private MultitenancyTransactionalItauAuditableBuilder() {
    }

    public static MultitenancyTransactionalItauAuditableBuilder javers() {
        return new MultitenancyTransactionalItauAuditableBuilder();
    }

    public MultitenancyTransactionalItauAuditableBuilder withTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
        return this;
    }

    @Override
    public ItauAuditable build() {
        if (txManager == null) {
            throw new ItauAuditableException(ItauAuditableExceptionCode.TRANSACTION_MANAGER_NOT_SET);
        }

        ItauAuditable javersCore = super.assembleItauAuditableInstance();

        ItauAuditable javersTransactional = new MultitenancyItauAuditableTransactionalDecorator(javersCore, getContainerComponent(MultitenancyItauAuditableSqlRepository.class), txManager);

        return javersTransactional;
    }
}
