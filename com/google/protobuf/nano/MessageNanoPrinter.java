// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano;

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
    
    private static void print(String str, Object o, final StringBuffer sb, final StringBuffer sb2) throws IllegalAccessException, InvocationTargetException {
        if (o == null) {
            return;
        }
        Label_0467: {
            if (!(o instanceof MessageNano)) {
                break Label_0467;
            }
            final int length = sb.length();
            if (str != null) {
                sb2.append(sb);
                sb2.append(deCamelCaseify(str));
                sb2.append(" <\n");
                sb.append("  ");
            }
            Object o2 = o.getClass();
            for (final Field field : ((Class)o2).getFields()) {
                final int modifiers = field.getModifiers();
                final String name = field.getName();
                if (!"cachedSize".equals(name)) {
                    if ((modifiers & 0x1) == 0x1 && (modifiers & 0x8) != 0x8 && !name.startsWith("_") && !name.endsWith("_")) {
                        final Class<?> type = field.getType();
                        final Object value = field.get(o);
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
            final Method[] methods = ((Class)o2).getMethods();
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
                    Block_23_Outer:Block_21_Outer:
                    while (true) {
                        if (!name2.startsWith("set")) {
                            break Label_0435;
                        }
                        substring = name2.substring(3);
                        try {
                            sb3 = new StringBuilder();
                            sb3.append("has");
                            sb3.append(substring);
                            if (((Class)o2).getMethod(sb3.toString(), (Class[])new Class[0]).invoke(o, new Object[0])) {
                                sb4 = new StringBuilder();
                                sb4.append("get");
                                sb4.append(substring);
                                print(substring, ((Class)o2).getMethod(sb4.toString(), (Class[])new Class[0]).invoke(o, new Object[0]), sb, sb2);
                            }
                            ++length5;
                            continue Label_0435_Outer;
                            Label_0678: {
                                sb2.append(o);
                            }
                            // iftrue(Label_0691:, str == null)
                            // iftrue(Label_0660:, !o instanceof String)
                            // iftrue(Label_0678:, !o instanceof byte[])
                            while (true) {
                            Block_22_Outer:
                                while (true) {
                                    break Label_0684;
                                Block_19_Outer:
                                    while (true) {
                                        str = sanitizeString((String)o);
                                        sb2.append("\"");
                                        sb2.append(str);
                                        sb2.append("\"");
                                        break Label_0684;
                                        while (true) {
                                            sb.setLength(length);
                                            sb2.append(sb);
                                            sb2.append(">\n");
                                            return;
                                            continue Block_23_Outer;
                                        }
                                        sb2.append("\n");
                                        return;
                                        Label_0598:
                                        str = deCamelCaseify(str);
                                        sb2.append(sb);
                                        sb2.append(str);
                                        sb2.append(": ");
                                        continue Block_19_Outer;
                                    }
                                    appendQuotedBytes((byte[])o, sb2);
                                    continue Block_22_Outer;
                                }
                                Label_0660:
                                continue Block_21_Outer;
                            }
                            // iftrue(Label_0598:, !o instanceof Map)
                            o = o;
                            str = deCamelCaseify(str);
                            o2 = ((Map<Object, Object>)o).entrySet().iterator();
                            // iftrue(Label_0691:, !o2.hasNext())
                            while (true) {
                                Label_0497: {
                                    break Label_0497;
                                    o = ((Iterator<Map.Entry<?, ?>>)o2).next();
                                    sb2.append(sb);
                                    sb2.append(str);
                                    sb2.append(" <\n");
                                    length5 = sb.length();
                                    sb.append("  ");
                                    print("key", ((Map.Entry<Object, V>)o).getKey(), sb, sb2);
                                    print("value", ((Map.Entry<K, Object>)o).getValue(), sb, sb2);
                                    sb.setLength(length5);
                                    sb2.append(sb);
                                    sb2.append(">\n");
                                }
                                continue;
                            }
                            Label_0691:;
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
