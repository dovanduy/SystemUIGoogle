// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import java.util.concurrent.Executors;
import android.content.Context;
import android.os.HandlerThread;
import android.os.Handler;
import java.util.concurrent.Executor;
import android.os.Looper;

public abstract class ConcurrencyModule
{
    public static DelayableExecutor provideBackgroundDelayableExecutor(final Looper looper) {
        return new ExecutorImpl(looper);
    }
    
    public static Executor provideBackgroundExecutor(final Looper looper) {
        return new ExecutorImpl(looper);
    }
    
    public static Handler provideBgHandler(final Looper looper) {
        return new Handler(looper);
    }
    
    public static Looper provideBgLooper() {
        final HandlerThread handlerThread = new HandlerThread("SysUiBg", 10);
        handlerThread.start();
        return handlerThread.getLooper();
    }
    
    public static Executor provideExecutor(final Looper looper) {
        return new ExecutorImpl(looper);
    }
    
    public static DelayableExecutor provideMainDelayableExecutor(final Looper looper) {
        return new ExecutorImpl(looper);
    }
    
    public static Executor provideMainExecutor(final Context context) {
        return context.getMainExecutor();
    }
    
    public static Handler provideMainHandler(final Looper looper) {
        return new Handler(looper);
    }
    
    public static Looper provideMainLooper() {
        return Looper.getMainLooper();
    }
    
    public static Executor provideUiBackgroundExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
