// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

public final class Ascii
{
    public static boolean isUpperCase(final char c) {
        return c >= 'A' && c <= 'Z';
    }
    
    public static String toLowerCase(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (isUpperCase(s.charAt(i))) {
                final char[] charArray = s.toCharArray();
                while (i < length) {
                    final char c = charArray[i];
                    if (isUpperCase(c)) {
                        charArray[i] = (char)(c ^ ' ');
                    }
                    ++i;
                }
                return String.valueOf(charArray);
            }
        }
        return s;
    }
}
