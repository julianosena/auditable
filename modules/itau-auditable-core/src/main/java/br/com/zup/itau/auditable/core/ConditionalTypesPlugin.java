package br.com.zup.itau.auditable.core;

import br.com.zup.itau.auditable.core.diff.appenders.PropertyChangeAppender;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;

import java.util.Collection;
import java.util.Collections;

/**
 * @author bartosz.walacik
 */
public abstract class ConditionalTypesPlugin {

    public Collection<Class<? extends PropertyChangeAppender<?>>> getPropertyChangeAppenders() {
        return Collections.emptyList();
    }

    public Collection<ItauAuditableType> getNewTypes() {
        return Collections.emptyList();
    }

    public void beforeAssemble(ItauAuditableBuilder itauAuditableBuilder) {}
}
