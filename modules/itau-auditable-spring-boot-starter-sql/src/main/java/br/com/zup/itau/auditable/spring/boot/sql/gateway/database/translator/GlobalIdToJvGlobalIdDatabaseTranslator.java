package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.JvGlobalIdDatabase;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.JvSnapshotDatabase;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalIdToJvGlobalIdDatabaseTranslator {

    public static JvGlobalIdDatabase translate(GlobalId globalId) {
        List<JvSnapshotDatabase> listOfJvSnapshotDatabase = globalId.getSnapshots()
                .stream()
                .map(SnapshotToJvSnapshotDatabaseTranslator::translate)
                .collect(Collectors.toList());


        return new JvGlobalIdDatabase(
                globalId.getGlobalId(),
                globalId.getLocalId(),
                globalId.getFragment(),
                globalId.getTypeName(),
                globalId.getOwnerIdFk(),
                listOfJvSnapshotDatabase
        );
    }
}
