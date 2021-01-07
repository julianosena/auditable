package br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator;

import br.com.zup.itau.auditable.spring.boot.sql.domain.Commit;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.CommitDatabase;

public class CommitDatabaseToCommitTranslator {

    public static Commit translate(CommitDatabase commitDatabase) {
        return new Commit(
                commitDatabase.getCommitId(),
                commitDatabase.getCommitPk(),
                commitDatabase.getAuthor(),
                commitDatabase.getCommitDate()
        );
    }
}
