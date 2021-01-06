package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response;

import java.util.Set;

public class SnapshotResponse {

    private Long snapshotPk;
    private String type;
    private Long version;
    private String state;
    private String changedProperties;
    private String managedType;

    public SnapshotResponse() {}

    public SnapshotResponse(Long snapshotPk, String type, Long version, String state, String changedProperties, String managedType) {
        this.snapshotPk = snapshotPk;
        this.type = type;
        this.version = version;
        this.state = state;
        this.changedProperties = changedProperties;
        this.managedType = managedType;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getChangedProperties() {
        return changedProperties;
    }

    public void setChangedProperties(String changedProperties) {
        this.changedProperties = changedProperties;
    }

    public String getManagedType() {
        return managedType;
    }

    public void setManagedType(String managedType) {
        this.managedType = managedType;
    }
}
