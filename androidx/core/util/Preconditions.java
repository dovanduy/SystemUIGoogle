// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.util;

public final class Preconditions
{
    public static void checkArgument(final boolean b, final Object obj) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException(String.valueOf(obj));
    }
    
    public static int checkArgumentNonnegative(final int n) {
        if (n >= 0) {
            return n;
        }
        throw new IllegalArgumentException();
    }
    
    public static <T> T checkNotNull(final T t) {
        if (t != null) {
            return t;
        }
        throw null;
    }
    
    public static <T> T checkNotNull(final T t, final Object obj) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(String.valueOf(obj));
    }
}
