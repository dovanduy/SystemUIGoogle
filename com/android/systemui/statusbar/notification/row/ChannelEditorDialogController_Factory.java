// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.app.INotificationManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ChannelEditorDialogController_Factory implements Factory<ChannelEditorDialogController>
{
    private final Provider<Context> cProvider;
    private final Provider<INotificationManager> noManProvider;
    
    public ChannelEditorDialogController_Factory(final Provider<Context> cProvider, final Provider<INotificationManager> noManProvider) {
        this.cProvider = cProvider;
        this.noManProvider = noManProvider;
    }
    
    public static ChannelEditorDialogController_Factory create(final Provider<Context> provider, final Provider<INotificationManager> provider2) {
        return new ChannelEditorDialogController_Factory(provider, provider2);
    }
    
    public static ChannelEditorDialogController provideInstance(final Provider<Context> provider, final Provider<INotificationManager> provider2) {
        return new ChannelEditorDialogController(provider.get(), provider2.get());
    }
    
    @Override
    public ChannelEditorDialogController get() {
        return provideInstance(this.cProvider, this.noManProvider);
    }
}
