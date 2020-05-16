// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.utils;

import java.util.concurrent.Future;
import android.os.Looper;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import android.os.Handler;

public class ThreadUtils
{
    private static volatile Thread sMainThread;
    private static volatile Handler sMainThreadHandler;
    private static volatile ExecutorService sThreadExecutor;
    
    public static void ensureMainThread() {
        if (isMainThread()) {
            return;
        }
        throw new RuntimeException("Must be called on the UI thread");
    }
    
    private static ExecutorService getThreadExecutor() {
        synchronized (ThreadUtils.class) {
            if (ThreadUtils.sThreadExecutor == null) {
                ThreadUtils.sThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            }
            return ThreadUtils.sThreadExecutor;
        }
    }
    
    public static Handler getUiThreadHandler() {
        if (ThreadUtils.sMainThreadHandler == null) {
            ThreadUtils.sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return ThreadUtils.sMainThreadHandler;
    }
    
    public static boolean isMainThread() {
        if (ThreadUtils.sMainThread == null) {
            ThreadUtils.sMainThread = Looper.getMainLooper().getThread();
        }
        return Thread.currentThread() == ThreadUtils.sMainThread;
    }
    
    public static Future postOnBackgroundThread(final Runnable runnable) {
        return getThreadExecutor().submit(runnable);
    }
    
    public static void postOnMainThread(final Runnable runnable) {
        getUiThreadHandler().post(runnable);
    }
}
