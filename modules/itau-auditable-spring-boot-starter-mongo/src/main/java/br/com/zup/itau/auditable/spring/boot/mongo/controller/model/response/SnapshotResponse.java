package br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response;

import java.util.List;
import java.util.Map;

public class SnapshotResponse {

    private final String type;

    private final long version;

    private final Map<String, Object> state;

    private final CommitMetadataResponse commit;

    private final List<String> changedProperties;

    public SnapshotResponse(String type,
                            long version,
                            Map<String, Object> state,
                            CommitMetadataResponse commit,
                            List<String> changedProperties) {
        this.type = type;
        this.version = version;
        this.state = state;
        this.commit = commit;
        this.changedProperties = changedProperties;
    }

    public String getType() {
        return type;
    }

    public long getVersion() {
        return version;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public CommitMetadataResponse getCommit() {
        return commit;
    }

    public List<String> getChangedProperties() {
        return changedProperties;
    }
}
