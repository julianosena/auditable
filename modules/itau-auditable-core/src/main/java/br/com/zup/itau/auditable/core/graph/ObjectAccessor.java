package br.com.zup.itau.auditable.core.graph;

import java.util.Optional;

public interface ObjectAccessor<T> {

    Class<T> getTargetClass();

    T access();

    Optional<Object> getLocalId();
}
