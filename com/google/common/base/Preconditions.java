// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

public final class Preconditions
{
    private static String badElementIndex(final int n, final int n2, final String s) {
        if (n < 0) {
            return Strings.lenientFormat("%s (%s) must not be negative", s, n);
        }
        if (n2 >= 0) {
            return Strings.lenientFormat("%s (%s) must be less than size (%s)", s, n, n2);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("negative size: ");
        sb.append(n2);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static String badPositionIndex(final int n, final int n2, final String s) {
        if (n < 0) {
            return Strings.lenientFormat("%s (%s) must not be negative", s, n);
        }
        if (n2 >= 0) {
            return Strings.lenientFormat("%s (%s) must not be greater than size (%s)", s, n, n2);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("negative size: ");
        sb.append(n2);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static String badPositionIndexes(final int i, final int j, final int n) {
        if (i < 0 || i > n) {
            return badPositionIndex(i, n, "start index");
        }
        if (j >= 0 && j <= n) {
            return Strings.lenientFormat("end index (%s) must not be less than start index (%s)", j, i);
        }
        return badPositionIndex(j, n, "end index");
    }
    
    public static void checkArgument(final boolean b) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException();
    }
    
    public static void checkArgument(final boolean b, final Object obj) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException(String.valueOf(obj));
    }
    
    public static void checkArgument(final boolean b, final String s, final Object o) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException(Strings.lenientFormat(s, o));
    }
    
    public static void checkArgument(final boolean b, final String s, final Object o, final Object o2) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException(Strings.lenientFormat(s, o, o2));
    }
    
    @CanIgnoreReturnValue
    public static int checkElementIndex(final int n, final int n2) {
        checkElementIndex(n, n2, "index");
        return n;
    }
    
    @CanIgnoreReturnValue
    public static int checkElementIndex(final int n, final int n2, final String s) {
        if (n >= 0 && n < n2) {
            return n;
        }
        throw new IndexOutOfBoundsException(badElementIndex(n, n2, s));
    }
    
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(final T t) {
        if (t != null) {
            return t;
        }
        throw null;
    }
    
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(final T t, final Object obj) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(String.valueOf(obj));
    }
    
    @CanIgnoreReturnValue
    public static int checkPositionIndex(final int n, final int n2) {
        checkPositionIndex(n, n2, "index");
        return n;
    }
    
    @CanIgnoreReturnValue
    public static int checkPositionIndex(final int n, final int n2, final String s) {
        if (n >= 0 && n <= n2) {
            return n;
        }
        throw new IndexOutOfBoundsException(badPositionIndex(n, n2, s));
    }
    
    public static void checkPositionIndexes(final int n, final int n2, final int n3) {
        if (n >= 0 && n2 >= n && n2 <= n3) {
            return;
        }
        throw new IndexOutOfBoundsException(badPositionIndexes(n, n2, n3));
    }
    
    public static void checkState(final boolean b) {
        if (b) {
            return;
        }
        throw new IllegalStateException();
    }
    
    public static void checkState(final boolean b, final Object obj) {
        if (b) {
            return;
        }
        throw new IllegalStateException(String.valueOf(obj));
    }
    
    public static void checkState(final boolean b, final String s, final int i) {
        if (b) {
            return;
        }
        throw new IllegalStateException(Strings.lenientFormat(s, i));
    }
    
    public static void checkState(final boolean b, final String s, final Object o) {
        if (b) {
            return;
        }
        throw new IllegalStateException(Strings.lenientFormat(s, o));
    }
}
