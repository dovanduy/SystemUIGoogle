// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class BackgroundExecutor
{
    private static final BackgroundExecutor sInstance;
    private final ExecutorService mExecutorService;
    
    static {
        sInstance = new BackgroundExecutor();
    }
    
    public BackgroundExecutor() {
        this.mExecutorService = Executors.newFixedThreadPool(2);
    }
    
    public static BackgroundExecutor get() {
        return BackgroundExecutor.sInstance;
    }
    
    public Future<?> submit(final Runnable runnable) {
        return this.mExecutorService.submit(runnable);
    }
}
