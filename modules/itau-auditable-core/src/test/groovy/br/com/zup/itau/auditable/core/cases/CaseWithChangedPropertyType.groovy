package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime

import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.*

/**
 * @author https://github.com/lnxmad
 */
class CaseWithChangedPropertyType extends Specification {
    @TypeName("ModelWithDateTime")
    static class Model1 {
        @Id int id

        LocalDateTime datetime
    }

    @TypeName("ModelWithDateTime")
    static class Model2 {
        @Id int id

        Instant datetime
    }

    def "should allow for property type change, from LocalDateTime to Instant"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        def localDateNow = LocalDateTime.now()
        def instantNow = Instant.now()

        itauAuditable.commit("author", new Model1(id: 1, datetime: localDateNow))
        itauAuditable.commit("author", new Model2(id: 1, datetime: instantNow))


        when:
        def snapshots = itauAuditable.findSnapshots(byInstanceId(1, "ModelWithDateTime").build())

        then:
        snapshots.size() == 2

        snapshots[0].getPropertyValue("datetime") == instantNow
        LocalDateTime.parse(snapshots[1].getPropertyValue("datetime")) == localDateNow

        when:
        def changes = itauAuditable.findChanges(byInstanceId(1, "ModelWithDateTime").build())

        then:
        println changes.prettyPrint()
        changes.size() == 1
        changes[0] instanceof ValueChange

        when:
        def shadows = itauAuditable.findShadows(byInstanceId(1, "ModelWithDateTime").build())

        then:
        shadows.size() == 2
        shadows[0].get().datetime == instantNow
        shadows[1].get().datetime == null
    }
}
