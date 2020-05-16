// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.ViewPropertyAnimator;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.view.animation.AccelerateInterpolator;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.internal.view.AppearanceRegion;
import android.view.WindowManager;
import android.view.View;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.internal.annotations.VisibleForTesting;

public class LightsOutNotifController
{
    @VisibleForTesting
    int mAppearance;
    private final CommandQueue.Callbacks mCallback;
    private final CommandQueue mCommandQueue;
    private int mDisplayId;
    private final NotificationEntryListener mEntryListener;
    private final NotificationEntryManager mEntryManager;
    private View mLightsOutNotifView;
    private final WindowManager mWindowManager;
    
    LightsOutNotifController(final WindowManager mWindowManager, final NotificationEntryManager mEntryManager, final CommandQueue mCommandQueue) {
        this.mCallback = new CommandQueue.Callbacks() {
            @Override
            public void onSystemBarAppearanceChanged(final int n, final int mAppearance, final AppearanceRegion[] array, final boolean b) {
                if (n != LightsOutNotifController.this.mDisplayId) {
                    return;
                }
                final LightsOutNotifController this$0 = LightsOutNotifController.this;
                this$0.mAppearance = mAppearance;
                this$0.updateLightsOutView();
            }
        };
        this.mEntryListener = new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                LightsOutNotifController.this.updateLightsOutView();
            }
            
            @Override
            public void onNotificationAdded(final NotificationEntry notificationEntry) {
                LightsOutNotifController.this.updateLightsOutView();
            }
            
            @Override
            public void onPostEntryUpdated(final NotificationEntry notificationEntry) {
                LightsOutNotifController.this.updateLightsOutView();
            }
        };
        this.mWindowManager = mWindowManager;
        this.mEntryManager = mEntryManager;
        this.mCommandQueue = mCommandQueue;
    }
    
    private void destroy() {
        this.mEntryManager.removeNotificationEntryListener(this.mEntryListener);
        this.mCommandQueue.removeCallback(this.mCallback);
    }
    
    private boolean hasActiveNotifications() {
        return this.mEntryManager.hasActiveNotifications();
    }
    
    private void init() {
        this.mDisplayId = this.mWindowManager.getDefaultDisplay().getDisplayId();
        this.mEntryManager.addNotificationEntryListener(this.mEntryListener);
        this.mCommandQueue.addCallback(this.mCallback);
        this.updateLightsOutView();
    }
    
    @VisibleForTesting
    boolean areLightsOut() {
        return (this.mAppearance & 0x4) != 0x0;
    }
    
    @VisibleForTesting
    boolean isShowingDot() {
        return this.mLightsOutNotifView.getVisibility() == 0 && this.mLightsOutNotifView.getAlpha() == 1.0f;
    }
    
    void setLightsOutNotifView(final View mLightsOutNotifView) {
        this.destroy();
        this.mLightsOutNotifView = mLightsOutNotifView;
        if (mLightsOutNotifView != null) {
            mLightsOutNotifView.setVisibility(8);
            this.mLightsOutNotifView.setAlpha(0.0f);
            this.init();
        }
    }
    
    @VisibleForTesting
    boolean shouldShowDot() {
        return this.hasActiveNotifications() && this.areLightsOut();
    }
    
    @VisibleForTesting
    void updateLightsOutView() {
        if (this.mLightsOutNotifView == null) {
            return;
        }
        final boolean shouldShowDot = this.shouldShowDot();
        if (shouldShowDot != this.isShowingDot()) {
            float n = 0.0f;
            if (shouldShowDot) {
                this.mLightsOutNotifView.setAlpha(0.0f);
                this.mLightsOutNotifView.setVisibility(0);
            }
            final ViewPropertyAnimator animate = this.mLightsOutNotifView.animate();
            if (shouldShowDot) {
                n = 1.0f;
            }
            final ViewPropertyAnimator alpha = animate.alpha(n);
            long duration;
            if (shouldShowDot) {
                duration = 750L;
            }
            else {
                duration = 250L;
            }
            alpha.setDuration(duration).setInterpolator((TimeInterpolator)new AccelerateInterpolator(2.0f)).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    final View access$000 = LightsOutNotifController.this.mLightsOutNotifView;
                    float alpha;
                    if (shouldShowDot) {
                        alpha = 1.0f;
                    }
                    else {
                        alpha = 0.0f;
                    }
                    access$000.setAlpha(alpha);
                    final View access$2 = LightsOutNotifController.this.mLightsOutNotifView;
                    int visibility;
                    if (shouldShowDot) {
                        visibility = 0;
                    }
                    else {
                        visibility = 8;
                    }
                    access$2.setVisibility(visibility);
                }
            }).start();
        }
    }
}
