package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.pico.ItauAuditableModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class DiffFactoryModule implements ItauAuditableModule{

    @Override
    public Collection<Class> getComponents() {
        return (Collection) Lists.asList(
                DiffFactory.class
        );
    }
}
