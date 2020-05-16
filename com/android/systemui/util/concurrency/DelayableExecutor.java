// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;

public interface DelayableExecutor extends Executor
{
    default Runnable executeDelayed(final Runnable runnable, final long n) {
        return this.executeDelayed(runnable, n, TimeUnit.MILLISECONDS);
    }
    
    Runnable executeDelayed(final Runnable p0, final long p1, final TimeUnit p2);
}
