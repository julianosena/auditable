package br.com.zup.itau.auditable.core.examples

import groovy.transform.TupleConstructor
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class GroovyDiffExample extends Specification {

    @TupleConstructor
    class Person {
        @Id login
        String lastName
    }

    def "should calculate diff for GroovyObjects"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

      when:
      def diff = itauAuditable.compare(
          new Person('bob','Uncle'),
          new Person('bob','Martin')
      )

      then:
      diff.changes.size() == 1
      diff.changes[0].left == 'Uncle'
      diff.changes[0].right == 'Martin'
    }
}
