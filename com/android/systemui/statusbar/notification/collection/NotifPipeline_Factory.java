// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifPipeline_Factory implements Factory<NotifPipeline>
{
    private final Provider<NotifCollection> notifCollectionProvider;
    private final Provider<ShadeListBuilder> shadeListBuilderProvider;
    
    public NotifPipeline_Factory(final Provider<NotifCollection> notifCollectionProvider, final Provider<ShadeListBuilder> shadeListBuilderProvider) {
        this.notifCollectionProvider = notifCollectionProvider;
        this.shadeListBuilderProvider = shadeListBuilderProvider;
    }
    
    public static NotifPipeline_Factory create(final Provider<NotifCollection> provider, final Provider<ShadeListBuilder> provider2) {
        return new NotifPipeline_Factory(provider, provider2);
    }
    
    public static NotifPipeline provideInstance(final Provider<NotifCollection> provider, final Provider<ShadeListBuilder> provider2) {
        return new NotifPipeline(provider.get(), provider2.get());
    }
    
    @Override
    public NotifPipeline get() {
        return provideInstance(this.notifCollectionProvider, this.shadeListBuilderProvider);
    }
}
