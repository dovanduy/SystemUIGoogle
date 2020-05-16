// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import java.util.concurrent.Executor;
import com.android.systemui.stackdivider.Divider;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class InstantAppNotifier_Factory implements Factory<InstantAppNotifier>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Divider> dividerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    
    public InstantAppNotifier_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<Executor> uiBgExecutorProvider, final Provider<Divider> dividerProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.uiBgExecutorProvider = uiBgExecutorProvider;
        this.dividerProvider = dividerProvider;
    }
    
    public static InstantAppNotifier_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<Executor> provider3, final Provider<Divider> provider4) {
        return new InstantAppNotifier_Factory(provider, provider2, provider3, provider4);
    }
    
    public static InstantAppNotifier provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<Executor> provider3, final Provider<Divider> provider4) {
        return new InstantAppNotifier(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public InstantAppNotifier get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.uiBgExecutorProvider, this.dividerProvider);
    }
}
