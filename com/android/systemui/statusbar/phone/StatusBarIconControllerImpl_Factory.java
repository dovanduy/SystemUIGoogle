// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class StatusBarIconControllerImpl_Factory implements Factory<StatusBarIconControllerImpl>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    
    public StatusBarIconControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static StatusBarIconControllerImpl_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new StatusBarIconControllerImpl_Factory(provider, provider2);
    }
    
    public static StatusBarIconControllerImpl provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new StatusBarIconControllerImpl(provider.get(), provider2.get());
    }
    
    @Override
    public StatusBarIconControllerImpl get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider);
    }
}
