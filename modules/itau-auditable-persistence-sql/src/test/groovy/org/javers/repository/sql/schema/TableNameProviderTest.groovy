package br.com.zup.itau.auditable.repository.sql.schema

import br.com.zup.itau.auditable.repository.sql.SqlRepositoryConfiguration
import spock.lang.Specification

class TableNameProviderTest extends Specification {

    def "should provide default names without schema" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, null, true, null, null, null, null))

        then:
        names.commitTableNameWithSchema == "audit_commit"
        names.commitPkSeqName.toString() == "audit_commit_pk_seq"

        names.globalIdTableNameWithSchema == "audit_global_id"
        names.globalIdPkSeqName.toString() == "audit_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "audit_snapshot"
        names.snapshotTablePkSeqName.toString() == "audit_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "audit_commit_property"
    }

    def "should provide default names with schema" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, 's', true, null, null, null, null))

        then:
        names.commitTableNameWithSchema == "s.audit_commit"
        names.commitPkSeqName.toString() == "s.audit_commit_pk_seq"

        names.globalIdTableNameWithSchema == "s.audit_global_id"
        names.globalIdPkSeqName.toString() == "s.audit_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s.audit_snapshot"
        names.snapshotTablePkSeqName.toString() == "s.audit_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "s.audit_commit_property"
    }

    def "should provide custom table names" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, null, true, "g", "c", "s", "cp"))

        then:
        names.commitTableNameWithSchema == "c"
        names.commitPkSeqName.toString() == "audit_commit_pk_seq"

        names.globalIdTableNameWithSchema == "g"
        names.globalIdPkSeqName.toString() == "audit_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s"
        names.snapshotTablePkSeqName.toString() == "audit_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "cp"
    }
}
