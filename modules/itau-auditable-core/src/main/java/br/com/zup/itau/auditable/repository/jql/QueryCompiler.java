package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.core.ItauAuditableCoreConfiguration;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalIdFactory;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

class QueryCompiler {
    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;
    private final ItauAuditableCoreConfiguration itauAuditableCoreConfiguration;

    public QueryCompiler(GlobalIdFactory globalIdFactory, TypeMapper typeMapper, ItauAuditableCoreConfiguration itauAuditableCoreConfiguration) {
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
        this.itauAuditableCoreConfiguration = itauAuditableCoreConfiguration;
    }

    void compile(JqlQuery query) {
        query.compile(globalIdFactory, typeMapper, itauAuditableCoreConfiguration.getCommitIdGenerator());
    }
}
