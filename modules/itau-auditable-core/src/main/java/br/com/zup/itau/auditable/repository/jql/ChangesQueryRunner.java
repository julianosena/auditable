package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;

import java.util.List;

class ChangesQueryRunner {
    private final QueryCompiler queryCompiler;
    private final ItauAuditableExtendedRepository repository;

    ChangesQueryRunner(QueryCompiler queryCompiler, ItauAuditableExtendedRepository repository) {
        this.queryCompiler = queryCompiler;
        this.repository = repository;
    }

    List<Change> queryForChanges(JqlQuery query) {
        queryCompiler.compile(query);

        if (query.isAnyDomainObjectQuery()) {
            return repository.getChanges(query.isNewObjectChanges(), query.getQueryParams());
        }

        if (query.isIdQuery()){
            return repository.getChangeHistory(query.getIdFilter(), query.getQueryParams());
        }

        if (query.isClassQuery()){
            return repository.getChangeHistory(query.getClassFilter(), query.getQueryParams());
        }

        if (query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            return repository.getValueObjectChangeHistory(
                    filter.getOwnerEntity(), filter.getPath(), query.getQueryParams());
        }

        throw new ItauAuditableException(ItauAuditableExceptionCode.MALFORMED_JQL, "queryForChanges: " + query + " is not supported");
    }
}
