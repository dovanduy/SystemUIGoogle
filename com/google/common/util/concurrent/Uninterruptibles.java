// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class Uninterruptibles
{
    @CanIgnoreReturnValue
    public static <V> V getUninterruptibly(final Future<V> future) throws ExecutionException {
        boolean b = false;
        try {
            return future.get();
        }
        catch (InterruptedException ex) {
            b = true;
            return future.get();
        }
        finally {
            if (b) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
