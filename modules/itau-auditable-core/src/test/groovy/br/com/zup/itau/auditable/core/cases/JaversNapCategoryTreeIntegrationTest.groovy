package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.diff.Diff
import br.com.zup.itau.auditable.core.diff.changetype.NewObject
import br.com.zup.itau.auditable.core.diff.changetype.ObjectRemoved
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange
import br.com.zup.itau.auditable.test.builder.CategoryTestBuilder
import spock.lang.Specification

import static br.com.zup.itau.auditable.core.ItauAuditableBuilder.itauAuditable
import static br.com.zup.itau.auditable.core.diff.DiffAssert.assertThat

/**
 * <b>Use case</b> of our client multiprogram.pl, comparing large CategoryC Trees
 *
 * @author bartosz walacik
 */
class ItauAuditableNapCategoryTreeIntegrationTest extends Specification {

    def "should check all nodes when calculating property changes"(){
        given:
        def cat1 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "name").build()
        def cat2 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "newName").build()
        ItauAuditable itauAuditable = itauAuditable().build()

        when:
        Diff diff = itauAuditable.compare(cat1, cat2)

        then:
        assertThat(diff).hasChanges(40).hasAllOfType(ValueChange)
    }

    def "should manage empty diff on big graphs"() {
        given:
        def cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        def cat2 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        ItauAuditable itauAuditable = itauAuditable().build()

        when:
        Diff diff = itauAuditable.compare(cat1, cat2)

        then:
        !diff.changes
    }

    def "should manage full diff on big graphs"() {
        given:
        def cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        def cat2 = CategoryTestBuilder.category(-1).deepWithChildNumber(5,5).build()
        ItauAuditable itauAuditable = itauAuditable().build()

        when:
        Diff diff = itauAuditable.compare(cat1, cat2)

        then:
        diff.getChangesByType(NewObject).size() == 3906
        diff.getChangesByType(ObjectRemoved).size() == 3906
        diff.getChanges().size() == 3906 * 2
    }
}
