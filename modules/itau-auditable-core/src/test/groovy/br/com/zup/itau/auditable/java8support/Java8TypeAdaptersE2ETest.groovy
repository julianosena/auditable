package br.com.zup.itau.auditable.java8support

import br.com.zup.itau.auditable.core.diff.changetype.ValueChange
import br.com.zup.itau.auditable.core.metamodel.type.ValueType
import br.com.zup.itau.auditable.core.model.DummyAddress
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.Year
import java.time.ZoneOffset
import java.time.ZonedDateTime

import static br.com.zup.itau.auditable.core.GlobalIdTestBuilder.valueObjectId
import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.itauAuditable
import static br.com.zup.itau.auditable.core.diff.DiffAssert.assertThat

/**
 * @author bartosz.walacik
 */
class Java8TypeAdaptersE2ETest extends Specification {

    @Unroll
    def "should register #j8type.simpleName as ValueTypes"(){
        given:
        def itauAuditable = itauAuditable().build()

        expect:
        itauAuditable.getTypeMapping(j8type) instanceof ValueType

        where:
        j8type << [LocalDate, LocalDateTime, LocalTime, Year, ZonedDateTime, ZoneOffset, OffsetDateTime, Instant, Period, Duration]
    }

    @Unroll
    def "should support optional values (#leftOptional, #rightOptional) changes" (){
        given:
        def itauAuditable = itauAuditable().build()
        def left =  new SnapshotEntity(optionalInteger: leftOptional)
        def right = new SnapshotEntity(optionalInteger: rightOptional)

        when:
        def diff = itauAuditable.compare(left,right)

        then:
        assertThat(diff).hasValueChangeAt("optionalInteger", leftOptional, rightOptional)

        where:
        leftOptional     | rightOptional
        Optional.empty() | Optional.of(1)
        Optional.of(1)   | Optional.of(2)
    }

    def "should support value changes in optional ValueObjects"(){
        def itauAuditable = itauAuditable().build()
        def left =  new SnapshotEntity(optionalValueObject: Optional.of(new DummyAddress("New York")) )
        def right = new SnapshotEntity(optionalValueObject: Optional.of(new DummyAddress("Paris")) )

        when:
        def diff = itauAuditable.compare(left,right)

        then:
        diff.changes.size() == 1
        def change = diff.changes[0]
        change.affectedGlobalId == valueObjectId(1, SnapshotEntity, "optionalValueObject")
        change instanceof ValueChange
        change.left == "New York"
        change.right == "Paris"
    }
}
