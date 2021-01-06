package br.com.zup.itau.auditable.core;

import com.google.gson.TypeAdapter;
import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.common.date.DateProvider;
import br.com.zup.itau.auditable.common.date.DefaultDateProvider;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.ItauAuditableCoreProperties.PrettyPrintDateFormats;
import br.com.zup.itau.auditable.core.commit.Commit;
import br.com.zup.itau.auditable.core.commit.CommitFactoryModule;
import br.com.zup.itau.auditable.core.commit.CommitId;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.DiffFactoryModule;
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm;
import br.com.zup.itau.auditable.core.diff.appenders.DiffAppendersModule;
import br.com.zup.itau.auditable.core.diff.custom.*;
import br.com.zup.itau.auditable.core.graph.GraphFactoryModule;
import br.com.zup.itau.auditable.core.graph.ObjectAccessHook;
import br.com.zup.itau.auditable.core.graph.TailoredItauAuditableMemberFactoryModule;
import br.com.zup.itau.auditable.core.json.JsonAdvancedTypeAdapter;
import br.com.zup.itau.auditable.core.json.JsonConverter;
import br.com.zup.itau.auditable.core.json.JsonConverterBuilder;
import br.com.zup.itau.auditable.core.json.JsonTypeAdapter;
import br.com.zup.itau.auditable.core.json.typeadapter.change.ChangeTypeAdaptersModule;
import br.com.zup.itau.auditable.core.json.typeadapter.commit.CommitTypeAdaptersModule;
import br.com.zup.itau.auditable.core.json.typeadapter.commit.DiffTypeDeserializer;
import br.com.zup.itau.auditable.core.metamodel.annotation.*;
import br.com.zup.itau.auditable.core.metamodel.clazz.*;
import br.com.zup.itau.auditable.core.metamodel.scanner.ScannerModule;
import br.com.zup.itau.auditable.core.metamodel.type.*;
import br.com.zup.itau.auditable.core.pico.AddOnsModule;
import br.com.zup.itau.auditable.core.snapshot.SnapshotModule;
import br.com.zup.itau.auditable.groovysupport.GroovyAddOns;
import br.com.zup.itau.auditable.guava.GuavaAddOns;
import br.com.zup.itau.auditable.jodasupport.JodaAddOns;
import br.com.zup.itau.auditable.mongosupport.MongoLong64JsonDeserializer;
import br.com.zup.itau.auditable.mongosupport.RequiredMongoSupportPredicate;
import br.com.zup.itau.auditable.repository.api.ConfigurationAware;
import br.com.zup.itau.auditable.repository.api.ItauAuditableExtendedRepository;
import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository;
import br.com.zup.itau.auditable.repository.inmemory.InMemoryRepository;
import br.com.zup.itau.auditable.repository.jql.JqlModule;
import br.com.zup.itau.auditable.shadow.ShadowModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static br.com.zup.itau.auditable.common.reflection.ReflectionUtil.findClasses;
import static br.com.zup.itau.auditable.common.reflection.ReflectionUtil.isClassPresent;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentIsNotNull;
import static br.com.zup.itau.auditable.common.validation.Validate.argumentsAreNotNull;

/**
 * Creates a Itaú Auditable instance based on your domain model metadata and custom configuration.
 * <br/><br/>
 *
 * For example, to build a Itaú Auditable instance configured with reasonable defaults:
 * <pre>
 * ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();
 * </pre>
 *
 * To build a Itaú Auditable instance with Entity type registered:
 * <pre>
 * ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable()
 *                              .registerEntity(MyEntity.class)
 *                              .build();
 * </pre>
 *
 * @see <a href="http://itauAuditable.org/documentation/domain-configuration/">http://itauAuditable.org/documentation/domain-configuration</a>
 * @author bartosz walacik
 */
public class ItauAuditableBuilder extends AbstractContainerBuilder {
    public static final Logger logger = LoggerFactory.getLogger(ItauAuditableBuilder.class);

    private final Map<Class, ClientsClassDefinition> clientsClassDefinitions = new LinkedHashMap<>();

    private final Map<Class, Function<Object, String>> mappedToStringFunction = new ConcurrentHashMap<>();

