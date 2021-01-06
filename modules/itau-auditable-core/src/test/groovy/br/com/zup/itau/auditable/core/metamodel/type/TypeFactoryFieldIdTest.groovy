package br.com.zup.itau.auditable.core.metamodel.type

import br.com.zup.itau.auditable.core.MappingStyle

/**
 * @author bartosz walacik
 */
class TypeFactoryFieldIdTest extends TypeFactoryIdTest {

    def setupSpec() {
        typeFactory = TypeFactoryTest.create(MappingStyle.FIELD)
    }
}
