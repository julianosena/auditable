package br.com.zup.itau.auditable.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import br.com.zup.itau.auditable.core.json.JsonTypeAdapterTemplate;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshotState;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotStateTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshotState> {

    @Override
    public Class getValueType() {
        return CdoSnapshotState.class;
    }

    @Override
    public CdoSnapshotState fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new UnsupportedOperationException("use CdoSnapshotStateDeserializer");
    }

    @Override
    public JsonElement toJson(CdoSnapshotState snapshotState, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        snapshotState.forEachProperty( (pName, pValue) -> jsonObject.add(pName, context.serialize(pValue)));
        return jsonObject;
    }
}
