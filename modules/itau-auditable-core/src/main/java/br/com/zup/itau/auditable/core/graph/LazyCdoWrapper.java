package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;

import java.util.function.Supplier;

class LazyCdoWrapper extends LiveCdo {
    private final Supplier<?> cdoSupplier;

    LazyCdoWrapper(Supplier<?> cdoSupplier, GlobalId globalId, ManagedType managedType) {
        super(globalId, managedType);
        this.cdoSupplier = cdoSupplier;
    }

    @Override
    Object wrappedCdo() {
        return cdoSupplier.get();
    }
}
