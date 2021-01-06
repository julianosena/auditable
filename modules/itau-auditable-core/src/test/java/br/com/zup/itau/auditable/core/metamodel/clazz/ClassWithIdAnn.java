package br.com.zup.itau.auditable.core.metamodel.clazz;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class ClassWithIdAnn {
    @Id
    private String some;
}
