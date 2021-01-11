package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.repository;

import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.Snapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRevisionsByIdAndTypeMongoRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Snapshot> execute(final String dataClass, final String id) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("globalId.entity").is(dataClass)
                .and("globalId.cdoId").is(id));
        return mongoTemplate.find(query, Snapshot.class);
    }
}
