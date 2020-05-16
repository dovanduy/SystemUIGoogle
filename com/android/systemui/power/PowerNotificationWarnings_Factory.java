// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import android.content.Context;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PowerNotificationWarnings_Factory implements Factory<PowerNotificationWarnings>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<Context> contextProvider;
    
    public PowerNotificationWarnings_Factory(final Provider<Context> contextProvider, final Provider<ActivityStarter> activityStarterProvider) {
        this.contextProvider = contextProvider;
        this.activityStarterProvider = activityStarterProvider;
    }
    
    public static PowerNotificationWarnings_Factory create(final Provider<Context> provider, final Provider<ActivityStarter> provider2) {
        return new PowerNotificationWarnings_Factory(provider, provider2);
    }
    
    public static PowerNotificationWarnings provideInstance(final Provider<Context> provider, final Provider<ActivityStarter> provider2) {
        return new PowerNotificationWarnings(provider.get(), provider2.get());
    }
    
    @Override
    public PowerNotificationWarnings get() {
        return provideInstance(this.contextProvider, this.activityStarterProvider);
    }
}
