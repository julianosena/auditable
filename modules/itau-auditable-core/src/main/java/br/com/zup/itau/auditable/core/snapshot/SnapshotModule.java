package br.com.zup.itau.auditable.core.snapshot;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class SnapshotModule extends InstantiatingModule {
    public SnapshotModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                SnapshotFactory.class,
                SnapshotDiffer.class,
                SnapshotGraphFactory.class,
                ChangedCdoSnapshotsFactory.class
        );
    }
}
