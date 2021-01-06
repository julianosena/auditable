package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange;
import br.com.zup.itau.auditable.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
class ValueChangeAppender implements PropertyChangeAppender<ValueChange> {

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return  propertyType instanceof PrimitiveOrValueType || propertyType instanceof TokenType;
    }

    /**
     * @param property supported property (of PrimitiveType or ValueObjectType)
     */
    @Override
    public ValueChange calculateChanges(NodePair pair, ItauAuditableProperty property) {

        Object leftValue = pair.getLeftPropertyValue(property);
        Object rightValue = pair.getRightPropertyValue(property);

        //special treatment for EmbeddedId - could be ValueObjects without good equals() implementation
        if (isIdProperty(pair, property)) {
            //For idProperty, only initial change is possible (from null to value).
            //If we have values on both sides, we know that they have the same String representation
            if (leftValue != null && rightValue != null) {
                return null;
            }
        } else {
            if (property.getType().equals(leftValue, rightValue)) {
                return null;
            }
        }

        return new ValueChange(pair.createPropertyChangeMetadata(property), leftValue, rightValue);
    }

    private boolean isIdProperty(NodePair nodePair, ItauAuditableProperty property){
        ManagedType managedType = nodePair.getManagedType();

        if (managedType instanceof EntityType) {
            return ((EntityType)managedType).isIdProperty(property);
        }
        return false;
    }
}
