package br.com.zup.itau.auditable.spring.boot.mongo.gateway;

import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;

import java.util.List;

public interface GetRevisionsByIdAndTypeMongoGateway {
    List<CdoSnapshot> execute(final String dataClass, final String id);
}
