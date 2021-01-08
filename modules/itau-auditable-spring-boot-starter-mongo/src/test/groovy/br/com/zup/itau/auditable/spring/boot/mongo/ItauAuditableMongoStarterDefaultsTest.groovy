package br.com.zup.itau.auditable.spring.boot.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
class ItauAuditableMongoStarterDefaultsTest extends Specification{
    static String DB_NAME = 'spring-mongo-default'

    @Autowired ItauAuditable itauAuditable

    @Autowired
    private MongoClient mongoClient; //from spring-boot-starter-data-mongodb

    @Autowired
    ItauAuditableMongoProperties itauAuditableProperties

    def "should provide default configuration"() {
        expect:
        itauAuditableProperties.algorithm == "simple"
        itauAuditableProperties.mappingStyle == "field"
       !itauAuditableProperties.newObjectSnapshot
        itauAuditableProperties.prettyPrint
       !itauAuditableProperties.typeSafeValues
        itauAuditableProperties.commitIdGenerator == "synchronized_sequence"
       !itauAuditableProperties.documentDbCompatibilityEnabled
        itauAuditableProperties.auditableAspectEnabled
        itauAuditableProperties.springDataAuditableRepositoryAspectEnabled
        itauAuditableProperties.packagesToScan == ""
       !itauAuditableProperties.mongodb
        itauAuditableProperties.objectAccessHook == "br.com.zup.itau.auditable.spring.mongodb.DBRefUnproxyObjectAccessHook"
        itauAuditableProperties.snapshotsCacheSize == 5000
    }

    def "should connect to Mongo configured in spring.data.mongodb properties"(){
      when:
      def dummyEntity = new DummyEntity(UUID.randomUUID().hashCode())
      itauAuditable.commit("a", dummyEntity)
      def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstance(dummyEntity).build())

      MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME)

      then:
      itauAuditable.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo-default"
      snapshots.size() == 1
      mongoDatabase.getCollection("audit_snapshots").countDocuments() == 1
    }
}
