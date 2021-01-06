package br.com.zup.itau.auditable.core

import br.com.zup.itau.auditable.common.date.DateProvider
import br.com.zup.itau.auditable.common.string.ShaDigest
import br.com.zup.itau.auditable.core.commit.CommitFactory
import br.com.zup.itau.auditable.core.graph.Cdo
import br.com.zup.itau.auditable.core.graph.LiveCdo
import br.com.zup.itau.auditable.core.graph.LiveCdoFactory
import br.com.zup.itau.auditable.core.graph.LiveCdoWrapper
import br.com.zup.itau.auditable.core.graph.LiveGraph
import br.com.zup.itau.auditable.core.graph.LiveGraphFactory
import br.com.zup.itau.auditable.core.graph.LiveNode
import br.com.zup.itau.auditable.core.graph.ObjectGraph
import br.com.zup.itau.auditable.core.json.JsonConverter
import br.com.zup.itau.auditable.core.json.JsonConverterBuilder
import br.com.zup.itau.auditable.core.metamodel.object.GlobalIdFactory
import br.com.zup.itau.auditable.core.metamodel.object.InstanceId
import br.com.zup.itau.auditable.core.metamodel.object.UnboundedValueObjectId
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId
import br.com.zup.itau.auditable.core.metamodel.property.Property
import br.com.zup.itau.auditable.core.metamodel.scanner.ClassScanner
import br.com.zup.itau.auditable.core.metamodel.type.TypeFactory
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper
import br.com.zup.itau.auditable.core.model.DummyAddress
import br.com.zup.itau.auditable.core.graph.ObjectHasher
import br.com.zup.itau.auditable.core.snapshot.SnapshotFactory
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository
import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository
import br.com.zup.itau.auditable.repository.jql.QueryRunner
import br.com.zup.itau.auditable.shadow.ShadowFactory

import java.util.stream.Collectors

/**
 * This is just a test builder,
 * don not confuse with unit test - {@link ItauAuditableBuilderTest}
 * <br/><br/>
 *
 * Provides default setup with well known Dummy* Entities.
 *
 * @author bartosz walacik
 */
class ItauAuditableTestBuilder {
    ItauAuditableBuilder itauAuditableBuilder

    private ItauAuditableTestBuilder (ItauAuditableBuilder itauAuditableBuilder) {
       this.itauAuditableBuilder = itauAuditableBuilder
       this.itauAuditableBuilder.build()
    }

    private ItauAuditableTestBuilder (MappingStyle mappingStyle) {
       itauAuditableBuilder = new ItauAuditableBuilder()
       itauAuditableBuilder.withMappingStyle(mappingStyle).build()
    }

    private ItauAuditableTestBuilder (DateProvider dateProvider) {
        itauAuditableBuilder = new ItauAuditableBuilder()
        itauAuditableBuilder.withDateTimeProvider(dateProvider).build()
    }

    private ItauAuditableTestBuilder (ItauAuditableRepository itauAuditableRepository) {
        itauAuditableBuilder = new ItauAuditableBuilder()
        itauAuditableBuilder.registerItauAuditableRepository(itauAuditableRepository).build()
    }

    private ItauAuditableTestBuilder (Class classToScan) {
        itauAuditableBuilder = new ItauAuditableBuilder()
        itauAuditableBuilder.scanTypeName(classToScan).build()
    }

    private ItauAuditableTestBuilder (String packagesToScan) {
        itauAuditableBuilder = new ItauAuditableBuilder()
        itauAuditableBuilder.withPackagesToScan(packagesToScan).build()
    }

    static ItauAuditableTestBuilder itauAuditableTestAssembly(){
        new ItauAuditableTestBuilder(MappingStyle.FIELD)
    }

    static ItauAuditableTestBuilder itauAuditableTestAssembly(String packagesToScan){
        new ItauAuditableTestBuilder(packagesToScan)
    }

    static ItauAuditableTestBuilder itauAuditableTestAssembly(Class classToScan){
        new ItauAuditableTestBuilder(classToScan)
    }

