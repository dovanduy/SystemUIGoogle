// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.phone.LockIcon;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.systemui.util.InjectionInflationController;
import android.content.Context;

public class SuperStatusBarViewFactory
{
    private final Context mContext;
    private final InjectionInflationController mInjectionInflationController;
    private final LockscreenLockIconController mLockIconController;
    private final NotificationRowComponent.Builder mNotificationRowComponentBuilder;
    private NotificationShadeWindowView mNotificationShadeWindowView;
    private NotificationShelf mNotificationShelf;
    private StatusBarWindowView mStatusBarWindowView;
    
    public SuperStatusBarViewFactory(final Context mContext, final InjectionInflationController mInjectionInflationController, final NotificationRowComponent.Builder mNotificationRowComponentBuilder, final LockscreenLockIconController mLockIconController) {
        this.mContext = mContext;
        this.mInjectionInflationController = mInjectionInflationController;
        this.mNotificationRowComponentBuilder = mNotificationRowComponentBuilder;
        this.mLockIconController = mLockIconController;
    }
    
    public NotificationShadeWindowView getNotificationShadeWindowView() {
        final NotificationShadeWindowView mNotificationShadeWindowView = this.mNotificationShadeWindowView;
        if (mNotificationShadeWindowView != null) {
            return mNotificationShadeWindowView;
        }
        final NotificationShadeWindowView mNotificationShadeWindowView2 = (NotificationShadeWindowView)this.mInjectionInflationController.injectable(LayoutInflater.from(this.mContext)).inflate(R$layout.super_notification_shade, (ViewGroup)null);
        if ((this.mNotificationShadeWindowView = mNotificationShadeWindowView2) != null) {
            final LockIcon lockIcon = (LockIcon)mNotificationShadeWindowView2.findViewById(R$id.lock_icon);
            if (lockIcon != null) {
                this.mLockIconController.attach(lockIcon);
            }
            return this.mNotificationShadeWindowView;
        }
        throw new IllegalStateException("R.layout.super_notification_shade could not be properly inflated");
    }
    
    public NotificationShelf getNotificationShelf(final ViewGroup viewGroup) {
        final NotificationShelf mNotificationShelf = this.mNotificationShelf;
        if (mNotificationShelf != null) {
            return mNotificationShelf;
        }
        final NotificationShelf mNotificationShelf2 = (NotificationShelf)this.mInjectionInflationController.injectable(LayoutInflater.from(this.mContext)).inflate(R$layout.status_bar_notification_shelf, viewGroup, false);
        this.mNotificationShelf = mNotificationShelf2;
        this.mNotificationRowComponentBuilder.activatableNotificationView(mNotificationShelf2).build().getActivatableNotificationViewController().init();
        final NotificationShelf mNotificationShelf3 = this.mNotificationShelf;
        if (mNotificationShelf3 != null) {
            return mNotificationShelf3;
        }
        throw new IllegalStateException("R.layout.status_bar_notification_shelf could not be properly inflated");
    }
    
    public StatusBarWindowView getStatusBarWindowView() {
        final StatusBarWindowView mStatusBarWindowView = this.mStatusBarWindowView;
        if (mStatusBarWindowView != null) {
            return mStatusBarWindowView;
        }
        final StatusBarWindowView mStatusBarWindowView2 = (StatusBarWindowView)this.mInjectionInflationController.injectable(LayoutInflater.from(this.mContext)).inflate(R$layout.super_status_bar, (ViewGroup)null);
        if ((this.mStatusBarWindowView = mStatusBarWindowView2) != null) {
            return mStatusBarWindowView2;
        }
        throw new IllegalStateException("R.layout.super_status_bar could not be properly inflated");
    }
}
