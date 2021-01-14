package br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CommitMetadataResponse {
    private final String author;
    private final LocalDateTime commitDate;
    @JsonProperty("commitId")
    private final Long id;

    public CommitMetadataResponse(String author, LocalDateTime commitDate, Long id) {
        this.author = author;
        this.commitDate = commitDate;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    public Long getId() {
        return id;
    }
}
