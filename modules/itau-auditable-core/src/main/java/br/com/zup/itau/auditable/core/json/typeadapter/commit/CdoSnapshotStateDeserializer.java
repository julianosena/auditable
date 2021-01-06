package br.com.zup.itau.auditable.core.json.typeadapter.commit;

import com.google.gson.*;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshotState;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshotStateBuilder;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshotStateBuilder.cdoSnapshotState;

/**
 * CdoSnapshotState can't be created by standard {@link CdoSnapshotStateTypeAdapter}
 * due to required managedType
 *
 * @author bartosz walacik
 */
class CdoSnapshotStateDeserializer {
    private static final Logger logger = LoggerFactory.getLogger(CdoSnapshotStateDeserializer.class);

    private final TypeMapper typeMapper;
    private final JsonDeserializationContext context;


    public CdoSnapshotStateDeserializer(TypeMapper typeMapper, JsonDeserializationContext context) {
        this.typeMapper = typeMapper;
        this.context = context;
    }

    public CdoSnapshotState deserialize(JsonElement stateElement, ManagedType managedType){
        Validate.argumentsAreNotNull(stateElement, managedType, context);
        JsonObject stateObject = (JsonObject) stateElement;

        CdoSnapshotStateBuilder builder = cdoSnapshotState();

        stateObject.entrySet().stream().forEach(e -> {
            builder.withPropertyValue(e.getKey(),
                    decodePropertyValue(e.getValue(), context, managedType.findProperty(e.getKey())));

        });

        return builder.build();
    }

    private Object decodePropertyValue(JsonElement propertyElement, JsonDeserializationContext context, Optional<ItauAuditableProperty> itauAuditablePropertyOptional) {

        if (!itauAuditablePropertyOptional.isPresent()) {
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

        ItauAuditableProperty itauAuditableProperty = itauAuditablePropertyOptional.get();
        ItauAuditableType expectedItauAuditableType = itauAuditableProperty.getType();

        // if primitives on both sides, they should match, otherwise, expectedType is ignored
        if (unmatchedPrimitivesOnBothSides(expectedItauAuditableType, propertyElement)) {
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

        // if collections of primitives on both sides, item types should match,
        // otherwise, item type from expectedType is ignored
        if (shouldUseBareContainerClass(expectedItauAuditableType, propertyElement)) {
            return context.deserialize(propertyElement, ((ContainerType) expectedItauAuditableType).getBaseJavaClass());
        }

        try {
            Type expectedJavaType = typeMapper.getDehydratedType(itauAuditableProperty.getGenericType());
            if (itauAuditableProperty.getType() instanceof TokenType) {
                return deserializeValueWithTypeGuessing(propertyElement, context);
            } else {
                return context.deserialize(propertyElement, expectedJavaType);
            }
        } catch (JsonSyntaxException | DateTimeParseException e) {
            logger.info("Can't deserialize type-safely the Snapshot property: "+ itauAuditableProperty +
                        ". JSON value: "+propertyElement +
                        ". Looks like a type mismatch after refactoring of " + itauAuditableProperty.getDeclaringClass().getSimpleName()+
                        " class.");
            // when users's class is refactored, persisted property value
            // can have different type than expected
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }
    }

    private Object deserializeValueWithTypeGuessing(JsonElement propertyElement, JsonDeserializationContext context) {
        if (propertyElement.isJsonPrimitive()){
            JsonPrimitive jsonPrimitive = (JsonPrimitive) propertyElement;

            if (jsonPrimitive.isString()) {
                return jsonPrimitive.getAsString();
            }
            if (jsonPrimitive.isNumber()) {
                if (jsonPrimitive.getAsString().equals(jsonPrimitive.getAsInt()+"")) {
                    return jsonPrimitive.getAsInt();
                }
                if (jsonPrimitive.getAsString().equals(jsonPrimitive.getAsLong()+"")) {
                    return jsonPrimitive.getAsLong();
                }
            }
        }
        return context.deserialize(propertyElement, Object.class);
    }

    private boolean unmatchedPrimitivesOnBothSides(ItauAuditableType expectedItauAuditableType, JsonElement propertyElement) {
        if (ifPrimitivesOnBothSides(expectedItauAuditableType, propertyElement)) {
            return !matches((PrimitiveOrValueType)expectedItauAuditableType, (JsonPrimitive) propertyElement);
        }
        return false;
    }

    private boolean ifPrimitivesOnBothSides(ItauAuditableType expectedItauAuditableType, JsonElement propertyElement) {
        return expectedItauAuditableType instanceof PrimitiveOrValueType &&
                ((PrimitiveOrValueType) expectedItauAuditableType).isJsonPrimitive() &&
                propertyElement instanceof JsonPrimitive;
    }

    private boolean shouldUseBareContainerClass(ItauAuditableType expectedItauAuditableType, JsonElement propertyElement){
        if(!(expectedItauAuditableType instanceof ContainerType) || !(propertyElement instanceof JsonArray)){
            return false;
        }

        ContainerType expectedContainerType = (ContainerType) expectedItauAuditableType;
        JsonArray propertyArray = (JsonArray) propertyElement;

        if (propertyArray.size() == 0) {
            return false;
        }

        JsonElement firstItem = propertyArray.get(0);
        ItauAuditableType itemType = typeMapper.getItauAuditableType(expectedContainerType.getItemType());
        return unmatchedPrimitivesOnBothSides(itemType, firstItem);
    }

    private boolean matches(PrimitiveOrValueType itauAuditablePrimitive, JsonPrimitive jsonPrimitive) {
        return (jsonPrimitive.isNumber() && itauAuditablePrimitive.isNumber()) ||
               (jsonPrimitive.isString() && itauAuditablePrimitive.isStringy()) ||
               (jsonPrimitive.isBoolean() && itauAuditablePrimitive.isBoolean());

    }

    private Object decodePropertyValueUsingJsonType(JsonElement propertyElement, JsonDeserializationContext context) {
        if (GlobalIdTypeAdapter.looksLikeGlobalId(propertyElement)) {
            return context.deserialize(propertyElement, GlobalId.class);
        }
        return context.deserialize(propertyElement, Object.class);
    }
}