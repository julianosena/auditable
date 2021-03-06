package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class GraphFactoryModule extends InstantiatingModule {
    public GraphFactoryModule(MutablePicoContainer container) {
        super(container);
    }
    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
               LiveCdoFactory.class,
               CollectionsCdoFactory.class,
               LiveGraphFactory.class,
               ObjectHasher.class,
               ObjectGraphBuilder.class,
               ObjectAccessHookDoNothingImpl.class);
    }
}
