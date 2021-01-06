package br.com.zup.itau.auditable.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditableAsync;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import org.springframework.core.annotation.Order;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <b>INCUBATING - ItauAuditable Async API has incubating status.</b>
 * <br/><br/>
 *
 *  Asynchronously commits all arguments passed to methods annotated with {@link ItauAuditableAuditableAsync}
 *  by calling {@link ItauAuditable#commitAsync(String, Object, Executor)} for each method argument.
 *  <br/><br/>
 *
 *  This is the {@link AfterReturning} aspect, it triggers
 *  only if a method exits normally, i.e. if no Exception has been thrown.
 */
@Aspect
@Order(0)
public class ItauAuditableAuditableAspectAsync {
    private final ItauAuditableCommitAdvice javersCommitAdvice;
    private Optional<CompletableFuture<Commit>> lastAsyncCommit = Optional.empty();

    public ItauAuditableAuditableAspectAsync(ItauAuditable javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider,Executor executor) {
        this(new ItauAuditableCommitAdvice(javers, authorProvider, commitPropertiesProvider, executor));
    }

    ItauAuditableAuditableAspectAsync(ItauAuditableCommitAdvice javersCommitAdvice) {
        this.javersCommitAdvice = javersCommitAdvice;
    }

    @AfterReturning("@annotation(br.com.zup.itau.auditable.spring.annotation.ItauAuditableAuditableAsync)")
    public void commitAdvice(JoinPoint pjp) {
        lastAsyncCommit = javersCommitAdvice.commitSaveMethodArgumentsAsync(pjp);
    }

    Optional<CompletableFuture<Commit>> getLastAsyncCommit() {
        return lastAsyncCommit;
    }
}