package br.com.zup.itau.auditable.spring.annotation;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.spring.auditable.aspect.ItauAuditableAuditableAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a deleting method (typically on a Repository method)
 * <br/><br/>
 *
 * Triggers {@link ItauAuditable#commitShallowDelete} for each method argument.
 * <br/><br/>
 *
 * Usage:
 *
 * <pre>
 *    {@literal @}ItauAuditableAuditableDelete
 *     void delete(DummyEntity entity) {
 *         ...
 *     }
 * </pre>
 *
 * or:
 *
 * <pre>
 *    {@literal @}ItauAuditableAuditableDelete(entity = DummyEntity)
 *     void deleteById(String id) {
 *         ...
 *     }
 * </pre>
 * @see ItauAuditableAuditableAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ItauAuditableAuditableDelete {

    /**
     * Entity class, required only when deleting by id, for example:
     */
    Class<?> entity() default Void.class;
}
