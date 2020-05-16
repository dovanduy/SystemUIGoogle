// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.text;

class CharsKt__CharKt extends CharsKt__CharJVMKt
{
    public static final boolean equals(final char c, final char c2, final boolean b) {
        return c == c2 || (b && (Character.toUpperCase(c) == Character.toUpperCase(c2) || Character.toLowerCase(c) == Character.toLowerCase(c2)));
    }
}
