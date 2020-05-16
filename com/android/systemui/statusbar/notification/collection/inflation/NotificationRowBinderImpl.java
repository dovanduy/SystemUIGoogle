// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.notification.InflationException;
import android.view.ViewGroup;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Objects;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.service.notification.StatusBarNotification;
import android.content.pm.PackageManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.RowInflaterTask;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationClicker;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.statusbar.notification.icon.IconManager;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import android.content.Context;

public class NotificationRowBinderImpl implements NotificationRowBinder
{
    private BindRowCallback mBindRowCallback;
    private final Context mContext;
    private final ExpandableNotificationRowComponent.Builder mExpandableNotificationRowComponentBuilder;
    private final IconManager mIconManager;
    private NotificationRowContentBinder.InflationCallback mInflationCallback;
    private NotificationListContainer mListContainer;
    private final NotificationMessagingUtil mMessagingUtil;
    private final NotifBindPipeline mNotifBindPipeline;
    private NotificationClicker mNotificationClicker;
    private final NotificationLockscreenUserManager mNotificationLockscreenUserManager;
    private final NotificationRemoteInputManager mNotificationRemoteInputManager;
    private NotificationPresenter mPresenter;
    private final RowContentBindStage mRowContentBindStage;
    private final Provider<RowInflaterTask> mRowInflaterTaskProvider;
    
    public NotificationRowBinderImpl(final Context mContext, final NotificationMessagingUtil mMessagingUtil, final NotificationRemoteInputManager mNotificationRemoteInputManager, final NotificationLockscreenUserManager mNotificationLockscreenUserManager, final NotifBindPipeline mNotifBindPipeline, final RowContentBindStage mRowContentBindStage, final NotificationInterruptStateProvider notificationInterruptStateProvider, final Provider<RowInflaterTask> mRowInflaterTaskProvider, final ExpandableNotificationRowComponent.Builder mExpandableNotificationRowComponentBuilder, final IconManager mIconManager) {
        this.mContext = mContext;
        this.mNotifBindPipeline = mNotifBindPipeline;
        this.mRowContentBindStage = mRowContentBindStage;
        this.mMessagingUtil = mMessagingUtil;
        this.mNotificationRemoteInputManager = mNotificationRemoteInputManager;
        this.mNotificationLockscreenUserManager = mNotificationLockscreenUserManager;
        this.mRowInflaterTaskProvider = mRowInflaterTaskProvider;
        this.mExpandableNotificationRowComponentBuilder = mExpandableNotificationRowComponentBuilder;
        this.mIconManager = mIconManager;
    }
    
    private void bindRow(final NotificationEntry entry, final PackageManager packageManager, final StatusBarNotification statusBarNotification, final ExpandableNotificationRow row) {
        this.mListContainer.bindRow(row);
        this.mNotificationRemoteInputManager.bindRow(row);
        entry.setRow(row);
        row.setEntry(entry);
        this.mNotifBindPipeline.manageRow(entry, row);
        this.mBindRowCallback.onBindRow(entry, packageManager, statusBarNotification, row);
    }
    
