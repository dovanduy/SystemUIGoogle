// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.util.Arrays;

public final class Objects extends ExtraObjectsMethodsForWeb
{
    public static boolean equal(final Object o, final Object obj) {
        return o == obj || (o != null && o.equals(obj));
    }
    
    public static int hashCode(final Object... a) {
        return Arrays.hashCode(a);
    }
}
