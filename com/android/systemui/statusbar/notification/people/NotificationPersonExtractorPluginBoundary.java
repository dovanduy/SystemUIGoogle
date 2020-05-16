// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import android.service.notification.StatusBarNotification;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.plugins.NotificationPersonExtractorPlugin;

public final class NotificationPersonExtractorPluginBoundary implements NotificationPersonExtractor
{
    private NotificationPersonExtractorPlugin plugin;
    
    public NotificationPersonExtractorPluginBoundary(final ExtensionController extensionController) {
        Intrinsics.checkParameterIsNotNull(extensionController, "extensionController");
        final ExtensionController.ExtensionBuilder<NotificationPersonExtractorPlugin> extension = extensionController.newExtension(NotificationPersonExtractorPlugin.class);
        extension.withPlugin(NotificationPersonExtractorPlugin.class);
        extension.withCallback(new Consumer<Object>() {
            final /* synthetic */ NotificationPersonExtractorPluginBoundary this$0;
            
            @Override
            public final void accept(final NotificationPersonExtractorPlugin notificationPersonExtractorPlugin) {
                NotificationPersonExtractorPluginBoundary.access$setPlugin$p(this.this$0, notificationPersonExtractorPlugin);
            }
        });
        this.plugin = extension.build().get();
    }
    
    public static final /* synthetic */ void access$setPlugin$p(final NotificationPersonExtractorPluginBoundary notificationPersonExtractorPluginBoundary, final NotificationPersonExtractorPlugin plugin) {
        notificationPersonExtractorPluginBoundary.plugin = plugin;
    }
    
    @Override
    public boolean isPersonNotification(final StatusBarNotification statusBarNotification) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        final NotificationPersonExtractorPlugin plugin = this.plugin;
        return plugin != null && plugin.isPersonNotification(statusBarNotification);
    }
}
