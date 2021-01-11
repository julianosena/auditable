package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "audit_snapshots")
public class Snapshot {

    @Id
    private final String id;

    private final CommitMetadata commitMetadata;

    private final Map<String, Object> state;

    private final SnapshotType type;

    private final List<String> changedProperties;

    private final long version;

    private final Map<String, String> globalId;

    public Snapshot(String id,
                    CommitMetadata commitMetadata,
                    Map<String, Object> state,
                    SnapshotType type,
                    List<String> changedProperties,
                    long version,
                    Map<String, String> globalId) {
        this.id = id;
        this.commitMetadata = commitMetadata;
        this.state = state;
        this.type = type;
        this.changedProperties = changedProperties;
        this.version = version;
        this.globalId = globalId;
    }

    public String getId() {
        return id;
    }

    public CommitMetadata getCommitMetadata() {
        return commitMetadata;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public SnapshotType getType() {
        return type;
    }

    public List<String> getChangedProperties() {
        return changedProperties;
    }

    public long getVersion() {
        return version;
    }

    public Map<String, String> getGlobalId() {
        return globalId;
    }
}
