package br.com.zup.itau.auditable.spring.boot;

import br.com.zup.itau.auditable.hibernate.integration.HibernateUnproxyObjectAccessHook;

public class DummySqlObjectAccessHook<T> extends HibernateUnproxyObjectAccessHook<T> {
}
