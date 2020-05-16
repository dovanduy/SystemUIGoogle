// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import dagger.internal.DoubleCheck;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.BlurUtils;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GlobalActionsImpl_Factory implements Factory<GlobalActionsImpl>
{
    private final Provider<BlurUtils> blurUtilsProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<GlobalActionsDialog> globalActionsDialogLazyProvider;
    
    public GlobalActionsImpl_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<GlobalActionsDialog> globalActionsDialogLazyProvider, final Provider<BlurUtils> blurUtilsProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.globalActionsDialogLazyProvider = globalActionsDialogLazyProvider;
        this.blurUtilsProvider = blurUtilsProvider;
    }
    
    public static GlobalActionsImpl_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<GlobalActionsDialog> provider3, final Provider<BlurUtils> provider4) {
        return new GlobalActionsImpl_Factory(provider, provider2, provider3, provider4);
    }
    
    public static GlobalActionsImpl provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<GlobalActionsDialog> provider3, final Provider<BlurUtils> provider4) {
        return new GlobalActionsImpl(provider.get(), provider2.get(), DoubleCheck.lazy(provider3), provider4.get());
    }
    
    @Override
    public GlobalActionsImpl get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.globalActionsDialogLazyProvider, this.blurUtilsProvider);
    }
}
