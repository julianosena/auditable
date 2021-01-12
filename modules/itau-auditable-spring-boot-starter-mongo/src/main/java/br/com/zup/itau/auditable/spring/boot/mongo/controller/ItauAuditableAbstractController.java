package br.com.zup.itau.auditable.spring.boot.mongo.controller;

import br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response.GlobalIdResponse;
import br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response.SnapshotResponse;
import br.com.zup.itau.auditable.spring.boot.mongo.controller.translator.SnapshotToSnapshotResponseTranslator;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.Snapshot;
import br.com.zup.itau.auditable.spring.boot.mongo.usecase.GetRevisionsByIdAndTypeUseCase;
import br.com.zup.itau.auditable.spring.boot.mongo.usecase.exception.GetRevisionsByIdAndTypeUseCaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public abstract class ItauAuditableAbstractController {

    @Autowired
    private GetRevisionsByIdAndTypeUseCase getRevisionsByIdAndTypeUseCase;

    @GetMapping(value = "/{id}/revisions", produces = APPLICATION_JSON_UTF8_VALUE)
    public GlobalIdResponse execute(@PathVariable("id") String id) throws GetRevisionsByIdAndTypeUseCaseException {
        final List<Snapshot> snapshots = this.getRevisionsByIdAndTypeUseCase.execute(this.getType(), id);
        final List<SnapshotResponse> snapshotResponses = snapshots.stream().map(SnapshotToSnapshotResponseTranslator::execute)
                .collect(Collectors.toList());
        return snapshots.isEmpty() ? new GlobalIdResponse() : new GlobalIdResponse(snapshots.get(0).getGlobalId().get("cdoId"), snapshotResponses);
    }

    public abstract String getType();
}
