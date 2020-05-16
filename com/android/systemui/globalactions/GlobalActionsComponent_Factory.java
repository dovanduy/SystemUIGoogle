// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.statusbar.policy.ExtensionController;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GlobalActionsComponent_Factory implements Factory<GlobalActionsComponent>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ExtensionController> extensionControllerProvider;
    private final Provider<GlobalActions> globalActionsProvider;
    
    public GlobalActionsComponent_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<ExtensionController> extensionControllerProvider, final Provider<GlobalActions> globalActionsProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.extensionControllerProvider = extensionControllerProvider;
        this.globalActionsProvider = globalActionsProvider;
    }
    
    public static GlobalActionsComponent_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<ExtensionController> provider3, final Provider<GlobalActions> provider4) {
        return new GlobalActionsComponent_Factory(provider, provider2, provider3, provider4);
    }
    
    public static GlobalActionsComponent provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<ExtensionController> provider3, final Provider<GlobalActions> provider4) {
        return new GlobalActionsComponent(provider.get(), provider2.get(), provider3.get(), provider4);
    }
    
    @Override
    public GlobalActionsComponent get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.extensionControllerProvider, this.globalActionsProvider);
    }
}
