package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.core.graph.ObjectGraph;

import java.util.Collections;

class EmptyGraph extends ObjectGraph {
    EmptyGraph() {
        super(Collections.emptySet());
    }
}

