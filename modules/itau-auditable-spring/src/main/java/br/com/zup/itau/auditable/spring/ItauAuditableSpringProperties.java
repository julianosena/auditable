package br.com.zup.itau.auditable.spring;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.common.reflection.ReflectionUtil;
import br.com.zup.itau.auditable.core.ItauAuditableCoreProperties;
import br.com.zup.itau.auditable.core.graph.ObjectAccessHook;

public abstract class ItauAuditableSpringProperties extends ItauAuditableCoreProperties {
    private boolean auditableAspectEnabled = true;
    private boolean springDataAuditableRepositoryAspectEnabled = true;
    private String objectAccessHook = defaultObjectAccessHook();

    public boolean isAuditableAspectEnabled() {
        return auditableAspectEnabled;
    }

    public boolean isSpringDataAuditableRepositoryAspectEnabled() {
        return springDataAuditableRepositoryAspectEnabled;
    }

    public void setAuditableAspectEnabled(boolean auditableAspectEnabled) {
        this.auditableAspectEnabled = auditableAspectEnabled;
    }

    public void setSpringDataAuditableRepositoryAspectEnabled(boolean springDataAuditableRepositoryAspectEnabled) {
        this.springDataAuditableRepositoryAspectEnabled = springDataAuditableRepositoryAspectEnabled;
    }

    public String getObjectAccessHook() {
        return objectAccessHook;
    }

    public void setObjectAccessHook(String objectAccessHook) {
        this.objectAccessHook = objectAccessHook;
    }

    protected abstract String defaultObjectAccessHook();

    public ObjectAccessHook createObjectAccessHookInstance() {
        Class<?> clazz = ReflectionUtil.classForName(objectAccessHook);
        if (!ObjectAccessHook.class.isAssignableFrom(clazz)) {
            throw new ItauAuditableException(ItauAuditableExceptionCode.CLASS_IS_NOT_INSTANCE_OF, objectAccessHook, ObjectAccessHook.class.getName());
        }
        return (ObjectAccessHook)ReflectionUtil.newInstance(clazz);
    }
}


