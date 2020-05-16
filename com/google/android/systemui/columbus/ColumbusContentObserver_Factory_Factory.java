// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.app.IActivityManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ColumbusContentObserver_Factory_Factory implements Factory<ColumbusContentObserver.Factory>
{
    private final Provider<IActivityManager> activityManagerServiceProvider;
    private final Provider<ContentResolverWrapper> contentResolverProvider;
    
    public ColumbusContentObserver_Factory_Factory(final Provider<ContentResolverWrapper> contentResolverProvider, final Provider<IActivityManager> activityManagerServiceProvider) {
        this.contentResolverProvider = contentResolverProvider;
        this.activityManagerServiceProvider = activityManagerServiceProvider;
    }
    
    public static ColumbusContentObserver_Factory_Factory create(final Provider<ContentResolverWrapper> provider, final Provider<IActivityManager> provider2) {
        return new ColumbusContentObserver_Factory_Factory(provider, provider2);
    }
    
    public static ColumbusContentObserver.Factory provideInstance(final Provider<ContentResolverWrapper> provider, final Provider<IActivityManager> provider2) {
        return new ColumbusContentObserver.Factory(provider.get(), provider2.get());
    }
    
    @Override
    public ColumbusContentObserver.Factory get() {
        return provideInstance(this.contentResolverProvider, this.activityManagerServiceProvider);
    }
}
