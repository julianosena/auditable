package br.com.zup.itau.auditable.core.pico;

import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import org.picocontainer.MutablePicoContainer;

/**
 * @author bartosz.walacik
 */
public abstract class LateInstantiatingModule extends InstantiatingModule {

    private final ItauAuditableCoreConfiguration configuration;

    public LateInstantiatingModule(ItauAuditableCoreConfiguration configuration, MutablePicoContainer container) {
        super(container);
        this.configuration = configuration;
    }

    protected ItauAuditableCoreConfiguration getConfiguration() {
        return configuration;
    }
}
