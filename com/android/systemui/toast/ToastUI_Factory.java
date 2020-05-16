// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.toast;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ToastUI_Factory implements Factory<ToastUI>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    
    public ToastUI_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static ToastUI_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new ToastUI_Factory(provider, provider2);
    }
    
    public static ToastUI provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new ToastUI(provider.get(), provider2.get());
    }
    
    @Override
    public ToastUI get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider);
    }
}
