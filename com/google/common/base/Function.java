// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

public interface Function<F, T>
{
    @CanIgnoreReturnValue
    T apply(final F p0);
    
    boolean equals(final Object p0);
}