    private final Set<Class> classesToScan = new HashSet<>();

    private final Set<ConditionalTypesPlugin> conditionalTypesPlugins;

    private ItauAuditableRepository repository;
    private DateProvider dateProvider;
    private long bootStart = System.currentTimeMillis();

    private IgnoredClassesStrategy ignoredClassesStrategy;

    public static ItauAuditableBuilder itauAuditable() {
        return new ItauAuditableBuilder();
    }

    /**
     * use static factory method {@link ItauAuditableBuilder#itauAuditable()}
     */
    protected ItauAuditableBuilder() {
        logger.debug("starting up Itaú Auditable ...");

        //conditional plugins
        conditionalTypesPlugins = new HashSet<>();

        if (isClassPresent("groovy.lang.MetaClass")) {
            conditionalTypesPlugins.add(new GroovyAddOns());
        }
        if (isClassPresent("org.joda.time.LocalDate")){
            conditionalTypesPlugins.add(new JodaAddOns());
        }
        if (isClassPresent("com.google.common.collect.Multimap")) {
            conditionalTypesPlugins.add(new GuavaAddOns());
        }

        // bootstrap pico container & core module
        bootContainer();
        addModule(new CoreItauAuditableModule(getContainer()));
    }

    public ItauAuditable build() {

        ItauAuditable itauAuditable = assembleItauAuditableInstance();
        repository.ensureSchema();

        long boot = System.currentTimeMillis() - bootStart;
        logger.info("Itaú Auditable instance started in {} ms", boot);
        return itauAuditable;
    }

    protected ItauAuditable assembleItauAuditableInstance(){
        // boot main modules
        addModule(new DiffFactoryModule());
        addModule(new CommitFactoryModule(getContainer()));
        addModule(new SnapshotModule(getContainer()));
        addModule(new GraphFactoryModule(getContainer()));
        addModule(new DiffAppendersModule(coreConfiguration(), getContainer()));
        addModule(new TailoredItauAuditableMemberFactoryModule(coreConfiguration(), getContainer()));
        addModule(new ScannerModule(coreConfiguration(), getContainer()));
        addModule(new ShadowModule(getContainer()));
        addModule(new JqlModule(getContainer()));

        // boot add-ons modules
        Set<ItauAuditableType> additionalTypes = bootAddOns();

        // boot TypeMapper module
        addComponent(new DynamicMappingStrategy(ignoredClassesStrategy));
        addModule(new TypeMapperModule(getContainer()));

        // boot JSON beans & domain aware typeAdapters
        additionalTypes.addAll( bootJsonConverter() );

        bootDateTimeProvider();

        // clases to scan & additionalTypes
        for (Class c : classesToScan){
            typeMapper().getItauAuditableType(c);
        }
        typeMapper().addPluginTypes(additionalTypes);

        mapRegisteredClasses();

        bootRepository();

        return getContainerComponent(ItauAuditableCore.class);
    }

    /**
     * @see <a href="http://itauAuditable.org/documentation/repository-configuration">http://itauAuditable.org/documentation/repository-configuration</a>
     */
    public ItauAuditableBuilder registerItauAuditableRepository(ItauAuditableRepository repository) {
        argumentsAreNotNull(repository);
        this.repository = repository;
        return this;
    }

