package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response;

import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;

import java.util.List;

public class GlobalIdResponse {

    private Long globalId;
    private Long localId;
    private String fragment;
    private String typeName;
    private Long ownerIdFk;
    private List<SnapshotResponse> snapshots;

    public GlobalIdResponse() { }

    public GlobalIdResponse(Long globalId,
                    Long localId,
                    String fragment,
                    String typeName,
                    Long ownerIdFk,
                    List<SnapshotResponse> snapshots) {

        this.globalId = globalId;
        this.localId = localId;
        this.fragment = fragment;
        this.typeName = typeName;
        this.ownerIdFk = ownerIdFk;
        this.snapshots = snapshots;
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
