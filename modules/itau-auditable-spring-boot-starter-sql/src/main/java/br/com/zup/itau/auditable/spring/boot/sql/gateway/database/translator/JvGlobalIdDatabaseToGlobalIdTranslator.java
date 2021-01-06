package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.JvGlobalIdDatabase;

import java.util.List;
import java.util.stream.Collectors;

public class JvGlobalIdDatabaseToGlobalIdTranslator {

    public static GlobalId translate(JvGlobalIdDatabase jvGlobalIdDatabase) {
        List<Snapshot> snapshotList = jvGlobalIdDatabase.getJvSnapshots()
                .stream()
                .map(JvSnapshotDatabaseToSnapshotTranslator::translate)
                .collect(Collectors.toList());

        return new GlobalId(
                jvGlobalIdDatabase.getGlobalIdPk(),
                jvGlobalIdDatabase.getLocalId(),
                jvGlobalIdDatabase.getFragment(),
                jvGlobalIdDatabase.getTypeName(),
                jvGlobalIdDatabase.getOwnerIdFk(),
                snapshotList
        );
    }
}
