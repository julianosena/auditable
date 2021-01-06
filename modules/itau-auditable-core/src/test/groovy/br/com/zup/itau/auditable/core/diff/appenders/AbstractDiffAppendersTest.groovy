package br.com.zup.itau.auditable.core.diff.appenders;

import br.com.zup.itau.auditable.core.diff.AbstractDiffTest
import br.com.zup.itau.auditable.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffAppendersTest extends AbstractDiffTest {

    SimpleListChangeAppender listChangeAppender() {
        new SimpleListChangeAppender(mapChangeAppender(), itauAuditable.typeMapper)
    }

    MapChangeAppender mapChangeAppender() {
        new MapChangeAppender(itauAuditable.typeMapper)
    }

    OptionalChangeAppender optionalChangeAppender(){
        new OptionalChangeAppender(itauAuditable.typeMapper)
    }

    LevenshteinListChangeAppender levenshteinListChangeAppender() {
        new LevenshteinListChangeAppender(itauAuditable.typeMapper)
    }

    ArrayChangeAppender arrayChangeAppender() {
        new ArrayChangeAppender(mapChangeAppender(), itauAuditable.typeMapper)
    }

    SetChangeAppender setChangeAppender() {
        new SetChangeAppender(itauAuditable.typeMapper)
    }
}
