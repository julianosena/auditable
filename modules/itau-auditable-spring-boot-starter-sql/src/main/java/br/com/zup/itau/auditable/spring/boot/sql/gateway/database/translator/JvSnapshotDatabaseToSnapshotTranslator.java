package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.Commit;
import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.SnapshotDatabase;

public class JvSnapshotDatabaseToSnapshotTranslator {

    public static Snapshot translate(SnapshotDatabase snapshotDatabase) {
        Commit commit = CommitDatabaseToCommitTranslator.translate(snapshotDatabase.getCommitDatabase());

        return new Snapshot(
                snapshotDatabase.getSnapshotPk(),
                snapshotDatabase.getType(),
                snapshotDatabase.getVersion(),
                snapshotDatabase.getState(),
                snapshotDatabase.getChangedProperties(),
                snapshotDatabase.getManagedType(),
                commit
        );
    }
}
