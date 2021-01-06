package br.com.zup.itau.auditable.shadow

import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.repository.jql.QueryBuilder

class FieldBasedShadowFactoryTest extends ShadowFactoryTest {
    @Override
    def setupSpec() {
        itauAuditableTestAssembly = ItauAuditableTestBuilder.itauAuditableTestAssembly(MappingStyle.FIELD)
        shadowFactory = itauAuditableTestAssembly.shadowFactory
        itauAuditable = itauAuditableTestAssembly.itauAuditable()
    }

    def "should manage immutable objects creation"(){
        given:
        def ref = new ImmutableEntity(2, null)
        def cdo = new ImmutableEntity(1, ref)
        itauAuditable.commit("author", cdo)
        def snapshot = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(1, ImmutableEntity).build())[0]

        when:
        def shadow = shadowFactory.createShadow(snapshot, {s, id -> itauAuditable.findSnapshots(QueryBuilder.byInstanceId(id.cdoId, ImmutableEntity).build())[0]})

        then:
        shadow instanceof ImmutableEntity
        shadow.id == 1
        shadow.entityRef instanceof ImmutableEntity
        shadow.entityRef.id == 2
    }
}
