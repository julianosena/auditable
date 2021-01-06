package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.ShallowReference
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName
import br.com.zup.itau.auditable.repository.jql.JqlQuery
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.shadow.Shadow
import spock.lang.Specification

import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Id

class Case886ProblemReadingShadowsWithEmbeddedId extends Specification {

    @TypeName("Agreement")
    class Agreement {

        @Id
        private UUID agreementId

        private UUID locationId

        @ShallowReference
        private List<AgreementMember> agreementMembers
    }

    @Embeddable
    class AgreementMemberId implements Serializable {
        private UUID agreementId
        private UUID memberId
    }

    class AgreementMember {
        @EmbeddedId
        private AgreementMemberId agreementMemberId

        AgreementMemberId getId() {
            return agreementMemberId
        }
    }

    def "should create Shadow with ShallowReference with EmbeddedId replaced with null"() {
        given:
        def javers = ItauAuditableBuilder.javers().build()

        println javers.getTypeMapping(Agreement).prettyPrint()
        println javers.getTypeMapping(AgreementMember).prettyPrint()
        println javers.getTypeMapping(AgreementMemberId).prettyPrint()
        println javers.getTypeMapping(UUID).prettyPrint()

        when:
        UUID agreementId = UUID.randomUUID()

        AgreementMemberId agreementMemberId = new AgreementMemberId(
                agreementId: agreementId,
                memberId: UUID.randomUUID() )

        AgreementMember agreementMember = new AgreementMember(agreementMemberId:agreementMemberId)

        Agreement agreement = new Agreement(
                agreementId: agreementId,
                locationId: UUID.randomUUID(),
                agreementMembers: [agreementMember])

        javers.commit("Agreement", agreement)

        JqlQuery query = QueryBuilder.byInstanceId(agreementId, Agreement.class).build()
        List<Shadow<Agreement>> shadows = javers.findShadows(query)

        then:
        Agreement a = shadows[0].get()
        a.agreementMembers == []
    }
}
