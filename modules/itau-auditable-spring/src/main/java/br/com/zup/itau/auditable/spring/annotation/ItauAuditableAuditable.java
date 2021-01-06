package br.com.zup.itau.auditable.spring.annotation;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAuditableAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a method (typically on a method in a Repository)
 * <br/><br/>
 *
 * Triggers {@link ItauAuditable#commit(String, Object)} for each method argument.
 *
 * @see ItauAuditableAuditableAspect
 * @author Pawel Szymczyk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ItauAuditableAuditable {
}
