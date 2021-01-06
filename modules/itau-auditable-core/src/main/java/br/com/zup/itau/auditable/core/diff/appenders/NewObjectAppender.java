package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.diff.GraphPair;
import br.com.zup.itau.auditable.core.diff.changetype.NewObject;

import java.util.Set;
import java.util.stream.Collectors;

class NewObjectAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return (Set)graphPair.getOnlyOnRight().stream()
                .map(input -> new NewObject(input.getGlobalId(), input.wrappedCdo(), graphPair.getCommitMetadata()))
                .collect(Collectors.toSet());
    }
}
