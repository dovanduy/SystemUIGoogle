// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import com.android.systemui.statusbar.policy.ExtensionController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationPersonExtractorPluginBoundary_Factory implements Factory<NotificationPersonExtractorPluginBoundary>
{
    private final Provider<ExtensionController> extensionControllerProvider;
    
    public NotificationPersonExtractorPluginBoundary_Factory(final Provider<ExtensionController> extensionControllerProvider) {
        this.extensionControllerProvider = extensionControllerProvider;
    }
    
    public static NotificationPersonExtractorPluginBoundary_Factory create(final Provider<ExtensionController> provider) {
        return new NotificationPersonExtractorPluginBoundary_Factory(provider);
    }
    
    public static NotificationPersonExtractorPluginBoundary provideInstance(final Provider<ExtensionController> provider) {
        return new NotificationPersonExtractorPluginBoundary(provider.get());
    }
    
    @Override
    public NotificationPersonExtractorPluginBoundary get() {
        return provideInstance(this.extensionControllerProvider);
    }
}
