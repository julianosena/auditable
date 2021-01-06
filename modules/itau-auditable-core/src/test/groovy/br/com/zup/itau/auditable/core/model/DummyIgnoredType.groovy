package br.com.zup.itau.auditable.core.model

import br.com.zup.itau.auditable.core.metamodel.annotation.DiffIgnore

/**
 * @author bartosz.walacik
 */
@DiffIgnore
class DummyIgnoredType {
    int value
}
