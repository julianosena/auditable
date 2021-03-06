package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.diff.appenders.NodeChangeAppender;
import br.com.zup.itau.auditable.core.diff.appenders.PropertyChangeAppender;
import br.com.zup.itau.auditable.core.diff.changetype.ObjectRemoved;
import br.com.zup.itau.auditable.core.graph.LiveGraphFactory;
import br.com.zup.itau.auditable.core.graph.ObjectGraph;
import br.com.zup.itau.auditable.core.graph.ObjectNode;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;
import br.com.zup.itau.auditable.core.metamodel.type.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Maciej Zasada
 * @author Bartosz Walacik
 */
public class DiffFactory {

    private final NodeMatcher nodeMatcher = new NodeMatcher();
    private final TypeMapper typeMapper;
    private final List<NodeChangeAppender> nodeChangeAppenders;
    private final List<PropertyChangeAppender> propertyChangeAppender;
    private final LiveGraphFactory graphFactory;
    private final ItauAuditableCoreConfiguration itauAuditableCoreConfiguration;

    public DiffFactory(TypeMapper typeMapper, List<NodeChangeAppender> nodeChangeAppenders, List<PropertyChangeAppender> propertyChangeAppender, LiveGraphFactory graphFactory, ItauAuditableCoreConfiguration itauAuditableCoreConfiguration) {
        this.typeMapper = typeMapper;
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.graphFactory = graphFactory;
        this.itauAuditableCoreConfiguration = itauAuditableCoreConfiguration;

        //sort by priority
        Collections.sort(propertyChangeAppender, (p1, p2) -> ((Integer)p1.priority()).compareTo(p2.priority()));
        this.propertyChangeAppender = propertyChangeAppender;
    }

    /**
     * @see ItauAuditable#compare(Object, Object)
     */
    public Diff compare(Object oldVersion, Object currentVersion) {
        return create(buildGraph(oldVersion), buildGraph(currentVersion), Optional.<CommitMetadata>empty());
    }

    public <T> Diff compareCollections(Collection<T> oldVersion, Collection<T> currentVersion, Class<T> itemClass) {
        return create(buildGraph(oldVersion, itemClass), buildGraph(currentVersion, itemClass), Optional.<CommitMetadata>empty());
    }

    private ObjectGraph buildGraph(Collection handle, Class itemClass) {
        return graphFactory.createLiveGraph(handle, itemClass);
    }

    public Diff create(ObjectGraph leftGraph, ObjectGraph rightGraph, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(leftGraph, rightGraph);

        GraphPair graphPair = new GraphPair(leftGraph, rightGraph, commitMetadata);
        return createAndAppendChanges(graphPair);
    }

    public Diff singleTerminal(GlobalId removedId, CommitMetadata commitMetadata){
        Validate.argumentsAreNotNull(removedId, commitMetadata);

        DiffBuilder diff = new DiffBuilder(itauAuditableCoreConfiguration.getPrettyValuePrinter());
        diff.addChange(new ObjectRemoved(removedId, empty(), of(commitMetadata)));

        return diff.build();
    }

    /**
     * @param newDomainObject object or handle to object graph, nullable
     */
    public Diff initial(Object newDomainObject) {
        ObjectGraph currentGraph = buildGraph(newDomainObject);

        GraphPair graphPair = new GraphPair(currentGraph);
        return createAndAppendChanges(graphPair);
    }

    private ObjectGraph buildGraph(Object handle) {
        if (handle == null) {
            return new EmptyGraph();
        }

        ItauAuditableType jType = typeMapper.getItauAuditableType(handle.getClass());
        if (jType instanceof ValueType || jType instanceof PrimitiveType){
            throw new ItauAuditableException(ItauAuditableExceptionCode.COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED,
                    jType.getClass().getSimpleName(), handle.getClass().getSimpleName());
        }
        return graphFactory.createLiveGraph(handle);
    }

    /**
     * Graph scope appender
     */
    private Diff createAndAppendChanges(GraphPair graphPair) {
        DiffBuilder diff = new DiffBuilder(itauAuditableCoreConfiguration.getPrettyValuePrinter());

        //calculate node scope diff
        for (NodeChangeAppender appender : nodeChangeAppenders) {
            diff.addChanges(appender.getChangeSet(graphPair));
        }

        //calculate snapshot of NewObjects
        if (itauAuditableCoreConfiguration.isNewObjectsSnapshot()) {
            for (ObjectNode node : graphPair.getOnlyOnRight()) {
                FakeNodePair pair = new FakeNodePair(node, graphPair.getCommitMetadata());
                appendPropertyChanges(diff, pair);
            }
        }

        //calculate property-to-property diff
        for (NodePair pair : nodeMatcher.match(graphPair)) {
            appendPropertyChanges(diff, pair);
        }

        return diff.build();
    }

    /* Node scope appender */
    private void appendPropertyChanges(DiffBuilder diff, NodePair pair) {
        List<ItauAuditableProperty> nodeProperties = pair.getProperties();
        for (ItauAuditableProperty property : nodeProperties) {

            //optimization, skip all appenders if null on both sides
            if (pair.isNullOnBothSides(property)) {
                continue;
            }

            ItauAuditableType itauAuditableType = property.getType();

            appendChanges(diff, pair, property, itauAuditableType);
        }
    }

    private void appendChanges(DiffBuilder diff, NodePair pair, ItauAuditableProperty property, ItauAuditableType itauAuditableType) {
        for (PropertyChangeAppender appender : propertyChangeAppender) {
            if (! appender.supports(itauAuditableType)){
                continue;
            }

            final Change change = appender.calculateChanges(pair, property);
            if (change != null) {
                diff.addChange(change, pair.getRight().wrappedCdo());
            }
            break;
        }
    }
}
