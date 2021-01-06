package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import spock.lang.Specification

/**
 * https://github.com/itauAuditable/itauAuditable/issues/501
 */
class Case501TypeTokens extends Specification {

    class Identified<ID> {
        ID id
    }

    class Versioned<ID, VER> extends Identified<ID> {
        VER version
    }

    class Person extends Versioned<Long, Long> {
        String name
    }

    def "should resolve type arguments"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
      def changes = itauAuditable.compare(new Person(id:1), new Person(id:1))

      expect:
      changes.changes.size() == 0
    }
}
