// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.os.PowerManager;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LatencyTester_Factory implements Factory<LatencyTester>
{
    private final Provider<BiometricUnlockController> biometricUnlockControllerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<PowerManager> powerManagerProvider;
    
    public LatencyTester_Factory(final Provider<Context> contextProvider, final Provider<BiometricUnlockController> biometricUnlockControllerProvider, final Provider<PowerManager> powerManagerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.biometricUnlockControllerProvider = biometricUnlockControllerProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static LatencyTester_Factory create(final Provider<Context> provider, final Provider<BiometricUnlockController> provider2, final Provider<PowerManager> provider3, final Provider<BroadcastDispatcher> provider4) {
        return new LatencyTester_Factory(provider, provider2, provider3, provider4);
    }
    
    public static LatencyTester provideInstance(final Provider<Context> provider, final Provider<BiometricUnlockController> provider2, final Provider<PowerManager> provider3, final Provider<BroadcastDispatcher> provider4) {
        return new LatencyTester(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public LatencyTester get() {
        return provideInstance(this.contextProvider, this.biometricUnlockControllerProvider, this.powerManagerProvider, this.broadcastDispatcherProvider);
    }
}
