package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalIdResponse {

    private Long globalId;
    private String localId;
    private String fragment;
    private Long ownerIdFk;
    private List<SnapshotResponse> snapshots;

    public GlobalIdResponse() { }

    public GlobalIdResponse(Long globalId,
                            String localId,
                            String fragment,
                            Long ownerIdFk,
                            List<SnapshotResponse> snapshots) {

        this.globalId = globalId;
        this.localId = localId;
        this.fragment = fragment;
        this.ownerIdFk = ownerIdFk;
        this.snapshots = snapshots;
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public Long getOwnerIdFk() {
        return ownerIdFk;
    }

    public void setOwnerIdFk(Long ownerIdFk) {
        this.ownerIdFk = ownerIdFk;
    }

    public List<SnapshotResponse> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<SnapshotResponse> snapshots) {
        this.snapshots = snapshots;
    }
}
