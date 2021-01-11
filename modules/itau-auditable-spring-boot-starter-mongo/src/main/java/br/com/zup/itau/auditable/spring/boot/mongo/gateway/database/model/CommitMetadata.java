package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public class CommitMetadata {
    private final String author;
    private final Map<String, String> properties;
    private final LocalDateTime commitDate;
    private final Instant commitDateInstant;
    private final String id;

    public CommitMetadata(String author, Map<String, String> properties, LocalDateTime commitDate, Instant commitDateInstant, String id) {
        this.author = author;
        this.properties = properties;
        this.commitDate = commitDate;
        this.commitDateInstant = commitDateInstant;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    public Instant getCommitDateInstant() {
        return commitDateInstant;
    }

    public String getId() {
        return id;
    }
}
