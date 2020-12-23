package org.javers.core;

public class JaversMongoContextHolder {

    private static final ThreadLocal<JaversMongoContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static ThreadLocal<JaversMongoContext> getContextHolder() {
        return CONTEXT_THREAD_LOCAL;
    }

    public static JaversMongoContext getContext() {

        JaversMongoContext javersMongoContext = CONTEXT_THREAD_LOCAL.get();

        if (javersMongoContext == null) {
            javersMongoContext = new JaversMongoContext();
            CONTEXT_THREAD_LOCAL.set(javersMongoContext);
        }
        return javersMongoContext;
    }

    public static void resetContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }

    public static boolean isEmpty() {
        return CONTEXT_THREAD_LOCAL.get() == null;
    }
}
