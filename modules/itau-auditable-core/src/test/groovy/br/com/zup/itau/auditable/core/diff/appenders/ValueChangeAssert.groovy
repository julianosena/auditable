package br.com.zup.itau.auditable.core.diff.appenders

import br.com.zup.itau.auditable.core.diff.changetype.ValueChange
import br.com.zup.itau.auditable.core.metamodel.object.ValueObjectId

class ValueChangeAssert {

    def ValueChange actual;

    private ValueChangeAssert(ValueChange actual) {
        this.actual = actual;
    }

    static ValueChangeAssert assertThat(ValueChange actual) {
        return new ValueChangeAssert(actual)
    }

    def hasValueObjectId(Class expected, def expectedOwnerId, String expectedFragment ){
        assert actual.affectedGlobalId instanceof ValueObjectId
        assert actual.affectedGlobalId.typeName == expected.name
        assert actual.affectedGlobalId.ownerId == expectedOwnerId
        assert actual.affectedGlobalId.fragment == expectedFragment
        this
    }

    def hasAffectedCdo(Object expectedAffectedCdo) {
        actual.affectedObject.get() == expectedAffectedCdo
        this
    }

    def hasPropertyName(String expectedName) {
        assert actual.propertyName == expectedName
        this
    }

    def hasLeftValue(Object expected) {
        assert actual.left == expected
        this;
    }

    def hasRightValue(Object expected) {
        assert actual.right == expected
        return this;
    }

    def haveLeftValueNull() {
        hasLeftValue(null)
    }
}