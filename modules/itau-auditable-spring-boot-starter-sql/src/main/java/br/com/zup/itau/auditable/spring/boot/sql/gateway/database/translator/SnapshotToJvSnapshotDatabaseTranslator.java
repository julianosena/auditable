package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.SnapshotDatabase;

public class SnapshotToJvSnapshotDatabaseTranslator {

    public static SnapshotDatabase translate(Snapshot snapshot) {
        return new SnapshotDatabase(
                snapshot.getSnapshotPk(),
                snapshot.getType(),
                snapshot.getVersion(),
                snapshot.getState(),
                snapshot.getChangedProperties(),
                snapshot.getManagedType()
        );
    }
}
