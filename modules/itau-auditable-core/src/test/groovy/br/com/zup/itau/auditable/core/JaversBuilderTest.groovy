package br.com.zup.itau.auditable.core

import br.com.zup.itau.auditable.core.diff.DiffFactory
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm
import br.com.zup.itau.auditable.core.diff.appenders.SimpleListChangeAppender
import br.com.zup.itau.auditable.core.diff.appenders.levenshtein.LevenshteinListChangeAppender
import br.com.zup.itau.auditable.core.examples.typeNames.NewEntityWithTypeAlias
import br.com.zup.itau.auditable.core.examples.typeNames.NewValueObjectWithTypeAlias
import br.com.zup.itau.auditable.core.graph.ObjectAccessHook
import br.com.zup.itau.auditable.core.metamodel.scanner.BeanBasedPropertyScanner
import br.com.zup.itau.auditable.core.metamodel.scanner.FieldBasedPropertyScanner
import br.com.zup.itau.auditable.core.metamodel.type.EntityType
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper
import br.com.zup.itau.auditable.core.metamodel.type.ValueObjectType
import br.com.zup.itau.auditable.core.model.DummyNetworkAddress
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.Id

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.itauAuditable

/**
 * @author bartosz walacik
 */
class ItauAuditableBuilderTest extends Specification {

    def "should scan Entities with @TypeName when packegaToScan is given"() {
        when:
        def itauAuditable = ItauAuditableTestBuilder.itauAuditableTestAssembly("org.zonk, br.com.zup.itau.auditable.core.examples.typeNames")
        def typeMapper = itauAuditable.typeMapper

        then:
        typeMapper.getItauAuditableManagedType("myValueObject").baseJavaClass == NewValueObjectWithTypeAlias
    }

    def "should scan given Entity when requested explicitly"() {
        when:
        def itauAuditable = ItauAuditableTestBuilder.itauAuditableTestAssembly(NewEntityWithTypeAlias)
        def typeMapper = itauAuditable.typeMapper

        then:
        typeMapper.getItauAuditableManagedType("myName").baseJavaClass == NewEntityWithTypeAlias
    }

    def "should manage Entity"() {
        when:
        def itauAuditable = itauAuditable().registerEntity(DummyEntity).build()

        then:
        itauAuditable.getTypeMapping(DummyEntity) instanceof EntityType
    }

    def "should manage ValueObject"() {
        when:
        def itauAuditable = itauAuditable().registerValueObject(DummyNetworkAddress).build()

        then:
        itauAuditable.getTypeMapping(DummyNetworkAddress) instanceof ValueObjectType
    }

    def "should create ItauAuditable"() {
        when:
        def itauAuditable = itauAuditable().build()

        then:
        itauAuditable != null
    }

    def "should create multiple ItauAuditable instances"() {
        when:
        def itauAuditable1 = itauAuditable().build()
        def itauAuditable2 = itauAuditable().build()

        then:
        itauAuditable1 != itauAuditable2
    }

    def "should contain ObjectAccessHook when given"() {
        given:
        def graphFactoryHook = Stub(ObjectAccessHook)
        ItauAuditableBuilder itauAuditableBuilder = itauAuditable().withObjectAccessHook(graphFactoryHook)

        when:
        itauAuditableBuilder.build()

        then:
        itauAuditableBuilder.getContainerComponent(ObjectAccessHook) == graphFactoryHook
    }

    def "should not contain FieldBasedPropertyScanner when Bean style"() {
        given:
        ItauAuditableBuilder itauAuditableBuilder = itauAuditable().withMappingStyle(MappingStyle.BEAN)

        when:
        itauAuditableBuilder.build()

        then:
        itauAuditableBuilder.getContainerComponent(FieldBasedPropertyScanner) == null
    }


    def "should not contain BeanBasedPropertyScanner when Field style"() {
        given:
        ItauAuditableBuilder itauAuditableBuilder = itauAuditable().withMappingStyle(MappingStyle.FIELD)

        when:
        itauAuditableBuilder.build()

        then:
        itauAuditableBuilder.getContainerComponent(BeanBasedPropertyScanner) == null
    }


    def "should create multiple itauAuditable containers"() {
        given:
        ItauAuditableBuilder builder1 = ItauAuditableBuilder.itauAuditable()
        ItauAuditableBuilder builder2 = ItauAuditableBuilder.itauAuditable()

        when:
        builder1.build()
        builder2.build()

        then:
        builder1.getContainerComponent(ItauAuditable) != builder2.getContainerComponent(ItauAuditable)
    }

    @Unroll
    def "should contain #clazz.getSimpleName() bean"() {
        given:
        ItauAuditableBuilder builder = itauAuditable()

        when:
        builder.build()

        then:
        assertThat(builder.getContainerComponent(clazz)) isInstanceOf(clazz)

        where:
        clazz << [ItauAuditable, TypeMapper, DiffFactory]
    }

    def "should contain singletons"() {
        given:
        ItauAuditableBuilder builder = itauAuditable()

        when:
        builder.build()

        then:
        builder.getContainerComponent(ItauAuditable) == builder.getContainerComponent(ItauAuditable)
    }

    def "should use LevenshteinListChangeAppender when selected"() {
        given:
        def builder = itauAuditable().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)

        when:
        builder.build()

        then:
        builder.getContainerComponent(LevenshteinListChangeAppender)
    }

    def "should use SimpleListChangeAppender by default"() {
        given:
        def builder = itauAuditable()

        when:
        builder.build()

        then:
        builder.getContainerComponent(SimpleListChangeAppender)
    }

    class DummyEntity {
        @Id
        int id
        DummyEntity parent
    }
}
