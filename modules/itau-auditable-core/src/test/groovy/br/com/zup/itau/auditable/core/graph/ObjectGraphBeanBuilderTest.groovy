package br.com.zup.itau.auditable.core.graph

import br.com.zup.itau.auditable.core.MappingStyle

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.javersTestAssembly

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    def setupSpec() {
        javers = javersTestAssembly(MappingStyle.BEAN)
    }
}
