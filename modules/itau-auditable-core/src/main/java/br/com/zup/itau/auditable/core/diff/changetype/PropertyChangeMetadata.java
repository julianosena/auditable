package br.com.zup.itau.auditable.core.diff.changetype;

import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

import java.util.Optional;

import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;

public class PropertyChangeMetadata {
    private final GlobalId affectedCdoId;
    private final String propertyName;
    private final Optional<CommitMetadata> commitMetadata;
    private final PropertyChangeType changeType;

    public PropertyChangeMetadata(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata, PropertyChangeType changeType) {
        argumentsAreNotNull(affectedCdoId, propertyName, commitMetadata, changeType);
        this.affectedCdoId = affectedCdoId;
        this.propertyName = propertyName;
        this.commitMetadata = commitMetadata;
        this.changeType = changeType;
    }

    public GlobalId getAffectedCdoId() {
        return affectedCdoId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    public PropertyChangeType getChangeType() {
        return changeType;
    }
}
