package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response;

import java.time.LocalDateTime;

public class CommitResponse {

    private Long commitId;
    private Long commitFk;
    private String author;
    private LocalDateTime commitDate;

    public CommitResponse(Long commitId, Long commitFk, String author, LocalDateTime commitDate) {
        this.commitId = commitId;
        this.commitFk = commitFk;
        this.author = author;
        this.commitDate = commitDate;
    }

    public Long getCommitId() {
        return commitId;
    }

    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }

    public Long getCommitFk() {
        return commitFk;
    }

    public void setCommitFk(Long commitFk) {
        this.commitFk = commitFk;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(LocalDateTime commitDate) {
        this.commitDate = commitDate;
    }
}
