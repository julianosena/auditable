package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.core.metamodel.clazz.EntityDefinition
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class Case640InterfaceGettersInheritance extends Specification {

    interface WithId {
        String getId()
        void setId(String id)
    }

    interface MyObject extends WithId, Serializable {
        //getId() should be inherited
    }

    def "should ... "(){
      given:
      ItauAuditable javers = ItauAuditableBuilder.javers()
              .withMappingStyle(MappingStyle.BEAN)
              .registerEntity(new EntityDefinition(MyObject.class, "id"))
              .build()

      expect:
      javers.getTypeMapping(MyObject).idProperty.name == "id"
    }
}
