package br.com.zup.itau.auditable.java8support;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;

import java.time.Period;

/**
 * @author bartosz.walacik
 */
class PeriodTypeAdapter extends BasicStringTypeAdapter<Period> {
    @Override
    public String serialize(Period sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Period deserialize(String serializedValue) {
        return Period.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Period.class;
    }
}
