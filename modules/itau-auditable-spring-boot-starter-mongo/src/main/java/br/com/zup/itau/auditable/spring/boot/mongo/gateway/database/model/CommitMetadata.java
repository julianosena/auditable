package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public class CommitMetadata {
    private final String author;
    private final Map<String, String> properties;
    private final LocalDateTime commitDate;
    private final Instant commitDateInstant;
    @Field("id")
    private final long commitId;

    public CommitMetadata(String author, Map<String, String> properties, LocalDateTime commitDate, Instant commitDateInstant, long commitId) {
        this.author = author;
        this.properties = properties;
        this.commitDate = commitDate;
        this.commitDateInstant = commitDateInstant;
        this.commitId = commitId;
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

    public long getCommitId() {
        return commitId;
    }
}
