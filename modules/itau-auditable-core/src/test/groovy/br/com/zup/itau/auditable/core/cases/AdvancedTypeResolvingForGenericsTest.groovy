package br.com.zup.itau.auditable.core.cases

import com.google.gson.reflect.TypeToken
import br.com.zup.itau.auditable.common.reflection.ConcreteWithActualType
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import spock.lang.Specification

/**
 * https://github.com/itauAuditable/itauAuditable/issues/77
 *
 * To resolve this issue, we added {@link br.com.zup.itau.auditable.common.reflection.ItauAuditableMember},
 * which cures:
 * ItauAuditableException: CLASS_EXTRACTION_ERROR Itaú Auditable bootstrap error - Don't know how to extract Class from type 'T'
 *
 * @author bartosz walacik
 */
class AdvancedTypeResolvingForGenericsTest extends Specification{

    def "should resolve actual types of Generic fields when inherited from Generic superclass"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build();

        when:
        def jType = itauAuditable.getTypeMapping(ConcreteWithActualType)

        then:
        jType.getProperty("id").genericType == String
        jType.getProperty("value").genericType == new TypeToken<List<String>>(){}.type
    }
}
