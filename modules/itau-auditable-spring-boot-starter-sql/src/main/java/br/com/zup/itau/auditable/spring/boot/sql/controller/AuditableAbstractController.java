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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

abstract public class AuditableAbstractController {

    @Autowired
    private GetRevisionsByIdAndTypeUseCase getRevisionsByIdAndTypeUseCase;

    @GetMapping("/revisions/{id}")
    public List<GlobalIdResponse> execute(@PathVariable("id") String id) throws ItauAuditableUseCaseException, ItauAuditableTranslatorException {
        List<GlobalId> revisions = this.getRevisionsByIdAndTypeUseCase.execute(id, this.getType());
        List<GlobalIdResponse> list = new ArrayList<>();
        for (GlobalId revision : revisions) {
            GlobalIdResponse translate = GlobalIdToGlobalIdResponseTranslator.translate(revision);
            list.add(translate);
        }
        return list;
    }

    public abstract String getType();
}
