package br.com.zup.itau.auditable.core.diff.changetype.map;

import br.com.zup.itau.auditable.common.string.PrettyValuePrinter;

/**
 * Entry added to a Map
 *
 * @author bartosz walacik
 */
public class EntryAdded extends EntryAddOrRemove {

    public EntryAdded(Object key, Object value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return valuePrinter.formatWithQuotes(getKey()) + " -> " +
               valuePrinter.formatWithQuotes(getValue()) + " added";
    }
}
