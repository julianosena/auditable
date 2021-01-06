package br.com.zup.itau.auditable.core.diff.changetype.container;

import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeMetadata;

import java.util.List;

/**
 * Changes on a Collection property
 *
 * @author bartosz walacik
 */
public abstract class CollectionChange extends ContainerChange {
    public CollectionChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes);
    }
}
