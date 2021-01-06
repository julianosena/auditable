package br.com.zup.itau.auditable.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.bson.types.ObjectId
import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode
import br.com.zup.itau.auditable.core.cases.MongoStoredEntity
import br.com.zup.itau.auditable.core.examples.typeNames.*
import br.com.zup.itau.auditable.core.metamodel.clazz.*
import br.com.zup.itau.auditable.core.model.*
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.EmbeddedId
import javax.persistence.Id

import static br.com.zup.itau.auditable.core.ItauAuditableTestBuilder.itauAuditableTestAssembly

class TypeMapperIntegrationTest extends Specification {

    def "should map groovy.lang.MetaClass as IgnoredType"(){
        when:
        def mapper = itauAuditableTestAssembly().typeMapper

        then:
        mapper.getItauAuditableType(MetaClass) instanceof IgnoredType
    }

    def "should find ValueObject by DuckType when properties match"(){
      when:
      def mapper = itauAuditableTestAssembly().typeMapper
      mapper.getItauAuditableType(NamedValueObjectOne) //touch
      mapper.getItauAuditableType(NamedValueObjectTwo) //touch

      then:
      mapper.getItauAuditableManagedType(new DuckType("namedValueObject", ["name", "one"] as Set), ManagedType).baseJavaClass == NamedValueObjectOne
      mapper.getItauAuditableManagedType(new DuckType("namedValueObject", ["name", "two"] as Set), ManagedType).baseJavaClass == NamedValueObjectTwo
    }

    def "should fallback to last mapped bare typeName when properties does not match"(){
        when:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.getItauAuditableType(NamedValueObjectOne) //touch
        mapper.getItauAuditableType(NamedValueObjectTwo) //touch

        then:
        mapper.getItauAuditableManagedType(new DuckType("namedValueObject", ["name", "another"] as Set), ManagedType).baseJavaClass == NamedValueObjectTwo
    }

    @Unroll
    def "should find #what type by typeName"(){
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.getItauAuditableType(clazz) //touch

        when:
        def managedType = mapper.getItauAuditableManagedType(typeName)

        then:
        managedType instanceof ManagedType
        managedType.baseJavaClass == clazz
        managedType.name == typeName

        where:
        what <<  ["Entity", "ValueObject", "retrofitted ValueObject"]
        clazz << [NewEntityWithTypeAlias, NewValueObjectWithTypeAlias, NewNamedValueObject]
        typeName << ["myName","myValueObject", "br.com.zup.itau.auditable.core.examples.typeNames.OldValueObject"]
    }

    def "should throw TYPE_NAME_NOT_FOUND Exception when TypeName is not found"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper

        when:
        mapper.getItauAuditableManagedType("not.registered")

