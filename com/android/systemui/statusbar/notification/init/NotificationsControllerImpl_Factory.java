// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.init;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import com.android.systemui.statusbar.notification.headsup.HeadsUpViewBinder;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.notification.headsup.HeadsUpBindController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.bubbles.BubbleController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationsControllerImpl_Factory implements Factory<NotificationsControllerImpl>
{
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotificationGroupAlertTransferHelper> groupAlertTransferHelperProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<HeadsUpBindController> headsUpBindControllerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<HeadsUpViewBinder> headsUpViewBinderProvider;
    private final Provider<NotifPipelineInitializer> newNotifPipelineProvider;
    private final Provider<NotifBindPipelineInitializer> notifBindPipelineInitializerProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<NotificationRowBinderImpl> notificationRowBinderProvider;
    private final Provider<RemoteInputUriController> remoteInputUriControllerProvider;
    
    public NotificationsControllerImpl_Factory(final Provider<FeatureFlags> featureFlagsProvider, final Provider<NotificationListener> notificationListenerProvider, final Provider<NotificationEntryManager> entryManagerProvider, final Provider<NotifPipelineInitializer> newNotifPipelineProvider, final Provider<NotifBindPipelineInitializer> notifBindPipelineInitializerProvider, final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, final Provider<NotificationRowBinderImpl> notificationRowBinderProvider, final Provider<RemoteInputUriController> remoteInputUriControllerProvider, final Provider<BubbleController> bubbleControllerProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<NotificationGroupAlertTransferHelper> groupAlertTransferHelperProvider, final Provider<HeadsUpManager> headsUpManagerProvider, final Provider<HeadsUpBindController> headsUpBindControllerProvider, final Provider<HeadsUpViewBinder> headsUpViewBinderProvider) {
        this.featureFlagsProvider = featureFlagsProvider;
        this.notificationListenerProvider = notificationListenerProvider;
        this.entryManagerProvider = entryManagerProvider;
        this.newNotifPipelineProvider = newNotifPipelineProvider;
        this.notifBindPipelineInitializerProvider = notifBindPipelineInitializerProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.notificationRowBinderProvider = notificationRowBinderProvider;
        this.remoteInputUriControllerProvider = remoteInputUriControllerProvider;
        this.bubbleControllerProvider = bubbleControllerProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.groupAlertTransferHelperProvider = groupAlertTransferHelperProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.headsUpBindControllerProvider = headsUpBindControllerProvider;
        this.headsUpViewBinderProvider = headsUpViewBinderProvider;
    }
    
    public static NotificationsControllerImpl_Factory create(final Provider<FeatureFlags> provider, final Provider<NotificationListener> provider2, final Provider<NotificationEntryManager> provider3, final Provider<NotifPipelineInitializer> provider4, final Provider<NotifBindPipelineInitializer> provider5, final Provider<DeviceProvisionedController> provider6, final Provider<NotificationRowBinderImpl> provider7, final Provider<RemoteInputUriController> provider8, final Provider<BubbleController> provider9, final Provider<NotificationGroupManager> provider10, final Provider<NotificationGroupAlertTransferHelper> provider11, final Provider<HeadsUpManager> provider12, final Provider<HeadsUpBindController> provider13, final Provider<HeadsUpViewBinder> provider14) {
        return new NotificationsControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14);
    }
    
    public static NotificationsControllerImpl provideInstance(final Provider<FeatureFlags> provider, final Provider<NotificationListener> provider2, final Provider<NotificationEntryManager> provider3, final Provider<NotifPipelineInitializer> provider4, final Provider<NotifBindPipelineInitializer> provider5, final Provider<DeviceProvisionedController> provider6, final Provider<NotificationRowBinderImpl> provider7, final Provider<RemoteInputUriController> provider8, final Provider<BubbleController> provider9, final Provider<NotificationGroupManager> provider10, final Provider<NotificationGroupAlertTransferHelper> provider11, final Provider<HeadsUpManager> provider12, final Provider<HeadsUpBindController> provider13, final Provider<HeadsUpViewBinder> provider14) {
        return new NotificationsControllerImpl(provider.get(), provider2.get(), provider3.get(), DoubleCheck.lazy(provider4), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get());
    }
    
    @Override
    public NotificationsControllerImpl get() {
        return provideInstance(this.featureFlagsProvider, this.notificationListenerProvider, this.entryManagerProvider, this.newNotifPipelineProvider, this.notifBindPipelineInitializerProvider, this.deviceProvisionedControllerProvider, this.notificationRowBinderProvider, this.remoteInputUriControllerProvider, this.bubbleControllerProvider, this.groupManagerProvider, this.groupAlertTransferHelperProvider, this.headsUpManagerProvider, this.headsUpBindControllerProvider, this.headsUpViewBinderProvider);
    }
}
