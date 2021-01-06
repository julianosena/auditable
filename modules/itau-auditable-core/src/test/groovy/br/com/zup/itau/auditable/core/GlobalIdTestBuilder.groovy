package br.com.zup.itau.auditable.core

import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.core.metamodel.object.UnboundedValueObjectId
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId

class GlobalIdTestBuilder {
    static ItauAuditableTestBuilder javersTestBuilder = ItauAuditableTestBuilder.javersTestAssembly()

    static InstanceId instanceId(Object instance){
        javersTestBuilder.instanceId(instance)
    }

    static InstanceId instanceId(Object localId, Class entity){
        javersTestBuilder.instanceId(localId, entity)
    }

    static ValueObjectId valueObjectId(Object localId, Class owningEntity, fragment) {
        new ValueObjectId("?", instanceId(localId, owningEntity), fragment)
    }

    static UnboundedValueObjectId unboundedValueObjectId(Class valueObject) {
        javersTestBuilder.unboundedValueObjectId(valueObject)
    }
}
