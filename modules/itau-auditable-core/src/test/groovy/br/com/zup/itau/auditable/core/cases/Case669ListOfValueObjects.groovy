package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm
import spock.lang.Specification


class Case669ListOfValueObjects extends Specification {

    class ListItem {
        String name
        String value
    }

    class TopLevelClass {
        List<ListItem> items
    }

    def "should compare Lists of Value Objects as Sets"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().withListCompareAlgorithm(ListCompareAlgorithm.AS_SET).build()

      def l1 = new TopLevelClass(items: [
              new ListItem(name: "name1", value: "value1"),
              new ListItem(name: "name2", value: "value2")
      ])

      def l2 = new TopLevelClass(items: [
              new ListItem(name: "name2", value: "value2"),
              new ListItem(name: "name1", value: "value1"),
      ])

      when:
      def diff = itauAuditable.compare(l1, l2)

      then:
      diff.changes.isEmpty()
    }
}
