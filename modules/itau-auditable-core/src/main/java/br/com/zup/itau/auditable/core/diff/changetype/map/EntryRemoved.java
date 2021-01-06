package br.com.zup.itau.auditable.core.diff.changetype.map;

import br.com.zup.itau.auditable.common.string.PrettyValuePrinter;

/**
 * Entry removed from a Map
 *
 * @author bartosz walacik
 */
public class EntryRemoved extends EntryAddOrRemove {

    public EntryRemoved(Object key, Object value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return valuePrinter.formatWithQuotes(getKey()) + " -> " +
                valuePrinter.formatWithQuotes(getValue()) + " removed";
    }
}
