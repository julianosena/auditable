package br.com.zup.itau.auditable.java8support;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;
import br.com.zup.itau.auditable.core.json.typeadapter.util.UtilTypeCoreAdapters;
import java.time.LocalDateTime;

/**
 * @author bartosz.walacik
 */
class LocalDateTimeTypeAdapter extends BasicStringTypeAdapter<LocalDateTime> {

    @Override
    public String serialize(LocalDateTime sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public LocalDateTime deserialize(String serializedValue) {
        return LocalDateTime.from(UtilTypeCoreAdapters.deserializeLocalDateTime(serializedValue));
    }

    @Override
    public Class getValueType() {
        return LocalDateTime.class;
    }
}
