// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.util;

public interface Pools$Pool<T>
{
    T acquire();
    
    boolean release(final T p0);
}
