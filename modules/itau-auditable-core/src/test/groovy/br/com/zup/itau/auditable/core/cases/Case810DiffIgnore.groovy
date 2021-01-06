package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.core.diff.Diff
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm
import br.com.zup.itau.auditable.core.metamodel.annotation.DiffIgnore
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import spock.lang.Specification

/**
 * https://github.com/itauAuditable/itauAuditable/issues/810
 */

class Building {
    private Integer id
    private Set<Floor> floors

    @Id
    Integer getId() {
        return id
    }

    void setId(Integer id) {
        this.id = id
    }

    Set<Floor> getFloors() {
        return floors
    }

    void setFloors(Set<Floor> floors) {
        this.floors = floors
    }
}

class Floor {
    private Set<Room> rooms

    Set<Room> getRooms() {
        return rooms
    }

    void setRooms(Set<Room> rooms) {
        this.rooms = rooms
    }
}

class Room {
    private String name
    private String number

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @DiffIgnore
    String getNumber() {
        return number
    }

    void setNumber(String number) {
        this.number = number
    }
}

class Case810DiffIgnore extends Specification {

    def "should ignore containers of ValueObjects when calculating Object Hash"() {
      when:
      ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable()
              .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
              .withMappingStyle(MappingStyle.BEAN)
              .build();


      Room room1 = new Room(name: "Room!", number:"Ignore me")
      Floor floor1 = new Floor(rooms:[room1])

      Room room2 = new Room(name: "Room!", number:"Different room")
      Floor floor2 = new Floor(rooms:[room2])

      Building building1 = new Building(id:1, floors:[floor1])
      Building building2 = new Building(id:1, floors:[floor2])

      Diff diff = itauAuditable.compare(building1, building2)

      println diff.prettyPrint()
      println diff.getChanges().size()

      then:
      diff.getChanges().size() == 0
    }
}
