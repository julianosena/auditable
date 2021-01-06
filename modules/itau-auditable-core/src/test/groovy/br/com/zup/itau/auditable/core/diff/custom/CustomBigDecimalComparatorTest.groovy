package br.com.zup.itau.auditable.core.diff.custom

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import spock.lang.Specification

class CustomBigDecimalComparatorTest extends Specification {

    class ValueObject {
        BigDecimal value
        List<BigDecimal> values
    }

    def "should compare BigDecimal properties with desired precision"(){
      given:
      def itauAuditable = ItauAuditableBuilder.itauAuditable()
             .registerValue(BigDecimal, new CustomBigDecimalComparator(2)).build()

      expect:
      itauAuditable.compare(new ValueObject(value: 1.123), new ValueObject(value: 1.124)).changes.size() == 0
      itauAuditable.compare(new ValueObject(value: 1.12), new ValueObject(value: 1.13)).changes.size() == 1
    }

    def "should compare BigDecimal lists with desired precision "(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable()
                .registerValue(BigDecimal, new CustomBigDecimalComparator(2)).build()

        expect:
        itauAuditable.compare(new ValueObject(values: [1.123]), new ValueObject(values: [1.124])).changes.size() == 0
        itauAuditable.compare(new ValueObject(values: [1.123]), new ValueObject(values: [1.124, 2])).changes.size() == 1
    }

    def "should compare BigDecimal with fixed equals"() {
        when:
        def itauAuditable = ItauAuditableBuilder.itauAuditable()
                .registerValue(BigDecimal, {a, b -> a.compareTo(b) == 0},
                                           {a -> a.stripTrailingZeros().toString()})
                .build()

        then:
        itauAuditable.compare(new ValueObject(value: 1.000), new ValueObject(value: 1.00)).changes.size() == 0
        itauAuditable.compare(new ValueObject(value: 1.100), new ValueObject(value: 1.20)).changes.size() == 1

        when:
        itauAuditable = ItauAuditableBuilder.itauAuditable()
                .registerValue(BigDecimal, new BigDecimalComparatorWithFixedEquals())
                .build()

        then:
        itauAuditable.compare(new ValueObject(value: 1.000), new ValueObject(value: 1.00)).changes.size() == 0
        itauAuditable.compare(new ValueObject(value: 1.100), new ValueObject(value: 1.20)).changes.size() == 1
    }
}
