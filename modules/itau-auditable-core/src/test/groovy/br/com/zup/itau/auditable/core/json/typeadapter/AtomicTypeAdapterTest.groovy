package br.com.zup.itau.auditable.core.json.typeadapter

import groovy.json.JsonSlurper
import br.com.zup.itau.auditable.core.diff.changetype.Atomic
import java.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.javersTestAssembly
import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.javersTestAssemblyTypeSafe

/**
 * @author bartosz walacik
 */
class AtomicTypeAdapterTest extends Specification {
    def "should serialize Atomic type-safely when switched on"() {
        given:
        def jsonConverter = javersTestAssemblyTypeSafe().jsonConverter

        when:
        def jsonText = jsonConverter.toJson(new Atomic(new LocalDate(2000, 1, 1)))

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.typeAlias == "LocalDate"
        json.value == "2000-01-01"
    }

    def "should serialize Atomic without type-safety by default"() {
        given:
        def jsonConverter = javersTestAssembly().jsonConverter

        when:
        def jsonText = jsonConverter.toJson(new Atomic(new LocalDate(2000, 1, 1)))

        then:
        jsonText == '"2000-01-01"'
    }

    @Unroll
    def "should not wrap primitive #primitive.class.simpleName when typeSafe is switched on"() {
        given:
        def jsonConverter = javersTestAssemblyTypeSafe().jsonConverter

        when:
        def jsonText = jsonConverter.toJson(new AtomicHolder(primitive))
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.atomic == primitive

        where:
        primitive << ["String", true, 1, 1.1.doubleValue()]

    }

    class AtomicHolder{
        Atomic atomic
        AtomicHolder(Object value) {
            this.atomic = new Atomic(value)
        }
    }


}
