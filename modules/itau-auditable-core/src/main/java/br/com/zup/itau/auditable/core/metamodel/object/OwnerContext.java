package br.com.zup.itau.auditable.core.metamodel.object;

/**
 * @author bartosz walacik
 */
public interface OwnerContext {

    GlobalId getOwnerId();

    String getPath();

    boolean requiresObjectHasher();
}
