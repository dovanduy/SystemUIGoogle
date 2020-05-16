// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.tv;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TvStatusBar_Factory implements Factory<TvStatusBar>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    
    public TvStatusBar_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static TvStatusBar_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new TvStatusBar_Factory(provider, provider2);
    }
    
    public static TvStatusBar provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new TvStatusBar(provider.get(), provider2.get());
    }
    
    @Override
    public TvStatusBar get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider);
    }
}
