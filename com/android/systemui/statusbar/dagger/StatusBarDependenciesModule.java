// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.dagger;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.notification.DynamicChildBindController;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.util.DeviceConfigProxy;
import java.util.concurrent.Executor;
import com.android.keyguard.KeyguardMediaPlayer;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import com.android.systemui.statusbar.NotificationListener;
import android.os.Handler;
import android.app.NotificationManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.tracing.ProtoTracer;
import android.content.Context;

public interface StatusBarDependenciesModule
{
    default CommandQueue provideCommandQueue(final Context context, final ProtoTracer protoTracer) {
        return new CommandQueue(context, protoTracer);
    }
    
    default NotificationListener provideNotificationListener(final Context context, final NotificationManager notificationManager, final Handler handler) {
        return new NotificationListener(context, notificationManager, handler);
    }
    
    default NotificationMediaManager provideNotificationMediaManager(final Context context, final Lazy<StatusBar> lazy, final Lazy<NotificationShadeWindowController> lazy2, final NotificationEntryManager notificationEntryManager, final MediaArtworkProcessor mediaArtworkProcessor, final KeyguardBypassController keyguardBypassController, final KeyguardMediaPlayer keyguardMediaPlayer, final Executor executor, final DeviceConfigProxy deviceConfigProxy) {
        return new NotificationMediaManager(context, lazy, lazy2, notificationEntryManager, mediaArtworkProcessor, keyguardBypassController, keyguardMediaPlayer, executor, deviceConfigProxy);
    }
    
    default NotificationRemoteInputManager provideNotificationRemoteInputManager(final Context context, final NotificationLockscreenUserManager notificationLockscreenUserManager, final SmartReplyController smartReplyController, final NotificationEntryManager notificationEntryManager, final Lazy<StatusBar> lazy, final StatusBarStateController statusBarStateController, final Handler handler, final RemoteInputUriController remoteInputUriController) {
        return new NotificationRemoteInputManager(context, notificationLockscreenUserManager, smartReplyController, notificationEntryManager, lazy, statusBarStateController, handler, remoteInputUriController);
    }
    
    default NotificationViewHierarchyManager provideNotificationViewHierarchyManager(final Context context, final Handler handler, final NotificationLockscreenUserManager notificationLockscreenUserManager, final NotificationGroupManager notificationGroupManager, final VisualStabilityManager visualStabilityManager, final StatusBarStateController statusBarStateController, final NotificationEntryManager notificationEntryManager, final KeyguardBypassController keyguardBypassController, final BubbleController bubbleController, final DynamicPrivacyController dynamicPrivacyController, final ForegroundServiceSectionController foregroundServiceSectionController, final DynamicChildBindController dynamicChildBindController) {
        return new NotificationViewHierarchyManager(context, handler, notificationLockscreenUserManager, notificationGroupManager, visualStabilityManager, statusBarStateController, notificationEntryManager, keyguardBypassController, bubbleController, dynamicPrivacyController, foregroundServiceSectionController, dynamicChildBindController);
    }
    
    default SmartReplyController provideSmartReplyController(final NotificationEntryManager notificationEntryManager, final IStatusBarService statusBarService) {
        return new SmartReplyController(notificationEntryManager, statusBarService);
    }
}
