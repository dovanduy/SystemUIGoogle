// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.eventbus;

import java.lang.reflect.InvocationTargetException;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;

class Subscriber
{
    private final Method method;
    final Object target;
    
    @Override
    public final boolean equals(final Object o) {
        final boolean b = o instanceof Subscriber;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final Subscriber subscriber = (Subscriber)o;
            b3 = b2;
            if (this.target == subscriber.target) {
                b3 = b2;
                if (this.method.equals(subscriber.method)) {
                    b3 = true;
                }
            }
        }
        return b3;
    }
    
    @Override
    public final int hashCode() {
        return (this.method.hashCode() + 31) * 31 + System.identityHashCode(this.target);
    }
    
    void invokeSubscriberMethod(final Object o) throws InvocationTargetException {
        try {
            final Method method = this.method;
            final Object target = this.target;
            Preconditions.checkNotNull(o);
            method.invoke(target, o);
        }
        catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof Error) {
                throw (Error)ex.getCause();
            }
            throw ex;
        }
        catch (IllegalAccessException cause) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Method became inaccessible: ");
            sb.append(o);
            throw new Error(sb.toString(), cause);
        }
        catch (IllegalArgumentException cause2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Method rejected target/argument: ");
            sb2.append(o);
            throw new Error(sb2.toString(), cause2);
        }
    }
    
    static final class SynchronizedSubscriber extends Subscriber
    {
        @Override
        void invokeSubscriberMethod(final Object o) throws InvocationTargetException {
            synchronized (this) {
                super.invokeSubscriberMethod(o);
            }
        }
    }
}
