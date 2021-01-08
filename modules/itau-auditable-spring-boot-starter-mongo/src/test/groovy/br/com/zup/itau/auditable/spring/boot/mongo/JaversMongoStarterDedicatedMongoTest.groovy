package br.com.zup.itau.auditable.spring.boot.mongo

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.repository.mongo.EmbeddedMongoFactory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.lang.Specification

abstract class ItauAuditableMongoStarterDedicatedMongoTest extends Specification {

    static int PORT = 32001

    /**
     * The embedded MongoDB authentication is not enabled
     * When connecting to a real MongoDB database
     * authentication can be enabled.
     * @see {@code ItauAuditableMongoProperties}
     */
    @Shared def embeddedMongo = EmbeddedMongoFactory.create(PORT)

    @Autowired
    ItauAuditable itauAuditable

    @Autowired
    ItauAuditableMongoProperties itauAuditableProperties

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should connect to Mongo configured with itauAuditable.mongodb properties"() {
        when:
        def dummyEntity = new DummyEntity(UUID.randomUUID().hashCode())
        itauAuditable.commit("a", dummyEntity)
        def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstance(dummyEntity).build())

        MongoDatabase dedicatedDb = MongoClients.create("mongodb://localhost:$PORT").getDatabase("itau-auditable-dedicated")

        then:
        snapshots.size() == 1
        dedicatedDb.getCollection("audit_snapshots").countDocuments() == 1
    }

    void cleanupSpec() {
        embeddedMongo.stop()
    }
}
