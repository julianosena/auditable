package br.com.zup.itau.auditable.repository.mongo.cases


import com.mongodb.client.MongoDatabase
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.repository.mongo.EmbeddedMongoFactory
import br.com.zup.itau.auditable.repository.mongo.MongoRepository
import spock.lang.Shared
import spock.lang.Specification

import javax.persistence.Id

class LargeNumberDeserializationCase extends Specification {
  public static final long ID_ONE_BILLION = 1000000000L
  public static final long ID_ONE_TRILLION = 1000000000L * 1000

  @Shared
  ItauAuditable javers

  @Shared
  def embeddedMongo = EmbeddedMongoFactory.create()

  def setupSpec() {
    MongoDatabase mongo = embeddedMongo.getClient().getDatabase("test")

    MongoRepository mongoRepo = new MongoRepository(mongo)
    javers = ItauAuditableBuilder.javers().registerItauAuditableRepository(mongoRepo).build()
  }

  void cleanupSpec() {
    embeddedMongo.stop()
  }

  static class MyEntity {
    @Id
    final private Long id
    final private String name

    MyEntity(Long id, String name) {
      this.id = id
      this.name = name
    }
  }

  def verifyMappingOfLargeId() {
    when:
    javers.commit("kent", new MyEntity(ID_ONE_BILLION, "red"))
    javers.commit("kent", new MyEntity(ID_ONE_BILLION, "blue"))

    then:
    noExceptionThrown()
  }

  def verifyMappingOfLargerId() {
    when:
    javers.commit("kent", new MyEntity(ID_ONE_TRILLION, "red"))
    javers.commit("kent", new MyEntity(ID_ONE_TRILLION, "blue"))

    then:
    noExceptionThrown()
  }
}
