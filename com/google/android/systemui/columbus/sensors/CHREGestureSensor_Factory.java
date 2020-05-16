// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class CHREGestureSensor_Factory implements Factory<CHREGestureSensor>
{
    private final Provider<Context> contextProvider;
    private final Provider<GestureConfiguration> gestureConfigurationProvider;
    private final Provider<GestureController> gestureControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    
    public CHREGestureSensor_Factory(final Provider<Context> contextProvider, final Provider<GestureConfiguration> gestureConfigurationProvider, final Provider<GestureController> gestureControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider) {
        this.contextProvider = contextProvider;
        this.gestureConfigurationProvider = gestureConfigurationProvider;
        this.gestureControllerProvider = gestureControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
    }
    
    public static CHREGestureSensor_Factory create(final Provider<Context> provider, final Provider<GestureConfiguration> provider2, final Provider<GestureController> provider3, final Provider<StatusBarStateController> provider4, final Provider<WakefulnessLifecycle> provider5) {
        return new CHREGestureSensor_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static CHREGestureSensor provideInstance(final Provider<Context> provider, final Provider<GestureConfiguration> provider2, final Provider<GestureController> provider3, final Provider<StatusBarStateController> provider4, final Provider<WakefulnessLifecycle> provider5) {
        return new CHREGestureSensor(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public CHREGestureSensor get() {
        return provideInstance(this.contextProvider, this.gestureConfigurationProvider, this.gestureControllerProvider, this.statusBarStateControllerProvider, this.wakefulnessLifecycleProvider);
    }
}
