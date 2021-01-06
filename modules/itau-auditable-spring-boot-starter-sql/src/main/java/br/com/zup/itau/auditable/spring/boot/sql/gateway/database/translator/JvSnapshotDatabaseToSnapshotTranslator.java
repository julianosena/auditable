package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.JvSnapshotDatabase;

public class JvSnapshotDatabaseToSnapshotTranslator {

    public static Snapshot translate(JvSnapshotDatabase jvSnapshotDatabase) {
        return new Snapshot(
                jvSnapshotDatabase.getSnapshotPk(),
                jvSnapshotDatabase.getType(),
                jvSnapshotDatabase.getVersion(),
                jvSnapshotDatabase.getState(),
                jvSnapshotDatabase.getChangedProperties(),
                jvSnapshotDatabase.getManagedType()
        );
    }
}
