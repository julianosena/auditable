package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import spock.lang.Specification

/**
 * @see https://github.com/itauAuditable/itauAuditable/issues/127
 * @author bartosz.walacik
 */
class ComparingValueObjectWithItsSubclassTest extends Specification {

    static class Store {
        @Id
        int id = 1;
        List<Bicycle> bicycles;
    }

    static class Bicycle {
        int speed;
    }

    static class Mountenbike extends Bicycle{
        int seatHeight;
    }

    def "should compare ValueObject with its subclass even if subclass has more fields"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        when:
        def diff = itauAuditable.compare(new Store(bicycles: [new Bicycle()]),
                                  new Store(bicycles: [new Mountenbike(seatHeight: 1)]))

        println diff

        then:
        diff.changes.size() == 1
        diff.changes[0].propertyName == "seatHeight"
        diff.changes[0].left == null
        diff.changes[0].right == 1
    }
}