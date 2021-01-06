package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

public class JqlModule extends InstantiatingModule {
    public JqlModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return Lists.<Class>asList(
                QueryRunner.class,
                ShadowQueryRunner.class,
                ShadowStreamQueryRunner.class,
                ChangesQueryRunner.class,
                SnapshotQueryRunner.class,
                QueryCompiler.class
        );
    }

}
