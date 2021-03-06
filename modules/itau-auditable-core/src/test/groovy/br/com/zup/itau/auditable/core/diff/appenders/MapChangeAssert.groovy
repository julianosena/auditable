package br.com.zup.itau.auditable.core.diff.appenders

import br.com.zup.itau.auditable.core.diff.changetype.map.EntryAdded
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryRemoved
import br.com.zup.itau.auditable.core.diff.changetype.map.EntryValueChange
import br.com.zup.itau.auditable.core.diff.changetype.map.MapChange

/**
 * @author bartosz walacik
 */
public class MapChangeAssert {
    MapChange actual

    static assertThat = { MapChange actual ->
        new MapChangeAssert(actual: actual)
    }

    MapChangeAssert hasSize(int expected) {
        assert actual.changes.size() == expected
        this
    }

    MapChangeAssert hasEntryValueChange(def key, def expectedLeftVal, def expectedRightVal) {
        assert actual.changes.find{it.key == key && it instanceof EntryValueChange && it.leftValue == expectedLeftVal && it.rightValue == expectedRightVal}
        this
    }

    MapChangeAssert hasEntryAdded(def expectedKey, def expectedVal) {
        assert actual.changes.find{it.key == expectedKey && it instanceof EntryAdded && it.value == expectedVal}
        this
    }

    MapChangeAssert hasEntryRemoved(def expectedKey, def expectedVal) {
        assert actual.changes.find{it.key == expectedKey && it instanceof EntryRemoved && it.value == expectedVal}
        this
    }
}
