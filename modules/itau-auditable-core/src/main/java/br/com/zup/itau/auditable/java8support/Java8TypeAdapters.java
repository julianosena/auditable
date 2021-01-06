package br.com.zup.itau.auditable.java8support;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.json.JsonTypeAdapter;

import java.util.List;

public class Java8TypeAdapters {

    public static List<JsonTypeAdapter> adapters() {
        return (List) Lists.immutableListOf(
            new LocalDateTypeAdapter(),
            new LocalDateTimeTypeAdapter(),
            new LocalTimeTypeAdapter(),
            new YearTypeAdapter(),
            new ZonedDateTimeTypeAdapter(),
            new ZoneOffsetTypeAdapter(),
            new OffsetDateTimeTypeAdapter(),
            new InstantTypeAdapter(),
            new PeriodTypeAdapter(),
            new DurationTypeAdapter());
    }
}