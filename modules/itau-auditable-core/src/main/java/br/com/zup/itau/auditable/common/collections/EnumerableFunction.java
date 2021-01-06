package br.com.zup.itau.auditable.common.collections;

import br.com.zup.itau.auditable.core.metamodel.object.EnumerationAwareOwnerContext;

@FunctionalInterface
public interface EnumerableFunction<F,T> {
    T apply(F input, EnumerationAwareOwnerContext ownerContext);
}
