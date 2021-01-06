package br.com.zup.itau.auditable.core.metamodel.type

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.MappingStyle
import br.com.zup.itau.auditable.core.examples.typeNames.*
import br.com.zup.itau.auditable.core.metamodel.annotation.DiffIgnore
import br.com.zup.itau.auditable.core.metamodel.annotation.DiffInclude
import br.com.zup.itau.auditable.core.metamodel.clazz.EntityDefinition
import br.com.zup.itau.auditable.core.metamodel.clazz.ItauAuditableEntity
import br.com.zup.itau.auditable.core.metamodel.clazz.ItauAuditableValue
import br.com.zup.itau.auditable.core.metamodel.clazz.ValueObjectDefinition
import br.com.zup.itau.auditable.core.metamodel.scanner.ClassScanner
import br.com.zup.itau.auditable.core.model.DummyAddress
import br.com.zup.itau.auditable.core.model.DummyIgnoredType
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.core.model.ShallowPhone
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.Id
import javax.persistence.Transient

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly
import static br.com.zup.itau.auditable.core.MappingStyle.BEAN
import static br.com.zup.itau.auditable.core.MappingStyle.FIELD
import static br.com.zup.itau.auditable.core.metamodel.clazz.EntityDefinitionBuilder.entityDefinition
import static br.com.zup.itau.auditable.core.metamodel.clazz.ValueObjectDefinitionBuilder.valueObjectDefinition

/**
 * @author Pawel Cierpiatka
 */
class TypeFactoryTest extends Specification {

    static def entityCreator(MappingStyle mappingStyle){
        def typeFactory = create(mappingStyle)
        return { clazz -> typeFactory.create(new EntityDefinition(clazz))}
    }

    static TypeFactory create(MappingStyle mappingStyle){
        def itauAuditableTestAssembly = itauAuditableTestAssembly(mappingStyle)
        def classScanner = itauAuditableTestAssembly.getContainerComponent(ClassScanner)
        new TypeFactory(classScanner, itauAuditableTestAssembly.typeMapper, new DynamicMappingStrategy())
    }

    def setupSpec() {
        typeFactory = create(MappingStyle.FIELD)
    }

    @Shared
    TypeFactory typeFactory

    def "should use name from @TypeName when inferring from prototype"(){
        given:
        def prototype = typeFactory.infer(AbstractValueObject)

        when:
        def jType = typeFactory.infer(NewNamedValueObject, Optional.of(prototype))

        then:
        jType.name == OldValueObject.name
    }

    @Unroll
    def "should use name from @TypeName for inferred #expectedType.simpleName"(){
        when:
        def type = typeFactory.infer(clazz)

        then:
        type.name == "myName"
        type.class == expectedType

        where:
        expectedType  << [ValueObjectType, EntityType]
        clazz << [ItauAuditableValueObjectWithTypeAlias, NewEntityWithTypeAlias]
    }

    @Unroll
    def "should use typeName from ClientClassDefinition for #expectedType.simpleName"(){
        when:
        def type = typeFactory.create(definition)

        then:
        type.name == "myName"
        type.class == expectedType

        where:
        expectedType  << [EntityType,ValueObjectType]
        definition << [entityDefinition(DummyUser).withTypeName("myName").build(),
                       valueObjectDefinition(DummyUser).withTypeName("myName").build()
        ]
    }

