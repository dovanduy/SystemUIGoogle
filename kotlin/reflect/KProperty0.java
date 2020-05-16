// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.reflect;

import kotlin.jvm.functions.Function0;

public interface KProperty0<R> extends KProperty<R>, Function0<R>
{
    R get();
    
    Getter<R> getGetter();
    
    public interface Getter<R> extends Object<R>, Function0<R>
    {
    }
}
