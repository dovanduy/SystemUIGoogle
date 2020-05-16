// 
// Decompiled by Procyon v0.5.36
// 

package androidx.arch.core.executor;

public class ArchTaskExecutor extends TaskExecutor
{
    private static volatile ArchTaskExecutor sInstance;
    private TaskExecutor mDefaultTaskExecutor;
    private TaskExecutor mDelegate;
    
    private ArchTaskExecutor() {
        final DefaultTaskExecutor defaultTaskExecutor = new DefaultTaskExecutor();
        this.mDefaultTaskExecutor = defaultTaskExecutor;
        this.mDelegate = defaultTaskExecutor;
    }
    
    public static ArchTaskExecutor getInstance() {
        if (ArchTaskExecutor.sInstance != null) {
            return ArchTaskExecutor.sInstance;
        }
        synchronized (ArchTaskExecutor.class) {
            if (ArchTaskExecutor.sInstance == null) {
                ArchTaskExecutor.sInstance = new ArchTaskExecutor();
            }
            return ArchTaskExecutor.sInstance;
        }
    }
    
    @Override
    public boolean isMainThread() {
        return this.mDelegate.isMainThread();
    }
    
    @Override
    public void postToMainThread(final Runnable runnable) {
        this.mDelegate.postToMainThread(runnable);
    }
}
