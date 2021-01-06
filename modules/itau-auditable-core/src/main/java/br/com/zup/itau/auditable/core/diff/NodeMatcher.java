package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.graph.ObjectNode;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

import java.util.*;

/**
 * @author bartosz walacik
 */
class NodeMatcher {
    /**
     * matching based on {@link br.com.zup.itau.auditable.core.metamodel.object.GlobalId}
     */
    public List<NodePair> match(GraphPair graphPair) {
        Validate.argumentIsNotNull(graphPair);

        List<NodePair> pairs = new ArrayList<>();
        Map<GlobalId, ObjectNode> rightMap = asMap(graphPair.getRightNodeSet());

        for (ObjectNode left : graphPair.getLeftNodeSet()) {
            GlobalId key = left.getGlobalId();
            if (rightMap.containsKey(key)) {
                pairs.add(new RealNodePair(left, rightMap.get(key), graphPair.getCommitMetadata()));
            }
        }

        return pairs;
    }

    private Map<GlobalId, ObjectNode> asMap(Set<ObjectNode> nodes) {
        Map<GlobalId, ObjectNode> map = new HashMap<>();

        for (ObjectNode node : nodes) {
            map.put(node.getGlobalId(),node);
        }

        return map;
    }
}
