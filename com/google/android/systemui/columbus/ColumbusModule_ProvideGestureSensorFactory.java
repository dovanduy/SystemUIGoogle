// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import android.content.Context;
import com.google.android.systemui.columbus.sensors.CHREGestureSensor;
import com.google.android.systemui.columbus.sensors.GestureSensorImpl;
import javax.inject.Provider;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideGestureSensorFactory implements Factory<GestureSensor>
{
    private final Provider<GestureSensorImpl> apGestureSensorProvider;
    private final Provider<CHREGestureSensor> chreGestureSensorProvider;
    private final Provider<Context> contextProvider;
    
    public ColumbusModule_ProvideGestureSensorFactory(final Provider<Context> contextProvider, final Provider<CHREGestureSensor> chreGestureSensorProvider, final Provider<GestureSensorImpl> apGestureSensorProvider) {
        this.contextProvider = contextProvider;
        this.chreGestureSensorProvider = chreGestureSensorProvider;
        this.apGestureSensorProvider = apGestureSensorProvider;
    }
    
    public static ColumbusModule_ProvideGestureSensorFactory create(final Provider<Context> provider, final Provider<CHREGestureSensor> provider2, final Provider<GestureSensorImpl> provider3) {
        return new ColumbusModule_ProvideGestureSensorFactory(provider, provider2, provider3);
    }
    
    public static GestureSensor provideInstance(final Provider<Context> provider, final Provider<CHREGestureSensor> provider2, final Provider<GestureSensorImpl> provider3) {
        return proxyProvideGestureSensor(provider.get(), DoubleCheck.lazy(provider2), DoubleCheck.lazy(provider3));
    }
    
    public static GestureSensor proxyProvideGestureSensor(final Context context, final Lazy<CHREGestureSensor> lazy, final Lazy<GestureSensorImpl> lazy2) {
        final GestureSensor provideGestureSensor = ColumbusModule.provideGestureSensor(context, lazy, lazy2);
        Preconditions.checkNotNull(provideGestureSensor, "Cannot return null from a non-@Nullable @Provides method");
        return provideGestureSensor;
    }
    
    @Override
    public GestureSensor get() {
        return provideInstance(this.contextProvider, this.chreGestureSensorProvider, this.apGestureSensorProvider);
    }
}
