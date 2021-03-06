package br.com.zup.itau.auditable.spring.boot.sql.usecase;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.GetRevisionsByIdAndTypeGateway;
import br.com.zup.itau.auditable.spring.boot.sql.usecase.exception.GetRevisionsByIdAndTypeUseCaseException;
import br.com.zup.itau.auditable.usecase.exception.ItauAuditableUseCaseException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetRevisionsByIdAndTypeUseCase {

    private final GetRevisionsByIdAndTypeGateway getRevisionsByIdAndTypeGateway;

    public GetRevisionsByIdAndTypeUseCase(GetRevisionsByIdAndTypeGateway getRevisionsByIdAndTypeGateway) {
        this.getRevisionsByIdAndTypeGateway = getRevisionsByIdAndTypeGateway;
    }

    public Optional<GlobalId> execute(String id, String type) throws ItauAuditableUseCaseException {
        try {

            return this.getRevisionsByIdAndTypeGateway.execute(id, type);

        } catch (Exception e){
            throw new GetRevisionsByIdAndTypeUseCaseException("Problemas ao buscar as revisões deste registro", e);
        }
    }

}
