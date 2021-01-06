package br.com.zup.itau.auditable.repository.sql.session;

import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.repository.sql.DialectName;

import static br.com.zup.itau.auditable.repository.sql.session.Parameter.longParam;

abstract class Dialect {
    private final DialectName dialectName;

    Dialect(DialectName dialectName) {
        Validate.argumentIsNotNull(dialectName);
        this.dialectName = dialectName;
    }

    boolean supportsSequences() {
        return getKeyGeneratorDefinition() instanceof KeyGeneratorDefinition.SequenceDefinition;
    }

    abstract <T extends KeyGeneratorDefinition> T getKeyGeneratorDefinition();

    DialectName getName() {
        return dialectName;
    }

    void limit(SelectBuilder query, long limit, long offset) {
        query.append("LIMIT ? OFFSET ?", longParam(limit), longParam(offset));
    }
}
