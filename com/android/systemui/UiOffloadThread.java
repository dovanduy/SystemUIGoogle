// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class UiOffloadThread
{
    private final ExecutorService mExecutorService;
    
    public UiOffloadThread() {
        this.mExecutorService = Executors.newSingleThreadExecutor();
    }
    
    public Future<?> execute(final Runnable runnable) {
        return this.mExecutorService.submit(runnable);
    }
}
