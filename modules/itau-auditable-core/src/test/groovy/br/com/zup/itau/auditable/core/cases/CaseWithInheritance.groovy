package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.DiffIgnore
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName
import spock.lang.Specification

import javax.persistence.Id

/**
 * @author bartosz.walacik
 */
class CaseWithInheritance extends Specification{
    @TypeName("A")
    class A {
        @Id
        String id
        String field
    }
    @TypeName("A")
    class B extends A {
        @DiffIgnore
        String someOtherField
    }

    def "should compare common properties when one class extends another"(){
      given:
      def a = new A(id:1, field:"aa")
      def b = new B(id:1, field:"bb", someOtherField:"b")
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

      when:
      def diff = itauAuditable.compare(a,b)

      then:
      println diff.prettyPrint()

      diff.changes.size() == 2

      with(diff.changes.find {it.propertyName == "field"}) {
          assert left == "aa"
          assert right == "bb"
      }
      with(diff.changes.find {it.propertyName == "someOtherField"}) {
          assert left == null
          assert right == "b"
      }
    }
}
