package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import spock.lang.Specification

class Case697ValueObjectWithNumber extends Specification {
    class NumberTest {
        Number testNumber

        NumberTest(Number testNumber) {
            this.testNumber = testNumber
        }
    }

    def "should compare Value Objects with Numbers"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

      def number1 = new NumberTest(23)
      def number2 = new NumberTest(20)

      when:
      def diff = itauAuditable.compare(number1, number2)

      then:
      diff.changes[0].left == 23
      diff.changes[0].right == 20
    }
}
