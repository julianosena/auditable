package br.com.zup.itau.auditable.spring.boot.sql.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

abstract public class AbstractController {

    public abstract String getType();

    @GetMapping("/revisions/{id}")
    public void execute(@PathVariable("id") int id) {
        this.getRevisionsByIdAndTypeUseCase.execute(id, this.getType());
    }
}
