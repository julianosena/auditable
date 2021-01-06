package br.com.zup.itau.auditable.core.examples;

import org.bson.types.ObjectId;
import org.fest.assertions.Assertions;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.cases.MongoStoredEntity;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.examples.adapter.ObjectIdTypeAdapter;
import org.junit.Test;

public class JsonTypeAdapterExample {

    @Test
    public void shouldSerializeValueToJsonWithTypeAdapter() {
        //given
        ItauAuditable javers = ItauAuditableBuilder.javers()
                .registerValueTypeAdapter(new ObjectIdTypeAdapter())
                .build();

        //when
        ObjectId id = ObjectId.get();
        MongoStoredEntity entity1 = new MongoStoredEntity(id, "alg1", "1.0", "name");
        MongoStoredEntity entity2 = new MongoStoredEntity(id, "alg1", "1.0", "another");
        Diff diff = javers.compare(entity1, entity2);

        //then
        String json = javers.getJsonConverter().toJson(diff);
        Assertions.assertThat(json).contains(id.toString());

        System.out.println(json);
    }
}
