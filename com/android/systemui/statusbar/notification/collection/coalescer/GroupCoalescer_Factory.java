// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coalescer;

import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.SystemClock;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GroupCoalescer_Factory implements Factory<GroupCoalescer>
{
    private final Provider<SystemClock> clockProvider;
    private final Provider<GroupCoalescerLogger> loggerProvider;
    private final Provider<DelayableExecutor> mainExecutorProvider;
    
    public GroupCoalescer_Factory(final Provider<DelayableExecutor> mainExecutorProvider, final Provider<SystemClock> clockProvider, final Provider<GroupCoalescerLogger> loggerProvider) {
        this.mainExecutorProvider = mainExecutorProvider;
        this.clockProvider = clockProvider;
        this.loggerProvider = loggerProvider;
    }
    
    public static GroupCoalescer_Factory create(final Provider<DelayableExecutor> provider, final Provider<SystemClock> provider2, final Provider<GroupCoalescerLogger> provider3) {
        return new GroupCoalescer_Factory(provider, provider2, provider3);
    }
    
    public static GroupCoalescer provideInstance(final Provider<DelayableExecutor> provider, final Provider<SystemClock> provider2, final Provider<GroupCoalescerLogger> provider3) {
        return new GroupCoalescer(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public GroupCoalescer get() {
        return provideInstance(this.mainExecutorProvider, this.clockProvider, this.loggerProvider);
    }
}
