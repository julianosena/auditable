package br.com.zup.itau.auditable.core.diff;

import br.com.zup.itau.auditable.core.diff.appenders.ListAsSetChangeAppender;
import br.com.zup.itau.auditable.core.diff.appenders.PropertyChangeAppender;
import br.com.zup.itau.auditable.core.diff.appenders.SimpleListChangeAppender;
import br.com.zup.itau.auditable.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;

public enum ListCompareAlgorithm {

    SIMPLE(SimpleListChangeAppender.class),
    LEVENSHTEIN_DISTANCE(LevenshteinListChangeAppender.class),
    AS_SET(ListAsSetChangeAppender.class);

    private final Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender;

    ListCompareAlgorithm(Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    public Class<? extends PropertyChangeAppender<ListChange>> getAppenderClass() {
        return listChangeAppender;
    }
}
