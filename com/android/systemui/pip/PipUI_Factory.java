// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PipUI_Factory implements Factory<PipUI>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<BasePipManager> pipManagerProvider;
    
    public PipUI_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<BasePipManager> pipManagerProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.pipManagerProvider = pipManagerProvider;
    }
    
    public static PipUI_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<BasePipManager> provider3) {
        return new PipUI_Factory(provider, provider2, provider3);
    }
    
    public static PipUI provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<BasePipManager> provider3) {
        return new PipUI(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public PipUI get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.pipManagerProvider);
    }
}
