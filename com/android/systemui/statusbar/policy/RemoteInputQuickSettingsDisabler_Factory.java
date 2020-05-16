// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RemoteInputQuickSettingsDisabler_Factory implements Factory<RemoteInputQuickSettingsDisabler>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configControllerProvider;
    private final Provider<Context> contextProvider;
    
    public RemoteInputQuickSettingsDisabler_Factory(final Provider<Context> contextProvider, final Provider<ConfigurationController> configControllerProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.configControllerProvider = configControllerProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static RemoteInputQuickSettingsDisabler_Factory create(final Provider<Context> provider, final Provider<ConfigurationController> provider2, final Provider<CommandQueue> provider3) {
        return new RemoteInputQuickSettingsDisabler_Factory(provider, provider2, provider3);
    }
    
    public static RemoteInputQuickSettingsDisabler provideInstance(final Provider<Context> provider, final Provider<ConfigurationController> provider2, final Provider<CommandQueue> provider3) {
        return new RemoteInputQuickSettingsDisabler(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public RemoteInputQuickSettingsDisabler get() {
        return provideInstance(this.contextProvider, this.configControllerProvider, this.commandQueueProvider);
    }
}
