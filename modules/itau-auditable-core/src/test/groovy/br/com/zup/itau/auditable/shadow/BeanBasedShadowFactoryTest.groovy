package br.com.zup.itau.auditable.shadow

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot

class BeanBasedShadowFactoryTest extends ShadowFactoryTest {
    @Override
    def setupSpec() {
        itauAuditableTestAssembly = ItauAuditableTestBuilder.itauAuditableTestAssembly(MappingStyle.BEAN)
        shadowFactory = itauAuditableTestAssembly.shadowFactory
        itauAuditable = itauAuditableTestAssembly.itauAuditable()
    }

    def "should throw SETTER_NOT_FOUND Exception when setter is not found"() {
        given:
        def entity = new ImmutableEntity(1, new ImmutableEntity(2))
        itauAuditable.commit("author", entity)

        when:
        CdoSnapshot snapshot = itauAuditable.getLatestSnapshot(1, ImmutableEntity).get()
        shadowFactory.createShadow(snapshot, byIdSupplier())

        then:
        ItauAuditableException e = thrown()
        e.code == ItauAuditableExceptionCode.SETTER_NOT_FOUND
    }
}
