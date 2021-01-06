package br.com.zup.itau.auditable.spring.sql

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.spring.boot.DummyEntity
import br.com.zup.itau.auditable.spring.boot.TestApplication
import br.com.zup.itau.auditable.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.transaction.Transactional

import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
@Transactional
class ItauAuditableSqlStarterIntegrationTest extends Specification {

    @Autowired
    ItauAuditable itauAuditable

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build default itauAuditable instance with auto-audit aspect"() {
        when:
        def entity = dummyEntityRepository.save(DummyEntity.random())
        assert dummyEntityRepository.getOne(entity.id)

        def snapshots = itauAuditable.findSnapshots(byInstanceId(entity.id, DummyEntity).build())

        then:
        snapshots.size() == 1
        snapshots[0].commitMetadata.properties.size() == 1

        and: "should support deprecated CommitPropertiesProvider.provide() "
        snapshots[0].commitMetadata.properties["deprecated commitPropertiesProvider.provide()"] == "still works"
        snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should call auto-audit aspect when saving iterable "(){
      given:
      List entities = (1..5).collect{ DummyEntity.random()}

      when:
      List persisted = dummyEntityRepository.saveAll(entities)

      then:
      persisted.collect {p -> itauAuditable.getLatestSnapshot(p.id, DummyEntity)}
               .each {s -> assert s.isPresent() }
    }
}