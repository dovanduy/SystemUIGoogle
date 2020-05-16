// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

public abstract class CharMatcher implements Object<Character>
{
    protected CharMatcher() {
    }
    
    public static CharMatcher is(final char c) {
        return new Is(c);
    }
    
    public static CharMatcher none() {
        return None.INSTANCE;
    }
    
    private static String showCharacter(final char c) {
        final char[] array;
        final char[] data = array = new char[6];
        array[0] = '\\';
        array[1] = 'u';
        array[3] = (array[2] = '\0');
        array[5] = (array[4] = '\0');
        final int n = 0;
        char c2 = c;
        for (int i = n; i < 4; ++i) {
            data[5 - i] = "0123456789ABCDEF".charAt(c2 & '\u000f');
            c2 >>= 4;
        }
        return String.copyValueOf(data);
    }
    
    public int indexIn(final CharSequence charSequence, int i) {
        final int length = charSequence.length();
        Preconditions.checkPositionIndex(i, length);
        while (i < length) {
            if (this.matches(charSequence.charAt(i))) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public abstract boolean matches(final char p0);
    
    abstract static class FastMatcher extends CharMatcher
    {
    }
    
    private static final class Is extends FastMatcher
    {
        private final char match;
        
        Is(final char c) {
            this.match = c;
        }
        
        @Override
        public boolean matches(final char c) {
            return c == this.match;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("CharMatcher.is('");
            sb.append(showCharacter(this.match));
            sb.append("')");
            return sb.toString();
        }
    }
    
    abstract static class NamedFastMatcher extends FastMatcher
    {
        private final String description;
        
        NamedFastMatcher(final String s) {
            Preconditions.checkNotNull(s);
            this.description = s;
        }
        
        @Override
        public final String toString() {
            return this.description;
        }
    }
    
    private static final class None extends NamedFastMatcher
    {
        static final None INSTANCE;
        
        static {
            INSTANCE = new None();
        }
        
        private None() {
            super("CharMatcher.none()");
        }
        
        @Override
        public int indexIn(final CharSequence charSequence, final int n) {
            Preconditions.checkPositionIndex(n, charSequence.length());
            return -1;
        }
        
        @Override
        public boolean matches(final char c) {
            return false;
        }
    }
    
    static final class Whitespace extends NamedFastMatcher
    {
        static final int SHIFT;
        
        static {
            SHIFT = Integer.numberOfLeadingZeros(31);
            new Whitespace();
        }
        
        Whitespace() {
            super("CharMatcher.whitespace()");
        }
        
        @Override
        public boolean matches(final char c) {
            return "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f \f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".charAt(1682554634 * c >>> Whitespace.SHIFT) == c;
        }
    }
}
