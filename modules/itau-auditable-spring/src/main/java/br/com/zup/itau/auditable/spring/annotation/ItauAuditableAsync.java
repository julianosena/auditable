package br.com.zup.itau.auditable.spring.annotation;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAspectAsync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Executor;

/**
 * <b>INCUBATING - ItauAuditable Async API has incubating status.</b>
 * <br/><br/>
 *
 * Enables asynchronous auto-audit aspect when put on a method (typically in a Repository).
 * <br/><br/>
 *
 * Triggers {@link ItauAuditable#commitAsync(String, Object, Executor)} for each method argument.
 * <br/><br/>
 *
 * <b>Important!</b> Works with MongoDB, not implemented for SQL repositories.
 *
 * @see ItauAuditableAspectAsync
 * @author Razi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ItauAuditableAsync {
}
