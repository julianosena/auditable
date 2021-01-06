package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.PropertyChange;
import br.com.zup.itau.auditable.core.diff.changetype.ReferenceChange;
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.UNSUPPORTED_OPTIONAL_CONTENT_TYPE;

/**
 * @author bartosz.walacik
 */
public class OptionalChangeAppender implements PropertyChangeAppender<PropertyChange> {

    private final TypeMapper typeMapper;

    public OptionalChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType instanceof OptionalType;
    }

    @Override
    public PropertyChange calculateChanges(NodePair pair, ItauAuditableProperty property) {
        OptionalType optionalType = ((ItauAuditableProperty) property).getType();
        ItauAuditableType contentType = typeMapper.getItauAuditableType(optionalType.getItemType());

        Optional leftOptional =  normalize((Optional) pair.getLeftDehydratedPropertyValueAndSanitize(property));
        Optional rightOptional = normalize((Optional) pair.getRightDehydratedPropertyValueAndSanitize(property));

        if (Objects.equals(leftOptional, rightOptional)) {
            return null;
        }
        if (contentType instanceof ManagedType) {
            return new ReferenceChange(pair.createPropertyChangeMetadata(property),
                    first(pair.getLeftReferences(property)),
                    first(pair.getRightReferences(property)),
                    flat(pair.getLeftPropertyValue(property)),
                    flat(pair.getRightPropertyValue(property)));
        }
        if (contentType instanceof PrimitiveOrValueType) {
            return new ValueChange(pair.createPropertyChangeMetadata(property), leftOptional, rightOptional);
        }

        throw new ItauAuditableException(UNSUPPORTED_OPTIONAL_CONTENT_TYPE, contentType);
    }

    private GlobalId first(List<GlobalId> refs){
        if (refs != null && refs.size() > 0) {
            return refs.get(0);
        }
        return null;
    }

    private Object flat(Object optional){
        if (optional instanceof Optional) {
            return ((Optional) optional).orElse(null);
        }
        return optional;
    }

    private Optional normalize(Optional optional) {
        if (optional == null) {
            return Optional.empty();
        }
        return optional;
    }
}
