package br.com.zup.itau.auditable.core;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.json.JsonConverterBuilder;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalIdFactory;
import br.com.zup.itau.auditable.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CoreItauAuditableModule extends InstantiatingModule {
    public CoreItauAuditableModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return Lists.<Class>asList(
                ItauAuditableCore.class,
                JsonConverterBuilder.class,
                ItauAuditableCoreConfiguration.class,
                GlobalIdFactory.class
        );
    }
}