    /**
     * Registers an {@link EntityType}. <br/>
     * Use @Id annotation to mark exactly one Id-property.
     * <br/><br/>
     *
     * Optionally, use @Transient or @{@link DiffIgnore} annotations to mark ignored properties.
     * <br/><br/>
     *
     * For example, Entities are: Person, Document
     *
     * @see <a href="http://itauAuditable.org/documentation/domain-configuration/#entity">http://itauAuditable.org/documentation/domain-configuration/#entity</a>
     * @see #registerEntity(EntityDefinition)
     */
    public ItauAuditableBuilder registerEntity(Class<?> entityClass) {
        argumentIsNotNull(entityClass);
        return registerEntity( new EntityDefinition(entityClass));
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Optionally, use @Transient or @{@link DiffIgnore} annotations to mark ignored properties.
     * <br/><br/>
     *
     * For example, ValueObjects are: Address, Point
     *
     * @see <a href="http://itauAuditable.org/documentation/domain-configuration/#value-object">http://itauAuditable.org/documentation/domain-configuration/#value-object</a>
     * @see #registerValueObject(ValueObjectDefinition)
     */
    public ItauAuditableBuilder registerValueObject(Class<?> valueObjectClass) {
        argumentIsNotNull(valueObjectClass);
        registerType(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    /**
     * Registers an {@link EntityType}. <br/>
     * Use this method if you are not willing to use {@link Entity} annotation.
     * <br/></br/>
     *
     * Recommended way to create {@link EntityDefinition} is {@link EntityDefinitionBuilder},
     * for example:
     * <pre>
     * itauAuditableBuilder.registerEntity(
     *     EntityDefinitionBuilder.entityDefinition(Person.class)
     *     .withIdPropertyName("id")
     *     .withTypeName("Person")
     *     .withIgnoredProperties("notImportantProperty","transientProperty")
     *     .build());
     * </pre>
     *
     * For simple cases, you can use {@link EntityDefinition} constructors,
     * for example:
     * <pre>
     * itauAuditableBuilder.registerEntity( new EntityDefinition(Person.class, "login") );
     * </pre>
     *
     * @see <a href="http://itauAuditable.org/documentation/domain-configuration/#entity">http://itauAuditable.org/documentation/domain-configuration/#entity</a>
     * @see EntityDefinitionBuilder#entityDefinition(Class)
     */
    public ItauAuditableBuilder registerEntity(EntityDefinition entityDefinition){
        argumentIsNotNull(entityDefinition);
        return registerType(entityDefinition);
    }

    /**
     * Generic version of {@link #registerEntity(EntityDefinition)} and
     * {@link #registerValueObject(ValueObjectDefinition)}
     */
    public ItauAuditableBuilder registerType(ClientsClassDefinition clientsClassDefinition) {
        argumentIsNotNull(clientsClassDefinition);
        clientsClassDefinitions.put(clientsClassDefinition.getBaseJavaClass(), clientsClassDefinition);
        return this;
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Use this method if you are not willing to use {@link ValueObject} annotations.
     * <br/></br/>
     *
     * Recommended way to create {@link ValueObjectDefinition} is {@link ValueObjectDefinitionBuilder}.
     * For example:
     * <pre>
     * itauAuditableBuilder.registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(Address.class)
     *     .withIgnoredProperties(ignoredProperties)
     *     .withTypeName(typeName)
     *     .build();
     * </pre>
     *
     * For simple cases, you can use {@link ValueObjectDefinition} constructors,
     * for example:
     * <pre>
     * itauAuditableBuilder.registerValueObject( new ValueObjectDefinition(Address.class, "ignored") );
     * </pre>
     *
     * @see <a href="http://itauAuditable.org/documentation/domain-configuration/#value-object">http://itauAuditable.org/documentation/domain-configuration/#value-object</a>
     * @see ValueObjectDefinitionBuilder#valueObjectDefinition(Class)
     */
    public ItauAuditableBuilder registerValueObject(ValueObjectDefinition valueObjectDefinition) {
        argumentIsNotNull(valueObjectDefinition);
        registerType(valueObjectDefinition);
        return this;
    }

    /**
     * Comma separated list of packages scanned by ItauAuditable in search of
     * your classes with the {@link TypeName} annotation.
     * <br/><br/>
     *
     * It's <b>important</b> to declare here all of your packages containing classes with {@literal @}TypeName,<br/>
     * because ItauAuditable needs <i>live</i> class definitions to properly deserialize Snapshots from {@link ItauAuditableRepository}.
     * <br/><br/>
     *
     * <b>For example</b>, consider this class:
     *
     * <pre>
     * {@literal @}Entity
     * {@literal @}TypeName("Person")
     *  class Person {
     *     {@literal @}Id
     *      private int id;
     *      private String name;
     *  }
     * </pre>
     *
     * In the scenario when ItauAuditable reads a Snapshot of type named 'Person'
     * before having a chance to map the Person class definition,
     * the 'Person' type will be mapped to generic {@link UnknownType}.
     * <br/><br/>
     *
     * Since 5.8.4, ItauAuditable logs <code>WARNING</code> when UnknownType is created
     * because Snapshots with UnknownType can't be properly deserialized from {@link ItauAuditableRepository}.
     *
     * @param packagesToScan e.g. "my.company.domain.person, my.company.domain.finance"
     * @since 2.3
     */
    public ItauAuditableBuilder withPackagesToScan(String packagesToScan) {
        if (packagesToScan == null || packagesToScan.trim().isEmpty()) {
            return this;
        }

        long start = System.currentTimeMillis();
        logger.info("scanning package(s): {}", packagesToScan);
        List<Class<?>> scan = findClasses(TypeName.class, packagesToScan.replaceAll(" ","").split(","));
		for (Class<?> c : scan) {
			scanTypeName(c);
		}
		long delta = System.currentTimeMillis() - start;
        logger.info("  found {} ManagedClasse(s) with @TypeName in {} ms", scan.size(), delta);

		return this;
    }

    /**
     * Register your class with &#64;{@link TypeName} annotation
     * in order to use it in all kinds of JQL queries.
     * <br/><br/>
     *
     * You can also use {@link #withPackagesToScan(String)}
     * to scan all your classes.
     * <br/><br/>
     *
     * Technically, this method is the convenient alias for {@link ItauAuditable#getTypeMapping(Type)}
     *
     * @since 1.4
     */
    public ItauAuditableBuilder scanTypeName(Class userType){
        classesToScan.add(userType);
        return this;
    }

    /**
     * Registers a simple value type (see {@link ValueType}).
     * <br/><br/>
     *
     * For example, values are: BigDecimal, LocalDateTime.
     * <br/><br/>
     *
     * Use this method if can't use the {@link Value} annotation.
     * <br/><br/>
     *
     * By default, Values are compared using {@link Object#equals(Object)}.
     * You can provide external <code>equals()</code> function
     * by registering a {@link CustomValueComparator}.
     * See {@link #registerValue(Class, CustomValueComparator)}.
     *
     * @see <a href="http://itauAuditable.org/documentation/domain-configuration/#ValueType">http://itauAuditable.org/documentation/domain-configuration/#ValueType</a>
     */
    public ItauAuditableBuilder registerValue(Class<?> valueClass) {
        argumentIsNotNull(valueClass);
        registerType(new ValueDefinition(valueClass));
        return this;
    }

    /**
     * Registers a {@link ValueType} with a custom comparator to be used instead of
     * {@link Object#equals(Object)}.
     * <br/><br/>
     *
     * For example, by default, BigDecimals are Values
     * compared using {@link java.math.BigDecimal#equals(Object)},
     * sadly it isn't the correct mathematical equality:
     *
     * <pre>
     *     new BigDecimal("1.000").equals(new BigDecimal("1.00")) == false
     * </pre>
     *
     * If you want to compare them in the right way &mdash; ignoring trailing zeros &mdash;
     * register this comparator:
     *
     * <pre>
     * ItauAuditableBuilder.itauAuditable()
     *     .registerValue(BigDecimal.class, new BigDecimalComparatorWithFixedEquals())
     *     .build();
     * </pre>
     *
     * @param <T> Value Type
     * @see <a href="http://itauAuditable.org/documentation/domain-configuration/#ValueType">http://itauAuditable.org/documentation/domain-configuration/#ValueType</a>
     * @see <a href="https://itauAuditable.org/documentation/diff-configuration/#custom-comparators">https://itauAuditable.org/documentation/diff-configuration/#custom-comparators</a>
     * @see BigDecimalComparatorWithFixedEquals
     * @see CustomBigDecimalComparator
     * @since 3.3
     */
    public <T> ItauAuditableBuilder registerValue(Class<T> valueClass, CustomValueComparator<T> customValueComparator) {
        argumentsAreNotNull(valueClass, customValueComparator);

        if (!clientsClassDefinitions.containsKey(valueClass)){
            registerType(new ValueDefinition(valueClass));
        }
        ValueDefinition def = getClassDefinition(valueClass);
        def.setCustomValueComparator(customValueComparator);

        return this;
    }

    /**
     * Lambda-style variant of {@link #registerValue(Class, CustomValueComparator)}.
     * <br/><br/>
     *
     * For example, you can register the comparator for BigDecimals with fixed equals:
     *
     * <pre>
     * ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable()
     *     .registerValue(BigDecimal.class, (a, b) -> a.compareTo(b) == 0,
     *                                           a -> a.stripTrailingZeros().toString())
     *     .build();
     * </pre>
     *
     * @param <T> Value Type
     * @see #registerValue(Class, CustomValueComparator)
     * @since 5.8
     */
    public <T> ItauAuditableBuilder registerValue(Class<T> valueClass,
                                           BiFunction<T, T, Boolean> equalsFunction,
                                           Function<T, String> toStringFunction) {
        Validate.argumentsAreNotNull(valueClass, equalsFunction, toStringFunction);

        return registerValue(valueClass, new CustomValueComparator<T>() {
            @Override
            public boolean equals(T a, T b) {
                return equalsFunction.apply(a,b);
            }

            @Override
            public String toString(@NotNull T value) {
                return toStringFunction.apply(value);
            }
        });
    }

    /**
     * <b>Deprecated</b>, use {@link #registerValue(Class, CustomValueComparator)}.
     *
     * <br/><br/>
     *
     * Since this comparator is not aligned with {@link Object#hashCode()},
     * it calculates incorrect results when a given Value is used in hashing context
     * (when comparing Sets with Values or Maps with Values as keys).
     *
     * @see CustomValueComparator
     */
    @Deprecated
    public <T> ItauAuditableBuilder registerValue(Class<T> valueClass, BiFunction<T, T, Boolean> equalsFunction) {
        Validate.argumentsAreNotNull(valueClass, equalsFunction);

        return registerValue(valueClass, new CustomValueComparator<T>() {
            @Override
            public boolean equals(T a, T b) {
                return equalsFunction.apply(a,b);
            }

            @Override
            public String toString(@NotNull T value) {
                return value.toString();
            }
        });
    }

    /**
     * <b>Deprecated</b>, use {@link #registerValue(Class, CustomValueComparator)}.
     *
     * @see CustomValueComparator
     * @since 3.7.6
     */
    @Deprecated
    public <T> ItauAuditableBuilder registerValueWithCustomToString(Class<T> valueClass, Function<T, String> toStringFunction) {
        Validate.argumentsAreNotNull(valueClass, toStringFunction);
        return registerValue(valueClass, (a,b) -> Objects.equals(a,b), toStringFunction);
    }

    /**
     * Marks given class as ignored by Itaú Auditable.
     * <br/><br/>
     *
     * Use this method as an alternative to the {@link DiffIgnore} annotation.
     *
     * @see DiffIgnore
     */
    public ItauAuditableBuilder registerIgnoredClass(Class<?> ignoredClass) {
        argumentIsNotNull(ignoredClass);
        registerType(new IgnoredTypeDefinition(ignoredClass));
        return this;
    }

    /**
     * A dynamic version of {@link ItauAuditableBuilder#registerIgnoredClass(Class)}.
     * <br/>
     * Registers a custom strategy for marking certain classes as ignored.
     * <br/><br/>
     *
     * For example, you can ignore classes by package naming convention:
     *
     * <pre>
     * ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable()
     *         .registerIgnoredClassesStrategy(c -> c.getName().startsWith("com.ignore.me"))
     *         .build();
     * </pre>
     *
     * Use this method as the alternative to the {@link DiffIgnore} annotation
     * or multiple calls of {@link ItauAuditableBuilder#registerIgnoredClass(Class)}.
     */
    public ItauAuditableBuilder registerIgnoredClassesStrategy(IgnoredClassesStrategy ignoredClassesStrategy) {
        argumentIsNotNull(ignoredClassesStrategy);
        this.ignoredClassesStrategy = ignoredClassesStrategy;
        return this;
    }

    /**
     * Registers a {@link ValueType} and its custom JSON adapter.
     * <br><br>
     *
     * Useful for not trivial ValueTypes when Gson's default representation isn't appropriate
     *
     * @see <a href="http://itauAuditable.org/documentation/repository-configuration/#json-type-adapters">http://itauAuditable.org/documentation/repository-configuration/#json-type-adapters</a>
     * @see JsonTypeAdapter
     */
    public ItauAuditableBuilder registerValueTypeAdapter(JsonTypeAdapter typeAdapter) {
        for (Class c : (List<Class>)typeAdapter.getValueTypes()){
            registerValue(c);
        }

        jsonConverterBuilder().registerJsonTypeAdapter(typeAdapter);
        return this;
    }

    /**
     * <font color='red'>INCUBATING</font><br/>
     *
     * For complex structures like Multimap
     * @since 3.1
     */
    public ItauAuditableBuilder registerJsonAdvancedTypeAdapter(JsonAdvancedTypeAdapter adapter) {
        jsonConverterBuilder().registerJsonAdvancedTypeAdapter(adapter);
        return this;
    }

    /**
     * Registers {@link ValueType} and its custom native
     * <a href="http://code.google.com/p/google-gson/">Gson</a> adapter.
     * <br/><br/>
     *
     * Useful when you already have Gson {@link TypeAdapter}s implemented.
     *
     * @see TypeAdapter
     */
    public ItauAuditableBuilder registerValueGsonTypeAdapter(Class valueType, TypeAdapter nativeAdapter) {
        registerValue(valueType);
        jsonConverterBuilder().registerNativeTypeAdapter(valueType, nativeAdapter);
        return this;
    }

    /**
     * Switch on when you need a type safe serialization for
     * heterogeneous collections like List, List&lt;Object&gt;.
     * <br/><br/>
     *
     * Heterogeneous collections are collections which contains items of different types
     * (or types unknown at compile time).
     * <br/><br/>
     *
     * This approach is generally discouraged, prefer statically typed collections
     * with exactly one type of items like List&lt;String&gt;.
     *
     * @see br.com.zup.itau.auditable.core.json.JsonConverterBuilder#typeSafeValues(boolean)
     * @param typeSafeValues default false
     */
    public ItauAuditableBuilder withTypeSafeValues(boolean typeSafeValues) {
        jsonConverterBuilder().typeSafeValues(typeSafeValues);
        return this;
    }

    /**
     * choose between JSON pretty or concise printing style, i.e. :
     *
     * <ul><li>pretty:
     * <pre>
     * {
     *     "value": 5
     * }
     * </pre>
     * </li><li>concise:
     * <pre>
     * {"value":5}
     * </pre>
     * </li></ul>
     *
     * @param prettyPrint default true
     */
    public ItauAuditableBuilder withPrettyPrint(boolean prettyPrint) {
        jsonConverterBuilder().prettyPrint(prettyPrint);
        return this;
    }

    public ItauAuditableBuilder registerEntities(Class<?>... entityClasses) {
        for(Class clazz : entityClasses) {
            registerEntity(clazz);
        }
        return this;
    }

    public ItauAuditableBuilder registerValueObjects(Class<?>... valueObjectClasses) {
        for(Class clazz : valueObjectClasses) {
            registerValueObject(clazz);
        }
        return this;
    }

    /**
     * Default style is {@link MappingStyle#FIELD}.
     *
     * @see <a href="http://itauAuditable.org/documentation/domain-configuration/#property-mapping-style">http://itauAuditable.org/documentation/domain-configuration/#property-mapping-style</a>
     */
    public ItauAuditableBuilder withMappingStyle(MappingStyle mappingStyle) {
        argumentIsNotNull(mappingStyle);
        coreConfiguration().withMappingStyle(mappingStyle);
        return this;
    }

    /**
     * <ul>
     * <li/> {@link CommitIdGenerator#SYNCHRONIZED_SEQUENCE} &mdash; for non-distributed applications
     * <li/> {@link CommitIdGenerator#RANDOM} &mdash; for distributed applications
     * </ul>
     * SYNCHRONIZED_SEQUENCE is used by default.
     */
    public ItauAuditableBuilder withCommitIdGenerator(CommitIdGenerator commitIdGenerator) {
        coreConfiguration().withCommitIdGenerator(commitIdGenerator);
        return this;
    }

    ItauAuditableBuilder withCustomCommitIdGenerator(Supplier<CommitId> commitIdGenerator) {
        coreConfiguration().withCustomCommitIdGenerator(commitIdGenerator);
        return this;
    }

    /**
     * When enabled, {@link ItauAuditable#compare(Object oldVersion, Object currentVersion)}
     * generates additional 'Snapshots' of new objects (objects added in currentVersion graph).
     * <br/>
     * For each new object, state of its properties is captured and returned as a Set of PropertyChanges.
     * These Changes have null at the left side and a current property value at the right side.
     * <br/><br/>
     *
     * Disabled by default.
     */
    public ItauAuditableBuilder withNewObjectsSnapshot(boolean newObjectsSnapshot){
        coreConfiguration().withNewObjectsSnapshot(newObjectsSnapshot);
        return this;
    }

    public ItauAuditableBuilder withObjectAccessHook(ObjectAccessHook objectAccessHook) {
        removeComponent(ObjectAccessHook.class);
        bindComponent(ObjectAccessHook.class, objectAccessHook);
        return this;
    }

    /**
     * Registers a {@link CustomPropertyComparator} for a given class and maps this class
     * to {@link CustomType}.
     * <br/><br/>
     *
     * <b>
     * Custom Types are not easy to manage, use it as a last resort,<br/>
     * only for corner cases like comparing custom Collection types.</b>
     * <br/><br/>
     *
     * In most cases, it's better to customize the ItauAuditable' diff algorithm using
     * much more simpler {@link CustomValueComparator},
     * see {@link #registerValue(Class, CustomValueComparator)}.
     *
     * @param <T> Custom Type
     * @see <a href="https://itauAuditable.org/documentation/diff-configuration/#custom-comparators">https://itauAuditable.org/documentation/diff-configuration/#custom-comparators</a>
     */
    public <T> ItauAuditableBuilder registerCustomType(Class<T> customType, CustomPropertyComparator<T, ?> comparator){
        registerType(new CustomDefinition(customType, comparator));
        bindComponent(comparator, new CustomToNativeAppenderAdapter(comparator, customType));
        return this;
    }

    /**
     * @deprecated Renamed to {@link #registerCustomType(Class, CustomPropertyComparator)}
     */
    @Deprecated
    public <T> ItauAuditableBuilder registerCustomComparator(CustomPropertyComparator<T, ?> comparator, Class<T> customType){
        return registerCustomType(customType, comparator);
    }

    /**
     * Choose between two algorithms for comparing list: ListCompareAlgorithm.SIMPLE
     * or ListCompareAlgorithm.LEVENSHTEIN_DISTANCE.
     * <br/><br/>
     * Generally, we recommend using LEVENSHTEIN_DISTANCE, because it's smarter.
     * However, it can be slow for long lists, so SIMPLE is enabled by default.
     * <br/><br/>
     *
     * Refer to <a href="http://itauAuditable.org/documentation/diff-configuration/#list-algorithms">http://itauAuditable.org/documentation/diff-configuration/#list-algorithms</a>
     * for description of both algorithms
     *
     * @param algorithm ListCompareAlgorithm.SIMPLE is used by default
     */
    public ItauAuditableBuilder withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        argumentIsNotNull(algorithm);
        coreConfiguration().withListCompareAlgorithm(algorithm);
        return this;
    }

  /**
   * DateProvider providers current util for {@link Commit#getCommitDate()}.
   * <br/>
   * By default, now() is used.
   * <br/>
   * Overriding default dateProvider probably makes sense only in test environment.
   */
    public ItauAuditableBuilder withDateTimeProvider(DateProvider dateProvider) {
        argumentIsNotNull(dateProvider);
        this.dateProvider = dateProvider;
        return this;
    }

    public ItauAuditableBuilder withPrettyPrintDateFormats(PrettyPrintDateFormats prettyPrintDateFormats) {
        coreConfiguration().withPrettyPrintDateFormats(prettyPrintDateFormats);
        return this;
    }

    public ItauAuditableBuilder withProperties(ItauAuditableCoreProperties itauAuditableProperties) {
        this.withListCompareAlgorithm(ListCompareAlgorithm.valueOf(itauAuditableProperties.getAlgorithm().toUpperCase()))
            .withCommitIdGenerator(CommitIdGenerator.valueOf(itauAuditableProperties.getCommitIdGenerator().toUpperCase()))
            .withMappingStyle(MappingStyle.valueOf(itauAuditableProperties.getMappingStyle().toUpperCase()))
            .withNewObjectsSnapshot(itauAuditableProperties.isNewObjectSnapshot())
            .withPrettyPrint(itauAuditableProperties.isPrettyPrint())
            .withTypeSafeValues(itauAuditableProperties.isTypeSafeValues())
            .withPackagesToScan(itauAuditableProperties.getPackagesToScan())
            .withPrettyPrintDateFormats(itauAuditableProperties.getPrettyPrintDateFormats());
        return this;
    }

    private void mapRegisteredClasses() {
        TypeMapper typeMapper = typeMapper();
        for (ClientsClassDefinition def : clientsClassDefinitions.values()) {
            typeMapper.registerClientsClass(def);
        }
    }

    private TypeMapper typeMapper() {
        return getContainerComponent(TypeMapper.class);
    }

    private ItauAuditableCoreConfiguration coreConfiguration() {
        return getContainerComponent(ItauAuditableCoreConfiguration.class);
    }

    private JsonConverterBuilder jsonConverterBuilder(){
        return getContainerComponent(JsonConverterBuilder.class);
    }

    private Set<ItauAuditableType> bootAddOns() {
        Set<ItauAuditableType> additionalTypes = new HashSet<>();

        for (ConditionalTypesPlugin plugin : conditionalTypesPlugins) {
            logger.info("loading "+plugin.getClass().getSimpleName()+" ...");

            plugin.beforeAssemble(this);

            additionalTypes.addAll(plugin.getNewTypes());

            AddOnsModule addOnsModule = new AddOnsModule(getContainer(), (Collection)plugin.getPropertyChangeAppenders());
            addModule(addOnsModule);
        }

        return additionalTypes;
    }

    /**
     * boots JsonConverter and registers domain aware typeAdapters
     */
    private Collection<ItauAuditableType> bootJsonConverter() {
        JsonConverterBuilder jsonConverterBuilder = jsonConverterBuilder();

        addModule(new ChangeTypeAdaptersModule(getContainer()));
        addModule(new CommitTypeAdaptersModule(getContainer()));

        if (new RequiredMongoSupportPredicate().test(repository)) {
            jsonConverterBuilder.registerNativeGsonDeserializer(Long.class, new MongoLong64JsonDeserializer());
        }

        jsonConverterBuilder.registerJsonTypeAdapters(getComponents(JsonTypeAdapter.class));
        jsonConverterBuilder.registerNativeGsonDeserializer(Diff.class, new DiffTypeDeserializer());
        JsonConverter jsonConverter = jsonConverterBuilder.build();
        addComponent(jsonConverter);

        return Lists.transform(jsonConverterBuilder.getBuiltInValueTypes(), c -> new ValueType(c));
    }

    private void bootDateTimeProvider() {
        if (dateProvider == null) {
            dateProvider = new DefaultDateProvider();
        }
        addComponent(dateProvider);
    }

    private void bootRepository(){
        if (repository == null){
            logger.info("using fake InMemoryRepository, register actual Repository implementation via ItauAuditableBuilder.registerItauAuditableRepository()");
            repository = new InMemoryRepository();
        }

        repository.setJsonConverter( getContainerComponent(JsonConverter.class));

        if (repository instanceof ConfigurationAware){
            ((ConfigurationAware) repository).setConfiguration(coreConfiguration());
        }

        bindComponent(ItauAuditableRepository.class, repository);

        //ItauAuditableExtendedRepository can be created after users calls ItauAuditableBuilder.registerItauAuditableRepository()
        addComponent(ItauAuditableExtendedRepository.class);
    }

    private <T extends ClientsClassDefinition> T getClassDefinition(Class<?> baseJavaClass) {
        return (T)clientsClassDefinitions.get(baseJavaClass);
    }
}
