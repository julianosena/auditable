package br.com.zup.itau.auditable.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditable;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.EmptyPropertiesProvider;
import org.springframework.core.annotation.Order;

/**
 * Commits all arguments passed to methods annotated with {@link ItauAuditable}
 * by calling {@link br.com.zup.itau.auditable.core.ItauAuditable#commit(String, Object)} for each method argument.
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
public class ItauAuditableAspect {
    private final ItauAuditableCommitAdvice itauAuditableCommitAdvice;

    public ItauAuditableAspect(br.com.zup.itau.auditable.core.ItauAuditable itauAuditable, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this(new ItauAuditableCommitAdvice(itauAuditable, authorProvider, commitPropertiesProvider) );
    }

    public ItauAuditableAspect(br.com.zup.itau.auditable.core.ItauAuditable itauAuditable, AuthorProvider authorProvider) {
        this(itauAuditable, authorProvider, new EmptyPropertiesProvider());
    }

    ItauAuditableAspect(ItauAuditableCommitAdvice itauAuditableCommitAdvice) {
        this.itauAuditableCommitAdvice = itauAuditableCommitAdvice;
    }

    @AfterReturning("@annotation(br.com.zup.itau.auditable.spring.annotation.ItauAuditable)")
    public void commitAdvice(JoinPoint pjp) {
        itauAuditableCommitAdvice.commitSaveMethodArguments(pjp);
    }

    @AfterReturning("@annotation(br.com.zup.itau.auditable.spring.annotation.ItauAuditableDelete)")
    public void commitDeleteAdvice(JoinPoint pjp) {
        itauAuditableCommitAdvice.commitDeleteMethodArguments(pjp);
    }
}