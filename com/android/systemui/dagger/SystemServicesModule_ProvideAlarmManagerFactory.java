// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.app.AlarmManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideAlarmManagerFactory implements Factory<AlarmManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideAlarmManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideAlarmManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideAlarmManagerFactory(provider);
    }
    
    public static AlarmManager provideInstance(final Provider<Context> provider) {
        return proxyProvideAlarmManager(provider.get());
    }
    
    public static AlarmManager proxyProvideAlarmManager(final Context context) {
        final AlarmManager provideAlarmManager = SystemServicesModule.provideAlarmManager(context);
        Preconditions.checkNotNull(provideAlarmManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideAlarmManager;
    }
    
    @Override
    public AlarmManager get() {
        return provideInstance(this.contextProvider);
    }
}