    def "should create EntityType with properties, ID property and reference to client's class"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyUser))

        then:
        entity instanceof EntityType
        entity.baseJavaClass == DummyUser
        entity.properties.size() > 2
        entity.idProperty.name == "name"
    }

    def "should create ValueObjectType with properties and reference to client's class"() {
        when:
        def vo = typeFactory.create(new ValueObjectDefinition(DummyAddress))


        then:
        vo instanceof ValueObjectType
        vo.baseJavaClass == DummyAddress
        vo.properties.size() > 2
    }

    def "should ignore properties with @DiffIgnored type"(){
        when:
        EntityType entity = typeFactory.infer(DummyUser)

        then:
        !entity.propertyNames.contains("propertyWithDiffIgnoredType")
        !entity.propertyNames.contains("propertyWithDiffIgnoredSubtype")
    }

    def "should map @DiffIgnored type as IgnoredType"(){
        expect:
        typeFactory.infer(DummyIgnoredType) instanceof IgnoredType
    }

    @Unroll
    def "should map @ShallowReference type as ShallowReference when using #style style"(){
        expect:
        create(style).infer(ShallowPhone) instanceof ShallowReferenceType

        where:
        style << [BEAN, FIELD]
    }

    def "should map as ValueObjectType by default"(){
        expect:
        typeFactory.infer(DummyAddress) instanceof ValueObjectType
    }

    def "should map as ValueType when @Value annotation is present "(){
        expect:
        typeFactory.infer(ItauAuditableValue) instanceof ValueType
    }

    def "should map as EntityType if property level @Id annotation is present"() {
        expect:
        typeFactory.infer(DummyUser) instanceof EntityType
    }

    def "should map as EntityType when @Entity annotation is present"() {
        expect:
        typeFactory.infer(ItauAuditableEntity) instanceof EntityType
    }

    @Unroll
    def "should ignore given #managedType properties"() {
        when:
        def jType = typeFactory.create(managedClassRecipe)

        then:
        !jType.hasProperty("city")
        !jType.hasProperty("kind")

        where:
        managedType << ["EntityType", "ValueObjectType"]
        managedClassRecipe << [new EntityDefinition(DummyAddress, "street", ["city", "kind"]),
                               new ValueObjectDefinition(DummyAddress, ["city", "kind"])]
    }

    def "should fail if given ignored EntityType property not exists"() {
        when:
        typeFactory.create(new EntityDefinition(DummyAddress, "street",["city__"]))

        then:
        ItauAuditableException e = thrown()
        e.code == ItauAuditableExceptionCode.PROPERTY_NOT_FOUND
    }

    @javax.persistence.Entity
    @br.com.zup.itau.auditable.core.metamodel.annotation.ValueObject
    class AmbiguousValueObjectType {
        @Id int id
    }

    @javax.persistence.Embeddable
    @br.com.zup.itau.auditable.core.metamodel.annotation.Entity
    class AmbiguousEntityType {
        @Id int id
    }

    def "should use itauAuditable type annotations first, when ambiguous type mapping"(){
        expect:
        typeFactory.infer(AmbiguousEntityType) instanceof EntityType
        typeFactory.infer(AmbiguousValueObjectType) instanceof ValueObjectType
    }

    class EntityWithMixedAnnotations extends ItauAuditableEntity {
        @DiffInclude
        String includedField
        @DiffIgnore
        String ignoredField
        @Transient
        String transientField;

        @DiffInclude
        String getIncludedField() {
            return includedField
        }

        @DiffIgnore
        String getIgnoredField() {
            return ignoredField
        }

        @Transient
        String getTransientField() {
            return transientField;
        }
    }

    def "should ignore @Transient and @DiffIgnore when @DiffInclude is present and only use included properties"() {
        when:
        def itauAuditableType = typeFactory.infer(EntityWithMixedAnnotations)

        then:
        !((ManagedType)itauAuditableType).getManagedClass().getManagedPropertiesFilter().hasIgnoredProperties()
        ((ManagedType)itauAuditableType).getPropertyNames().toArray() == ["includedField"]
    }

    class PropsClass {
        int id
        int a
        int b
    }

    @Unroll
    def "should ignore all props of defined #classType which are not in the 'included' list of properties"() {
        when:
        def type = typeFactory.create(definition)

        then:
        type.properties.size() == 1
        type.properties[0].name == "id"
        if (type instanceof EntityType) {
            assert type.idProperty.name == "id"
        }
        println type.prettyPrint()

        where:
        definition << [entityDefinition(PropsClass)
                               .withIdPropertyName("id")
                               .withIncludedProperties(["id"]).build(),
                       valueObjectDefinition(PropsClass)
                               .withIncludedProperties(["id"]).build()
        ]
        classType << ["EntityType", "ValueObjectType"]
    }

    class EntityWithIncluded {
        @DiffInclude @Id int id
        int a
    }

    class ValueObjectWithIncluded {
        @DiffInclude int id
        int a
    }

    @Unroll
    def "should ignore all props of inferred #clazz.simpleName without @DiffInclude annotation"() {
        when:
        def type = typeFactory.infer(clazz)

        then:
        type.properties.size() == 1
        type.properties[0].name == "id"
        if (type instanceof EntityType) {
            assert type.idProperty.name == "id"
        }
        println type.prettyPrint()

        where:
        clazz << [EntityWithIncluded, ValueObjectWithIncluded]
    }
}