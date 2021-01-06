package br.com.zup.itau.auditable.guava;

import br.com.zup.itau.auditable.common.collections.Sets;
import br.com.zup.itau.auditable.core.ConditionalTypesPlugin;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.diff.appenders.PropertyChangeAppender;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;

import java.util.Collection;
import java.util.Set;

/**
 * @author akrystian
 */
public class GuavaAddOns extends ConditionalTypesPlugin {
    @Override
    public Collection<Class<? extends PropertyChangeAppender<?>>> getPropertyChangeAppenders(){
        return (Set)Sets.asSet(MultisetChangeAppender.class, MultimapChangeAppender.class);
    }

    @Override
    public Collection<ItauAuditableType> getNewTypes() {
        return (Set)Sets.asSet(MultimapType.getInstance(),
                               MultisetType.getInstance());
    }

    @Override
    public void beforeAssemble(ItauAuditableBuilder itauAuditableBuilder) {
        itauAuditableBuilder.registerJsonAdvancedTypeAdapter(new MultimapTypeAdapter());
        itauAuditableBuilder.registerJsonAdvancedTypeAdapter(new MultisetTypeAdapter());
    }
}
