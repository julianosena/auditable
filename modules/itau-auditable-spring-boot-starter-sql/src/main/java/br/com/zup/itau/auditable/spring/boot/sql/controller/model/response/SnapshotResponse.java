package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SnapshotResponse {

    private Long snapshotPk;
    private String type;
    private Long version;
    private Map<String, Object> state;
    private List<String> changedProperties;
    private CommitResponse commit;

    public SnapshotResponse() {}

    public SnapshotResponse(Long snapshotPk, String type, Long version, Map<String, Object> state, List<String> changedProperties, CommitResponse commit) {
        this.snapshotPk = snapshotPk;
        this.type = type;
        this.version = version;
        this.state = state;
        this.changedProperties = changedProperties;
        this.commit = commit;
    }

    public Long getSnapshotPk() {
        return snapshotPk;
    }

    public void setSnapshotPk(Long snapshotPk) {
        this.snapshotPk = snapshotPk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Object getState() {
        return state;
    }

    public void setState(Map<String, Object> state) {
        this.state = state;
    }

    public List<String> getChangedProperties() {
        return changedProperties;
    }

    public void setChangedProperties(List<String> changedProperties) {
        this.changedProperties = changedProperties;
    }

    public CommitResponse getCommit() {
        return commit;
    }

    public void setCommit(CommitResponse commit) {
        this.commit = commit;
    }
}
