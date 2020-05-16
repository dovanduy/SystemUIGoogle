// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.os.Handler;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import java.util.Set;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SystemKeyPress_Factory implements Factory<SystemKeyPress>
{
    private final Provider<Set<Integer>> blockingKeysProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Long> gateDurationProvider;
    private final Provider<Handler> handlerProvider;
    
    public SystemKeyPress_Factory(final Provider<Context> contextProvider, final Provider<Handler> handlerProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<Long> gateDurationProvider, final Provider<Set<Integer>> blockingKeysProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.gateDurationProvider = gateDurationProvider;
        this.blockingKeysProvider = blockingKeysProvider;
    }
    
    public static SystemKeyPress_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<CommandQueue> provider3, final Provider<Long> provider4, final Provider<Set<Integer>> provider5) {
        return new SystemKeyPress_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static SystemKeyPress provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<CommandQueue> provider3, final Provider<Long> provider4, final Provider<Set<Integer>> provider5) {
        return new SystemKeyPress(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public SystemKeyPress get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.commandQueueProvider, this.gateDurationProvider, this.blockingKeysProvider);
    }
}
