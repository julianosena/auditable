package br.com.zup.itau.auditable.core.diff

import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.graph.LiveGraph
import br.com.zup.itau.auditable.core.graph.ObjectNode
import br.com.zup.itau.auditable.core.metamodel.property.Property
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType
import spock.lang.Shared
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffTest extends Specification {
    @Shared ItauAuditableTestBuilder javers = javersTestAssembly()

    ObjectNode buildGraph(def any) {
        javers.createLiveGraph(any).root()
    }

    LiveGraph buildLiveGraph(def any) {
        javers.createLiveGraph(any)
    }

    EntityType getEntity(Class forClass) {
        (EntityType)javers.typeMapper.getItauAuditableType(forClass)
    }

    Property getManagedProperty(Class forClass, String propertyName) {
        javers.typeMapper.getItauAuditableType(forClass).getProperty(propertyName)
    }

    Property getProperty(Class forClass, String propName) {
        getEntity(forClass).getProperty(propName)
    }

    RealNodePair realNodePair(def leftCdo, def rightCdo){
        new RealNodePair(buildGraph(leftCdo), buildGraph(rightCdo))
    }

    ItauAuditableType getItauAuditableType(def javaType){
        javers.typeMapper.getItauAuditableType(javaType)
    }
}
