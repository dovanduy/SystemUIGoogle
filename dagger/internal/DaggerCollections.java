// 
// Decompiled by Procyon v0.5.36
// 

package dagger.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.HashSet;

public final class DaggerCollections
{
    private static int calculateInitialCapacity(final int n) {
        if (n < 3) {
            return n + 1;
        }
        if (n < 1073741824) {
            return (int)(n / 0.75f + 1.0f);
        }
        return Integer.MAX_VALUE;
    }
    
    static <T> HashSet<T> newHashSetWithExpectedSize(final int n) {
        return new HashSet<T>(calculateInitialCapacity(n));
    }
    
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(final int n) {
        return new LinkedHashMap<K, V>(calculateInitialCapacity(n));
    }
    
    public static <T> List<T> presizedList(final int initialCapacity) {
        if (initialCapacity == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<T>(initialCapacity);
    }
}
