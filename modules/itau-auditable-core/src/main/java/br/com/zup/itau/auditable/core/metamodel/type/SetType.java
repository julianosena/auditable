package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.collections.EnumerableFunction;
import br.com.zup.itau.auditable.common.collections.Sets;
import br.com.zup.itau.auditable.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;

public class SetType extends CollectionType{

    public SetType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object empty() {
        return Collections.emptySet();
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return super.mapToSet(sourceEnumerable, mapFunction, owner);
    }

    @Override
    public Object map(Object sourceEnumerable, Function mapFunction, boolean filterNulls) {
        Set sourceCol = Sets.wrapNull(sourceEnumerable);
        return unmodifiableSet((Set)sourceCol.stream()
                .map(sourceVal -> sourceVal == null ? null : mapFunction.apply(sourceVal))
                .filter(mappedVal -> !filterNulls || mappedVal != null)
                .collect(Collectors.toSet()));
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return Set.class;
    }
}
