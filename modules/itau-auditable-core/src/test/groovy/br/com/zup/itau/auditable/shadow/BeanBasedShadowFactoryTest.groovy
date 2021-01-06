package br.com.zup.itau.auditable.shadow

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot

class BeanBasedShadowFactoryTest extends ShadowFactoryTest {
    @Override
    def setupSpec() {
        javersTestAssembly = ItauAuditableTestBuilder.javersTestAssembly(MappingStyle.BEAN)
        shadowFactory = javersTestAssembly.shadowFactory
        javers = javersTestAssembly.javers()
    }

    def "should throw SETTER_NOT_FOUND Exception when setter is not found"() {
        given:
        def entity = new ImmutableEntity(1, new ImmutableEntity(2))
        javers.commit("author", entity)

        when:
        CdoSnapshot snapshot = javers.getLatestSnapshot(1, ImmutableEntity).get()
        shadowFactory.createShadow(snapshot, byIdSupplier())

        then:
        ItauAuditableException e = thrown()
        e.code == ItauAuditableExceptionCode.SETTER_NOT_FOUND
    }
}
