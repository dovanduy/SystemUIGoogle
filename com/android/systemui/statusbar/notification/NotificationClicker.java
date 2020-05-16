// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import com.android.systemui.DejankUtils;
import java.util.function.Consumer;
import android.util.Log;
import android.os.SystemClock;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Optional;
import com.android.systemui.bubbles.BubbleController;
import android.view.View$OnClickListener;

public final class NotificationClicker implements View$OnClickListener
{
    private final BubbleController mBubbleController;
    private final NotificationActivityStarter mNotificationActivityStarter;
    private final Optional<StatusBar> mStatusBar;
    
    public NotificationClicker(final Optional<StatusBar> mStatusBar, final BubbleController mBubbleController, final NotificationActivityStarter mNotificationActivityStarter) {
        this.mStatusBar = mStatusBar;
        this.mBubbleController = mBubbleController;
        this.mNotificationActivityStarter = mNotificationActivityStarter;
    }
    
    private boolean isMenuVisible(final ExpandableNotificationRow expandableNotificationRow) {
        return expandableNotificationRow.getProvider() != null && expandableNotificationRow.getProvider().isMenuVisible();
    }
    
    public void onClick(final View view) {
        if (!(view instanceof ExpandableNotificationRow)) {
            Log.e("NotificationClicker", "NotificationClicker called on a view that is not a notification row.");
            return;
        }
        this.mStatusBar.ifPresent(new _$$Lambda$NotificationClicker$yfbp07NzZ_fwFSXR0B2xHX63zDM(view));
        final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
        final StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        if (sbn == null) {
            Log.e("NotificationClicker", "NotificationClicker called on an unclickable notification,");
            return;
        }
        if (this.isMenuVisible(expandableNotificationRow)) {
            expandableNotificationRow.animateTranslateNotification(0.0f);
            return;
        }
        if (expandableNotificationRow.isChildInGroup() && this.isMenuVisible(expandableNotificationRow.getNotificationParent())) {
            expandableNotificationRow.getNotificationParent().animateTranslateNotification(0.0f);
            return;
        }
        if (expandableNotificationRow.isSummaryWithChildren() && expandableNotificationRow.areChildrenExpanded()) {
            return;
        }
        expandableNotificationRow.setJustClicked(true);
        DejankUtils.postAfterTraversal(new _$$Lambda$NotificationClicker$CH899qDTz3sQ4q5eBHDPoFomAGA(expandableNotificationRow));
        if (!expandableNotificationRow.getEntry().isBubble()) {
            this.mBubbleController.collapseStack();
        }
        this.mNotificationActivityStarter.onNotificationClicked(sbn, expandableNotificationRow);
    }
    
    public void register(final ExpandableNotificationRow expandableNotificationRow, final StatusBarNotification statusBarNotification) {
        final Notification notification = statusBarNotification.getNotification();
        if (notification.contentIntent == null && notification.fullScreenIntent == null && !expandableNotificationRow.getEntry().isBubble()) {
            expandableNotificationRow.setOnClickListener(null);
        }
        else {
            expandableNotificationRow.setOnClickListener((View$OnClickListener)this);
        }
    }
}
