package br.com.zup.itau.auditable.repository.jql;

import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

/**
 * @author bartosz.walacik
 */
abstract class Filter {
    abstract boolean matches(GlobalId globalId);
}
