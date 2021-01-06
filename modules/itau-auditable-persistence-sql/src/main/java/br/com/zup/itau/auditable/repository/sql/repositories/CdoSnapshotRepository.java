package br.com.zup.itau.auditable.repository.sql.repositories;

import br.com.zup.itau.auditable.core.json.JsonConverter;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.repository.sql.schema.SchemaNameAware;
import br.com.zup.itau.auditable.repository.sql.schema.TableNameProvider;
import br.com.zup.itau.auditable.repository.sql.session.Session;

import java.util.List;

import static br.com.zup.itau.auditable.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotRepository extends SchemaNameAware {

    private JsonConverter jsonConverter;
    private final GlobalIdRepository globalIdRepository;

    public CdoSnapshotRepository(GlobalIdRepository globalIdRepository, TableNameProvider tableNameProvider) {
        super(tableNameProvider);
        this.globalIdRepository = globalIdRepository;
    }

    public void save(long commitIdPk, List<CdoSnapshot> cdoSnapshots, Session session) {
        for (CdoSnapshot cdoSnapshot : cdoSnapshots) {
            long globalIdPk = globalIdRepository.getOrInsertId(cdoSnapshot.getGlobalId(), session);

            session.insert("Snapshot")
                    .into(getSnapshotTableNameWithSchema())
                    .value(SNAPSHOT_TYPE, cdoSnapshot.getType().toString())
                    .value(SNAPSHOT_GLOBAL_ID_FK, globalIdPk)
                    .value(SNAPSHOT_COMMIT_FK, commitIdPk)
                    .value(SNAPSHOT_VERSION, cdoSnapshot.getVersion())
                    .value(SNAPSHOT_STATE, jsonConverter.toJson(cdoSnapshot.getState()))
                    .value(SNAPSHOT_CHANGED, jsonConverter.toJson(cdoSnapshot.getChanged()))
                    .value(SNAPSHOT_MANAGED_TYPE, cdoSnapshot.getManagedType().getName())
                    .sequence(SNAPSHOT_PK, getSnapshotTablePkSeqName().nameWithSchema())
                    .execute();
        }
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
