// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class WorkLockActivity_Factory implements Factory<WorkLockActivity>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    
    public WorkLockActivity_Factory(final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static WorkLockActivity_Factory create(final Provider<BroadcastDispatcher> provider) {
        return new WorkLockActivity_Factory(provider);
    }
    
    public static WorkLockActivity provideInstance(final Provider<BroadcastDispatcher> provider) {
        return new WorkLockActivity(provider.get());
    }
    
    @Override
    public WorkLockActivity get() {
        return provideInstance(this.broadcastDispatcherProvider);
    }
}
