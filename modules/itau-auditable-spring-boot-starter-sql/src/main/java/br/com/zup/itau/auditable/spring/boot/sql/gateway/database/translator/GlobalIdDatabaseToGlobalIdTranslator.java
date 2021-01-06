package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.GlobalIdDatabase;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalIdDatabaseToGlobalIdTranslator {

    public static GlobalId translate(GlobalIdDatabase globalIdDatabase) {
        List<Snapshot> snapshotList = globalIdDatabase.getJvSnapshots()
                .stream()
                .map(JvSnapshotDatabaseToSnapshotTranslator::translate)
                .collect(Collectors.toList());

        return new GlobalId(
                globalIdDatabase.getGlobalIdPk(),
                globalIdDatabase.getLocalId(),
                globalIdDatabase.getFragment(),
                globalIdDatabase.getTypeName(),
                globalIdDatabase.getOwnerIdFk(),
                snapshotList
        );
    }
}
