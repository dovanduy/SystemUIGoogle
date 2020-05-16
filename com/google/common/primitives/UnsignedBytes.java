// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.primitives;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import java.nio.ByteOrder;
import sun.misc.Unsafe;
import java.util.Comparator;

public final class UnsignedBytes
{
    public static int compare(final byte b, final byte b2) {
        return toInt(b) - toInt(b2);
    }
    
    static Comparator<byte[]> lexicographicalComparatorJavaImpl() {
        return PureJavaComparator.INSTANCE;
    }
    
    public static int toInt(final byte b) {
        return b & 0xFF;
    }
    
    static class LexicographicalComparatorHolder
    {
        static final String UNSAFE_COMPARATOR_NAME;
        
        static {
            final StringBuilder sb = new StringBuilder();
            sb.append(LexicographicalComparatorHolder.class.getName());
            sb.append("$UnsafeComparator");
            UNSAFE_COMPARATOR_NAME = sb.toString();
            getBestComparator();
        }
        
        static Comparator<byte[]> getBestComparator() {
            try {
                return (Comparator<byte[]>)Class.forName(LexicographicalComparatorHolder.UNSAFE_COMPARATOR_NAME).getEnumConstants()[0];
            }
            finally {
                return UnsignedBytes.lexicographicalComparatorJavaImpl();
            }
        }
        
        enum PureJavaComparator implements Comparator<byte[]>
        {
            INSTANCE;
            
            @Override
            public int compare(final byte[] array, final byte[] array2) {
                for (int min = Math.min(array.length, array2.length), i = 0; i < min; ++i) {
                    final int compare = UnsignedBytes.compare(array[i], array2[i]);
                    if (compare != 0) {
                        return compare;
                    }
                }
                return array.length - array2.length;
            }
            
            @Override
            public String toString() {
                return "UnsignedBytes.lexicographicalComparator() (pure Java version)";
            }
        }
        
        enum UnsafeComparator implements Comparator<byte[]>
        {
            static final boolean BIG_ENDIAN;
            static final int BYTE_ARRAY_BASE_OFFSET;
            
            INSTANCE;
            
            static final Unsafe theUnsafe;
            
            static {
                BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
                BYTE_ARRAY_BASE_OFFSET = (theUnsafe = getUnsafe()).arrayBaseOffset(byte[].class);
                if ("64".equals(System.getProperty("sun.arch.data.model")) && UnsafeComparator.BYTE_ARRAY_BASE_OFFSET % 8 == 0 && UnsafeComparator.theUnsafe.arrayIndexScale(byte[].class) == 1) {
                    return;
                }
                throw new Error();
            }
            
            private static Unsafe getUnsafe() {
                try {
                    return Unsafe.getUnsafe();
                }
                catch (SecurityException ex2) {
                    try {
                        return AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>)new PrivilegedExceptionAction<Unsafe>() {
                            @Override
                            public Unsafe run() throws Exception {
                                for (final Field field : Unsafe.class.getDeclaredFields()) {
                                    field.setAccessible(true);
                                    final Object value = field.get(null);
                                    if (Unsafe.class.isInstance(value)) {
                                        return Unsafe.class.cast(value);
                                    }
                                }
                                throw new NoSuchFieldError("the Unsafe");
                            }
                        });
                    }
                    catch (PrivilegedActionException ex) {
                        throw new RuntimeException("Could not initialize intrinsics", ex.getCause());
                    }
                }
            }
            
            @Override
            public int compare(final byte[] o, final byte[] o2) {
                final int min = Math.min(o.length, o2.length);
                int n = 0;
                int i;
                while (true) {
                    i = n;
                    if (n >= (min & 0xFFFFFFF8)) {
                        break;
                    }
                    final Unsafe theUnsafe = UnsafeComparator.theUnsafe;
                    final long n2 = UnsafeComparator.BYTE_ARRAY_BASE_OFFSET;
                    final long n3 = n;
                    final long long1 = theUnsafe.getLong(o, n2 + n3);
                    final long long2 = UnsafeComparator.theUnsafe.getLong(o2, UnsafeComparator.BYTE_ARRAY_BASE_OFFSET + n3);
                    if (long1 != long2) {
                        if (UnsafeComparator.BIG_ENDIAN) {
                            return UnsignedLongs.compare(long1, long2);
                        }
                        final int n4 = Long.numberOfTrailingZeros(long1 ^ long2) & 0xFFFFFFF8;
                        return (int)(long1 >>> n4 & 0xFFL) - (int)(0xFFL & long2 >>> n4);
                    }
                    else {
                        n += 8;
                    }
                }
                while (i < min) {
                    final int compare = UnsignedBytes.compare(o[i], o2[i]);
                    if (compare != 0) {
                        return compare;
                    }
                    ++i;
                }
                return o.length - o2.length;
            }
            
            @Override
            public String toString() {
                return "UnsignedBytes.lexicographicalComparator() (sun.misc.Unsafe version)";
            }
        }
    }
}
