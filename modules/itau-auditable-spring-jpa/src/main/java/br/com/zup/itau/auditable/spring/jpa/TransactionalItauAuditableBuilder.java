package br.com.zup.itau.auditable.spring.jpa;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author bartosz walacik
 */
public final class TransactionalItauAuditableBuilder extends ItauAuditableBuilder {
    private PlatformTransactionManager txManager;

    private TransactionalItauAuditableBuilder() {
    }

    public static TransactionalItauAuditableBuilder javers() {
        return new TransactionalItauAuditableBuilder();
    }

    public TransactionalItauAuditableBuilder withTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
        return this;
    }

    @Override
    public ItauAuditable build() {
        if (txManager == null) {
            throw new ItauAuditableException(ItauAuditableExceptionCode.TRANSACTION_MANAGER_NOT_SET);
        }

        ItauAuditable javersCore = super.assembleItauAuditableInstance();

        ItauAuditable javersTransactional = new ItauAuditableTransactionalDecorator(javersCore, getContainerComponent(ItauAuditableSqlRepository.class), txManager);

        return javersTransactional;
    }
}
