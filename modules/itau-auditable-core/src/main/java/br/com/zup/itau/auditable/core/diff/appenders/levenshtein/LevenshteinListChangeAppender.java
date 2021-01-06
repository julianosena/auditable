package br.com.zup.itau.auditable.core.diff.appenders.levenshtein;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.diff.EqualsFunction;
import br.com.zup.itau.auditable.core.diff.NodePair;
import br.com.zup.itau.auditable.core.diff.appenders.CorePropertyChangeAppender;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.core.metamodel.type.ListType;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author kornel kielczewski
 */
public class LevenshteinListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final TypeMapper typeMapper;

    LevenshteinListChangeAppender(TypeMapper typeMapper) {
        Validate.argumentsAreNotNull(typeMapper);
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(ItauAuditableType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ItauAuditableProperty property) {
        ItauAuditableType itemType = typeMapper.getContainerItemType(property);

        final List leftList =  (List) leftValue;
        final List rightList = (List) rightValue;

        EqualsFunction equalsFunction = itemType::equals;
        Backtrack backtrack = new Backtrack(equalsFunction);
        StepsToChanges stepsToChanges = new StepsToChanges(equalsFunction);

        final BacktrackSteps[][] steps = backtrack.evaluateSteps(leftList, rightList);
        final List<ContainerElementChange> changes = stepsToChanges.convert(steps, leftList, rightList);

        ListChange result = getListChange(pair, property, changes);
        if (result != null) {
            renderNotParametrizedWarningIfNeeded(itemType.getBaseJavaType(), "item", "List", property);
        }
        return result;
    }

    private ListChange getListChange(NodePair pair, ItauAuditableProperty property, List<ContainerElementChange> changes) {
        final ListChange result;

        if (changes.size() == 0) {
            result = null;
        } else {
            result = new ListChange(pair.createPropertyChangeMetadata(property), changes);
        }
        return result;
    }
}
