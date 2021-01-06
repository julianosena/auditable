package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator;

import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.SnapshotResponse;
import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;

public class SnapshotToSnapshotResponseTranslator {

    public static SnapshotResponse translate(Snapshot snapshot) {
        return new SnapshotResponse(
                snapshot.getSnapshotPk(),
                snapshot.getType(),
                snapshot.getVersion(),
                snapshot.getState(),
                snapshot.getChangedProperties(),
                snapshot.getManagedType()
        );
    }
}
