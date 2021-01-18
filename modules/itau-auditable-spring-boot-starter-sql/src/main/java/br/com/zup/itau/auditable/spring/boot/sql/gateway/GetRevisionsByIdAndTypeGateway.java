package br.com.zup.itau.auditable.spring.boot.sql.gateway;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.exception.ItauAuditableGatewayException;

import java.util.Optional;

public interface GetRevisionsByIdAndTypeGateway {
    Optional<GlobalId> execute(String id, String type) throws ItauAuditableGatewayException;
}
