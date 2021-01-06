package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.JvSnapshotDatabase;

public class SnapshotToJvSnapshotDatabaseTranslator {

    public static JvSnapshotDatabase translate(Snapshot snapshot) {
        return new JvSnapshotDatabase(
                snapshot.getSnapshotPk(),
                snapshot.getType(),
                snapshot.getVersion(),
                snapshot.getState(),
                snapshot.getChangedProperties(),
                snapshot.getManagedType()
        );
    }
}
