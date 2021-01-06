package br.com.zup.itau.auditable.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableAsync;
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
 *  Asynchronously commits all arguments passed to methods annotated with {@link ItauAuditableAsync}
 *  by calling {@link ItauAuditable#commitAsync(String, Object, Executor)} for each method argument.
 *  <br/><br/>
 *
 *  This is the {@link AfterReturning} aspect, it triggers
 *  only if a method exits normally, i.e. if no Exception has been thrown.
 */
@Aspect
@Order(0)
public class ItauAuditableAspectAsync {
    private final ItauAuditableCommitAdvice itauAuditableCommitAdvice;
    private Optional<CompletableFuture<Commit>> lastAsyncCommit = Optional.empty();

    public ItauAuditableAspectAsync(ItauAuditable itauAuditable, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider, Executor executor) {
        this(new ItauAuditableCommitAdvice(itauAuditable, authorProvider, commitPropertiesProvider, executor));
    }

    ItauAuditableAspectAsync(ItauAuditableCommitAdvice itauAuditableCommitAdvice) {
        this.itauAuditableCommitAdvice = itauAuditableCommitAdvice;
    }

    @AfterReturning("@annotation(br.com.zup.itau.auditable.spring.annotation.ItauAuditableAsync)")
    public void commitAdvice(JoinPoint pjp) {
        lastAsyncCommit = itauAuditableCommitAdvice.commitSaveMethodArgumentsAsync(pjp);
    }

    Optional<CompletableFuture<Commit>> getLastAsyncCommit() {
        return lastAsyncCommit;
    }
}