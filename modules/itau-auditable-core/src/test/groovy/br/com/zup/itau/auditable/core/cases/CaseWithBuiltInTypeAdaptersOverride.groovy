package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter
import br.com.zup.itau.auditable.core.json.typeadapter.util.FileTypeAdapter
import br.com.zup.itau.auditable.core.json.typeadapter.util.JavaSqlDateTypeAdapter
import br.com.zup.itau.auditable.core.json.typeadapter.util.JavaSqlTimeTypeAdapter
import br.com.zup.itau.auditable.core.json.typeadapter.util.JavaSqlTimestampTypeAdapter
import br.com.zup.itau.auditable.core.json.typeadapter.util.JavaUtilDateTypeAdapter
import br.com.zup.itau.auditable.core.json.typeadapter.util.UUIDTypeAdapter
import br.com.zup.itau.auditable.java8support.DurationTypeAdapter
import br.com.zup.itau.auditable.java8support.InstantTypeAdapter
import br.com.zup.itau.auditable.java8support.LocalDateTimeTypeAdapter
import br.com.zup.itau.auditable.java8support.LocalDateTypeAdapter
import br.com.zup.itau.auditable.java8support.LocalTimeTypeAdapter
import br.com.zup.itau.auditable.java8support.OffsetDateTimeTypeAdapter
import br.com.zup.itau.auditable.java8support.PeriodTypeAdapter
import br.com.zup.itau.auditable.java8support.YearTypeAdapter
import br.com.zup.itau.auditable.java8support.ZoneOffsetTypeAdapter
import br.com.zup.itau.auditable.java8support.ZonedDateTimeTypeAdapter
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Time
import java.sql.Timestamp
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

class DummyCustomTypeAdapter<T> extends BasicStringTypeAdapter<T>{
    private BasicStringTypeAdapter<T> delegate

    DummyCustomTypeAdapter(BasicStringTypeAdapter<T> delegate) {
        this.delegate = delegate
    }

    @Override
    String serialize(T sourceValue) {
        "!_" + delegate.serialize(sourceValue)
    }

    @Override
    T deserialize(String serializedValue) {
        delegate.deserialize(serializedValue.replace("!_",""))
    }

    @Override
    Class getValueType() {
        delegate.getValueType()
    }
}

class CaseWithBuiltInTypeAdaptersOverride extends Specification {
    @Unroll
    def "should support custom JsonTypeAdapter for #adapter.getValueType()"() {
        given:
        def converter = ItauAuditableBuilder.javers().registerValueTypeAdapter(adapter).build().getJsonConverter()
        def defaultConverter = ItauAuditableBuilder.javers().build().getJsonConverter()

        expect:
        converter.toJson(obj) == "\"" + adapter.serialize(obj) + "\""
        converter.fromJson("\"" + adapter.serialize(obj) + "\"", adapter.getValueType()) ==
                adapter.deserialize(adapter.serialize(obj))
        converter.toJson(obj) != defaultConverter.toJson(obj)

        where:
        adapter << [
                new DummyCustomTypeAdapter(new LocalDateTypeAdapter()),
                new DummyCustomTypeAdapter(new LocalDateTimeTypeAdapter()),
                new DummyCustomTypeAdapter(new LocalTimeTypeAdapter()),
                new DummyCustomTypeAdapter(new YearTypeAdapter()),
                new DummyCustomTypeAdapter(new ZonedDateTimeTypeAdapter()),
                new DummyCustomTypeAdapter(new ZoneOffsetTypeAdapter()),
                new DummyCustomTypeAdapter(new OffsetDateTimeTypeAdapter()),
                new DummyCustomTypeAdapter(new InstantTypeAdapter()),
                new DummyCustomTypeAdapter(new PeriodTypeAdapter()),
                new DummyCustomTypeAdapter(new DurationTypeAdapter()),
                new DummyCustomTypeAdapter(new JavaUtilDateTypeAdapter()),
                new DummyCustomTypeAdapter(new JavaSqlDateTypeAdapter()),
                new DummyCustomTypeAdapter(new JavaSqlTimestampTypeAdapter()),
                new DummyCustomTypeAdapter(new JavaSqlTimeTypeAdapter()),
                new DummyCustomTypeAdapter(new FileTypeAdapter()),
                new DummyCustomTypeAdapter(new UUIDTypeAdapter())
        ]

        obj << [
                LocalDate.now(),
                LocalDateTime.now(),
                LocalTime.now(),
                Year.now(),
                ZonedDateTime.now(),
                ZoneOffset.ofHours(0),
                OffsetDateTime.now(),
                Instant.now(),
                Period.ofDays(1),
                Duration.ofHours(1),
                new Date(),
                new java.sql.Date(2020, 1, 1),
                Timestamp.from(Instant.now()),
                new Time(1, 1, 1),
                new File("file"),
                UUID.randomUUID()
        ]
    }
}
