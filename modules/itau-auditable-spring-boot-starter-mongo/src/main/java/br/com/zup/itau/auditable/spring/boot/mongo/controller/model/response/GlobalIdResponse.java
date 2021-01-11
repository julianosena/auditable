package br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response;

import java.util.List;

public class GlobalIdResponse {

    private final String localId;

    private final List<SnapshotResponse> snapshots;

    public GlobalIdResponse(String localId, List<SnapshotResponse> snapshots) {
        this.localId = localId;
        this.snapshots = snapshots;
    }

    public String getLocalId() {
        return localId;
    }

    public List<SnapshotResponse> getSnapshots() {
        return snapshots;
    }
}
