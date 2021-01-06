package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.diff.GraphPair;

import java.util.Set;

/**
 * Node scope change appender (NewObject & ObjectRemoved)
 */
public interface NodeChangeAppender {

   Set<Change> getChangeSet(GraphPair graphPair);

}
