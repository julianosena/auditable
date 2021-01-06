package br.com.zup.itau.auditable.repository.sql

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.model.SnapshotEntity

import java.sql.Connection
import java.sql.DriverManager

import static br.com.zup.itau.auditable.repository.sql.SqlRepositoryBuilder.sqlRepository

class H2SqlRepositoryE2ETest extends ItauAuditableSqlRepositoryE2ETest {

    @Override
    Connection createConnection() {
        DriverManager.getConnection("jdbc:h2:mem:test")
    }

    @Override
    DialectName getDialect() {
        DialectName.H2
    }

    @Override
    String getSchema() {
        return null
    }

    @Override
    boolean useRandomCommitIdGenerator() {
        false
    }

    def "should fail when schema is not created"(){
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable()
                .registerItauAuditableRepository(sqlRepository()
                .withConnectionProvider({ DriverManager.getConnection("jdbc:h2:mem:empty-test") } as ConnectionProvider)
                .withSchemaManagementEnabled(false)
                .withDialect(getDialect())
                .withGlobalIdTableName(globalIdTableName)
                .withCommitTableName(commitTableName)
                .withSnapshotTableName(snapshotTableName)
                .withCommitPropertyTableName(commitPropertyTableName)
                .build()).build()

        when:
        itauAuditable.commit("author", new SnapshotEntity(id: 1))

        then:
        ItauAuditableException e = thrown()
        e.code == ItauAuditableExceptionCode.SQL_EXCEPTION
    }

    /**
     * see https://github.com/itauAuditable/itauAuditable/issues/532
     */
    def "should evict sequence allocation cache"() {
        given:
        (1..50).each {
            itauAuditable.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }

        when:
        clearTables()
        execute("alter sequence "+ schemaManager.commitPkSeqName +" restart with 1")
        execute("alter sequence "+ schemaManager.globalIdPkSeqName +" restart with 1")
        execute("alter sequence "+ schemaManager.snapshotTablePkSeqName +" restart with 1")
        def sqlRepository = (ItauAuditableSqlRepository) repository
        sqlRepository.evictSequenceAllocationCache()
        sqlRepository.evictCache()

        then:
        (1..150).each {
            itauAuditable.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }
    }
}
