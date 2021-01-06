package br.com.zup.itau.auditable.core.diff.changetype;

import br.com.zup.itau.auditable.common.string.PrettyValuePrinter;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

import java.util.Optional;

/**
 * New object added to a graph
 *
 * @author bartosz walacik
 */
public final class NewObject extends Change {

    NewObject(GlobalId newId, Optional<Object> newCdo) {
        this(newId, newCdo, Optional.empty());
    }

    public NewObject(GlobalId newId, Optional<Object> newCdo, Optional<CommitMetadata> commitMetadata) {
        super(newId, newCdo, commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NewObject) {
            NewObject that = (NewObject) obj;
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
        return "new object: " + getAffectedGlobalId().value();
    }
}
