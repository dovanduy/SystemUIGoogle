// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Executor;

enum DirectExecutor implements Executor
{
    INSTANCE;
    
    @Override
    public void execute(final Runnable runnable) {
        runnable.run();
    }
    
    @Override
    public String toString() {
        return "MoreExecutors.directExecutor()";
    }
}
