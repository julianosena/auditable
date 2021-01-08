package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator;

import br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.CommitResponse;
import br.com.zup.itau.auditable.spring.boot.sql.domain.Commit;

public class CommitToCommitResponseTranslator {

    public static CommitResponse translate(Commit commit) {
        return new CommitResponse(
                commit.getCommitId(),
                commit.getCommitFk(),
                commit.getAuthor(),
                commit.getCommitDate()
        );
    }
}
