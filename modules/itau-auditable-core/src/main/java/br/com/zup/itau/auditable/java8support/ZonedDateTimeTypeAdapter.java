package br.com.zup.itau.auditable.java8support;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;
import br.com.zup.itau.auditable.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.time.ZonedDateTime;

/**
 * @author bartosz.walacik
 */
class ZonedDateTimeTypeAdapter extends BasicStringTypeAdapter<ZonedDateTime> {

    @Override
    public String serialize(ZonedDateTime sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public ZonedDateTime deserialize(String serializedValue) {
        return UtilTypeCoreAdapters.deserializeToZonedDateTime(serializedValue);
    }


    @Override
    public Class getValueType() {
        return ZonedDateTime.class;
    }
}
