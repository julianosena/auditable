package br.com.zup.itau.auditable.spring.boot.sql.gateway;

import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;

import java.util.List;

public interface GetRevisionsByIdAndTypeGateway {
    List<Snapshot> execute(Long id, String type);
}
