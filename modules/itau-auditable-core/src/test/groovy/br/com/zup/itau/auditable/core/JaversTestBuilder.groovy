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
    ItauAuditableBuilder javersBuilder

    private ItauAuditableTestBuilder (ItauAuditableBuilder javersBuilder) {
       this.javersBuilder = javersBuilder
       this.javersBuilder.build()
    }

    private ItauAuditableTestBuilder (MappingStyle mappingStyle) {
       javersBuilder = new ItauAuditableBuilder()
       javersBuilder.withMappingStyle(mappingStyle).build()
    }

    private ItauAuditableTestBuilder (DateProvider dateProvider) {
        javersBuilder = new ItauAuditableBuilder()
        javersBuilder.withDateTimeProvider(dateProvider).build()
    }

    private ItauAuditableTestBuilder (ItauAuditableRepository javersRepository) {
        javersBuilder = new ItauAuditableBuilder()
        javersBuilder.registerItauAuditableRepository(javersRepository).build()
    }

    private ItauAuditableTestBuilder (Class classToScan) {
        javersBuilder = new ItauAuditableBuilder()
        javersBuilder.scanTypeName(classToScan).build()
    }

    private ItauAuditableTestBuilder (String packagesToScan) {
        javersBuilder = new ItauAuditableBuilder()
        javersBuilder.withPackagesToScan(packagesToScan).build()
    }

    static ItauAuditableTestBuilder javersTestAssembly(){
        new ItauAuditableTestBuilder(MappingStyle.FIELD)
    }

    static ItauAuditableTestBuilder javersTestAssembly(String packagesToScan){
        new ItauAuditableTestBuilder(packagesToScan)
    }

    static ItauAuditableTestBuilder javersTestAssembly(Class classToScan){
        new ItauAuditableTestBuilder(classToScan)
    }

    static ItauAuditableTestBuilder javersTestAssembly(ItauAuditableRepository javersRepository){
        new ItauAuditableTestBuilder(javersRepository)
    }

    static ItauAuditableTestBuilder javersTestAssembly(MappingStyle mappingStyle){
        new ItauAuditableTestBuilder(mappingStyle)
    }

    static ItauAuditableTestBuilder javersTestAssembly(DateProvider dateProvider){
        new ItauAuditableTestBuilder(dateProvider)
    }

    static ItauAuditableTestBuilder javersTestAssemblyTypeSafe() {
        new ItauAuditableTestBuilder(new ItauAuditableBuilder().withTypeSafeValues(true))
    }

    static ItauAuditable newInstance() {
        javersTestAssembly().javers()
    }

    ItauAuditable javers() {
        javersBuilder.getContainerComponent(ItauAuditable)
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
        javersBuilder.getContainerComponent(SnapshotFactory)
    }

    ItauAuditableExtendedRepository getItauAuditableRepository(){
        javersBuilder.getContainerComponent(ItauAuditableExtendedRepository)
    }

    TypeMapper getTypeMapper(){
        javersBuilder.getContainerComponent(TypeMapper)
    }

    QueryRunner getQueryRunner(){
        javersBuilder.getContainerComponent(QueryRunner)
    }

    GlobalIdFactory getGlobalIdFactory(){
        javersBuilder.getContainerComponent(GlobalIdFactory)
    }

    LiveCdoFactory getLiveCdoFactory(){
        javersBuilder.getContainerComponent(LiveCdoFactory)
    }

    CommitFactory getCommitFactory(){
        javersBuilder.getContainerComponent(CommitFactory)
    }

    JsonConverter getJsonConverter() {
        javersBuilder.getContainerComponent(JsonConverter)
    }

    ShadowFactory getShadowFactory() {
        javersBuilder.getContainerComponent(ShadowFactory)
    }


    JsonConverter getJsonConverterMinifiedPrint() {
        ItauAuditableBuilder.javers().withPrettyPrint(false).build().getJsonConverter()
    }

    JsonConverterBuilder getJsonConverterBuilder() {
        javersBuilder.getContainerComponent(JsonConverterBuilder)
    }

    String hash(Object obj) {
        def jsonState = jsonConverter.toJson(javers().commit("", obj).snapshots[0].state)
        ShaDigest.longDigest(jsonState)
    }

    String addressHash(String city){
        hash(new DummyAddress(city))
    }

    def getContainerComponent(Class type) {
        javersBuilder.getContainerComponent(type)
    }

    ObjectGraph createLiveGraph(Object liveCdo) {
        javersBuilder.getContainerComponent(LiveGraphFactory).createLiveGraph(liveCdo)
    }

    LiveNode createLiveNode(Object liveCdo) {
        javersBuilder.getContainerComponent(LiveGraphFactory).createLiveGraph(liveCdo).root()
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
