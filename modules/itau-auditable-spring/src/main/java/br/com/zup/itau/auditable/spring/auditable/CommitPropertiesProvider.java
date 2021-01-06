package br.com.zup.itau.auditable.spring.auditable;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository;
import br.com.zup.itau.auditable.spring.annotation.ItauAuditableSpringData;
import org.springframework.data.repository.CrudRepository;

import java.util.Collections;
import java.util.Map;

/**
 * Provides commit properties
 * for {@link ItauAuditable#commit(String, Object, Map)}
 * called by ItauAuditable auto-audit aspect &mdash; {@link ItauAuditableSpringData}.
 * <br/><br/>
 *
 * Implementation has to be thread-safe.
 *
 * @author bartosz.walacik
 */
public interface CommitPropertiesProvider {

    /**
     * Provides object-specific ItauAuditable commit properties when a  given object is committed (saved or updated)
     * to {@link ItauAuditableRepository}.
     *
     * <br/><br/>
     * This method is called by {@link ItauAuditableSpringData} aspect
     * to get properties for commit created when
     * {@link CrudRepository#save(Object)} and
     * {@link CrudRepository#saveAll(Iterable)} methods are called.
     *
     * <br/><br/>
     * Default implementation returns empty Map
     *
     * @param domainObject saved object
     */
    default Map<String, String> provideForCommittedObject(Object domainObject) {
        return Collections.emptyMap();
    }

    /**
     * Provides object-specific commit properties when a given object is deleted from {@link ItauAuditableRepository}.
     *
     * <br/><br/>
     * This method is called by {@link ItauAuditableSpringData} aspect
     * to get properties for commit created when
     * {@link CrudRepository#delete(Object)} and
     * {@link CrudRepository#deleteAll(Iterable)} methods are called.
     *
     * <br/><br/>
     * Default implementation delegates to {@link #provideForCommittedObject(Object)}
     *
     * @param domainObject affected object
     */
    default Map<String, String> provideForDeletedObject(Object domainObject) {
        return provideForCommittedObject(domainObject);
    }

    /**
     * Provides object-specific commit properties when a given object is deleted from {@link ItauAuditableRepository}
     * by its Id.
     *
     * <br/><br/>
     * This method is called by {@link ItauAuditableSpringData} aspect
     * to get properties for commit created when
     * {@link CrudRepository#deleteById(Object)} methods are called.
     *
     * <br/><br/>
     * Default implementation returns empty Map
     */
    default Map<String, String> provideForDeleteById(Class<?> domainObjectClass, Object domainObjectId) {
        return Collections.emptyMap();
    }

    /**
     * This method is deprecated
     * and replaced with {@link #provideForCommittedObject(Object)}
     *
     * @Deprecated
     */
    @Deprecated
    default Map<String, String> provide() {
        return Collections.emptyMap();
    }
}
