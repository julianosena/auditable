package br.com.zup.itau.auditable.core.diff

import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.graph.LiveGraph
import br.com.zup.itau.auditable.core.graph.ObjectNode
import br.com.zup.itau.auditable.core.metamodel.property.Property
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType
import spock.lang.Shared
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffTest extends Specification {
    @Shared ItauAuditableTestBuilder itauAuditable = itauAuditableTestAssembly()

    ObjectNode buildGraph(def any) {
        itauAuditable.createLiveGraph(any).root()
    }

    LiveGraph buildLiveGraph(def any) {
        itauAuditable.createLiveGraph(any)
    }

    EntityType getEntity(Class forClass) {
        (EntityType)itauAuditable.typeMapper.getItauAuditableType(forClass)
    }

    Property getManagedProperty(Class forClass, String propertyName) {
        itauAuditable.typeMapper.getItauAuditableType(forClass).getProperty(propertyName)
    }

    Property getProperty(Class forClass, String propName) {
        getEntity(forClass).getProperty(propName)
    }

    RealNodePair realNodePair(def leftCdo, def rightCdo){
        new RealNodePair(buildGraph(leftCdo), buildGraph(rightCdo))
    }

    ItauAuditableType getItauAuditableType(def javaType){
        itauAuditable.typeMapper.getItauAuditableType(javaType)
    }
}
