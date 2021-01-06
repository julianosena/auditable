package br.com.zup.itau.auditable.core.graph

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.metamodel.object.GlobalIdFactory
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import br.com.zup.itau.auditable.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.model.DummyUser.dummyUser

abstract class ObjectNodeTest extends Specification {

    protected def createEntity

    @Shared
    GlobalIdFactory globalIdFactory = ItauAuditableTestBuilder.itauAuditableTestAssembly().globalIdFactory

    private ObjectNode objectNode(Object cdo, EntityType entity) {
        new LiveNode(new LiveCdoWrapper(cdo, entity.createIdFromInstance(cdo), entity))
    }

    def "should hold Entity reference"() {
        given:
        def cdo = dummyUser()
        def entity = createEntity(DummyUser)

        when:
        def wrapper = objectNode(cdo, entity)

        then:
        wrapper.managedType == entity
    }


    def "should hold GlobalId"() {
        given:
        def cdo = dummyUser("Mad Kaz")
        def entity = createEntity(DummyUser)

        when:
        ObjectNode wrapper = objectNode(cdo, entity)

        then:
        wrapper.globalId == entity.createIdFromInstance(cdo)
    }

    def "should hold Cdo reference"() {
        given:
        def cdo = dummyUser()
        def entity = createEntity(DummyUser)

        when:
        def wrapper = objectNode(cdo, entity)

        then:
        wrapper.wrappedCdo().get() == cdo
    }


    def "should throw exception when Entity without id"() {
        given:
        def cdo = new DummyUser()
        def entity = createEntity(DummyUser)

        when:
        objectNode(cdo, entity)

        then:
        ItauAuditableException exception = thrown(ItauAuditableException)
        exception.code == ItauAuditableExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID
    }

    def "should have reflexive equals method"() {
        when:
        def objectNode = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

        then:
        objectNode == objectNode
    }

    def "should return false when equals method has null arg"() {
        when:
        ObjectNode first = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

        then:
        first != null
    }
}
