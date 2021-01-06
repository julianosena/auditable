package br.com.zup.itau.auditable.core.examples

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import spock.lang.Specification

class CustomToStringExample extends Specification {

    @TypeName("Entity")
    class Entity {
        @Id Point id
        String data
    }

    class Point {
        double x
        double y

        String myToString() {
            "("+ (int)x +"," +(int)y + ")"
        }
    }

    def "should use smartToString() function to build Entity Id from ValueObject"(){
      given:
      Point p1 = new Point(x: 1, y: 3)
      Point p2 = new Point(x: 1, y: 3)

      Entity entity1 = new Entity(id: p1)
      Entity entity2 = new Entity(id: p2)

      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

      expect:
      !p1.equals(p2)
      itauAuditable.compare(entity1, entity2).changes.size() == 0

      def id = itauAuditable.getTypeMapping(Entity).createIdFromInstance(entity1)
      id.value() == "Entity/1.0,3.0"
    }

    def """should use default reflectiveToString() function to build Entity Id from Value" +
           when no CustomValueComparator is registered""" (){
      given:
      Entity entity = new Entity(id: new Point(x: 1/3, y: 4/3))

      when:
      def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
      InstanceId id = itauAuditable.getTypeMapping(Entity).createIdFromInstance(entity)

      then:
      id.value() == "Entity/0.3333333333,1.3333333333"
    }

    def """should use custom toString() function to build Entity Id from Value" +
           when CustomValueComparator is registered""" (){
        given:
        Entity entity = new Entity(id: new Point(x: 1/3, y: 4/3))

        when:
        def itauAuditable = ItauAuditableBuilder.itauAuditable()
                .registerValue(Point, {a,b -> Objects.equals(a,b)}, {p -> p.myToString()})
                .build()
        InstanceId id = itauAuditable.getTypeMapping(Entity).createIdFromInstance(entity)

        then:
        id.value() == "Entity/(0,1)"
    }
}
