package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.pico.LateInstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class DiffAppendersModule extends LateInstantiatingModule {

    private final Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender;

    public DiffAppendersModule(ItauAuditableCoreConfiguration javersCoreConfiguration, MutablePicoContainer container) {
        super(javersCoreConfiguration, container);
        this.listChangeAppender = javersCoreConfiguration.getListCompareAlgorithm().getAppenderClass();
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection)Lists.asList(
                NewObjectAppender.class,
                MapChangeAppender.class,
                CollectionAsListChangeAppender.class,
                listChangeAppender,
                SetChangeAppender.class,
                ArrayChangeAppender.class,
                ObjectRemovedAppender.class,
                ReferenceChangeAppender.class,
                OptionalChangeAppender.class,
                ValueChangeAppender.class
        );
    }
}
