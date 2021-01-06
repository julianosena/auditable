package br.com.zup.itau.auditable.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import br.com.zup.itau.auditable.core.json.JsonTypeAdapterTemplate;

/**
 * @author bartosz walacik
 */
class JsonElementFakeAdapter extends JsonTypeAdapterTemplate<JsonElement> {
    @Override
    public Class getValueType() {
        return JsonElement.class;
    }

    @Override
    public JsonElement fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        return json;
    }

    @Override
    public JsonElement toJson(JsonElement sourceValue, JsonSerializationContext jsonSerializationContext) {
        throw new UnsupportedOperationException();
    }
}
