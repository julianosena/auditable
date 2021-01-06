package br.com.zup.itau.auditable.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;
import br.com.zup.itau.auditable.core.metamodel.type.PrimitiveOrValueType;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableDelete;
import br.com.zup.itau.auditable.spring.auditable.AspectUtil;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static br.com.zup.itau.auditable.repository.jql.InstanceIdDTO.instanceId;

/**
 * @author Pawel Szymczyk
 */
public class ItauAuditableCommitAdvice {

    private final ItauAuditable itauAuditable;
    private final AuthorProvider authorProvider;
    private final CommitPropertiesProvider commitPropertiesProvider;
    private final Executor executor;

    public ItauAuditableCommitAdvice(ItauAuditable itauAuditable, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.itauAuditable = itauAuditable;
        this.authorProvider = authorProvider;
        this.commitPropertiesProvider = commitPropertiesProvider;
        this.executor = null;
    }

    public ItauAuditableCommitAdvice(ItauAuditable itauAuditable, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider, Executor executor) {
		this.itauAuditable = itauAuditable;
		this.authorProvider = authorProvider;
		this.commitPropertiesProvider = commitPropertiesProvider;
    	this.executor = executor;
	}

	void commitSaveMethodArguments(JoinPoint pjp) {
        for (Object arg : AspectUtil.collectArguments(pjp)) {
            commitObject(arg);
        }
    }

    void commitDeleteMethodArguments(JoinPoint jp) {
        for (Object arg : AspectUtil.collectArguments(jp)) {
            ItauAuditableType itauAuditableType = itauAuditable.getTypeMapping(arg.getClass());
            if (itauAuditableType instanceof ManagedType) {
                commitShallowDelete(arg);
            } else if (itauAuditableType instanceof PrimitiveOrValueType) {
                commitShallowDeleteById(arg, getDomainTypeToDelete(jp, arg));
            }
        }
    }

    private Class<?> getDomainTypeToDelete(JoinPoint jp, Object id) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        ItauAuditableDelete itauAuditableAuditableDelete = method.getAnnotation(ItauAuditableDelete.class);
        Class<?> entity = itauAuditableAuditableDelete.entity();
        if (entity == Void.class) {
            throw new ItauAuditableException(ItauAuditableExceptionCode.WRONG_USAGE_OF_JAVERS_AUDITABLE_DELETE, id, method);
        }
        return entity;
    }

    public void commitObject(Object domainObject) {
        String author = authorProvider.provide();
        itauAuditable.commit(author, domainObject, propsForCommit(domainObject));
    }

    public void commitShallowDelete(Object domainObject) {
        String author = authorProvider.provide();

        itauAuditable.commitShallowDelete(author, domainObject, Maps.merge(
                commitPropertiesProvider.provideForDeletedObject(domainObject),
                commitPropertiesProvider.provide()));
    }

    public void commitShallowDeleteById(Object domainObjectId, Class<?> domainType) {
        String author = authorProvider.provide();

        itauAuditable.commitShallowDeleteById(author, instanceId(domainObjectId, domainType), Maps.merge(
                commitPropertiesProvider.provideForDeleteById(domainType, domainObjectId),
                commitPropertiesProvider.provide()));
    }

    Optional<CompletableFuture<Commit>> commitSaveMethodArgumentsAsync(JoinPoint pjp) {
        List<CompletableFuture<Commit>> futures = AspectUtil.collectArguments(pjp)
                .stream()
                .map(arg -> commitObjectAsync(arg))
                .collect(Collectors.toList());

        return futures.size() == 0 ? Optional.empty() : Optional.of(futures.get(futures.size() - 1));
    }

    CompletableFuture<Commit> commitObjectAsync(Object domainObject) {
        String author = this.authorProvider.provide();
        return this.itauAuditable.commitAsync(author, domainObject, propsForCommit(domainObject), executor);
    }

    private Map<String, String> propsForCommit(Object domainObject) {
        return Maps.merge(
                commitPropertiesProvider.provideForCommittedObject(domainObject),
                commitPropertiesProvider.provide());
    }
}
