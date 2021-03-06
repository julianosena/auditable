package br.com.zup.itau.auditable.core.metamodel.scanner;

import br.com.zup.itau.auditable.common.reflection.ItauAuditableMember;
import br.com.zup.itau.auditable.common.reflection.ReflectionUtil;

import java.util.List;

/**
 * @author pawel szymczyk
 */
class BeanBasedPropertyScanner extends PropertyScanner {

    BeanBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        super(annotationNamesProvider);
    }

    @Override
    List<ItauAuditableMember> getMembers(Class<?> managedClass) {
        return (List)ReflectionUtil.getAllGetters(managedClass);
    }
}
