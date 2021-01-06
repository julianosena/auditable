package br.com.zup.itau.auditable.core.json.typeadapter.util;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;

import java.util.UUID;

/**
 * @author bartosz.walacik
 */
class UUIDTypeAdapter extends BasicStringTypeAdapter<UUID> {

    @Override
    public String serialize(UUID sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public UUID deserialize(String serializedValue) {
        return UUID.fromString(serializedValue);
    }

    @Override
    public Class getValueType() {
        return UUID.class;
    }
}
