package br.com.zup.itau.auditable.core.metamodel.type

import br.com.zup.itau.auditable.core.model.DummyAddress
import spock.lang.Specification

import static groovyx.gpars.GParsPool.withPool

/**
 * @author bartosz.walacik
 */
class TypeMapperConcurrentTest extends Specification {

    def "should not create more than one ItauAuditableType for given Java type, even if called from many threads "(){
      given:
      TypeFactory typeFactory = Mock()
      def typeMapper = new TypeMapper(typeFactory)

      when:
      withPool 100, {
          (1..100).collectParallel {
                println "calling getItauAuditableManagedType() in thread no. "+it
                typeMapper.getItauAuditableType(DummyAddress)
          }
      }

      then:
      //no better idea how to test thread-safety without Mocks
      1 * typeFactory.infer(DummyAddress, _) >> Stub(ValueObjectType)
    }
}