package br.com.zup.itau.auditable.java8support;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bartosz.walacik
 */
class OffsetDateTimeTypeAdapter extends BasicStringTypeAdapter<OffsetDateTime> {
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String serialize(OffsetDateTime sourceValue) {
        return sourceValue.format(ISO_FORMAT);
    }

    @Override
    public OffsetDateTime deserialize(String serializedValue) {
        return OffsetDateTime.parse(serializedValue, ISO_FORMAT);
    }

    @Override
    public Class getValueType() {
        return OffsetDateTime.class;
    }
}
