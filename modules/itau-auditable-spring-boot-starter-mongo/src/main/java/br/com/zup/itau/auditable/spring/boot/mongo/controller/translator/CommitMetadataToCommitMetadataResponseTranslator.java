package br.com.zup.itau.auditable.spring.boot.mongo.controller.translator;

import br.com.zup.itau.auditable.spring.boot.mongo.controller.model.response.CommitMetadataResponse;
import br.com.zup.itau.auditable.spring.boot.mongo.gateway.database.model.CommitMetadata;

public class CommitMetadataToCommitMetadataResponseTranslator {

    private CommitMetadataToCommitMetadataResponseTranslator() {
    }

    public static CommitMetadataResponse execute(final CommitMetadata commitMetadata) {
        return new CommitMetadataResponse(commitMetadata.getAuthor(),
                commitMetadata.getCommitDate(),
                commitMetadata.getCommitId());
    }
}
