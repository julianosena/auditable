package br.com.zup.itau.auditable.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use {@code DiffInclude} annotation to tell Itaú Auditable which properties to include in diff/commit
 * operations for a given class.
 * All other properties in this class and all properties in its subclasses will be ignored by Itaú Auditable.
 * <br/>
 *
 * If some properties in a subclass should be included, apply the {@code DiffInclude} annotation on them.
 * <br/><br/>
 *
 * For example, Itaú Auditable will ignore {@code bar} in the {@code A} class and both {@code bar} and {@code qux}
 * in the {@code B} class.
 * <pre>
 * class A {
 *     &#64;Id
 *     &#64;DiffInclude
 *     private Long id;
 *
 *     &#64;DiffInclude
 *     private String foo;
 *
 *     private String bar;
 * }
 *
 * class B extends A {
 *     private String qux;
 * }
 * </pre>
 *
 *
 * The above is equivalent to:
 * <pre>
 * class A {
 *     &#64;Id
 *     private Long id;
 *
 *     private String foo;
 *
 *     &#64;DiffIgnore
 *     private String bar;
 * }
 *
 * class B extends A {
 *     &#64;DiffIgnore
 *     private String qux;
 * }
 * </pre>
 *
 * <b>Warning</b>: {@code DiffInclude} can't be mixed with {@code DiffIgnore} in the same class.
 *
 * @see DiffIgnore
 * @author Iulian Stefanica
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface DiffInclude {
}
