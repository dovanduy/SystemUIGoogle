// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.notification.row.RowInflaterTask;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.icon.IconManager;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationRowBinderImpl_Factory implements Factory<NotificationRowBinderImpl>
{
    private final Provider<Context> contextProvider;
    private final Provider<ExpandableNotificationRowComponent.Builder> expandableNotificationRowComponentBuilderProvider;
    private final Provider<IconManager> iconManagerProvider;
    private final Provider<NotifBindPipeline> notifBindPipelineProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptionStateProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<NotificationMessagingUtil> notificationMessagingUtilProvider;
    private final Provider<NotificationRemoteInputManager> notificationRemoteInputManagerProvider;
    private final Provider<RowContentBindStage> rowContentBindStageProvider;
    private final Provider<RowInflaterTask> rowInflaterTaskProvider;
    
    public NotificationRowBinderImpl_Factory(final Provider<Context> contextProvider, final Provider<NotificationMessagingUtil> notificationMessagingUtilProvider, final Provider<NotificationRemoteInputManager> notificationRemoteInputManagerProvider, final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, final Provider<NotifBindPipeline> notifBindPipelineProvider, final Provider<RowContentBindStage> rowContentBindStageProvider, final Provider<NotificationInterruptStateProvider> notificationInterruptionStateProvider, final Provider<RowInflaterTask> rowInflaterTaskProvider, final Provider<ExpandableNotificationRowComponent.Builder> expandableNotificationRowComponentBuilderProvider, final Provider<IconManager> iconManagerProvider) {
        this.contextProvider = contextProvider;
        this.notificationMessagingUtilProvider = notificationMessagingUtilProvider;
        this.notificationRemoteInputManagerProvider = notificationRemoteInputManagerProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.notifBindPipelineProvider = notifBindPipelineProvider;
        this.rowContentBindStageProvider = rowContentBindStageProvider;
        this.notificationInterruptionStateProvider = notificationInterruptionStateProvider;
        this.rowInflaterTaskProvider = rowInflaterTaskProvider;
        this.expandableNotificationRowComponentBuilderProvider = expandableNotificationRowComponentBuilderProvider;
        this.iconManagerProvider = iconManagerProvider;
    }
    
    public static NotificationRowBinderImpl_Factory create(final Provider<Context> provider, final Provider<NotificationMessagingUtil> provider2, final Provider<NotificationRemoteInputManager> provider3, final Provider<NotificationLockscreenUserManager> provider4, final Provider<NotifBindPipeline> provider5, final Provider<RowContentBindStage> provider6, final Provider<NotificationInterruptStateProvider> provider7, final Provider<RowInflaterTask> provider8, final Provider<ExpandableNotificationRowComponent.Builder> provider9, final Provider<IconManager> provider10) {
        return new NotificationRowBinderImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
    }
    
    public static NotificationRowBinderImpl provideInstance(final Provider<Context> provider, final Provider<NotificationMessagingUtil> provider2, final Provider<NotificationRemoteInputManager> provider3, final Provider<NotificationLockscreenUserManager> provider4, final Provider<NotifBindPipeline> provider5, final Provider<RowContentBindStage> provider6, final Provider<NotificationInterruptStateProvider> provider7, final Provider<RowInflaterTask> provider8, final Provider<ExpandableNotificationRowComponent.Builder> provider9, final Provider<IconManager> provider10) {
        return new NotificationRowBinderImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8, provider9.get(), provider10.get());
    }
    
    @Override
    public NotificationRowBinderImpl get() {
        return provideInstance(this.contextProvider, this.notificationMessagingUtilProvider, this.notificationRemoteInputManagerProvider, this.notificationLockscreenUserManagerProvider, this.notifBindPipelineProvider, this.rowContentBindStageProvider, this.notificationInterruptionStateProvider, this.rowInflaterTaskProvider, this.expandableNotificationRowComponentBuilderProvider, this.iconManagerProvider);
    }
}
