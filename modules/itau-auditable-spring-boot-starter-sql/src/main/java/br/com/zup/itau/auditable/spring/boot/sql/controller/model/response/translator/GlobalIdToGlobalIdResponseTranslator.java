package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator;

import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.GlobalIdResponse;
import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.SnapshotResponse;
import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalIdToGlobalIdResponseTranslator {

    public static GlobalIdResponse translate(GlobalId globalId){
        List<SnapshotResponse> snapshotList = globalId.getSnapshots()
                .stream()
                .map(SnapshotToSnapshotResponseTranslator::translate)
                .collect(Collectors.toList());

        return new GlobalIdResponse(
                globalId.getGlobalId(),
                globalId.getLocalId(),
                globalId.getFragment(),
                globalId.getTypeName(),
                globalId.getOwnerIdFk(),
                snapshotList);
    }

}
