package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.core.MappingStyle;
import br.com.zup.itau.auditable.core.pico.LateInstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

public class TailoredItauAuditableMemberFactoryModule extends LateInstantiatingModule {

    public TailoredItauAuditableMemberFactoryModule(ItauAuditableCoreConfiguration configuration, MutablePicoContainer container) {
        super(configuration, container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        MappingStyle mappingStyle = getConfiguration().getMappingStyle();

        if (mappingStyle == MappingStyle.BEAN) {
            return (Collection) Lists.asList(TailoredItauAuditableMethodFactory.class);
        } else if (mappingStyle == MappingStyle.FIELD) {
            return (Collection) Lists.asList(TailoredItauAuditableFieldFactory.class);
        } else {
            throw new RuntimeException("not implementation for " + mappingStyle);
        }
    }
}
