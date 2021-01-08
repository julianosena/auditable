package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model;

import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshotState;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.object.SnapshotType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "jv_snapshots")
public class CdoSnapshot {

    @Id
    private String id;

    private CommitMetadata commitMetadata;

    private CdoSnapshotState state;

    private SnapshotType type;

    private List<String> changed;

    private long version;

    private GlobalId globalId;

    public CdoSnapshot() {
    }

    public CdoSnapshot(String id,
                       CommitMetadata commitMetadata,
                       CdoSnapshotState state,
                       SnapshotType type,
                       List<String> changed,
                       long version,
                       GlobalId globalId) {
        this.id = id;
        this.commitMetadata = commitMetadata;
        this.state = state;
        this.type = type;
        this.changed = changed;
        this.version = version;
        this.globalId = globalId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CommitMetadata getCommitMetadata() {
        return commitMetadata;
    }

    public void setCommitMetadata(CommitMetadata commitMetadata) {
        this.commitMetadata = commitMetadata;
    }

    public CdoSnapshotState getState() {
        return state;
    }

    public void setState(CdoSnapshotState state) {
        this.state = state;
    }

    public SnapshotType getType() {
        return type;
    }

    public void setType(SnapshotType type) {
        this.type = type;
    }

    public List<String> getChanged() {
        return changed;
    }

    public void setChanged(List<String> changed) {
        this.changed = changed;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    public void setGlobalId(GlobalId globalId) {
        this.globalId = globalId;
    }
}
