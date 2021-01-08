package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database;

import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.GetRevisionsByIdAndTypeMongoGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRevisionsByIdAndTypeMongoGatewayImpl implements GetRevisionsByIdAndTypeMongoGateway {

    @Override
    public List<CdoSnapshot> execute(final String dataClass, final String id) {

        return null;
    }
}
