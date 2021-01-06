package br.com.zup.itau.auditable.core.diff.changetype;

import br.com.zup.itau.auditable.common.string.PrettyValuePrinter;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

import java.util.Optional;

/**
 * Object removed from a graph
 *
 * @author bartosz walacik
 */
public final class ObjectRemoved extends Change {

    ObjectRemoved(GlobalId removed, Optional<Object> removedCdo) {
        this(removed, removedCdo, Optional.empty());
    }

    public ObjectRemoved(GlobalId removed, Optional<Object> removedCdo, Optional<CommitMetadata> commitMetadata) {
        super(removed, removedCdo, commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ObjectRemoved) {
            ObjectRemoved that = (ObjectRemoved) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);
        return "object removed: " + getAffectedGlobalId().value();
    }
}
