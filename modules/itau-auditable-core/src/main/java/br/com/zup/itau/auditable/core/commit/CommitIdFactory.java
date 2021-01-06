package br.com.zup.itau.auditable.core.commit;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;

import static br.com.zup.itau.auditable.core.CommitIdGenerator.*;

class CommitIdFactory {
    private final ItauAuditableCoreConfiguration javersCoreConfiguration;
    private final ItauAuditableExtendedRepository javersRepository;
    private final CommitSeqGenerator commitSeqGenerator;
    private final DistributedCommitSeqGenerator distributedCommitSeqGenerator;

    CommitIdFactory(ItauAuditableCoreConfiguration javersCoreConfiguration, ItauAuditableExtendedRepository javersRepository, CommitSeqGenerator commitSeqGenerator, DistributedCommitSeqGenerator distributedCommitSeqGenerator) {
        this.javersCoreConfiguration = javersCoreConfiguration;
        this.javersRepository = javersRepository;
        this.commitSeqGenerator = commitSeqGenerator;
        this.distributedCommitSeqGenerator = distributedCommitSeqGenerator;
    }

    CommitId nextId() {
        if (javersCoreConfiguration.getCommitIdGenerator() == SYNCHRONIZED_SEQUENCE) {
            CommitId head = javersRepository.getHeadId();
            return commitSeqGenerator.nextId(head);
        }

        if (javersCoreConfiguration.getCommitIdGenerator() == RANDOM) {
            return distributedCommitSeqGenerator.nextId();
        }

        if (javersCoreConfiguration.getCommitIdGenerator() == CUSTOM) {
            return javersCoreConfiguration.getCustomCommitIdGenerator().get();
        }

        throw new ItauAuditableException(ItauAuditableExceptionCode.NOT_IMPLEMENTED);
    }
}
