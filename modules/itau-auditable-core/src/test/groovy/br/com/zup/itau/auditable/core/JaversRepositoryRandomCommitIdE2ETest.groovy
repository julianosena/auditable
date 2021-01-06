package br.com.zup.itau.auditable.core

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.commit.CommitId
import br.com.zup.itau.auditable.repository.jql.QueryBuilder

class ItauAuditableRepositoryRandomCommitIdE2ETest extends ItauAuditableRepositoryShadowE2ETest {

    def "should prevent from using toCommitId() filter with RANDOM CommitIdGenerator" () {
        when:
        def query = QueryBuilder
                .anyDomainObject()
                .toCommitId(CommitId.valueOf("4900110407498891977.00"))
                .build()
        javers.findSnapshots(query)

        then:
        def e = thrown(ItauAuditableException)
        e.code == ItauAuditableExceptionCode.MALFORMED_JQL
        println e
    }

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
