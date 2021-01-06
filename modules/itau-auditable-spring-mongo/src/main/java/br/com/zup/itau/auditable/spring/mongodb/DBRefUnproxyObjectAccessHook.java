package br.com.zup.itau.auditable.spring.mongodb;

import br.com.zup.itau.auditable.core.graph.ObjectAccessHook;
import br.com.zup.itau.auditable.core.graph.ObjectAccessProxy;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;

import java.util.Optional;

public class DBRefUnproxyObjectAccessHook<T> implements ObjectAccessHook<T> {
    @Override
    public Optional<ObjectAccessProxy<T>> createAccessor(T entity) {
        if (entity instanceof LazyLoadingProxy) {
            LazyLoadingProxy proxy = (LazyLoadingProxy) entity;

            return Optional.of(new ObjectAccessProxy(() -> proxy.getTarget(),
                    proxy.getTarget().getClass(),
                    proxy.toDBRef().getId().toString()));
        }
        return Optional.empty();
    }
}

