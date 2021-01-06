package br.com.zup.itau.auditable.core.json.typeadapter

import br.com.zup.itau.auditable.core.commit.CommitId
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly

/**
* @author pawel szymczyk
*/
class CommitIdTypeAdapterTest extends Specification{

    def "should serialize CommitId to Json"() {
        given:
        def itauAuditable = itauAuditableTestAssembly()
        def commitId = new CommitId(13, 7)

        when:
        def jsonText = itauAuditable.jsonConverter.toJson(commitId)

        then:
        jsonText == "13.07"
    }

    def "should deserialize CommitId"() {

        given:
        def json = "12.9"

        when:
        def commitId = itauAuditableTestAssembly().jsonConverter.fromJson(json, CommitId)

        then:
        commitId.getMajorId() == 12
        commitId.getMinorId() == 90
    }
}
