// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DarkIconDispatcherImpl_Factory implements Factory<DarkIconDispatcherImpl>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    
    public DarkIconDispatcherImpl_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static DarkIconDispatcherImpl_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new DarkIconDispatcherImpl_Factory(provider, provider2);
    }
    
    public static DarkIconDispatcherImpl provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new DarkIconDispatcherImpl(provider.get(), provider2.get());
    }
    
    @Override
    public DarkIconDispatcherImpl get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider);
    }
}
