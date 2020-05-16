// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Strings
{
    public static String lenientFormat(final String obj, final Object... array) {
        final String value = String.valueOf(obj);
        final int n = 0;
        Object[] array2;
        if (array == null) {
            array2 = new Object[] { "(Object[])null" };
        }
        else {
            int n2 = 0;
            while (true) {
                array2 = array;
                if (n2 >= array.length) {
                    break;
                }
                array[n2] = lenientToString(array[n2]);
                ++n2;
            }
        }
        final StringBuilder sb = new StringBuilder(value.length() + array2.length * 16);
        int start = 0;
        int i;
        for (i = n; i < array2.length; ++i) {
            final int index = value.indexOf("%s", start);
            if (index == -1) {
                break;
            }
            sb.append(value, start, index);
            sb.append(array2[i]);
            start = index + 2;
        }
        sb.append(value, start, value.length());
        if (i < array2.length) {
            sb.append(" [");
            final int n3 = i + 1;
            sb.append(array2[i]);
            for (int j = n3; j < array2.length; ++j) {
                sb.append(", ");
                sb.append(array2[j]);
            }
            sb.append(']');
        }
        return sb.toString();
    }
    
    private static String lenientToString(final Object obj) {
        try {
            return String.valueOf(obj);
        }
        catch (Exception thrown) {
            final StringBuilder sb = new StringBuilder();
            sb.append(obj.getClass().getName());
            sb.append('@');
            sb.append(Integer.toHexString(System.identityHashCode(obj)));
            final String string = sb.toString();
            final Logger logger = Logger.getLogger("com.google.common.base.Strings");
            final Level warning = Level.WARNING;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Exception during lenientFormat for ");
            sb2.append(string);
            logger.log(warning, sb2.toString(), thrown);
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("<");
            sb3.append(string);
            sb3.append(" threw ");
            sb3.append(thrown.getClass().getName());
            sb3.append(">");
            return sb3.toString();
        }
    }
    
    static boolean validSurrogatePairAt(final CharSequence charSequence, final int n) {
        boolean b = true;
        if (n < 0 || n > charSequence.length() - 2 || !Character.isHighSurrogate(charSequence.charAt(n)) || !Character.isLowSurrogate(charSequence.charAt(n + 1))) {
            b = false;
        }
        return b;
    }
}
