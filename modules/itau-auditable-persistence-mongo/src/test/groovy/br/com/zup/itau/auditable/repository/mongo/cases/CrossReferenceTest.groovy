package br.com.zup.itau.auditable.repository.mongo.cases

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.DiffIgnore
import br.com.zup.itau.auditable.core.metamodel.annotation.Value
import br.com.zup.itau.auditable.repository.mongo.EmbeddedMongoFactory
import br.com.zup.itau.auditable.repository.mongo.MongoRepository
import spock.lang.Shared
import spock.lang.Specification

import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Decouple cross reference object by DiffIgnore
 *
 * @author hank cp
 */
class CrossReferenceTest extends Specification {

    @Shared def embeddedMongo = EmbeddedMongoFactory.create()
    @Shared MongoClient mongoClient

    def setupSpec() {
        mongoClient = embeddedMongo.getClient()
    }

    void cleanupSpec() {
        embeddedMongo.stop()
    }

    class CrossReferenceHost {
        @Id long id
        CrossReferenceObjectA a
    }

    @Value
    class CrossReferenceObjectA {
        @OneToMany
        List<CrossReferenceObjectB> bList

        @OneToMany
        @DiffIgnore
        List<CrossReferenceObjectB> bListIgnored
    }

    @Value
    class CrossReferenceObjectB {
        public int value;
        public CrossReferenceObjectA a

        CrossReferenceObjectB(int value, CrossReferenceObjectA a) {
            this.value = value
            this.a = a
        }
    }

    def "should not throw StackOverflowError exception for ValueType cross references"() {
        given:
        MongoDatabase mongo = mongoClient.getDatabase("test")

        def javers = ItauAuditableBuilder.javers()
                .registerItauAuditableRepository(new MongoRepository(mongo))
                .build()

        when:
        def host = new CrossReferenceHost()
        host.id = 1
        host.a = new CrossReferenceObjectA()
        host.a.bListIgnored = [new CrossReferenceObjectB(1, host.a),
                               new CrossReferenceObjectB(2, host.a),
                               new CrossReferenceObjectB(3, host.a)]

        def commit = javers.commit("author", host)
        println commit

        then:
        commit
    }
}
