package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator;

import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.CommitResponse;
import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.SnapshotResponse;
import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class SnapshotToSnapshotResponseTranslator {

    public static SnapshotResponse translate(Snapshot snapshot) throws JsonProcessingException {
        String changedProperties = snapshot.getChangedProperties();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> changedPropertiesList = objectMapper.readValue(changedProperties, new TypeReference<List<String>>() {
        });

        Map<String, Object> state = objectMapper.readValue(snapshot.getState(), new TypeReference<Map<String, Object>>() {
        });

        CommitResponse commitResponse = CommitToCommitResponseTranslator.translate(snapshot.getCommit());

        return new SnapshotResponse(
                snapshot.getSnapshotPk(),
                snapshot.getType(),
                snapshot.getVersion(),
                state,
                changedPropertiesList,
                commitResponse
        );
    }
}
