package br.com.zup.itau.auditable.core.json.typeadapter

import br.com.zup.itau.auditable.core.json.DummyPointJsonTypeAdapter
import br.com.zup.itau.auditable.core.json.DummyPointNativeTypeAdapter
import br.com.zup.itau.auditable.core.model.DummyPoint
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class JsonConverterCustomTypeAdapterTest extends Specification {

    def "should use custom typeAdapter when converting to json"() {
        given:
        def jsonConverter= javersTestAssembly().jsonConverterBuilder
                          .registerJsonTypeAdapter(new DummyPointJsonTypeAdapter()).build()

        when:
        def json = jsonConverter.toJson( new DummyPoint(1,2))

        then:
        json == '"1,2"'
    }

    def  "should use custom typeAdapter when converting from json"() {
        given:
        def jsonConverter= javersTestAssembly().jsonConverterBuilder
                          .registerJsonTypeAdapter(new DummyPointJsonTypeAdapter()).build()
        when:
        def person = jsonConverter.fromJson('"1,2"',DummyPoint.class)

        then:
        person == new DummyPoint(1,2)
    }

    def "should use custom native Gson typeAdapter when converting to json"() {
        given:
        def jsonConverter= javersTestAssembly().jsonConverterBuilder
                          .registerNativeTypeAdapter(DummyPoint, new DummyPointNativeTypeAdapter()).build()

        when:
        def json = jsonConverter.toJson( new DummyPoint(1,2))

        then:
        json == '"1,2"'
    }

    def  "should use custom native Gson typeAdapter when converting from json"() {
        given:
        def jsonConverter= javersTestAssembly().jsonConverterBuilder
                          .registerNativeTypeAdapter(DummyPoint, new DummyPointNativeTypeAdapter()).build()
        when:
        def person = jsonConverter.fromJson('"1,2"',DummyPoint.class)

        then:
        person == new DummyPoint(1,2)
    }
}
