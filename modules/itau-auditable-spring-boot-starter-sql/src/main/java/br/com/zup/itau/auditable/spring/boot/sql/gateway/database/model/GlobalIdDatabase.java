package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "jv_global_id")
public class GlobalIdDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "global_id_pk")
    private Long globalIdPk;

    @Column(name = "local_id")
    private Long localId;

    @Column(name = "fragment")
    private String fragment;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "owner_id_fk")
    private Long ownerIdFk;

    @OneToMany(mappedBy = "globalId", fetch = FetchType.LAZY)
    private List<SnapshotDatabase> jvSnapshots;

    public GlobalIdDatabase() { }

    public GlobalIdDatabase(Long globalIdPk,
                            Long localId,
                            String fragment,
                            String typeName,
                            Long ownerIdFk,
                            List<SnapshotDatabase> jvSnapshots) {

        this.globalIdPk = globalIdPk;
        this.localId = localId;
        this.fragment = fragment;
        this.typeName = typeName;
        this.ownerIdFk = ownerIdFk;
        this.jvSnapshots = jvSnapshots;
    }

    public Long getGlobalIdPk() {
        return globalIdPk;
    }

    public void setGlobalIdPk(Long globalIdPk) {
        this.globalIdPk = globalIdPk;
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

    public List<SnapshotDatabase> getJvSnapshots() {
        return jvSnapshots;
    }

    public void setJvSnapshots(List<SnapshotDatabase> jvSnapshots) {
        this.jvSnapshots = jvSnapshots;
    }
}
