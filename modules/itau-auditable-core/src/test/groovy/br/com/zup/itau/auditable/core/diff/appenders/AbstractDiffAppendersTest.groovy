package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.AbstractDiffTest
import br.com.zup.itau.auditable.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffAppendersTest extends AbstractDiffTest {

    SimpleListChangeAppender listChangeAppender() {
        new SimpleListChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    MapChangeAppender mapChangeAppender() {
        new MapChangeAppender(javers.typeMapper)
    }

    OptionalChangeAppender optionalChangeAppender(){
        new OptionalChangeAppender(javers.typeMapper)
    }

    LevenshteinListChangeAppender levenshteinListChangeAppender() {
        new LevenshteinListChangeAppender(javers.typeMapper)
    }

    ArrayChangeAppender arrayChangeAppender() {
        new ArrayChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    SetChangeAppender setChangeAppender() {
        new SetChangeAppender(javers.typeMapper)
    }
}
