// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.text;

import kotlin.jvm.internal.Intrinsics;

class StringsKt__StringsJVMKt extends StringsKt__StringNumberConversionsKt
{
    public static final boolean endsWith(final String s, final String suffix, final boolean b) {
        Intrinsics.checkParameterIsNotNull(s, "$this$endsWith");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (!b) {
            return s.endsWith(suffix);
        }
        return regionMatches(s, s.length() - suffix.length(), suffix, 0, suffix.length(), true);
    }
    
    public static boolean equals(final String s, final String s2, final boolean b) {
        if (s == null) {
            return s2 == null;
        }
        boolean b2;
        if (!b) {
            b2 = s.equals(s2);
        }
        else {
            b2 = s.equalsIgnoreCase(s2);
        }
        return b2;
    }
    
    public static final boolean regionMatches(final String s, final int n, final String s2, final int n2, final int n3, final boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull(s, "$this$regionMatches");
        Intrinsics.checkParameterIsNotNull(s2, "other");
        boolean b;
        if (!ignoreCase) {
            b = s.regionMatches(n, s2, n2, n3);
        }
        else {
            b = s.regionMatches(ignoreCase, n, s2, n2, n3);
        }
        return b;
    }
    
    public static final boolean startsWith(final String s, final String prefix, final boolean b) {
        Intrinsics.checkParameterIsNotNull(s, "$this$startsWith");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!b) {
            return s.startsWith(prefix);
        }
        return regionMatches(s, 0, prefix, 0, prefix.length(), b);
    }
}
