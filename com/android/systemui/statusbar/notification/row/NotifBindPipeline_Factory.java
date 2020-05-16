// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.os.Looper;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifBindPipeline_Factory implements Factory<NotifBindPipeline>
{
    private final Provider<CommonNotifCollection> collectionProvider;
    private final Provider<NotifBindPipelineLogger> loggerProvider;
    private final Provider<Looper> mainLooperProvider;
    
    public NotifBindPipeline_Factory(final Provider<CommonNotifCollection> collectionProvider, final Provider<NotifBindPipelineLogger> loggerProvider, final Provider<Looper> mainLooperProvider) {
        this.collectionProvider = collectionProvider;
        this.loggerProvider = loggerProvider;
        this.mainLooperProvider = mainLooperProvider;
    }
    
    public static NotifBindPipeline_Factory create(final Provider<CommonNotifCollection> provider, final Provider<NotifBindPipelineLogger> provider2, final Provider<Looper> provider3) {
        return new NotifBindPipeline_Factory(provider, provider2, provider3);
    }
    
    public static NotifBindPipeline provideInstance(final Provider<CommonNotifCollection> provider, final Provider<NotifBindPipelineLogger> provider2, final Provider<Looper> provider3) {
        return new NotifBindPipeline(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public NotifBindPipeline get() {
        return provideInstance(this.collectionProvider, this.loggerProvider, this.mainLooperProvider);
    }
}
