package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ElementValueChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ValueAdded;
import br.com.zup.itau.auditable.core.diff.changetype.container.ValueRemoved;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryAdded;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryChange;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryRemoved;
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryValueChange;

import java.util.function.Function;

/**
 * @author pawel szymczyk
 */
class MapChangesToListChangesFunction implements Function<EntryChange, ContainerElementChange> {

    @Override
    public ContainerElementChange apply(EntryChange input) {
        int index = (int)input.getKey();
        if (input instanceof EntryAdded) {
            return new ValueAdded(index, ((EntryAdded) input).getValue());
        } else if (input instanceof EntryRemoved) {
            return new ValueRemoved(index, ((EntryRemoved) input).getValue());
        } else if (input instanceof EntryValueChange) {
            return new ElementValueChange(index, ((EntryValueChange) input).getLeftValue(),
                                                 ((EntryValueChange) input).getRightValue());
        }

        throw new IllegalArgumentException("Unknown change type: " + input.getClass().getSimpleName());
    }
}
