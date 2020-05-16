// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import kotlin.reflect.KCallable;
import kotlin.reflect.KProperty0;

public abstract class PropertyReference0 extends PropertyReference implements KProperty0
{
    @Override
    protected KCallable computeReflected() {
        Reflection.property0(this);
        return this;
    }
    
    @Override
    public Getter getGetter() {
        return (Getter)((KProperty0)this.getReflected()).getGetter();
    }
    
    @Override
    public Object invoke() {
        return this.get();
    }
}
