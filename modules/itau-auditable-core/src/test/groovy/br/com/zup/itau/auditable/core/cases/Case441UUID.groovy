package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import spock.lang.Specification

class Case441UUID extends Specification {

    class Entity {
        @Id
        UUID id
        String val
    }

    def "should use UUID.toString() in InstanceId"(){
        when:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        UUID u = UUID.randomUUID()

        def diff = itauAuditable.compare(new Entity(id:u, val:"a"), new Entity(id:u, val:"b"))

        then:
        InstanceId id = diff.changes[0].affectedGlobalId
        id.value().endsWith("/" + u)
    }
}
