package br.com.zup.itau.auditable.core.graph

import br.com.zup.itau.auditable.core.ItauAuditableTestBuilder
import br.com.zup.itau.auditable.core.graph.ObjectHasher
import br.com.zup.itau.auditable.core.model.DummyAddress
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class ObjectHasherTest extends Specification {
    @Shared ObjectHasher objectHasher
    @Shared ItauAuditableTestBuilder javers

    def setupSpec(){
        javers = ItauAuditableTestBuilder.javersTestAssembly()
        objectHasher = new ObjectHasher(javers.snapshotFactory, javers.jsonConverter)
    }

    def "should calculate hash of ValueObject "(){
        given:
        def address = new DummyAddress('Warsaw', 'Mokotowska')

        when:
        def node = javers.createLiveNode(address)
        def hash = objectHasher.hash([node.cdo])

        then:
        hash == 'ba4a8532bc3fa2c16990e2a21e06cd1f'
        hash == javers.hash(address)
    }
}
