package br.com.zup.itau.auditable.shadow;

import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.core.metamodel.type.EnumerableType;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ShadowBuilder {
    private final CdoSnapshot cdoSnapshot;
    private Object shadow;
    private Set<Wiring> wirings = new HashSet<>();

    ShadowBuilder(CdoSnapshot cdoSnapshot, Object shadow) {
        this.cdoSnapshot = cdoSnapshot;
        this.shadow = shadow;
    }

    void withStub(Object shadowStub) {
        this.shadow = shadowStub;
    }

    Object getShadow() {
        return shadow;
    }

    /**
     * nullable
     */
    CdoSnapshot getCdoSnapshot() {
        return cdoSnapshot;
    }

    void addReferenceWiring(ItauAuditableProperty property, ShadowBuilder targetShadow) {
        this.wirings.add(new ReferenceWiring(property, targetShadow));
    }

    void addEnumerableWiring(ItauAuditableProperty property, Object targetWithShadows) {
        this.wirings.add(new EnumerableWiring(property, targetWithShadows));
    }

    void wire() {
        wirings.forEach(Wiring::wire);
    }

    private abstract class Wiring {
        final ItauAuditableProperty property;

        Wiring(ItauAuditableProperty property) {
            this.property = property;
        }

        abstract void wire();
    }

    private class ReferenceWiring extends Wiring {
        final ShadowBuilder target;

        ReferenceWiring(ItauAuditableProperty property, ShadowBuilder targetShadow) {
            super(property);
            this.target = targetShadow;
        }

        @Override
        void wire() {
            property.set(shadow, target.shadow);
        }
    }

    private class EnumerableWiring extends Wiring {
        final Object targetWithShadows;

        EnumerableWiring(ItauAuditableProperty property, Object targetWithShadows) {
            super(property);
            this.targetWithShadows = targetWithShadows;
        }

        @Override
        void wire() {
            EnumerableType propertyType = property.getType();

            Object targetContainer = propertyType.map(targetWithShadows, (valueOrShadow) -> {
                if (valueOrShadow instanceof ShadowBuilder) {
                    //injecting reference to shadow
                    return ((ShadowBuilder) valueOrShadow).shadow;
                }
                return valueOrShadow; //vale is passed as is
            });

            property.set(shadow, targetContainer);
        }
    }
}
