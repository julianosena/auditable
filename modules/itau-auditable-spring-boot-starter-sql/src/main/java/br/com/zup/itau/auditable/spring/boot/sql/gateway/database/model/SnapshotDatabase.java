package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "jv_snapshot")
public class SnapshotDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

    public SnapshotDatabase() { }

    public SnapshotDatabase(Long snapshotPk,
                            String type,
                            Long version,
                            String state,
                            String changedProperties,
                            String managedType) {

        this.snapshotPk = snapshotPk;
        this.type = type;
        this.version = version;
        this.state = state;
        this.changedProperties = changedProperties;
        this.managedType = managedType;
    }

    @Id
    @Column(name = "snapshot_pk")
    private Long snapshotPk;

    @Column(name = "type")
    private String type;

    @Column(name = "version")
    private Long version;

    @Column(name = "state")
    private String state;

    @Column(name = "changed_properties")
    private String changedProperties;

    @Column(name = "managed_type")
    private String managedType;

    @ManyToOne
    @JoinColumn(name = "global_id_fk", referencedColumnName = "global_id_pk")
    private GlobalIdDatabase globalId;

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

    public GlobalIdDatabase getGlobalId() {
        return globalId;
    }

    public void setGlobalId(GlobalIdDatabase globalId) {
        this.globalId = globalId;
    }
}
