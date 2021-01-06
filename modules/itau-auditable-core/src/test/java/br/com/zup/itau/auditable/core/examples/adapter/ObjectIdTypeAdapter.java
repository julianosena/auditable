package br.com.zup.itau.auditable.core.examples.adapter;

import org.bson.types.ObjectId;
import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;

public class ObjectIdTypeAdapter extends BasicStringTypeAdapter {

    @Override
    public String serialize(Object sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Object deserialize(String serializedValue) {
        return new ObjectId(serializedValue);
    }

    @Override
    public Class getValueType() {
        return ObjectId.class;
    }
}
