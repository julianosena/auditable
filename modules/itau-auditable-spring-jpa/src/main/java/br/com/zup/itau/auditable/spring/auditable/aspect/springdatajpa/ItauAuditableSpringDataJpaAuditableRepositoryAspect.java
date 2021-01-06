package br.com.zup.itau.auditable.spring.auditable.aspect.springdatajpa;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.aspect.springdata.AbstractSpringAuditableRepositoryAspect;
import org.springframework.core.annotation.Order;

/**
 * Commits all arguments passed to save(), delete() and saveAndFlush() methods
 * in Spring Data JpaRepository
 * when repositories are annotated with (class-level) @ItauAuditableSpringDataAuditable.
 */
@Aspect
@Order(0)
public class ItauAuditableSpringDataJpaAuditableRepositoryAspect extends AbstractSpringAuditableRepositoryAspect {
    public ItauAuditableSpringDataJpaAuditableRepositoryAspect(ItauAuditable javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    @AfterReturning("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteExecuted(JoinPoint pjp) {
        onDelete(pjp);
    }

    @AfterReturning("execution(public * deleteById(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteByIdExecuted(JoinPoint pjp) {
        onDelete(pjp);
    }

    @AfterReturning("execution(public * deleteAll(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteAllExecuted(JoinPoint pjp) {
        onDelete(pjp);
    }

    @AfterReturning(value = "execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)", returning = "responseEntity")
    public void onSaveExecuted(JoinPoint pjp, Object responseEntity) {
        onSave(pjp, responseEntity);
    }

    @AfterReturning(value = "execution(public * saveAll(..)) && this(org.springframework.data.repository.CrudRepository)", returning = "responseEntity")
    public void onSaveAllExecuted(JoinPoint pjp, Object responseEntity) {
        onSave(pjp, responseEntity);
    }

    @AfterReturning(value = "execution(public * saveAndFlush(..)) && this(org.springframework.data.jpa.repository.JpaRepository)", returning = "responseEntity")
    public void onSaveAndFlushExecuted(JoinPoint pjp, Object responseEntity) {
        onSave(pjp, responseEntity);
    }

    @AfterReturning("execution(public * deleteInBatch(..)) && this(org.springframework.data.jpa.repository.JpaRepository)")
    public void onDeleteInBatchExecuted(JoinPoint pjp) {
        onDelete(pjp);
    }
}
