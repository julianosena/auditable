package br.com.zup.itau.auditable.core.graph;

import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.ManagedType;

import static br.com.zup.itau.auditable.common.validation.Validate.argumentCheck;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentIsNotNull;

class LiveCdoWrapper extends LiveCdo {
    private Object wrappedCdo;

    LiveCdoWrapper(Object wrappedCdo, GlobalId globalId, ManagedType managedType) {
        super(globalId, managedType);

        argumentIsNotNull(wrappedCdo);
        argumentCheck(managedType.isInstance(wrappedCdo), "wrappedCdo is not an instance of given managedType '"+managedType.getBaseJavaClass().getSimpleName()+"' but '"+wrappedCdo.getClass().getSimpleName()+"'");

        this.wrappedCdo = wrappedCdo;
    }

    @Override
    Object wrappedCdo() {
        return wrappedCdo;
    }
}
