package br.com.zup.itau.auditable.spring.boot.sql.gateway;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.exception.ItauAuditableGatewayException;

import java.util.List;

public interface GetRevisionsByIdAndTypeGateway {
    List<GlobalId> execute(Long id, String type) throws ItauAuditableGatewayException;
}
