package br.com.zup.itau.auditable.spring.boot.sql.controller;

import br.com.zup.itau.auditable.spring.boot.sql.usecase.GetRevisionsByIdAndTypeUseCase;
import br.com.zup.itau.auditable.usecase.exception.ItauAuditableUseCaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

abstract public class AbstractController {

    @Autowired
    private GetRevisionsByIdAndTypeUseCase getRevisionsByIdAndTypeUseCase;

    @GetMapping("/revisions/{id}")
    public void execute(@PathVariable("id") Long id) throws ItauAuditableUseCaseException {
        this.getRevisionsByIdAndTypeUseCase.execute(id, this.getType());
    }

    public abstract String getType();
}
