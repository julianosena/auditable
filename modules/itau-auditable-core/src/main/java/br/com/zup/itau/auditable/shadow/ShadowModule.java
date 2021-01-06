package br.com.zup.itau.auditable.shadow;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class ShadowModule extends InstantiatingModule {
    public ShadowModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                ShadowFactory.class
        );
    }
}
