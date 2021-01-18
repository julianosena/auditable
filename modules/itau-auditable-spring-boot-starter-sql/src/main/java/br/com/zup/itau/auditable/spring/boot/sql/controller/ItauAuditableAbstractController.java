package br.com.zup.itau.auditable.spring.boot.sql.controller;

import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.GlobalIdResponse;
import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator.GlobalIdToGlobalIdResponseTranslator;
import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator.exception.ItauAuditableTranslatorException;
import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.usecase.GetRevisionsByIdAndTypeUseCase;
import br.com.zup.itau.auditable.usecase.exception.ItauAuditableUseCaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

abstract public class ItauAuditableAbstractController {

    @Autowired
    private GetRevisionsByIdAndTypeUseCase getRevisionsByIdAndTypeUseCase;

    @GetMapping("/{id}/revisions")
    public GlobalIdResponse execute(@PathVariable("id") String id) throws ItauAuditableUseCaseException, ItauAuditableTranslatorException {
        Optional<GlobalId> revision = this.getRevisionsByIdAndTypeUseCase.execute(id, this.getType());
        return GlobalIdToGlobalIdResponseTranslator.translate(revision);
    }

    public abstract String getType();
}
