package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId
import br.com.zup.itau.auditable.repository.inmemory.InMemoryRepository
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * see https://github.com/javers/javers/issues/723
 */
class Case723TypeNameNotFound extends Specification {

    @TypeName("not.existing.Entity")
    class Entity {
        @Id
        int id
        String value
        VO ref
    }

    @TypeName("not.existing.ValueObject")
    class VO {
        String value
    }

    def "should load Snapshots of removed Classes"(){
      given:
      def repo = new InMemoryRepository()
      def javers = ItauAuditableBuilder.javers().registerItauAuditableRepository(repo) .build()

      when:
      javers.commit("author", new Entity(id:1, value: "some", ref: new VO(value: "aaa")))

      // new javers instance - fresh TypeMapper state
      javers = ItauAuditableBuilder.javers().registerItauAuditableRepository(repo) .build()

      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, "not.existing.Entity").build()).get(0)

      then:
      snapshot.getPropertyValue("value") == "some"
      snapshot.getPropertyValue("id") == 1
      snapshot.getPropertyValue("ref") instanceof ValueObjectId
      snapshot.getPropertyValue("ref").value() == "not.existing.Entity/1#ref"
      snapshot.globalId.value() == "not.existing.Entity/1"

      when:
      snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, "not.existing.Entity")
                        .withChildValueObjects().build())
                        .find{it.globalId.value() == "not.existing.Entity/1#ref"}

      then:
      snapshot.getPropertyValue("value") == "aaa"
    }
}
