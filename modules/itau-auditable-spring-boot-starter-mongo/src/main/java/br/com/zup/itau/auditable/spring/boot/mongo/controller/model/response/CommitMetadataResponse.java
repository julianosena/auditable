package br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response;

import java.time.LocalDateTime;

public class CommitMetadataResponse {
    private final String author;
    private final LocalDateTime commitDate;

    public CommitMetadataResponse(String author, LocalDateTime commitDate) {
        this.author = author;
        this.commitDate = commitDate;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }
}
