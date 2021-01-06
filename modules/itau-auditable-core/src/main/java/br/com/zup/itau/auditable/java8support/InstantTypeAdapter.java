package br.com.zup.itau.auditable.java8support;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;
import br.com.zup.itau.auditable.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.time.Instant;

/**
 * @author bartosz.walacik
 */
class InstantTypeAdapter extends BasicStringTypeAdapter<Instant> {
    @Override
    public String serialize(Instant sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Instant deserialize(String serializedValue) {
        return UtilTypeCoreAdapters.deserializeToInstant(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Instant.class;
    }
}
