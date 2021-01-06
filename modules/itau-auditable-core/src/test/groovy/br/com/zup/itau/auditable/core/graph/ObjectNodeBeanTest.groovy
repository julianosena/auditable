package br.com.zup.itau.auditable.core.graph

import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.core.metamodel.type.TypeFactoryTest

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly

/**
 * @author bartosz walacik
 */
class ObjectNodeBeanTest extends ObjectNodeTest{

    def setup() {
        createEntity = TypeFactoryTest.entityCreator(MappingStyle.BEAN)
    }
}
