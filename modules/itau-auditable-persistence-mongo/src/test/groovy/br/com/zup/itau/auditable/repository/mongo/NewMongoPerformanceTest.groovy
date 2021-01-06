package br.com.zup.itau.auditable.repository.mongo

import com.mongodb.client.MongoClients
import br.com.zup.itau.auditable.repository.jql.NewPerformanceTest
import spock.lang.Ignore

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.javers

@Ignore
class NewMongoPerformanceTest extends NewPerformanceTest {

    def setup() {
        def mongoRepository = new MongoRepository(MongoClients.create().getDatabase("j_int_test"))
        javers = javers().registerItauAuditableRepository(mongoRepository).build()
    }
}
