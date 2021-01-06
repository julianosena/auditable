package br.com.zup.itau.auditable.common.reflection;

/**
 * @author bartosz walacik
 */
public interface ArgumentResolver {
    Object resolve(Class argType);
}
