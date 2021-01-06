package br.com.zup.itau.auditable.core.cases

import com.google.gson.reflect.TypeToken
import br.com.zup.itau.auditable.common.reflection.ConcreteWithActualType
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/77
 *
 * To resolve this issue, we added {@link br.com.zup.itau.auditable.common.reflection.ItauAuditableMember},
 * which cures:
 * ItauAuditableException: CLASS_EXTRACTION_ERROR JaVers bootstrap error - Don't know how to extract Class from type 'T'
 *
 * @author bartosz walacik
 */
class AdvancedTypeResolvingForGenericsTest extends Specification{

    def "should resolve actual types of Generic fields when inherited from Generic superclass"() {
        given:
        def javers = ItauAuditableBuilder.javers().build();

        when:
        def jType = javers.getTypeMapping(ConcreteWithActualType)

        then:
        jType.getProperty("id").genericType == String
        jType.getProperty("value").genericType == new TypeToken<List<String>>(){}.type
    }
}
