package br.com.zup.itau.auditable.repository.api;

import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;

public interface ConfigurationAware {

    void setConfiguration(ItauAuditableCoreConfiguration coreConfiguration);
}
