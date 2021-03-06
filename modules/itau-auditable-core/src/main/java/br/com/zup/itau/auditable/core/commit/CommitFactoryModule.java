package br.com.zup.itau.auditable.core.commit;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CommitFactoryModule extends InstantiatingModule {
    public CommitFactoryModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection)Lists.asList(
                CommitFactory.class,
                CommitSeqGenerator.class,
                CommitIdFactory.class,
                DistributedCommitSeqGenerator.class
        );
    }
}
