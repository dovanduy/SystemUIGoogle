// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.reflect;

import kotlin.jvm.functions.Function1;

public interface KProperty1<T, R> extends KProperty<R>, Function1<T, R>
{
    R get(final T p0);
    
    Getter<T, R> getGetter();
    
    public interface Getter<T, R> extends Object<R>, Function1<T, R>
    {
    }
}
