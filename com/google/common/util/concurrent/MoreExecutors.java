// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.concurrent.RejectedExecutionException;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

public final class MoreExecutors
{
    public static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }
    
    static Executor rejectionPropagatingExecutor(final Executor executor, final AbstractFuture<?> abstractFuture) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(abstractFuture);
        if (executor == directExecutor()) {
            return executor;
        }
        return new Executor() {
            boolean thrownFromDelegate = true;
            
            @Override
            public void execute(final Runnable runnable) {
                try {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Executor.this.thrownFromDelegate = false;
                            runnable.run();
                        }
                    });
                }
                catch (RejectedExecutionException exception) {
                    if (this.thrownFromDelegate) {
                        abstractFuture.setException(exception);
                    }
                }
            }
        };
    }
    
    static class Application
    {
        void addShutdownHook(final Thread hook) {
            Runtime.getRuntime().addShutdownHook(hook);
        }
    }
}
