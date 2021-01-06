package br.com.zup.itau.auditable.jodasupport;

import br.com.zup.itau.auditable.core.ConditionalTypesPlugin;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class JodaAddOns extends ConditionalTypesPlugin {

    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateHourMinuteSecondMillis();

    static String serialize(LocalDateTime date) {
        return ISO_DATE_TIME_FORMATTER.print(date);
    }

    static LocalDateTime deserialize(String serializedValue) {
        if (serializedValue == null) {
            return null;
        }
        if (serializedValue.length() == 19) {
            return deserialize(serializedValue + ".0");
        }
        return ISO_DATE_TIME_FORMATTER.parseLocalDateTime(serializedValue);
    }

    @Override
    public void beforeAssemble(ItauAuditableBuilder itauAuditableBuilder) {
        itauAuditableBuilder.registerValueTypeAdapter(new LocalDateTimeTypeAdapter());
        itauAuditableBuilder.registerValueTypeAdapter(new LocalDateTypeAdapter());
    }
}