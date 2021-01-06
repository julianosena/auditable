package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.common.string.ToStringBuilder;

class ShadowScopeDefinition {
    private final ShadowScope shadowScope;
    private final int maxGapsToFill;

    ShadowScopeDefinition(ShadowScope shadowScope, int maxGapsToFill) {
        this.shadowScope = shadowScope;
        this.maxGapsToFill = maxGapsToFill;
    }

    ShadowScope getScope() {
        return shadowScope;
    }

    int getMaxGapsToFill() {
        return maxGapsToFill;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "shadowScope", shadowScope,
                "maxGapsToFill", maxGapsToFill);
    }
}
