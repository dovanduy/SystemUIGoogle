// 
// Decompiled by Procyon v0.5.36
// 

package androidx.dynamicanimation.animation;

public abstract class FloatPropertyCompat<T>
{
    public FloatPropertyCompat(final String s) {
    }
    
    public abstract float getValue(final T p0);
    
    public abstract void setValue(final T p0, final float p1);
}
