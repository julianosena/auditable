package br.com.zup.itau.auditable.spring.boot.sql.domain;

import java.util.List;

public class GlobalId {

    private Long globalId;
    private String localId;
    private String fragment;
    private String typeName;
    private Long ownerIdFk;
    private List<Snapshot> snapshots;

    public GlobalId() { }

    public GlobalId(Long globalId,
                    String localId,
                    String fragment,
                    String typeName,
                    Long ownerIdFk,
                    List<Snapshot> snapshots) {

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

    public List<Snapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<Snapshot> snapshots) {
        this.snapshots = snapshots;
    }
}
