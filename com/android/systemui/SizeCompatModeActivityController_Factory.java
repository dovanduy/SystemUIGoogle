// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SizeCompatModeActivityController_Factory implements Factory<SizeCompatModeActivityController>
{
    private final Provider<ActivityManagerWrapper> amProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    
    public SizeCompatModeActivityController_Factory(final Provider<Context> contextProvider, final Provider<ActivityManagerWrapper> amProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.amProvider = amProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static SizeCompatModeActivityController_Factory create(final Provider<Context> provider, final Provider<ActivityManagerWrapper> provider2, final Provider<CommandQueue> provider3) {
        return new SizeCompatModeActivityController_Factory(provider, provider2, provider3);
    }
    
    public static SizeCompatModeActivityController provideInstance(final Provider<Context> provider, final Provider<ActivityManagerWrapper> provider2, final Provider<CommandQueue> provider3) {
        return new SizeCompatModeActivityController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public SizeCompatModeActivityController get() {
        return provideInstance(this.contextProvider, this.amProvider, this.commandQueueProvider);
    }
}
