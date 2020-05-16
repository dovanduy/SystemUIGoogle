// 
// Decompiled by Procyon v0.5.36
// 

package androidx.arch.core.executor;

import java.lang.reflect.InvocationTargetException;
import android.os.Handler$Callback;
import android.os.Build$VERSION;
import android.os.Looper;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;
import android.os.Handler;
import java.util.concurrent.ExecutorService;

public class DefaultTaskExecutor extends TaskExecutor
{
    private final ExecutorService mDiskIO;
    private final Object mLock;
    private volatile Handler mMainHandler;
    
    public DefaultTaskExecutor() {
        this.mLock = new Object();
        this.mDiskIO = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private final AtomicInteger mThreadId = new AtomicInteger(0);
            
            @Override
            public Thread newThread(final Runnable target) {
                final Thread thread = new Thread(target);
                thread.setName(String.format("arch_disk_io_%d", this.mThreadId.getAndIncrement()));
                return thread;
            }
        });
    }
    
    private static Handler createAsync(final Looper looper) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 28) {
            return Handler.createAsync(looper);
        }
        if (sdk_INT < 16) {
            goto Label_0083;
        }
        try {
            return Handler.class.getDeclaredConstructor(Looper.class, Handler$Callback.class, Boolean.TYPE).newInstance(looper, null, Boolean.TRUE);
        }
        catch (InvocationTargetException ex) {
            return new Handler(looper);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException ex2) {
            goto Label_0083;
        }
    }
    
    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
    
    @Override
    public void postToMainThread(final Runnable runnable) {
        if (this.mMainHandler == null) {
            synchronized (this.mLock) {
                if (this.mMainHandler == null) {
                    this.mMainHandler = createAsync(Looper.getMainLooper());
                }
            }
        }
        this.mMainHandler.post(runnable);
    }
}