        then:
        ItauAuditableException e = thrown()
        e.code == ItauAuditableExceptionCode.TYPE_NAME_NOT_FOUND
        println e
    }

    @Unroll
    def "should override Entity type inferred form annotations when ValueObject is explicitly registered for #queryClass.simpleName"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.registerClientsClass(new ValueObjectDefinition(queryClass))

        when:
        def jType = mapper.getItauAuditableType(queryClass)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == queryClass

        where:
        queryClass <<  [JpaEntity,
                        ClassWithEntityAnn,
                        ClassWithIdAnn]
    }

    def "should override ValueObject type inferred form annotations when Entity is explicitly registered"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.registerClientsClass(new EntityDefinition(JpaEmbeddable,"some"))

        when:
        def jType = mapper.getItauAuditableType(JpaEmbeddable)

        then:
        jType.class == EntityType
        jType.idProperty.name == "some"
    }

    def "should map as ValueObject by default"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper

        when:
        def jType = mapper.getItauAuditableType(DummyAddress)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == DummyAddress
    }

    class SomeEntity {
        SomeId id
    }

    class SomeId {
        int id
    }

    def "should infer as Value when a class is used as Id and there are no other clues "(){
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.registerClientsClass(EntityDefinitionBuilder.entityDefinition(SomeEntity).withIdPropertyName("id").build())

        when:
        def someEntityType = mapper.getItauAuditableType(SomeEntity)
        def someIdType = mapper.getItauAuditableType(SomeId)

        then:
        someEntityType.class == EntityType
        someIdType.class == ValueType
    }

    @Unroll
    def "should infer as Value when a class is used as @#usedAnn.simpleName and there are no other clues"(){
        given:
        def mapper = itauAuditableTestAssembly().typeMapper

        when:
        mapper.getItauAuditableType(entity)
        def jType = mapper.getItauAuditableType(idType)

        then:
        jType.class == ValueType
        jType.baseJavaClass == idType

        where:
        entity <<  [MongoStoredEntity, DummyEntityWithEmbeddedId]
        usedAnn << [Id, EmbeddedId]
        idType <<  [ObjectId, DummyPoint]
    }

    def "should map as Entity when a class has @Id property annotation"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper

        when:
        def jType = mapper.getItauAuditableType(DummyUser)

        then:
        jType.class == EntityType
        jType.baseJavaClass == DummyUser
        jType.idProperty.name == "name"
    }

    def "should map as Entity when a class has @EmbeddedId property annotation"(){
        given:
        def mapper = itauAuditableTestAssembly().typeMapper

        when:
        def jType = mapper.getItauAuditableType(DummyEntityWithEmbeddedId)

        then:
        jType.class == EntityType
        jType.baseJavaClass == DummyEntityWithEmbeddedId
        jType.idProperty.name == "point"
    }

    @Unroll
    def "should map as #expectedItauAuditableType.simpleName for annotated class #queryClass.simpleName"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper

        when:
        def jType = mapper.getItauAuditableType(queryClass)

        then:
        jType.class == expectedItauAuditableType
        jType.baseJavaClass == queryClass

        where:
        queryClass << [ItauAuditableEntity,
                       ItauAuditableValueObject,
                       ItauAuditableValue,
                       JpaEntity,
                       JpaEmbeddable]
        expectedItauAuditableType << [EntityType, ValueObjectType, ValueType, EntityType, ValueObjectType]

    }

    @Unroll
    def "should map as #expectedItauAuditableType.simpleName for explicitly registered class"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.registerClientsClass(givenDefinition)

        when:
        def jType = mapper.getItauAuditableType(DummyUser)

        then:
        jType.class == expectedItauAuditableType
        jType.baseJavaClass == DummyUser

        where:
        givenDefinition << [
                new EntityDefinition(DummyUser,"inheritedInt"),
                new ValueObjectDefinition(DummyUser),
                new ValueDefinition(DummyUser)]
        expectedItauAuditableType << [EntityType, ValueObjectType, ValueType]
    }

    class DummyUserOne extends AbstractDummyUser {
        @Id int id
    }

    @Unroll
    def "should spawn #expectedItauAuditableType.simpleName from explicitly mapped superclass"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.registerClientsClass(givenDefinitionOfSuperclass)

        when:
        def jType = mapper.getItauAuditableType(DummyUserOne)

        then:
        jType.class == expectedItauAuditableType
        jType.baseJavaClass == DummyUserOne

        where:
        givenDefinitionOfSuperclass << [
                new EntityDefinition(AbstractDummyUser,"inheritedInt"),
                new ValueObjectDefinition(AbstractDummyUser),
                new ValueDefinition(AbstractDummyUser)]
        expectedItauAuditableType << [EntityType, ValueObjectType, ValueType]
    }

    def "should inherit custom idProperty mapping from explicitly mapped Entity"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper

        when:
        mapper.registerClientsClass(new EntityDefinition(AbstractDummyUser,"inheritedInt"))
        def jType = mapper.getItauAuditableType(DummyUser)

        then:
        jType instanceof EntityType
        jType.idProperty.name == "inheritedInt"
    }

    def "should spawn from mapped superclass when querying for generic class"() {
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.registerClientsClass(new ValueDefinition(AbstractDummyUser))

        when:
        def jType = mapper.getItauAuditableType(new TypeToken<DummyGenericUser<String>>(){}.type)

        then:
        jType.class == ValueType
        jType.baseJavaClass == DummyGenericUser
    }

    def "should map subtype of @DiffIgnored type as IgnoredType"(){
        given:
        def mapper = itauAuditableTestAssembly().typeMapper
        mapper.getItauAuditableType(DummyIgnoredType)

        expect:
        mapper.getItauAuditableType(IgnoredSubType) instanceof IgnoredType
    }

    class DummyGenericUser<T> extends AbstractDummyUser {}
}
