package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.diff.GraphPair;
import br.com.zup.itau.auditable.core.diff.changetype.ObjectRemoved;

import java.util.Set;
import java.util.stream.Collectors;

class ObjectRemovedAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return (Set)graphPair.getOnlyOnLeft().stream()
                .map(input -> new ObjectRemoved(input.getGlobalId(), input.wrappedCdo(), graphPair.getCommitMetadata()))
                .collect(Collectors.toSet());
    }
}
