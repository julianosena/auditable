package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "jv_commit")
public class CommitDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "commit_id")
    private Long commitId;

    @Column(name = "commit_pk")
    private Long commitPk;

    @Column(name = "author")
    private String author;

    @Column(name = "commit_date")
    private LocalDateTime commitDate;

    public CommitDatabase() { }

    public Long getCommitId() {
        return commitId;
    }

    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }

    public Long getCommitPk() {
        return commitPk;
    }

    public void setCommitPk(Long commitPk) {
        this.commitPk = commitPk;
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
