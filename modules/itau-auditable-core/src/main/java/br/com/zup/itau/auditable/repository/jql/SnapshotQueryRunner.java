package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalIdFactory;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;

import java.util.List;
import java.util.Optional;

class SnapshotQueryRunner {
    private final QueryCompiler queryCompiler;
    private final GlobalIdFactory globalIdFactory;
    private final ItauAuditableExtendedRepository repository;

    SnapshotQueryRunner(QueryCompiler queryCompiler, GlobalIdFactory globalIdFactory, ItauAuditableExtendedRepository repository) {
        this.queryCompiler = queryCompiler;
        this.globalIdFactory = globalIdFactory;
        this.repository = repository;
    }

    Optional<CdoSnapshot> runQueryForLatestSnapshot(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return repository.getLatest(globalIdFactory.createFromDto(globalId));
    }

    List<CdoSnapshot> queryForSnapshots(JqlQuery query){
        queryCompiler.compile(query);

        List<CdoSnapshot> result;
        if (query.isAnyDomainObjectQuery()) {
            result = repository.getSnapshots(query.getQueryParams());
        } else
        if (query.isIdQuery()){
            result = repository.getStateHistory(query.getIdFilter(), query.getQueryParams());
        } else
        if (query.isClassQuery()){
            result = repository.getStateHistory(query.getClassFilter(), query.getQueryParams());
        } else
        if (query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            result = repository.getValueObjectStateHistory(filter.getOwnerEntity(), filter.getPath(), query.getQueryParams());
        } else {
            throw new ItauAuditableException(ItauAuditableExceptionCode.MALFORMED_JQL, "queryForSnapshots: " + query + " is not supported");
        }

        return result;
    }
}
