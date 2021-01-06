package br.com.zup.itau.auditable.core.commit;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;

import static br.com.zup.itau.auditable.core.CommitIdGenerator.*;

class CommitIdFactory {
    private final ItauAuditableCoreConfiguration itauAuditableCoreConfiguration;
    private final ItauAuditableExtendedRepository itauAuditableRepository;
    private final CommitSeqGenerator commitSeqGenerator;
    private final DistributedCommitSeqGenerator distributedCommitSeqGenerator;

    CommitIdFactory(ItauAuditableCoreConfiguration itauAuditableCoreConfiguration, ItauAuditableExtendedRepository itauAuditableRepository, CommitSeqGenerator commitSeqGenerator, DistributedCommitSeqGenerator distributedCommitSeqGenerator) {
        this.itauAuditableCoreConfiguration = itauAuditableCoreConfiguration;
        this.itauAuditableRepository = itauAuditableRepository;
        this.commitSeqGenerator = commitSeqGenerator;
        this.distributedCommitSeqGenerator = distributedCommitSeqGenerator;
    }

    CommitId nextId() {
        if (itauAuditableCoreConfiguration.getCommitIdGenerator() == SYNCHRONIZED_SEQUENCE) {
            CommitId head = itauAuditableRepository.getHeadId();
            return commitSeqGenerator.nextId(head);
        }

        if (itauAuditableCoreConfiguration.getCommitIdGenerator() == RANDOM) {
            return distributedCommitSeqGenerator.nextId();
        }

        if (itauAuditableCoreConfiguration.getCommitIdGenerator() == CUSTOM) {
            return itauAuditableCoreConfiguration.getCustomCommitIdGenerator().get();
        }

        throw new ItauAuditableException(ItauAuditableExceptionCode.NOT_IMPLEMENTED);
    }
}
