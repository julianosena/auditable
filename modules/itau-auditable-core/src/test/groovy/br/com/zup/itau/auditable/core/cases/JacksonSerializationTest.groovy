package br.com.zup.itau.auditable.core.cases

import com.fasterxml.jackson.databind.ObjectMapper
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class JacksonSerializationTest extends Specification {

    def "should not fail when converting Diff to JSON using Jackson"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
      def left = new SnapshotEntity(id:1, intProperty: 1)
      def right = new SnapshotEntity(id:1, intProperty: 2)
      def mapper = new ObjectMapper()
      itauAuditable.commit("a", left)
      itauAuditable.commit("a", right)

      when:
      def diff = itauAuditable.compare(left, right)
      def json = mapper.writeValueAsString(diff)

      then:
      assert json

      when:
      def changes = itauAuditable.findChanges(QueryBuilder.byInstanceId(1,SnapshotEntity).build())
      json = mapper.writeValueAsString(changes)

      then:
      assert json
    }
}
