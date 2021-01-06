package br.com.zup.itau.auditable.groovysupport;

import br.com.zup.itau.auditable.common.reflection.ReflectionUtil;
import br.com.zup.itau.auditable.core.ConditionalTypesPlugin;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;

/**
 * @author bartosz.walacik
 */
public class GroovyAddOns extends ConditionalTypesPlugin {
    @Override
    public void beforeAssemble(ItauAuditableBuilder itauAuditableBuilder) {
        Class<?> metaClass = ReflectionUtil.classForName("groovy.lang.MetaClass");
        itauAuditableBuilder.registerIgnoredClass(metaClass);
    }
}
