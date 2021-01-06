package br.com.zup.itau.auditable.spring.annotation;

import br.com.zup.itau.auditable.spring.auditable.aspect.springdata.ItauAuditableSpringDataAuditableRepositoryAspect;
import org.springframework.data.repository.CrudRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables ItauAuditable auto-audit aspect when put on Spring Data {@link CrudRepository}
 *
 * @see ItauAuditableSpringDataAuditableRepositoryAspect
 * @author Florian Gessner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ItauAuditableSpringDataAuditable {
}
