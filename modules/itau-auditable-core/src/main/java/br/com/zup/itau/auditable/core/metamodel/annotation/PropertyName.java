package br.com.zup.itau.auditable.core.metamodel.annotation;

import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use PropertyName annotation to give a name for a Class property (getter or field).
 * <br/>
 * This name will be used <b>everywhere</b> by JaVers.
 * <br/><br/>
 *
 * This annotation (together with {@link TypeName}) solves the problem of refactoring names while using
 * {@link ItauAuditableRepository}.
 *
 * @since 3.1
 */
@Target ({FIELD, METHOD})
@Retention (RUNTIME)
public @interface PropertyName {
	String value();
}
