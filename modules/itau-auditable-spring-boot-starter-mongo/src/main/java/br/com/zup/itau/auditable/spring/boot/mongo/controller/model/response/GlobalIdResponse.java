package br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalIdResponse {

    private String localId;

    private List<SnapshotResponse> snapshots;

    public GlobalIdResponse(String localId, List<SnapshotResponse> snapshots) {
        this.localId = localId;
        this.snapshots = snapshots;
    }

    public GlobalIdResponse() {
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public List<SnapshotResponse> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<SnapshotResponse> snapshots) {
        this.snapshots = snapshots;
    }
}
