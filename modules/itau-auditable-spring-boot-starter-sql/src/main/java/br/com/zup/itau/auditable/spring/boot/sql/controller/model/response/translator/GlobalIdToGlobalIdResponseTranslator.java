package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator;

import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.GlobalIdResponse;
import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.SnapshotResponse;
import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator.exception.ItauAuditableTranslatorException;
import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.domain.Snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalIdToGlobalIdResponseTranslator {

    public static GlobalIdResponse translate(GlobalId globalId) throws ItauAuditableTranslatorException {
        try {
            List<SnapshotResponse> snapshotList = new ArrayList<>();
            for (Snapshot snapshot : globalId.getSnapshots()) {
                SnapshotResponse translate = SnapshotToSnapshotResponseTranslator.translate(snapshot);
                snapshotList.add(translate);
            }

            return new GlobalIdResponse(
                    globalId.getGlobalId(),
                    globalId.getLocalId(),
                    globalId.getFragment(),
                    globalId.getOwnerIdFk(),
                    snapshotList);
        } catch (Exception e) {
            throw new ItauAuditableTranslatorException("Ocorreu o seguinte erro ao converter a resposta", e);
        }
    }

}
