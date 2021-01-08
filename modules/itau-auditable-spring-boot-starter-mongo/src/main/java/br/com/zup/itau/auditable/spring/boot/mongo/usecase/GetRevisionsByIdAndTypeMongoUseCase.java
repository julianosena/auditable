package br.com.zup.itau.auditable.spring.boot.mongo.usecase;

import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.GetRevisionsByIdAndTypeMongoGateway;
import br.com.zup.itau.auditable.spring.boot.mongo.usecase.exception.GetRevisionsByIdAndTypeMongoUseCaseException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRevisionsByIdAndTypeMongoUseCase {

    private final GetRevisionsByIdAndTypeMongoGateway getRevisionsByIdAndTypeMongoGateway;

    public GetRevisionsByIdAndTypeMongoUseCase(GetRevisionsByIdAndTypeMongoGateway getRevisionsByIdAndTypeMongoGateway) {
        this.getRevisionsByIdAndTypeMongoGateway = getRevisionsByIdAndTypeMongoGateway;
    }

    public List<CdoSnapshot> execute(final String dataClass, final String id) throws GetRevisionsByIdAndTypeMongoUseCaseException {
        try {
            return getRevisionsByIdAndTypeMongoGateway.execute(dataClass, id);
        } catch (Exception e) {
            throw new GetRevisionsByIdAndTypeMongoUseCaseException("Error to get revisions by id ant type in mongo", e);
        }
    }
}
