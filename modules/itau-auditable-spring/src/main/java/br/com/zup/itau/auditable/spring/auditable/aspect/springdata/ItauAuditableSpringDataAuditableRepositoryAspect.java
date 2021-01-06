package br.com.zup.itau.auditable.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;

import org.springframework.core.annotation.Order;
import java.util.Map;

/**
 * Calls {@link ItauAuditable#commit(String, Object, Map)} on objects returned from save() methods in Spring Data CrudRepository
 * when a repository is annotated with (class-level) @ItauAuditableSpringDataAuditable.
 * <br/><br/>
 *
 * Calls {@link ItauAuditable#commitShallowDelete(String, Object, Map)} on arguments passed to delete() methods.
 */
@Aspect
@Order(0)
public class ItauAuditableSpringDataAuditableRepositoryAspect extends AbstractSpringAuditableRepositoryAspect {
    public ItauAuditableSpringDataAuditableRepositoryAspect(ItauAuditable javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
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
}