    static ItauAuditableTestBuilder itauAuditableTestAssembly(ItauAuditableRepository itauAuditableRepository){
        new ItauAuditableTestBuilder(itauAuditableRepository)
    }

    static ItauAuditableTestBuilder itauAuditableTestAssembly(MappingStyle mappingStyle){
        new ItauAuditableTestBuilder(mappingStyle)
    }

    static ItauAuditableTestBuilder itauAuditableTestAssembly(DateProvider dateProvider){
        new ItauAuditableTestBuilder(dateProvider)
    }

    static ItauAuditableTestBuilder itauAuditableTestAssemblyTypeSafe() {
        new ItauAuditableTestBuilder(new ItauAuditableBuilder().withTypeSafeValues(true))
    }

    static ItauAuditable newInstance() {
        itauAuditableTestAssembly().itauAuditable()
    }

    ItauAuditable itauAuditable() {
        itauAuditableBuilder.getContainerComponent(ItauAuditable)
    }

    Cdo createCdoWrapper(Object cdo){
        def mType = getTypeMapper().getItauAuditableManagedType(cdo.class)
        def id = instanceId(cdo)

        new LiveCdoWrapper(cdo, id, mType)
    }

    Property getProperty(Class type, String propName) {
        getTypeMapper().getItauAuditableManagedType(type).getProperty(propName)
    }

    SnapshotFactory getSnapshotFactory() {
        itauAuditableBuilder.getContainerComponent(SnapshotFactory)
    }

    ItauAuditableExtendedRepository getItauAuditableRepository(){
        itauAuditableBuilder.getContainerComponent(ItauAuditableExtendedRepository)
    }

    TypeMapper getTypeMapper(){
        itauAuditableBuilder.getContainerComponent(TypeMapper)
    }

    QueryRunner getQueryRunner(){
        itauAuditableBuilder.getContainerComponent(QueryRunner)
    }

    GlobalIdFactory getGlobalIdFactory(){
        itauAuditableBuilder.getContainerComponent(GlobalIdFactory)
    }

    LiveCdoFactory getLiveCdoFactory(){
        itauAuditableBuilder.getContainerComponent(LiveCdoFactory)
    }

    CommitFactory getCommitFactory(){
        itauAuditableBuilder.getContainerComponent(CommitFactory)
    }

    JsonConverter getJsonConverter() {
        itauAuditableBuilder.getContainerComponent(JsonConverter)
    }

    ShadowFactory getShadowFactory() {
        itauAuditableBuilder.getContainerComponent(ShadowFactory)
    }


    JsonConverter getJsonConverterMinifiedPrint() {
        ItauAuditableBuilder.itauAuditable().withPrettyPrint(false).build().getJsonConverter()
    }

    JsonConverterBuilder getJsonConverterBuilder() {
        itauAuditableBuilder.getContainerComponent(JsonConverterBuilder)
    }

    String hash(Object obj) {
        def jsonState = jsonConverter.toJson(itauAuditable().commit("", obj).snapshots[0].state)
        ShaDigest.longDigest(jsonState)
    }

    String addressHash(String city){
        hash(new DummyAddress(city))
    }

    def getContainerComponent(Class type) {
        itauAuditableBuilder.getContainerComponent(type)
    }

    ObjectGraph createLiveGraph(Object liveCdo) {
        itauAuditableBuilder.getContainerComponent(LiveGraphFactory).createLiveGraph(liveCdo)
    }

    LiveNode createLiveNode(Object liveCdo) {
        itauAuditableBuilder.getContainerComponent(LiveGraphFactory).createLiveGraph(liveCdo).root()
    }

    InstanceId instanceId(Object instance){
        getGlobalIdFactory().createId(instance)
    }

    InstanceId instanceId(Object localId, Class entity){
        getGlobalIdFactory().createInstanceId(localId, entity)
    }

    ValueObjectId valueObjectId(Object localId, Class owningEntity, fragment) {
        getGlobalIdFactory().createValueObjectIdFromPath(instanceId(localId, owningEntity), fragment)
    }

    UnboundedValueObjectId unboundedValueObjectId(Class valueObject) {
        getGlobalIdFactory().createUnboundedValueObjectId(valueObject)
    }

}
