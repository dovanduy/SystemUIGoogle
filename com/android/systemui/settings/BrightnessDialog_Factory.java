// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BrightnessDialog_Factory implements Factory<BrightnessDialog>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    
    public BrightnessDialog_Factory(final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static BrightnessDialog_Factory create(final Provider<BroadcastDispatcher> provider) {
        return new BrightnessDialog_Factory(provider);
    }
    
    public static BrightnessDialog provideInstance(final Provider<BroadcastDispatcher> provider) {
        return new BrightnessDialog(provider.get());
    }
    
    @Override
    public BrightnessDialog get() {
        return provideInstance(this.broadcastDispatcherProvider);
    }
}
