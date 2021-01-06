package br.com.zup.itau.auditable.repository.jql

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.examples.typeNames.NewEntityWithTypeAlias
import br.com.zup.itau.auditable.core.examples.typeNames.NewValueObjectWithTypeAlias
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz.walacik
 */
class QueryRunnerIntegrationTest extends Specification {

    def "should throw MALFORMED_JQL when  "(){
      given:
      def javers = ItauAuditableBuilder.javers().build()

      when:
      javers.findSnapshots(QueryBuilder.byInstance("value").build())

      then:
      def e = thrown(ItauAuditableException)
      println(e)
      e.code == ItauAuditableExceptionCode.MALFORMED_JQL
    }

    @Unroll
    def "should touch Entity and ValueObject classes before running #queryType query"(){
        given:
        def javers = ItauAuditableTestBuilder.javersTestAssembly()
        def queryRunner = javers.queryRunner
        def typeMapper = javers.typeMapper

        when:
        action.call(queryRunner, query)

        then:
        expectedMappedTypes.each {
            assert typeMapper.getItauAuditableManagedType(it)
        }

        where:
        query << [QueryBuilder.byInstanceId(1, NewEntityWithTypeAlias).build(),
                  QueryBuilder.byInstanceId(1, NewEntityWithTypeAlias).withChangedProperty("id").build(),
                  QueryBuilder.byClass(NewEntityWithTypeAlias).build(),
                  QueryBuilder.byClass(NewValueObjectWithTypeAlias).build(),
                 // QueryBuilder.byValueObject(NewEntityWithTypeAlias,"valueObject").build(),
                 // QueryBuilder.byValueObjectId(1, NewEntityWithTypeAlias,"valueObject").build()
                ] * 2
        expectedMappedTypes << [
                ["myName"],
                ["myName"],
                ["myName"],
                ["myValueObject"],
                // ["myName", "myValueObject"],
                // ["myName", "myValueObject"]
        ] * 2
        queryType << ["byInstanceId",
                      "byInstanceId and Property",
                      "Entity byClass",
                      "ValueObject byClass",
                    // "byValueObject",
                    // "byValueObjectId"
        ] * 2
        action << [ { runner, query -> runner.queryForChanges(query) } ] * 4 +
                  [ { runner, query -> runner.queryForSnapshots(query) } ] * 4


    }
}
