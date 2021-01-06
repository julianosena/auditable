package br.com.zup.itau.auditable.repository.sql.pico;

import br.com.zup.itau.auditable.core.pico.ItauAuditableModule;
import br.com.zup.itau.auditable.repository.sql.ItauAuditableSqlRepository;
import br.com.zup.itau.auditable.repository.sql.MultitenancyItauAuditableSqlRepository;
import br.com.zup.itau.auditable.repository.sql.finders.CdoSnapshotFinder;
import br.com.zup.itau.auditable.repository.sql.finders.CommitPropertyFinder;
import br.com.zup.itau.auditable.repository.sql.finders.MultitenancyCdoSnapshotFinder;
import br.com.zup.itau.auditable.repository.sql.finders.MultitenancyCommitPropertyFinder;
import br.com.zup.itau.auditable.repository.sql.repositories.*;
import br.com.zup.itau.auditable.repository.sql.schema.*;
import org.polyjdbc.core.query.QueryRunnerFactory;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides Pico beans setup for sql repositories
 *
 * @author bartosz walacik
 */
public class ItauAuditableSqlModule implements ItauAuditableModule {
    private static Class[] moduleComponents = new Class[]{
            MultitenancyItauAuditableSqlRepository.class,
            ItauAuditableSqlRepository.class,
            FixedSchemaFactory.class,
            MultitenancySchemaFactory.class,
            ItauAuditableSchemaManager.class,
            MultitenancyItauAuditableSchemaManager.class,
            QueryRunnerFactory.class,
            GlobalIdRepository.class,
            MultitenancyGlobalIdRepository.class,
            CommitMetadataRepository.class,
            MultitenancyCommitMetadataRepository.class,
            CdoSnapshotRepository.class,
            MultitenancyCdoSnapshotRepository.class,
            CdoSnapshotFinder.class,
            MultitenancyCdoSnapshotFinder.class,
            CommitPropertyFinder.class,
            MultitenancyCommitPropertyFinder.class,
            TableNameProvider.class,
            MultitenancyTableNameProvider.class
    };

    @Override
    public Collection<Class> getComponents() {
        return Arrays.asList(moduleComponents);
    }
}
