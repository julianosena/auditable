package br.com.zup.itau.auditable.core

import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.core.metamodel.object.UnboundedValueObjectId
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId

class GlobalIdTestBuilder {
    static ItauAuditableTestBuilder itauAuditableTestBuilder = ItauAuditableTestBuilder.itauAuditableTestAssembly()

    static InstanceId instanceId(Object instance){
        itauAuditableTestBuilder.instanceId(instance)
    }

    static InstanceId instanceId(Object localId, Class entity){
        itauAuditableTestBuilder.instanceId(localId, entity)
    }

    static ValueObjectId valueObjectId(Object localId, Class owningEntity, fragment) {
        new ValueObjectId("?", instanceId(localId, owningEntity), fragment)
    }

    static UnboundedValueObjectId unboundedValueObjectId(Class valueObject) {
        itauAuditableTestBuilder.unboundedValueObjectId(valueObject)
    }
}
