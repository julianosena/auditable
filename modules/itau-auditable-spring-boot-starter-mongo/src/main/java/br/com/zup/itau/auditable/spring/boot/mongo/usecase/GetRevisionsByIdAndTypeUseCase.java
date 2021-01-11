package br.com.zup.itau.auditable.spring.boot.mongo.usecase;

import br.com.zup.itau.auditable.spring.boot.mongo.gateway.GetRevisionsByIdAndTypeMongoGateway;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.Snapshot;
import br.com.zup.itau.auditable.spring.boot.mongo.usecase.exception.GetRevisionsByIdAndTypeUseCaseException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRevisionsByIdAndTypeUseCase {

    private final GetRevisionsByIdAndTypeMongoGateway getRevisionsByIdAndTypeMongoGateway;

    public GetRevisionsByIdAndTypeUseCase(GetRevisionsByIdAndTypeMongoGateway getRevisionsByIdAndTypeMongoGateway) {
        this.getRevisionsByIdAndTypeMongoGateway = getRevisionsByIdAndTypeMongoGateway;
    }

    public List<Snapshot> execute(final String dataClass, final String id) throws GetRevisionsByIdAndTypeUseCaseException {
        try {
            return getRevisionsByIdAndTypeMongoGateway.execute(dataClass, id);
        } catch (Exception e) {
            throw new GetRevisionsByIdAndTypeUseCaseException("Erro ao buscar as revisões por id e tipo", e);
        }
    }
}
