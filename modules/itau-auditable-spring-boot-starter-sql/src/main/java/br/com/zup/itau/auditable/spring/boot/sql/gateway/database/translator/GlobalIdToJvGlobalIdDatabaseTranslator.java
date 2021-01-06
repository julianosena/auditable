package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.GlobalIdDatabase;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.SnapshotDatabase;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalIdToJvGlobalIdDatabaseTranslator {

    public static GlobalIdDatabase translate(GlobalId globalId) {
        List<SnapshotDatabase> listOfSnapshotDatabase = globalId.getSnapshots()
                .stream()
                .map(SnapshotToJvSnapshotDatabaseTranslator::translate)
                .collect(Collectors.toList());


        return new GlobalIdDatabase(
                globalId.getGlobalId(),
                globalId.getLocalId(),
                globalId.getFragment(),
                globalId.getTypeName(),
                globalId.getOwnerIdFk(),
                listOfSnapshotDatabase
        );
    }
}
