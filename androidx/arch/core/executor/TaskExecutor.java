// 
// Decompiled by Procyon v0.5.36
// 

package androidx.arch.core.executor;

public abstract class TaskExecutor
{
    public abstract boolean isMainThread();
    
    public abstract void postToMainThread(final Runnable p0);
}
