package br.com.zup.itau.auditable.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.repository.jql.QueryBuilder;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringDataAuditable;
import br.com.zup.itau.auditable.spring.auditable.AspectUtil;
import br.com.zup.itau.auditable.spring.auditable.AuthorProvider;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableCommitAdvice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import java.util.Optional;

public class AbstractSpringAuditableRepositoryAspect {
    private final ItauAuditableCommitAdvice javersCommitAdvice;
    private final ItauAuditable javers;

    protected AbstractSpringAuditableRepositoryAspect(ItauAuditable javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.javers = javers;
        this.javersCommitAdvice = new ItauAuditableCommitAdvice(javers, authorProvider, commitPropertiesProvider);
    }

    protected void onSave(JoinPoint pjp, Object returnedObject) {
        getRepositoryInterface(pjp).ifPresent(i ->
                AspectUtil.collectReturnedObjects(returnedObject).forEach(javersCommitAdvice::commitObject));
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
            if (i.isAnnotationPresent(ItauAuditableSpringDataAuditable.class) && CrudRepository.class.isAssignableFrom(i)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }


    void handleDelete(RepositoryMetadata repositoryMetadata, Object domainObjectOrId) {
            if (isIdClass(repositoryMetadata, domainObjectOrId)) {
                Class<?> domainType = repositoryMetadata.getDomainType();
                if (javers.findSnapshots(QueryBuilder.byInstanceId(domainObjectOrId, domainType).limit(1).build()).size() == 0) {
                    return;
                }
                javersCommitAdvice.commitShallowDeleteById(domainObjectOrId, domainType);
            } else if (isDomainClass(repositoryMetadata, domainObjectOrId)) {
                if (javers.findSnapshots(QueryBuilder.byInstance(domainObjectOrId).limit(1).build()).size() == 0) {
                    return;
                }
                javersCommitAdvice.commitShallowDelete(domainObjectOrId);
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
