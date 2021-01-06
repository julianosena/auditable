package br.com.zup.itau.auditable.core.cases

import org.bson.types.ObjectId
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

class Case560ShadowScopeNpe extends Specification {

    def "should not throw NPE while getting Shadows "(){
        given:
        def javers = ItauAuditableBuilder.javers().build()
        def id = ObjectId.get()
        def entity = new MongoStoredEntity(id, "alg1", "1.0", "name")
        javers.commit(id.toString(),entity)

        when:
        def query = QueryBuilder.byInstanceId(id, MongoStoredEntity.class).withScopeCommitDeep().build();
        def shadows = javers.findShadows(query)

        then:
        shadows
    }
}
