package org.javers.core;

public class AuditableContextHolder {

    private static final ThreadLocal<AuditableContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static ThreadLocal<AuditableContext> getContextHolder() {
        return CONTEXT_THREAD_LOCAL;
    }

    public static AuditableContext getContext() {

        AuditableContext auditableContext = CONTEXT_THREAD_LOCAL.get();

        if (auditableContext == null) {
            auditableContext = new AuditableContext();
            CONTEXT_THREAD_LOCAL.set(auditableContext);
        }
        return auditableContext;
    }

    public static void resetContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }

    public static boolean isEmpty() {
        return CONTEXT_THREAD_LOCAL.get() == null;
    }
}
