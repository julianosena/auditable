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

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.javers

/**
 * @author bartosz walacik
 */
class ItauAuditableBuilderTest extends Specification {

    def "should scan Entities with @TypeName when packegaToScan is given"() {
        when:
        def javers = ItauAuditableTestBuilder.javersTestAssembly("org.zonk, br.com.zup.itau.auditable.core.examples.typeNames")
        def typeMapper = javers.typeMapper

        then:
        typeMapper.getItauAuditableManagedType("myValueObject").baseJavaClass == NewValueObjectWithTypeAlias
    }

    def "should scan given Entity when requested explicitly"() {
        when:
        def javers = ItauAuditableTestBuilder.javersTestAssembly(NewEntityWithTypeAlias)
        def typeMapper = javers.typeMapper

        then:
        typeMapper.getItauAuditableManagedType("myName").baseJavaClass == NewEntityWithTypeAlias
    }

    def "should manage Entity"() {
        when:
        def javers = javers().registerEntity(DummyEntity).build()

        then:
        javers.getTypeMapping(DummyEntity) instanceof EntityType
    }

    def "should manage ValueObject"() {
        when:
        def javers = javers().registerValueObject(DummyNetworkAddress).build()

        then:
        javers.getTypeMapping(DummyNetworkAddress) instanceof ValueObjectType
    }

    def "should create ItauAuditable"() {
        when:
        def javers = javers().build()

        then:
        javers != null
    }

    def "should create multiple ItauAuditable instances"() {
        when:
        def javers1 = javers().build()
        def javers2 = javers().build()

        then:
        javers1 != javers2
    }

    def "should contain ObjectAccessHook when given"() {
        given:
        def graphFactoryHook = Stub(ObjectAccessHook)
        ItauAuditableBuilder javersBuilder = javers().withObjectAccessHook(graphFactoryHook)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(ObjectAccessHook) == graphFactoryHook
    }

    def "should not contain FieldBasedPropertyScanner when Bean style"() {
        given:
        ItauAuditableBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(FieldBasedPropertyScanner) == null
    }


    def "should not contain BeanBasedPropertyScanner when Field style"() {
        given:
        ItauAuditableBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(BeanBasedPropertyScanner) == null
    }


    def "should create multiple javers containers"() {
        given:
        ItauAuditableBuilder builder1 = ItauAuditableBuilder.javers()
        ItauAuditableBuilder builder2 = ItauAuditableBuilder.javers()

        when:
        builder1.build()
        builder2.build()

        then:
        builder1.getContainerComponent(ItauAuditable) != builder2.getContainerComponent(ItauAuditable)
    }

    @Unroll
    def "should contain #clazz.getSimpleName() bean"() {
        given:
        ItauAuditableBuilder builder = javers()

        when:
        builder.build()

        then:
        assertThat(builder.getContainerComponent(clazz)) isInstanceOf(clazz)

        where:
        clazz << [ItauAuditable, TypeMapper, DiffFactory]
    }

    def "should contain singletons"() {
        given:
        ItauAuditableBuilder builder = javers()

        when:
        builder.build()

        then:
        builder.getContainerComponent(ItauAuditable) == builder.getContainerComponent(ItauAuditable)
    }

    def "should use LevenshteinListChangeAppender when selected"() {
        given:
        def builder = javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)

        when:
        builder.build()

        then:
        builder.getContainerComponent(LevenshteinListChangeAppender)
    }

    def "should use SimpleListChangeAppender by default"() {
        given:
        def builder = javers()

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
