package br.com.zup.itau.auditable.spring.jpa;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.Changes;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.changelog.ChangeProcessor;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChange;
import br.com.zup.itau.auditable.core.json.JsonConverter;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.property.Property;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.repository.jql.GlobalIdDTO;
import br.com.zup.itau.auditable.repository.jql.JqlQuery;
import br.com.zup.itau.auditable.repository.sql.MultitenancyItauAuditableSqlRepository;
import br.com.zup.itau.auditable.shadow.Shadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.*;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * Transactional wrapper for core Itaú Auditable instance.
 * Provides integration with Spring JPA TransactionManager
 *
 * @author bartosz walacik
 */
public class MultitenancyItauAuditableTransactionalDecorator implements InitializingBean, ItauAuditable {
    private static final Logger logger = LoggerFactory.getLogger(MultitenancyItauAuditableTransactionalDecorator.class);

    private final ItauAuditable delegate;
    private final MultitenancyItauAuditableSqlRepository itauAuditableSqlRepository;

    private final PlatformTransactionManager txManager;

    MultitenancyItauAuditableTransactionalDecorator(ItauAuditable delegate, MultitenancyItauAuditableSqlRepository itauAuditableSqlRepository, PlatformTransactionManager txManager) {
        Validate.argumentsAreNotNull(delegate, itauAuditableSqlRepository, txManager);
        this.delegate = delegate;
        this.itauAuditableSqlRepository = itauAuditableSqlRepository;
        this.txManager = txManager;
    }

    @Override
    public CompletableFuture<Commit> commitAsync(String author, Object currentVersion, Map<String, String> commitProperties, Executor executor) {
        throw new ItauAuditableException(ItauAuditableExceptionCode.NOT_IMPLEMENTED,
                "itauAuditable.commitAsync() is not available for SQL");
    }

    @Override
    public CompletableFuture<Commit> commitAsync(String author, Object currentVersion, Executor executor) {
        throw new ItauAuditableException(ItauAuditableExceptionCode.NOT_IMPLEMENTED,
                "itauAuditable.commitAsync() is not available for SQL");
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion) {
        registerRollbackListener();
        this.ensureSchema();
        return delegate.commit(author, currentVersion);
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion, Map<String, String> commitProperties) {
        registerRollbackListener();
        this.ensureSchema();
        return delegate.commit(author, currentVersion, commitProperties);
    }

    @Override
    @Transactional
    public Commit commitShallowDelete(String author, Object deleted) {
        this.ensureSchema();
        return delegate.commitShallowDelete(author, deleted);
    }

    @Override
    @Transactional
    public Commit commitShallowDelete(String author, Object deleted, Map<String, String> properties) {
        this.ensureSchema();
        return delegate.commitShallowDelete(author, deleted, properties);
    }

    @Override
    @Transactional
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId) {
        this.ensureSchema();
        return delegate.commitShallowDeleteById(author, globalId);
    }

    @Override
    @Transactional
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId, Map<String, String> properties) {
        this.ensureSchema();
        return delegate.commitShallowDeleteById(author, globalId, properties);
    }

    @Override
    public Diff compare(Object oldVersion, Object currentVersion) {
        return delegate.compare(oldVersion, currentVersion);
    }

    @Override
    public <T> Diff compareCollections(Collection<T> oldVersion, Collection<T> currentVersion, Class<T> itemClass) {
        return delegate.compareCollections(oldVersion, currentVersion, itemClass);
    }

    @Override
    public Diff initial(Object newDomainObject) {
        return delegate.initial(newDomainObject);
    }

    @Transactional
    @Override
    public Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entityClass) {
        return delegate.getLatestSnapshot(localId, entityClass);
    }

    @Transactional
    @Override
    public Optional<CdoSnapshot> getHistoricalSnapshot(Object localId, Class entity, LocalDateTime effectiveDate) {
        return delegate.getHistoricalSnapshot(localId, entity, effectiveDate);
    }

    @Transactional
    @Override
    public <T> List<Shadow<T>> findShadows(JqlQuery query) {
        return delegate.findShadows(query);
    }

    @Transactional
    @Override
    public <T> Stream<Shadow<T>> findShadowsAndStream(JqlQuery query) {
        return delegate.findShadowsAndStream(query);
    }

    @Transactional
    @Override
    public List<CdoSnapshot> findSnapshots(JqlQuery query) {
        return delegate.findSnapshots(query);
    }

    @Transactional
    @Override
    public Changes findChanges(JqlQuery query) {
        return delegate.findChanges(query);
    }

    @Override
    public JsonConverter getJsonConverter() {
        return delegate.getJsonConverter();
    }

    @Override
    public <T> T processChangeList(List<Change> changes, ChangeProcessor<T> changeProcessor) {
        return delegate.processChangeList(changes, changeProcessor);
    }

    @Override
    public <T extends ItauAuditableType> T getTypeMapping(Type clientsType) {
        return delegate.getTypeMapping(clientsType);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
       ensureSchema();
    }

    private void ensureSchema() {
        if (itauAuditableSqlRepository.getConfiguration().isSchemaManagementEnabled()) {
            TransactionTemplate tmpl = new TransactionTemplate(txManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    itauAuditableSqlRepository.ensureSchema();
                }
            });
        }
    }

    @Override
    public Property getProperty(PropertyChange propertyChange) {
        return delegate.getProperty(propertyChange);
    }

    private void registerRollbackListener() {
        if (itauAuditableSqlRepository.getConfiguration().isGlobalIdCacheDisabled()) {
            return;
        }
        if(TransactionSynchronizationManager.isSynchronizationActive() &&
           TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
                @Override
                public void afterCompletion(int status) {
                    if (TransactionSynchronization.STATUS_ROLLED_BACK == status) {
                        logger.info("evicting itauAuditableSqlRepository local cache due to transaction rollback");
                        itauAuditableSqlRepository.evictCache();
                    }
                }
            });
        }
    }
}
