package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.core.metamodel.object.EnumerationAwareOwnerContext;
import br.com.zup.itau.auditable.core.metamodel.object.OwnerContext;

/**
 * @author bartosz.walacik
 */
class IndexableEnumerationOwnerContext extends EnumerationAwareOwnerContext {
    private int index;

    IndexableEnumerationOwnerContext(OwnerContext ownerContext) {
        super(ownerContext);
    }

    @Override
    public String getEnumeratorContextPath() {
        return ""+(index++);
    }
}
