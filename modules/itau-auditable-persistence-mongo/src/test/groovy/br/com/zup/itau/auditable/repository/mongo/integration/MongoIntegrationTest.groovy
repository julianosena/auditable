package br.com.zup.itau.auditable.repository.mongo.integration

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import br.com.zup.itau.auditable.repository.mongo.ItauAuditableMongoRepositoryE2ETest

/**
 * runs e2e test suite with real MongoDB at localhost
 *
 * @author bartosz walacik
 */
class MongoIntegrationTest extends ItauAuditableMongoRepositoryE2ETest {

    @Override
    protected MongoDatabase getMongoDb() {
        MongoClients.create("mongodb://localhost:27017").getDatabase("j_int_test")
    }
}
