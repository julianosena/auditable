package br.com.zup.itau.auditable.core.metamodel.scanner;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.core.MappingStyle;
import br.com.zup.itau.auditable.core.pico.LateInstantiatingModule;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class ScannerModule extends LateInstantiatingModule {
    private static final Logger logger = ItauAuditableBuilder.logger;

    public ScannerModule(ItauAuditableCoreConfiguration configuration, MutablePicoContainer container) {
        super(configuration, container);
    }

    @Override
    protected Collection<Class> getImplementations() {

        MappingStyle mappingStyle = getConfiguration().getMappingStyle();
        logger.info("mappingStyle: "+mappingStyle.name());

        Class<? extends PropertyScanner> usedPropertyScanner;
        if (mappingStyle == MappingStyle.BEAN){
            usedPropertyScanner = BeanBasedPropertyScanner.class;
        } else if (mappingStyle == MappingStyle.FIELD){
            usedPropertyScanner = FieldBasedPropertyScanner.class;
        } else{
            throw new RuntimeException("not implementation for "+mappingStyle);
        }

        return (Collection) Lists.asList(
                ClassScanner.class,
                ClassAnnotationsScanner.class,
                AnnotationNamesProvider.class,
                usedPropertyScanner
        );
    }
}
