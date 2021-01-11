package br.com.zup.itau.auditable.spring.boot.mongo.controller.translator;

import br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response.SnapshotResponse;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.Snapshot;

public class SnapshotToSnapshotResponseTranslator {
    private SnapshotToSnapshotResponseTranslator() {
    }

    public static SnapshotResponse execute(final Snapshot snapshot) {
        return new SnapshotResponse(snapshot.getType().name(),
                snapshot.getVersion(),
                snapshot.getState(),
                CommitMetadataToCommitMetadataResponseTranslator.execute(snapshot.getCommitMetadata()),
                snapshot.getChangedProperties());
    }
}
