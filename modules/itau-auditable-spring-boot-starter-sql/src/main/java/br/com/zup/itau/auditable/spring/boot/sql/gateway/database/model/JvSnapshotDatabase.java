package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "jv_snapshot")
public class JvSnapshotDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Set<String> changedProperties;

    @Column(name = "managed_type")
    private String managedType;

    @ManyToOne
    @JoinColumn(name = "jv_global_fk")
    private JvGlobalIdDatabase globalId;

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

    public Set<String> getChangedProperties() {
        return changedProperties;
    }

    public void setChangedProperties(Set<String> changedProperties) {
        this.changedProperties = changedProperties;
    }

    public String getManagedType() {
        return managedType;
    }

    public void setManagedType(String managedType) {
        this.managedType = managedType;
    }

    public JvGlobalIdDatabase getGlobalId() {
        return globalId;
    }

    public void setGlobalId(JvGlobalIdDatabase globalId) {
        this.globalId = globalId;
    }
}
