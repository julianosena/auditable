package br.com.zup.itau.auditable.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditable;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.EmptyPropertiesProvider;
import org.springframework.core.annotation.Order;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Commits all arguments passed to methods annotated with {@link ItauAuditableAuditable}
 * by calling {@link ItauAuditable#commit(String, Object)} for each method argument.
 * <br/><br/>
 *
 * This is the {@link AfterReturning} aspect, it triggers
 * only if a method exits normally, i.e. if no Exception has been thrown.
 * <br/><br/>
 *
 * Spring @Transactional attributes (like noRollbackFor or noRollbackForClassName)
 * have no effects on this aspect.
 */
@Aspect
@Order(0)
public class ItauAuditableAuditableAspect {
    private final ItauAuditableCommitAdvice javersCommitAdvice;

    public ItauAuditableAuditableAspect(ItauAuditable javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this(new ItauAuditableCommitAdvice(javers, authorProvider, commitPropertiesProvider) );
    }

    public ItauAuditableAuditableAspect(ItauAuditable javers, AuthorProvider authorProvider) {
        this(javers, authorProvider, new EmptyPropertiesProvider());
    }

    ItauAuditableAuditableAspect(ItauAuditableCommitAdvice javersCommitAdvice) {
        this.javersCommitAdvice = javersCommitAdvice;
    }

    @AfterReturning("@annotation(br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditable)")
    public void commitAdvice(JoinPoint pjp) {
        javersCommitAdvice.commitSaveMethodArguments(pjp);
    }

    @AfterReturning("@annotation(br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditableDelete)")
    public void commitDeleteAdvice(JoinPoint pjp) {
        javersCommitAdvice.commitDeleteMethodArguments(pjp);
    }
}