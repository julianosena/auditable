package br.com.zup.itau.auditable.repository.sql.finders;

import com.google.common.base.Joiner;
import br.com.zup.itau.auditable.repository.sql.schema.MultitenancyTableNameProvider;
import br.com.zup.itau.auditable.repository.sql.session.Session;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static br.com.zup.itau.auditable.repository.sql.schema.FixedSchemaFactory.*;

public class MultitenancyCommitPropertyFinder {

    private final MultitenancyTableNameProvider tableNameProvider;

    public MultitenancyCommitPropertyFinder(MultitenancyTableNameProvider tableNameProvider) {
        this.tableNameProvider = tableNameProvider;
    }

    List<CommitPropertyDTO> findCommitPropertiesOfSnaphots(Collection<Long> commitPKs, Session session) {
        if (commitPKs.isEmpty()) {
            return Collections.emptyList();
        }

        return session.select(COMMIT_PROPERTY_COMMIT_FK + ", " + COMMIT_PROPERTY_NAME + ", " + COMMIT_PROPERTY_VALUE)
               .from(tableNameProvider.getCommitPropertyTableNameWithSchema())
               .queryName("commit properties")
               .and(COMMIT_PROPERTY_COMMIT_FK + " in (" + Joiner.on(",").join(commitPKs) + ")")
               .executeQuery(resultSet -> new CommitPropertyDTO(
                       resultSet.getLong(COMMIT_PROPERTY_COMMIT_FK),
                       resultSet.getString(COMMIT_PROPERTY_NAME),
                       resultSet.getString(COMMIT_PROPERTY_VALUE)));
    }
}
