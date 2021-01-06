package br.com.zup.itau.auditable.java8support;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;
import br.com.zup.itau.auditable.core.json.JsonTypeAdapter;

import java.time.ZoneOffset;

/**
 * @author bartosz.walacik
 */
class ZoneOffsetTypeAdapter extends BasicStringTypeAdapter<ZoneOffset> {
    @Override
    public String serialize(ZoneOffset sourceValue) {
        return sourceValue.getId();
    }

    @Override
    public ZoneOffset deserialize(String serializedValue) {
        return ZoneOffset.of(serializedValue);
    }

    @Override
    public Class getValueType() {
        return ZoneOffset.class;
    }
}
