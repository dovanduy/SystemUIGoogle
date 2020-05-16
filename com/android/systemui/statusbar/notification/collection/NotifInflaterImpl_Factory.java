// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifInflaterImpl_Factory implements Factory<NotifInflaterImpl>
{
    private final Provider<NotifInflationErrorManager> errorManagerProvider;
    private final Provider<NotifCollection> notifCollectionProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;
    
    public NotifInflaterImpl_Factory(final Provider<IStatusBarService> statusBarServiceProvider, final Provider<NotifCollection> notifCollectionProvider, final Provider<NotifInflationErrorManager> errorManagerProvider, final Provider<NotifPipeline> notifPipelineProvider) {
        this.statusBarServiceProvider = statusBarServiceProvider;
        this.notifCollectionProvider = notifCollectionProvider;
        this.errorManagerProvider = errorManagerProvider;
        this.notifPipelineProvider = notifPipelineProvider;
    }
    
    public static NotifInflaterImpl_Factory create(final Provider<IStatusBarService> provider, final Provider<NotifCollection> provider2, final Provider<NotifInflationErrorManager> provider3, final Provider<NotifPipeline> provider4) {
        return new NotifInflaterImpl_Factory(provider, provider2, provider3, provider4);
    }
    
    public static NotifInflaterImpl provideInstance(final Provider<IStatusBarService> provider, final Provider<NotifCollection> provider2, final Provider<NotifInflationErrorManager> provider3, final Provider<NotifPipeline> provider4) {
        return new NotifInflaterImpl(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public NotifInflaterImpl get() {
        return provideInstance(this.statusBarServiceProvider, this.notifCollectionProvider, this.errorManagerProvider, this.notifPipelineProvider);
    }
}
