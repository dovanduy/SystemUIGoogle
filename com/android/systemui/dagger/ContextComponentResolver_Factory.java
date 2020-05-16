// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.SystemUI;
import android.app.Service;
import com.android.systemui.recents.RecentsImplementation;
import android.content.BroadcastReceiver;
import android.app.Activity;
import java.util.Map;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ContextComponentResolver_Factory implements Factory<ContextComponentResolver>
{
    private final Provider<Map<Class<?>, Provider<Activity>>> activityCreatorsProvider;
    private final Provider<Map<Class<?>, Provider<BroadcastReceiver>>> broadcastReceiverCreatorsProvider;
    private final Provider<Map<Class<?>, Provider<RecentsImplementation>>> recentsCreatorsProvider;
    private final Provider<Map<Class<?>, Provider<Service>>> serviceCreatorsProvider;
    private final Provider<Map<Class<?>, Provider<SystemUI>>> systemUICreatorsProvider;
    
    public ContextComponentResolver_Factory(final Provider<Map<Class<?>, Provider<Activity>>> activityCreatorsProvider, final Provider<Map<Class<?>, Provider<Service>>> serviceCreatorsProvider, final Provider<Map<Class<?>, Provider<SystemUI>>> systemUICreatorsProvider, final Provider<Map<Class<?>, Provider<RecentsImplementation>>> recentsCreatorsProvider, final Provider<Map<Class<?>, Provider<BroadcastReceiver>>> broadcastReceiverCreatorsProvider) {
        this.activityCreatorsProvider = activityCreatorsProvider;
        this.serviceCreatorsProvider = serviceCreatorsProvider;
        this.systemUICreatorsProvider = systemUICreatorsProvider;
        this.recentsCreatorsProvider = recentsCreatorsProvider;
        this.broadcastReceiverCreatorsProvider = broadcastReceiverCreatorsProvider;
    }
    
    public static ContextComponentResolver_Factory create(final Provider<Map<Class<?>, Provider<Activity>>> provider, final Provider<Map<Class<?>, Provider<Service>>> provider2, final Provider<Map<Class<?>, Provider<SystemUI>>> provider3, final Provider<Map<Class<?>, Provider<RecentsImplementation>>> provider4, final Provider<Map<Class<?>, Provider<BroadcastReceiver>>> provider5) {
        return new ContextComponentResolver_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static ContextComponentResolver provideInstance(final Provider<Map<Class<?>, Provider<Activity>>> provider, final Provider<Map<Class<?>, Provider<Service>>> provider2, final Provider<Map<Class<?>, Provider<SystemUI>>> provider3, final Provider<Map<Class<?>, Provider<RecentsImplementation>>> provider4, final Provider<Map<Class<?>, Provider<BroadcastReceiver>>> provider5) {
        return new ContextComponentResolver(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public ContextComponentResolver get() {
        return provideInstance(this.activityCreatorsProvider, this.serviceCreatorsProvider, this.systemUICreatorsProvider, this.recentsCreatorsProvider, this.broadcastReceiverCreatorsProvider);
    }
}
