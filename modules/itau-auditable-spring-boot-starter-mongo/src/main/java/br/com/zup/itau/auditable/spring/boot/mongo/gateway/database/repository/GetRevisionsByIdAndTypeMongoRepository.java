package br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.repository;

import br.com.zup.itau.auditable.core.AuditableContextHolder;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.CdoSnapshot;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRevisionsByIdAndTypeMongoRepository {

    @Autowired
    private MongoClient mongoClient;

    public List<CdoSnapshot> execute(final String dataClass, final String id) {
        String databaseName = AuditableContextHolder.getContext().getDatabaseName();

        String name = "DATABASE_NAME_DEFAULT";

        if (null != databaseName) {
            name = databaseName;
        }

        MongoDatabase database = mongoClient.getDatabase(name);

        System.out.println(database.getCollection("jv_snapshots"));
        return null;
    }
}
