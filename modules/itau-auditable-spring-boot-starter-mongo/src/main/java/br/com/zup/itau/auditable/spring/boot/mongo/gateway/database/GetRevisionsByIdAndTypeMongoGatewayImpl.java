package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database;

import br.com.zup.itau.auditable.spring.boot.mongo.gateway.GetRevisionsByIdAndTypeMongoGateway;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.Snapshot;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.repository.GetRevisionsByIdAndTypeMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRevisionsByIdAndTypeMongoGatewayImpl implements GetRevisionsByIdAndTypeMongoGateway {

    @Autowired
    private GetRevisionsByIdAndTypeMongoRepository getRevisionsByIdAndTypeMongoRepository;

    @Override
    public List<Snapshot> execute(final String dataClass, final String id) {
        return getRevisionsByIdAndTypeMongoRepository.execute(dataClass, id);
    }
}
