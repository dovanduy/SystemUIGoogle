// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

final class CollectPreconditions
{
    static void checkEntryNotNull(final Object obj, final Object obj2) {
        if (obj == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("null key in entry: null=");
            sb.append(obj2);
            throw new NullPointerException(sb.toString());
        }
        if (obj2 != null) {
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("null value in entry: ");
        sb2.append(obj);
        sb2.append("=null");
        throw new NullPointerException(sb2.toString());
    }
    
    @CanIgnoreReturnValue
    static int checkNonnegative(final int i, final String str) {
        if (i >= 0) {
            return i;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" cannot be negative but was: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    static void checkRemove(final boolean b) {
        Preconditions.checkState(b, "no calls to next() since the last call to remove()");
    }
}
