// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.text;

import kotlin.collections.ArraysKt;
import kotlin.ranges.IntProgression;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.jvm.internal.Intrinsics;

class StringsKt__StringsKt extends StringsKt__StringsJVMKt
{
    public static final boolean contains(final CharSequence charSequence, final CharSequence charSequence2, final boolean b) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$contains");
        Intrinsics.checkParameterIsNotNull(charSequence2, "other");
        final boolean b2 = charSequence2 instanceof String;
        final boolean b3 = true;
        return (b2 ? (indexOf$default(charSequence, (String)charSequence2, 0, b, 2, null) >= 0) : (indexOf$StringsKt__StringsKt$default(charSequence, charSequence2, 0, charSequence.length(), b, false, 16, null) >= 0)) && b3;
    }
    
    public static final int getLastIndex(final CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$lastIndex");
        return charSequence.length() - 1;
    }
    
    public static final int indexOf(final CharSequence charSequence, final char ch, int fromIndex, final boolean b) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$indexOf");
        if (!b && charSequence instanceof String) {
            fromIndex = ((String)charSequence).indexOf(ch, fromIndex);
        }
        else {
            fromIndex = indexOfAny(charSequence, new char[] { ch }, fromIndex, b);
        }
        return fromIndex;
    }
    
    public static final int indexOf(final CharSequence charSequence, final String str, int fromIndex, final boolean b) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$indexOf");
        Intrinsics.checkParameterIsNotNull(str, "string");
        if (!b && charSequence instanceof String) {
            fromIndex = ((String)charSequence).indexOf(str, fromIndex);
        }
        else {
            fromIndex = indexOf$StringsKt__StringsKt$default(charSequence, str, fromIndex, charSequence.length(), b, false, 16, null);
        }
        return fromIndex;
    }
    
    private static final int indexOf$StringsKt__StringsKt(final CharSequence charSequence, final CharSequence charSequence2, int n, int n2, final boolean b, final boolean b2) {
        IntProgression downTo;
        if (!b2) {
            downTo = new IntRange(RangesKt.coerceAtLeast(n, 0), RangesKt.coerceAtMost(n2, charSequence.length()));
        }
        else {
            downTo = RangesKt.downTo(RangesKt.coerceAtMost(n, getLastIndex(charSequence)), RangesKt.coerceAtLeast(n2, 0));
        }
        if (charSequence instanceof String && charSequence2 instanceof String) {
            n = downTo.getFirst();
            final int last = downTo.getLast();
            n2 = downTo.getStep();
            if (n2 >= 0) {
                if (n > last) {
                    return -1;
                }
            }
            else if (n < last) {
                return -1;
            }
            while (!StringsKt__StringsJVMKt.regionMatches((String)charSequence2, 0, (String)charSequence, n, charSequence2.length(), b)) {
                if (n == last) {
                    return -1;
                }
                n += n2;
            }
            return n;
        }
        n = downTo.getFirst();
        n2 = downTo.getLast();
        final int step = downTo.getStep();
        if (step >= 0) {
            if (n > n2) {
                return -1;
            }
        }
        else if (n < n2) {
            return -1;
        }
        while (!regionMatchesImpl(charSequence2, 0, charSequence, n, charSequence2.length(), b)) {
            if (n == n2) {
                return -1;
            }
            n += step;
        }
        return n;
    }
    
    static /* synthetic */ int indexOf$StringsKt__StringsKt$default(final CharSequence charSequence, final CharSequence charSequence2, final int n, final int n2, final boolean b, boolean b2, final int n3, final Object o) {
        if ((n3 & 0x10) != 0x0) {
            b2 = false;
        }
        return indexOf$StringsKt__StringsKt(charSequence, charSequence2, n, n2, b, b2);
    }
    
    public static /* synthetic */ int indexOf$default(final CharSequence charSequence, final String s, int n, boolean b, final int n2, final Object o) {
        if ((n2 & 0x2) != 0x0) {
            n = 0;
        }
        if ((n2 & 0x4) != 0x0) {
            b = false;
        }
        return indexOf(charSequence, s, n, b);
    }
    
    public static final int indexOfAny(final CharSequence charSequence, final char[] array, int coerceAtLeast, final boolean b) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$indexOfAny");
        Intrinsics.checkParameterIsNotNull(array, "chars");
        if (!b && array.length == 1 && charSequence instanceof String) {
            return ((String)charSequence).indexOf(ArraysKt.single(array), coerceAtLeast);
        }
        coerceAtLeast = RangesKt.coerceAtLeast(coerceAtLeast, 0);
        final int lastIndex = getLastIndex(charSequence);
        Label_0134: {
            if (coerceAtLeast <= lastIndex) {
            Label_0064:
                while (true) {
                    final char char1 = charSequence.charAt(coerceAtLeast);
                    final int length = array.length;
                    int i = 0;
                    while (true) {
                        while (i < length) {
                            if (CharsKt__CharKt.equals(array[i], char1, b)) {
                                final boolean b2 = true;
                                if (b2) {
                                    return coerceAtLeast;
                                }
                                if (coerceAtLeast != lastIndex) {
                                    ++coerceAtLeast;
                                    continue Label_0064;
                                }
                                break Label_0134;
                            }
                            else {
                                ++i;
                            }
                        }
                        final boolean b2 = false;
                        continue;
                    }
                }
            }
        }
        return -1;
    }
    
    public static final boolean regionMatchesImpl(final CharSequence charSequence, final int n, final CharSequence charSequence2, final int n2, final int n3, final boolean b) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$regionMatchesImpl");
        Intrinsics.checkParameterIsNotNull(charSequence2, "other");
        if (n2 >= 0 && n >= 0 && n <= charSequence.length() - n3 && n2 <= charSequence2.length() - n3) {
            for (int i = 0; i < n3; ++i) {
                if (!CharsKt__CharKt.equals(charSequence.charAt(n + i), charSequence2.charAt(n2 + i), b)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static CharSequence trim(final CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$trim");
        int n = charSequence.length() - 1;
        int i = 0;
        int n2 = 0;
        while (i <= n) {
            int n3;
            if (n2 == 0) {
                n3 = i;
            }
            else {
                n3 = n;
            }
            final boolean whitespace = CharsKt__CharJVMKt.isWhitespace(charSequence.charAt(n3));
            if (n2 == 0) {
                if (!whitespace) {
                    n2 = 1;
                }
                else {
                    ++i;
                }
            }
            else {
                if (!whitespace) {
                    break;
                }
                --n;
            }
        }
        return charSequence.subSequence(i, n + 1);
    }
}
