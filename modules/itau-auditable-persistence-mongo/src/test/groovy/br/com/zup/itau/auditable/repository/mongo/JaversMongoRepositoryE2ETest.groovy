package br.com.zup.itau.auditable.repository.mongo

import com.mongodb.client.MongoDatabase
import br.com.zup.itau.auditable.core.ItauAuditableRepositoryShadowE2ETest
import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository
import br.com.zup.itau.auditable.repository.api.QueryParamsBuilder

import static br.com.zup.itau.auditable.core.model.DummyUser.dummyUser
import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId

/**
 * runs e2e test suite with mongo db provided by subclasses
 *
 * @author bartosz walacik
 */
abstract class ItauAuditableMongoRepositoryE2ETest extends ItauAuditableRepositoryShadowE2ETest {
    protected abstract MongoDatabase getMongoDb()

    ItauAuditableTestBuilder itauAuditableTestBuilder

    @Override
    def setup() {
        repository.jsonConverter = itauAuditable.jsonConverter
        itauAuditableTestBuilder = ItauAuditableTestBuilder.itauAuditableTestAssembly()
    }

    @Override
    protected ItauAuditableRepository prepareItauAuditableRepository() {
        MongoRepository mongoRepository = new MongoRepository(getMongoDb())
        mongoRepository.clean()
        mongoRepository
    }

    def "should commit and read snapshot of Entity containing map field with dot keys"() {
        given:
        def cdo = new SnapshotEntity(id: 1, mapOfPrimitives: ['primitive.value':1])

        when:
        itauAuditable.commit('author', cdo)
        def snapshots = itauAuditable.findSnapshots(byInstanceId(1, SnapshotEntity).build())

        then:
        snapshots[0].getPropertyValue('mapOfPrimitives') == ['primitive.value':1]
    }

    def "should persist head id"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository

        def commitFactory = itauAuditableTestBuilder.commitFactory

        def kazikV1 = dummyUser("Kazik").withAge(1)
        def kazikV2 = dummyUser("Kazik").withAge(2)

        def commit1 = commitFactory.create("author", [:], kazikV1)
        def commit2 = commitFactory.create("author", [:], kazikV2)

        when:
        mongoRepository.persist(commit1)

        then:
        mongoRepository.getHeadId().getMajorId() == 1
        mongoRepository.getHeadId().getMinorId() == 0

        when:
        mongoRepository.persist(commit2)

        then:
        mongoRepository.getHeadId().getMajorId() == 1
        mongoRepository.getHeadId().getMinorId() == 1
    }

    def "should persist commit and get latest snapshot"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository
        def commitFactory = itauAuditableTestBuilder.commitFactory

        def kazik = new DummyUser("kazik")
        def id = itauAuditableTestBuilder.instanceId(new DummyUser("kazik"))

        when:
        //persist
        mongoRepository.persist(commitFactory.create("andy", [:], kazik))

        //get last snapshot
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId == id
        latest.get().size() == 1
    }

    def "should get last commit by GlobalId"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository

        def commitFactory = itauAuditableTestBuilder.commitFactory
        def id = itauAuditableTestBuilder.globalIdFactory.createInstanceId("kazik", DummyUser)

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", [:], kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.typeName == DummyUser.name
    }

    def "should get last commit by InstanceIdDTO"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository
        def commitFactory = itauAuditableTestBuilder.commitFactory
        def id = itauAuditableTestBuilder.instanceId(new DummyUser("kazik"))

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", [:], kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.typeName == DummyUser.name
    }

    def "should get state history"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository

        def kazikV1 = dummyUser("kazik").withAge(12)
        def kazikV2 = dummyUser("kazik").withAge(13)

        itauAuditable.commit("andy", kazikV1)
        itauAuditable.commit("andy", kazikV2)

        def id = itauAuditableTestBuilder.instanceId(new DummyUser("kazik"))
        def queryParams = QueryParamsBuilder.withLimit(2).build()

        when:
        def history = mongoRepository.getStateHistory(id, queryParams)

        then:
        history.size() == 2
    }
}