    private void updateNotification(final NotificationEntry notificationEntry, final PackageManager packageManager, final StatusBarNotification statusBarNotification, final ExpandableNotificationRow expandableNotificationRow) {
        final boolean b = false;
        try {
            notificationEntry.targetSdk = packageManager.getApplicationInfo(statusBarNotification.getPackageName(), 0).targetSdkVersion;
        }
        catch (PackageManager$NameNotFoundException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed looking up ApplicationInfo for ");
            sb.append(statusBarNotification.getPackageName());
            Log.e("NotificationViewManager", sb.toString(), (Throwable)ex);
        }
        final int targetSdk = notificationEntry.targetSdk;
        boolean legacy = b;
        if (targetSdk >= 9) {
            legacy = b;
            if (targetSdk < 21) {
                legacy = true;
            }
        }
        expandableNotificationRow.setLegacy(legacy);
        this.mIconManager.updateIconTags(notificationEntry, notificationEntry.targetSdk);
        expandableNotificationRow.setOnActivatedListener((ActivatableNotificationView.OnActivatedListener)this.mPresenter);
        final boolean importantMessaging = this.mMessagingUtil.isImportantMessaging(statusBarNotification, notificationEntry.getImportance());
        final boolean ambient = notificationEntry.isAmbient();
        final RowContentBindParams rowContentBindParams = this.mRowContentBindStage.getStageParams(notificationEntry);
        rowContentBindParams.setUseIncreasedCollapsedHeight(importantMessaging);
        rowContentBindParams.setUseLowPriority(notificationEntry.isAmbient());
        expandableNotificationRow.setNeedsRedaction(this.mNotificationLockscreenUserManager.needsRedaction(notificationEntry));
        rowContentBindParams.rebindAllContentViews();
        this.mRowContentBindStage.requestRebind(notificationEntry, new _$$Lambda$NotificationRowBinderImpl$u77yndHVHXF6N6Z8mt2KNczHzyw(this, expandableNotificationRow, importantMessaging, ambient));
        final NotificationClicker mNotificationClicker = this.mNotificationClicker;
        Objects.requireNonNull(mNotificationClicker);
        mNotificationClicker.register(expandableNotificationRow, statusBarNotification);
    }
    
    @Override
    public void inflateViews(final NotificationEntry notificationEntry, final Runnable onDismissRunnable) throws InflationException {
        final ViewGroup viewParentForNotification = this.mListContainer.getViewParentForNotification(notificationEntry);
        final PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, notificationEntry.getSbn().getUser().getIdentifier());
        final StatusBarNotification sbn = notificationEntry.getSbn();
        if (notificationEntry.rowExists()) {
            this.mIconManager.updateIcons(notificationEntry);
            notificationEntry.reset();
            this.updateNotification(notificationEntry, packageManagerForUser, sbn, notificationEntry.getRow());
            notificationEntry.getRowController().setOnDismissRunnable(onDismissRunnable);
        }
        else {
            this.mIconManager.createIcons(notificationEntry);
            this.mRowInflaterTaskProvider.get().inflate(this.mContext, viewParentForNotification, notificationEntry, (RowInflaterTask.RowInflationFinishedListener)new _$$Lambda$NotificationRowBinderImpl$02ioNJJPa5d3UCwTG0KVIOT4blk(this, notificationEntry, onDismissRunnable, packageManagerForUser, sbn));
        }
    }
    
    @Override
    public void onNotificationRankingUpdated(final NotificationEntry notificationEntry, final Integer n, final NotificationUiAdjustment notificationUiAdjustment, final NotificationUiAdjustment notificationUiAdjustment2) {
        if (NotificationUiAdjustment.needReinflate(notificationUiAdjustment, notificationUiAdjustment2)) {
            if (notificationEntry.rowExists()) {
                notificationEntry.reset();
                this.updateNotification(notificationEntry, StatusBar.getPackageManagerForUser(this.mContext, notificationEntry.getSbn().getUser().getIdentifier()), notificationEntry.getSbn(), notificationEntry.getRow());
            }
        }
        else if (n != null && notificationEntry.getImportance() != n && notificationEntry.rowExists()) {
            notificationEntry.getRow().onNotificationRankingUpdated();
        }
    }
    
    public void setInflationCallback(final NotificationRowContentBinder.InflationCallback mInflationCallback) {
        this.mInflationCallback = mInflationCallback;
    }
    
    public void setNotificationClicker(final NotificationClicker mNotificationClicker) {
        this.mNotificationClicker = mNotificationClicker;
    }
    
    public void setUpWithPresenter(final NotificationPresenter mPresenter, final NotificationListContainer mListContainer, final BindRowCallback mBindRowCallback) {
        this.mPresenter = mPresenter;
        this.mListContainer = mListContainer;
        this.mBindRowCallback = mBindRowCallback;
        this.mIconManager.attach();
    }
    
    public interface BindRowCallback
    {
        void onBindRow(final NotificationEntry p0, final PackageManager p1, final StatusBarNotification p2, final ExpandableNotificationRow p3);
    }
}
