package br.com.zup.itau.auditable.core.metamodel.clazz;

import br.com.zup.itau.auditable.common.collections.Arrays;
import br.com.zup.itau.auditable.common.validation.Validate;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for {@link EntityDefinition},
 * allows to set all optional attributes:
 * Id-properties, ignoredProperties and typeAlias, for example:
 * <pre>
 * EntityDefinitionBuilder.entityDefinition(Person.class)
 *    .withIdPropertyName("firstName")
 *    .withIdPropertyName("lastName")
 *    .withIgnoredProperties(ignoredProperties)
 *    .withTypeName("typeName")
 *    .build();
 *</pre>
 *
 * @since 1.4
 * @author bartosz.walacik
 */
public class EntityDefinitionBuilder extends ClientsClassDefinitionBuilder<EntityDefinitionBuilder>{
    private List<String> idPropertyNames = new ArrayList<>();
    private boolean shallowReference;

    EntityDefinitionBuilder(Class<?> entity) {
        super(entity);
    }

    public static EntityDefinitionBuilder entityDefinition(Class<?> entity) {
        return new EntityDefinitionBuilder(entity);
    }

    public EntityDefinitionBuilder withIdPropertyName(String idPropertyName) {
        Validate.argumentIsNotNull(idPropertyName);
        if (!idPropertyNames.contains(idPropertyName)) {
            idPropertyNames.add(idPropertyName);
        }
        return this;
    }

    public EntityDefinitionBuilder withIdPropertyNames(String... idPropertyNames) {
        Validate.argumentIsNotNull(idPropertyNames);
        return withIdPropertyNames((List) Arrays.asList(idPropertyNames));
    }

    public EntityDefinitionBuilder withIdPropertyNames(List<String> idPropertyNames) {
        Validate.argumentIsNotNull(idPropertyNames);
        idPropertyNames.forEach(this::withIdPropertyName);
        return this;
    }

    public EntityDefinitionBuilder withShallowReference(){
        this.shallowReference = true;
        return this;
    }

    @Override
    public EntityDefinition build() {
        return new EntityDefinition(this);
    }

    List<String> getIdPropertyNames() {
        return idPropertyNames;
    }

    boolean isShallowReference() {
        return shallowReference;
    }
}
