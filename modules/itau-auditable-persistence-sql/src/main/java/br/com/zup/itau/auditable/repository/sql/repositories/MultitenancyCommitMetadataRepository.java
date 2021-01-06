package br.com.zup.itau.auditable.repository.sql.repositories;

import br.com.zup.itau.auditable.core.commit.CommitId;
import br.com.zup.itau.auditable.core.json.typeadapter.util.UtilTypeCoreAdapters;
import br.com.zup.itau.auditable.repository.sql.schema.MultitenancySchemaNameAware;
import br.com.zup.itau.auditable.repository.sql.schema.MultitenancyTableNameProvider;
import br.com.zup.itau.auditable.repository.sql.schema.TableNameProvider;
import br.com.zup.itau.auditable.repository.sql.session.Session;
import org.polyjdbc.core.type.Timestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static br.com.zup.itau.auditable.repository.sql.schema.FixedSchemaFactory.*;

public class MultitenancyCommitMetadataRepository extends MultitenancySchemaNameAware {

    public MultitenancyCommitMetadataRepository(MultitenancyTableNameProvider tableNameProvider) {
        super(tableNameProvider);
    }

    public long save(String author, Map<String, String> properties, LocalDateTime date, Instant dateInstant, CommitId commitId, Session session) {
        long commitPk = session.insert("Commit")
                .into(getCommitTableNameWithSchema())
                .value(COMMIT_AUTHOR, author)
                .value(COMMIT_COMMIT_DATE, date)
                .value(COMMIT_COMMIT_DATE_INSTANT, UtilTypeCoreAdapters.serialize(dateInstant))
                .value(COMMIT_COMMIT_ID, commitId.valueAsNumber())
                .sequence(COMMIT_PK, getCommitPkSeqName().nameWithSchema())
                .executeAndGetSequence();

        insertCommitProperties(commitPk, properties, session);
        return commitPk;
    }

    private void insertCommitProperties(long commitPk, Map<String, String> properties, Session session) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            session.insert("CommitProperty")
                   .into(getCommitPropertyTableNameWithSchema())
                   .value(COMMIT_PROPERTY_COMMIT_FK, commitPk)
                   .value(COMMIT_PROPERTY_NAME, property.getKey())
                   .value(COMMIT_PROPERTY_VALUE, property.getValue())
                   .execute();
        }
    }

    boolean isCommitPersisted(CommitId commitId, Session session) {
        long count = session.select("count(*)")
               .from(getCommitTableNameWithSchema())
               .and(COMMIT_COMMIT_ID, commitId.valueAsNumber())
               .queryForLong("isCommitPersisted");

        return count > 0;
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return new Timestamp(UtilTypeCoreAdapters.toUtilDate(commitMetadata));
    }

    public CommitId getCommitHeadId(Session session) {
        Optional<BigDecimal> maxCommitId = selectMaxCommitId(session);

        return maxCommitId.map(max -> CommitId.valueOf(maxCommitId.get()))
                .orElse(null);
    }

    private Optional<BigDecimal> selectMaxCommitId(Session session) {
        return session.select("MAX(" + COMMIT_COMMIT_ID + ")")
                .from(getCommitTableNameWithSchema())
                .queryForOptionalBigDecimal("max CommitId");
    }
}
