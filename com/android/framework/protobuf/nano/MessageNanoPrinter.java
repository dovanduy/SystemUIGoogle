// 
// Decompiled by Procyon v0.5.36
// 

package com.android.framework.protobuf.nano;

import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

public final class MessageNanoPrinter
{
    private static void appendQuotedBytes(final byte[] array, final StringBuffer sb) {
        if (array == null) {
            sb.append("\"\"");
            return;
        }
        sb.append('\"');
        for (int i = 0; i < array.length; ++i) {
            final int j = array[i] & 0xFF;
            if (j != 92 && j != 34) {
                if (j >= 32 && j < 127) {
                    sb.append((char)j);
                }
                else {
                    sb.append(String.format("\\%03o", j));
                }
            }
            else {
                sb.append('\\');
                sb.append((char)j);
            }
        }
        sb.append('\"');
    }
    
    private static String deCamelCaseify(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (i == 0) {
                sb.append(Character.toLowerCase(char1));
            }
            else if (Character.isUpperCase(char1)) {
                sb.append('_');
                sb.append(Character.toLowerCase(char1));
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    private static String escapeString(final String s) {
        final int length = s.length();
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 >= ' ' && char1 <= '~' && char1 != '\"' && char1 != '\'') {
                sb.append(char1);
            }
            else {
                sb.append(String.format("\\u%04x", (int)char1));
            }
        }
        return sb.toString();
    }
    
    public static <T extends MessageNano> String print(final T t) {
        if (t == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        try {
            print(null, t, new StringBuffer(), sb);
            return sb.toString();
        }
        catch (InvocationTargetException ex) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Error printing proto: ");
            sb2.append(ex.getMessage());
            return sb2.toString();
        }
        catch (IllegalAccessException ex2) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Error printing proto: ");
            sb3.append(ex2.getMessage());
            return sb3.toString();
        }
    }
    
    private static void print(String str, Object iterator, final StringBuffer sb, final StringBuffer sb2) throws IllegalAccessException, InvocationTargetException {
        if (iterator == null) {
            return;
        }
        Label_0467: {
            if (!(iterator instanceof MessageNano)) {
                break Label_0467;
            }
            final int length = sb.length();
            if (str != null) {
                sb2.append(sb);
                sb2.append(deCamelCaseify(str));
                sb2.append(" <\n");
                sb.append("  ");
            }
            Object class1 = iterator.getClass();
            for (final Field field : ((Class)class1).getFields()) {
                final int modifiers = field.getModifiers();
                final String name = field.getName();
                if (!"cachedSize".equals(name)) {
                    if ((modifiers & 0x1) == 0x1 && (modifiers & 0x8) != 0x8 && !name.startsWith("_") && !name.endsWith("_")) {
                        final Class<?> type = field.getType();
                        final Object value = field.get(iterator);
                        if (type.isArray()) {
                            if (type.getComponentType() == Byte.TYPE) {
                                print(name, value, sb, sb2);
                            }
                            else {
                                int length3;
                                if (value == null) {
                                    length3 = 0;
                                }
                                else {
                                    length3 = Array.getLength(value);
                                }
                                for (int j = 0; j < length3; ++j) {
                                    print(name, Array.get(value, j), sb, sb2);
                                }
                            }
                        }
                        else {
                            print(name, value, sb, sb2);
                        }
                    }
                }
            }
            final Method[] methods = ((Class)class1).getMethods();
            final int length4 = methods.length;
            int length5 = 0;
        Label_0435_Outer:
            while (true) {
                Label_0441: {
                    if (length5 >= length4) {
                        break Label_0441;
                    }
                    final String name2 = methods[length5].getName();
                    String substring;
                    StringBuilder sb3;
                    StringBuilder sb4;
                    Label_0496_Outer:Label_0684_Outer:
                    while (true) {
                        if (!name2.startsWith("set")) {
                            break Label_0435;
                        }
                        substring = name2.substring(3);
                        try {
                            sb3 = new StringBuilder();
                            sb3.append("has");
                            sb3.append(substring);
                            if (((Class)class1).getMethod(sb3.toString(), (Class[])new Class[0]).invoke(iterator, new Object[0])) {
                                sb4 = new StringBuilder();
                                sb4.append("get");
                                sb4.append(substring);
                                print(substring, ((Class)class1).getMethod(sb4.toString(), (Class[])new Class[0]).invoke(iterator, new Object[0]), sb, sb2);
                            }
                            ++length5;
                            continue Label_0435_Outer;
                            // iftrue(Label_0691:, str == null)
                            while (true) {
                                sb.setLength(length);
                                sb2.append(sb);
                                sb2.append(">\n");
                                return;
                                continue Label_0496_Outer;
                            }
                            // iftrue(Label_0691:, !iterator.hasNext())
                            // iftrue(Label_0660:, !iterator instanceof String)
                            // iftrue(Label_0678:, !iterator instanceof byte[])
                            // iftrue(Label_0598:, !iterator instanceof Map)
                        Label_0684:
                            while (true) {
                                Block_23: {
                                Block_20_Outer:
                                    while (true) {
                                        Block_21: {
                                            break Block_21;
                                            Label_0598: {
                                                str = deCamelCaseify(str);
                                            }
                                            sb2.append(sb);
                                            sb2.append(str);
                                            sb2.append(": ");
                                            while (true) {
                                                Block_22: {
                                                    break Block_22;
                                                    iterator = iterator;
                                                    str = deCamelCaseify(str);
                                                    iterator = ((Map<Object, Object>)iterator).entrySet().iterator();
                                                    continue Block_20_Outer;
                                                    Label_0678:
                                                    sb2.append(iterator);
                                                    break Label_0684;
                                                }
                                                str = sanitizeString((String)iterator);
                                                sb2.append("\"");
                                                sb2.append(str);
                                                sb2.append("\"");
                                                break Label_0684;
                                                Label_0660:
                                                break Block_23;
                                                Label_0691:
                                                return;
                                                continue Label_0684_Outer;
                                            }
                                        }
                                        class1 = ((Iterator<Map.Entry<Object, Object>>)iterator).next();
                                        sb2.append(sb);
                                        sb2.append(str);
                                        sb2.append(" <\n");
                                        length5 = sb.length();
                                        sb.append("  ");
                                        print("key", ((Map.Entry<Object, Object>)class1).getKey(), sb, sb2);
                                        print("value", ((Map.Entry<Object, Object>)class1).getValue(), sb, sb2);
                                        sb.setLength(length5);
                                        sb2.append(sb);
                                        sb2.append(">\n");
                                        continue Label_0684_Outer;
                                    }
                                    sb2.append("\n");
                                    return;
                                }
                                appendQuotedBytes((byte[])iterator, sb2);
                                continue Label_0684;
                            }
                        }
                        catch (NoSuchMethodException ex) {
                            continue;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
    
    private static String sanitizeString(final String s) {
        String string = s;
        if (!s.startsWith("http")) {
            string = s;
            if (s.length() > 200) {
                final StringBuilder sb = new StringBuilder();
                sb.append(s.substring(0, 200));
                sb.append("[...]");
                string = sb.toString();
            }
        }
        return escapeString(string);
    }
}
