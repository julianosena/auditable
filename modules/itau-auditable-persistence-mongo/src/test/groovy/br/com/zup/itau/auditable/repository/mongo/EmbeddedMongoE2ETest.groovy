package br.com.zup.itau.auditable.repository.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import spock.lang.Shared

/**
 * @author bartosz walacik
 */
class EmbeddedMongoE2ETest extends ItauAuditableMongoRepositoryE2ETest {

  @Shared def embeddedMongo = EmbeddedMongoFactory.create()
  @Shared MongoClient mongoClient

  def setupSpec() {
    mongoClient = embeddedMongo.getClient()
  }

  void cleanupSpec() {
    embeddedMongo.stop()
  }

  @Override
  protected MongoDatabase getMongoDb() {
    mongoClient.getDatabase("test")
  }
}
