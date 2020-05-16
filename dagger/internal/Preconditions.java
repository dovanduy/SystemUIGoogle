// 
// Decompiled by Procyon v0.5.36
// 

package dagger.internal;

public final class Preconditions
{
    public static <T> T checkNotNull(final T t) {
        if (t != null) {
            return t;
        }
        throw null;
    }
    
    public static <T> T checkNotNull(final T t, final String s) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(s);
    }
}
