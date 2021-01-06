package br.com.zup.itau.auditable.core.model

import br.com.zup.itau.auditable.core.metamodel.annotation.IgnoreDeclaredProperties

/**
 * Created by Ian Agius
 */
@IgnoreDeclaredProperties
class DummyIgnoredPropertiesType extends DummyUser{
    private int propertyThatShouldBeIgnored
    private int anotherIgnored

    int getPropertyThatShouldBeIgnored() {
        return propertyThatShouldBeIgnored
    }

    int getAnotherIgnored() {
        return anotherIgnored
    }
}
