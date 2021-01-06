package br.com.zup.itau.auditable.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.repository.jql.QueryBuilder;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import br.com.zup.itau.auditable.spring.auditable.AspectUtil;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableCommitAdvice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import java.util.Optional;

public class AbstractSpringAuditableRepositoryAspect {
    private final ItauAuditableCommitAdvice itauAuditableCommitAdvice;
    private final ItauAuditable itauAuditable;

    protected AbstractSpringAuditableRepositoryAspect(ItauAuditable itauAuditable, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.itauAuditable = itauAuditable;
        this.itauAuditableCommitAdvice = new ItauAuditableCommitAdvice(itauAuditable, authorProvider, commitPropertiesProvider);
    }

    protected void onSave(JoinPoint pjp, Object returnedObject) {
        getRepositoryInterface(pjp).ifPresent(i ->
                AspectUtil.collectReturnedObjects(returnedObject).forEach(itauAuditableCommitAdvice::commitObject));
    }

    protected void onDelete(JoinPoint pjp) {
        getRepositoryInterface(pjp).ifPresent( i -> {
            RepositoryMetadata metadata = DefaultRepositoryMetadata.getMetadata(i);
            for (Object deletedObject : AspectUtil.collectArguments(pjp)) {
                handleDelete(metadata, deletedObject);
            }
        });
    }

    private Optional<Class> getRepositoryInterface(JoinPoint pjp) {
        for (Class i : pjp.getTarget().getClass().getInterfaces()) {
            if (i.isAnnotationPresent(ItauAuditableSpringData.class) && CrudRepository.class.isAssignableFrom(i)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }


    void handleDelete(RepositoryMetadata repositoryMetadata, Object domainObjectOrId) {
            if (isIdClass(repositoryMetadata, domainObjectOrId)) {
                Class<?> domainType = repositoryMetadata.getDomainType();
                if (itauAuditable.findSnapshots(QueryBuilder.byInstanceId(domainObjectOrId, domainType).limit(1).build()).size() == 0) {
                    return;
                }
                itauAuditableCommitAdvice.commitShallowDeleteById(domainObjectOrId, domainType);
            } else if (isDomainClass(repositoryMetadata, domainObjectOrId)) {
                if (itauAuditable.findSnapshots(QueryBuilder.byInstance(domainObjectOrId).limit(1).build()).size() == 0) {
                    return;
                }
                itauAuditableCommitAdvice.commitShallowDelete(domainObjectOrId);
            } else {
                throw new IllegalArgumentException("Domain object or object id expected");
            }
    }

    private boolean isDomainClass(RepositoryMetadata metadata, Object o) {
        return metadata.getDomainType().isAssignableFrom(o.getClass());
    }

    private boolean isIdClass(RepositoryMetadata metadata, Object o) {
        return metadata.getIdType().isAssignableFrom(o.getClass());
    }

}
