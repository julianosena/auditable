package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.ShallowReference
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName
import br.com.zup.itau.auditable.core.metamodel.annotation.Value
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository
import br.com.zup.itau.auditable.repository.inmemory.InMemoryRepository
import br.com.zup.itau.auditable.repository.jql.JqlQuery
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

import javax.persistence.Id
import static java.util.UUID.*

/**
 * https://github.com/javers/javers/issues/897
 */
class CaseEmbeddedIdDeserializeProblem extends Specification {

    protected ItauAuditableRepository repository = new InMemoryRepository()
    protected ItauAuditable javers

    def setup() {
        javers = buildItauAuditableInstance()
    }

    ItauAuditable buildItauAuditableInstance() {
        def javersBuilder = ItauAuditableBuilder
                .javers()
                .registerItauAuditableRepository(repository)

        javersBuilder.build()
    }

    @TypeName("Agreement")
    class Agreement {

        @Id
        UUID agreementId

        @ShallowReference
        List<AgreementMember> agreementMembers
    }

    @TypeName("AgreementMember")
    class AgreementMember {

        @Id
        AgreementMemberId agreementMemberId
    }

    @Value
    static class AgreementMemberId implements Serializable {
        UUID agreementId
        UUID memberId
    }

    def "should read shadows for classes with EmbeddedId"() {
        given:
        def agreementId = randomUUID()

        AgreementMemberId agreementMemberId = new AgreementMemberId(
                agreementId:agreementId,
                memberId:randomUUID()
        )

        AgreementMember agreementMember = new AgreementMember(agreementMemberId:agreementMemberId)
        Agreement agreement = new Agreement(
                agreementId: agreementId,
                agreementMembers: [agreementMember]
        )

        javers.commit("Agreement", agreement)

        //query by typeName:
        JqlQuery query = QueryBuilder.byInstanceId(agreement.agreementId, "Agreement").build()

        //read immediately:
        when:
        List<CdoSnapshot> snapshots1 = javers.findSnapshots(query)

        then:
        snapshots1.size() > 0
        snapshots1[0].state.getPropertyValue("agreementMembers")[0] instanceof InstanceId
        snapshots1[0].getManagedType().baseJavaClass.getName().equals(this.class.name + "\$Agreement")

        //read same data after restart:
        when:
        def javers2 = buildItauAuditableInstance()

        //This is critical, because fresh ItauAuditable instance doesn't know the Agreement class.
        //On production, ItauAuditableBuilder.withPackagesToScan() should be used.
        javers2.getTypeMapping(Agreement)

        List<CdoSnapshot> snapshots2 = javers2.findSnapshots(query)

        //expecting the same result but fail:
        then:
        snapshots2.size() > 0
        snapshots2[0].state.getPropertyValue("agreementMembers")[0] instanceof InstanceId
        snapshots2[0].getManagedType().baseJavaClass.getName().equals(this.class.name + "\$Agreement")//java.lang.Object
    }
}