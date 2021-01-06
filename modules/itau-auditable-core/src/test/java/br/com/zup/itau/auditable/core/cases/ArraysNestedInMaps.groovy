package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import spock.lang.Specification

/**
 * see https://github.com/itauAuditable/itauAuditable/issues/546
 * @author bartosz.walacik
 */
class ArraysNestedInMaps extends Specification {

    class WithMap {
        Map<String, UUID[]> mapUUID
        Map<String, int[]> mapInt
    }

    def "should compare Arrays as Values when on Value position"(){
      given:
      UUID uuid = UUID.randomUUID()
      def map1 = new WithMap(mapUUID: [1: [uuid].toArray()] , mapInt: [1:[1].toArray()])
      def map2 = new WithMap(mapUUID: [1: [uuid].toArray()] , mapInt: [1:[1].toArray()])

      when:
      def diff = ItauAuditableBuilder.itauAuditable().build().compare(map1, map2)

      then:
      diff.changes.size() == 0
    }
}
