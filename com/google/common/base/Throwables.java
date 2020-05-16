// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Throwables
{
    static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
    private static final Object jla;
    
    static {
        if ((jla = getJLA()) != null) {
            getGetMethod();
        }
        if (Throwables.jla != null) {
            getSizeMethod();
        }
    }
    
    private static Method getGetMethod() {
        return getJlaMethod("getStackTraceElement", Throwable.class, Integer.TYPE);
    }
    
    private static Object getJLA() {
        Object invoke = null;
        try {
            invoke = Class.forName("sun.misc.SharedSecrets", false, null).getMethod("getJavaLangAccess", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            return invoke;
        }
        catch (ThreadDeath invoke) {
            throw invoke;
        }
        finally {
            return invoke;
        }
    }
    
    private static Method getJlaMethod(final String name, final Class<?>... parameterTypes) throws ThreadDeath {
        try {
            return Class.forName("sun.misc.JavaLangAccess", false, null).getMethod(name, parameterTypes);
        }
        catch (ThreadDeath threadDeath) {
            throw threadDeath;
        }
        finally {
            return null;
        }
    }
    
    private static Method getSizeMethod() {
        try {
            final Method jlaMethod = getJlaMethod("getStackTraceDepth", Throwable.class);
            if (jlaMethod == null) {
                return null;
            }
            jlaMethod.invoke(getJLA(), new Throwable());
            return jlaMethod;
        }
        catch (UnsupportedOperationException | IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
    }
    
    public static void throwIfUnchecked(final Throwable t) {
        Preconditions.checkNotNull(t);
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (!(t instanceof Error)) {
            return;
        }
        throw (Error)t;
    }
}
