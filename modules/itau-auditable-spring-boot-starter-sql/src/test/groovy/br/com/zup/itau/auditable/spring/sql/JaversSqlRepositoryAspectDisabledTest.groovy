package br.com.zup.itau.auditable.spring.sql

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.spring.boot.DummyEntity
import br.com.zup.itau.auditable.spring.boot.TestApplication
import br.com.zup.itau.auditable.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @author mwesolowski
 */
@SpringBootTest(classes = [TestApplication], properties = ["javers.springDataAuditableRepositoryAspectEnabled=false"])
class ItauAuditableSqlRepositoryAspectDisabledTest extends Specification{

    @Autowired
    ItauAuditable javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build javers instance without auto-audit aspect"() {
        when:
        def entity = DummyEntity.random()
        dummyEntityRepository.save(entity)

        then:
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(entity.id, DummyEntity).build())
        assert snapshots.size() == 0
    }
}