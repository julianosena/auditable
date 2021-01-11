package br.com.zup.itau.auditable.spring.boot.mongo.gateway;

import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.Snapshot;

import java.util.List;

public interface GetRevisionsByIdAndTypeMongoGateway {
    List<Snapshot> execute(final String dataClass, final String id);
}
