package br.com.zup.itau.auditable.core;

/**
 * A strategy used in
 * {@link ItauAuditableBuilder#registerIgnoredClassesStrategy(IgnoredClassesStrategy)}
 */
@FunctionalInterface
public interface IgnoredClassesStrategy {

    /**
     * Allows to mark classes as ignored by ItauAuditable.
     * <br/><br/>
     *
     * When a class is ignored, all properties
     * (found in other classes) with this class type are ignored.
     * <br/><br/>
     *
     * Called in runtime once for each class
     *
     * @return true if a class should be ignored
     */
    boolean isIgnored(Class<?> domainClass);
}
