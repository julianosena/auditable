package br.com.zup.itau.auditable.core;

import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository;

import java.util.Comparator;

/**
 * @author bartosz.walacik
 */
public enum CommitIdGenerator {
    /**
     * Generates neat, sequential commit identifiers.
     * Based on {@link ItauAuditableRepository#getHeadId()}.
     * <br/><br/>
     *
     * Should not be used in distributed applications.
     */
    SYNCHRONIZED_SEQUENCE {
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDateInstant)
                    .thenComparing(CommitMetadata::getId);
        }
    },

    /**
     * Non-blocking algorithm based on UUID.
     * <br/><br/>
     *
     * Suitable for distributed applications.<br/>
     *
     * <b>Warning!</b> When RANDOM generator is set,
     * Shadow query runner sorts commits by commitDateInstant.
     * It means, that Shadow queries would be correct only
     * if all application servers have synchronized clocks.
     */
    RANDOM {
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDateInstant);
        }
    },

    /**
     * Provided by user
     */
    CUSTOM {
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDateInstant);
        }
    };

    public abstract Comparator<CommitMetadata> getComparator();
}
