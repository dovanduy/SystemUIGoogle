// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import android.animation.TimeInterpolator;
import android.util.Property;
import java.util.Iterator;
import java.util.LinkedHashSet;
import com.android.systemui.Interpolators;
import kotlin.jvm.internal.Intrinsics;
import java.util.ArrayList;
import android.view.animation.Interpolator;
import android.animation.ObjectAnimator;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Set;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.PanelExpansionListener;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

public final class NotificationWakeUpCoordinator implements OnHeadsUpChangedListener, StateListener, PanelExpansionListener
{
    private final KeyguardBypassController bypassController;
    private boolean collapsedEnoughToHide;
    private final DozeParameters dozeParameters;
    private boolean fullyAwake;
    private float mDozeAmount;
    private final Set<NotificationEntry> mEntrySetToClearWhenFinished;
    private final HeadsUpManager mHeadsUpManager;
    private float mLinearDozeAmount;
    private float mLinearVisibilityAmount;
    private final NotificationWakeUpCoordinator$mNotificationVisibility.NotificationWakeUpCoordinator$mNotificationVisibility$1 mNotificationVisibility;
    private float mNotificationVisibleAmount;
    private boolean mNotificationsVisible;
    private boolean mNotificationsVisibleForExpansion;
    private NotificationStackScrollLayout mStackScroller;
    private float mVisibilityAmount;
    private ObjectAnimator mVisibilityAnimator;
    private Interpolator mVisibilityInterpolator;
    private boolean notificationsFullyHidden;
    private boolean pulseExpanding;
    private boolean pulsing;
    private int state;
    private final StatusBarStateController statusBarStateController;
    private final ArrayList<WakeUpListener> wakeUpListeners;
    private boolean wakingUp;
    private boolean willWakeUp;
    
    public NotificationWakeUpCoordinator(final HeadsUpManager mHeadsUpManager, final StatusBarStateController statusBarStateController, final KeyguardBypassController bypassController, final DozeParameters dozeParameters) {
        Intrinsics.checkParameterIsNotNull(mHeadsUpManager, "mHeadsUpManager");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(bypassController, "bypassController");
        Intrinsics.checkParameterIsNotNull(dozeParameters, "dozeParameters");
        this.mHeadsUpManager = mHeadsUpManager;
        this.statusBarStateController = statusBarStateController;
        this.bypassController = bypassController;
        this.dozeParameters = dozeParameters;
        this.mNotificationVisibility = new NotificationWakeUpCoordinator$mNotificationVisibility.NotificationWakeUpCoordinator$mNotificationVisibility$1("notificationVisibility");
        this.mVisibilityInterpolator = Interpolators.FAST_OUT_SLOW_IN_REVERSE;
        this.mEntrySetToClearWhenFinished = new LinkedHashSet<NotificationEntry>();
        this.wakeUpListeners = new ArrayList<WakeUpListener>();
        this.state = 1;
        this.mHeadsUpManager.addListener(this);
        this.statusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.addListener((WakeUpListener)new WakeUpListener() {
            final /* synthetic */ NotificationWakeUpCoordinator this$0;
            
            @Override
            public void onFullyHiddenChanged(final boolean b) {
                if (b && NotificationWakeUpCoordinator.access$getMNotificationsVisibleForExpansion$p(this.this$0)) {
                    this.this$0.setNotificationsVisibleForExpansion(false, false, false);
                }
            }
        });
    }
    
    public static final /* synthetic */ boolean access$getMNotificationsVisibleForExpansion$p(final NotificationWakeUpCoordinator notificationWakeUpCoordinator) {
        return notificationWakeUpCoordinator.mNotificationsVisibleForExpansion;
    }
    
    private final void handleAnimationFinished() {
        if (this.mLinearDozeAmount == 0.0f || this.mLinearVisibilityAmount == 0.0f) {
            final Iterator<NotificationEntry> iterator = this.mEntrySetToClearWhenFinished.iterator();
            while (iterator.hasNext()) {
                iterator.next().setHeadsUpAnimatingAway(false);
            }
            this.mEntrySetToClearWhenFinished.clear();
        }
    }
    
    private final void notifyAnimationStart(final boolean b) {
        final NotificationStackScrollLayout mStackScroller = this.mStackScroller;
        if (mStackScroller != null) {
            mStackScroller.notifyHideAnimationStart(b ^ true);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        throw null;
    }
    
    private final void setNotificationsFullyHidden(final boolean notificationsFullyHidden) {
        if (this.notificationsFullyHidden != notificationsFullyHidden) {
            this.notificationsFullyHidden = notificationsFullyHidden;
            final Iterator<WakeUpListener> iterator = this.wakeUpListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onFullyHiddenChanged(notificationsFullyHidden);
            }
        }
    }
    
    private final void setNotificationsVisible(final boolean mNotificationsVisible, final boolean b, final boolean b2) {
        if (this.mNotificationsVisible == mNotificationsVisible) {
            return;
        }
        this.mNotificationsVisible = mNotificationsVisible;
        final ObjectAnimator mVisibilityAnimator = this.mVisibilityAnimator;
        if (mVisibilityAnimator != null) {
            mVisibilityAnimator.cancel();
        }
        if (b) {
            this.notifyAnimationStart(mNotificationsVisible);
            this.startVisibilityAnimation(b2);
        }
        else {
            float visibilityAmount;
            if (mNotificationsVisible) {
                visibilityAmount = 1.0f;
            }
            else {
                visibilityAmount = 0.0f;
            }
            this.setVisibilityAmount(visibilityAmount);
        }
    }
    
    private final void setVisibilityAmount(final float mLinearVisibilityAmount) {
        this.mLinearVisibilityAmount = mLinearVisibilityAmount;
        this.mVisibilityAmount = this.mVisibilityInterpolator.getInterpolation(mLinearVisibilityAmount);
        this.handleAnimationFinished();
        this.updateHideAmount();
    }
    
    private final boolean shouldAnimateVisibility() {
        return this.dozeParameters.getAlwaysOn() && !this.dozeParameters.getDisplayNeedsBlanking();
    }
    
    private final void startVisibilityAnimation(final boolean b) {
        final float mNotificationVisibleAmount = this.mNotificationVisibleAmount;
        float n = 0.0f;
        if (mNotificationVisibleAmount == 0.0f || mNotificationVisibleAmount == 1.0f) {
            Interpolator mVisibilityInterpolator;
            if (this.mNotificationsVisible) {
                mVisibilityInterpolator = Interpolators.TOUCH_RESPONSE;
            }
            else {
                mVisibilityInterpolator = Interpolators.FAST_OUT_SLOW_IN_REVERSE;
            }
            this.mVisibilityInterpolator = mVisibilityInterpolator;
        }
        if (this.mNotificationsVisible) {
            n = 1.0f;
        }
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)this, (Property)this.mNotificationVisibility, new float[] { n });
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        long duration = 500;
        if (b) {
            duration /= (long)1.5f;
        }
        ofFloat.setDuration(duration);
        ofFloat.start();
        this.mVisibilityAnimator = ofFloat;
    }
    
    private final boolean updateDozeAmountIfBypass() {
        if (this.bypassController.getBypassEnabled()) {
            float n = 1.0f;
            if (this.statusBarStateController.getState() == 0 || this.statusBarStateController.getState() == 2) {
                n = 0.0f;
            }
            this.setDozeAmount(n, n);
            return true;
        }
        return false;
    }
    
    private final void updateHideAmount() {
        final float min = Math.min(1.0f - this.mLinearVisibilityAmount, this.mLinearDozeAmount);
        final float min2 = Math.min(1.0f - this.mVisibilityAmount, this.mDozeAmount);
        final NotificationStackScrollLayout mStackScroller = this.mStackScroller;
        if (mStackScroller != null) {
            mStackScroller.setHideAmount(min, min2);
            this.setNotificationsFullyHidden(min == 1.0f);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        throw null;
    }
    
    private final void updateNotificationVisibility(final boolean b, final boolean b2) {
        final boolean mNotificationsVisibleForExpansion = this.mNotificationsVisibleForExpansion;
        final boolean b3 = false;
        final boolean b4 = mNotificationsVisibleForExpansion || this.mHeadsUpManager.hasNotifications();
        boolean b5 = b3;
        if (b4) {
            b5 = b3;
            if (this.getCanShowPulsingHuns()) {
                b5 = true;
            }
        }
        if (!b5 && this.mNotificationsVisible && (this.wakingUp || this.willWakeUp) && this.mDozeAmount != 0.0f) {
            return;
        }
        this.setNotificationsVisible(b5, b, b2);
    }
    
    public final void addListener(final WakeUpListener e) {
        Intrinsics.checkParameterIsNotNull(e, "listener");
        this.wakeUpListeners.add(e);
    }
    
    public final boolean getCanShowPulsingHuns() {
        boolean pulsing = this.pulsing;
        if (this.bypassController.getBypassEnabled()) {
            pulsing = (pulsing || ((this.wakingUp || this.willWakeUp || this.fullyAwake) && this.statusBarStateController.getState() == 1));
            if (this.collapsedEnoughToHide) {
                pulsing = false;
            }
        }
        return pulsing;
    }
    
    public final boolean getNotificationsFullyHidden() {
        return this.notificationsFullyHidden;
    }
    
    public final float getWakeUpHeight() {
        final NotificationStackScrollLayout mStackScroller = this.mStackScroller;
        if (mStackScroller != null) {
            return mStackScroller.getWakeUpHeight();
        }
        Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        throw null;
    }
    
    public final boolean isPulseExpanding() {
        final NotificationStackScrollLayout mStackScroller = this.mStackScroller;
        if (mStackScroller != null) {
            return mStackScroller.isPulseExpanding();
        }
        Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        throw null;
    }
    
    @Override
    public void onDozeAmountChanged(final float n, final float n2) {
        if (this.updateDozeAmountIfBypass()) {
            return;
        }
        if (n != 1.0f && n != 0.0f) {
            final float mLinearDozeAmount = this.mLinearDozeAmount;
            if (mLinearDozeAmount == 0.0f || mLinearDozeAmount == 1.0f) {
                this.notifyAnimationStart(this.mLinearDozeAmount == 1.0f);
            }
        }
        this.setDozeAmount(n, n2);
    }
    
    @Override
    public void onDozingChanged(final boolean b) {
        if (b) {
            this.setNotificationsVisible(false, false, false);
        }
    }
    
    @Override
    public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        final boolean shouldAnimateVisibility = this.shouldAnimateVisibility();
        boolean b2;
        if (!b) {
            b2 = shouldAnimateVisibility;
            if (this.mLinearDozeAmount != 0.0f) {
                b2 = shouldAnimateVisibility;
                if (this.mLinearVisibilityAmount != 0.0f) {
                    if (notificationEntry.isRowDismissed()) {
                        b2 = false;
                    }
                    else {
                        b2 = shouldAnimateVisibility;
                        if (!this.wakingUp) {
                            b2 = shouldAnimateVisibility;
                            if (!this.willWakeUp) {
                                notificationEntry.setHeadsUpAnimatingAway(true);
                                this.mEntrySetToClearWhenFinished.add(notificationEntry);
                                b2 = shouldAnimateVisibility;
                            }
                        }
                    }
                }
            }
        }
        else {
            b2 = shouldAnimateVisibility;
            if (this.mEntrySetToClearWhenFinished.contains(notificationEntry)) {
                this.mEntrySetToClearWhenFinished.remove(notificationEntry);
                notificationEntry.setHeadsUpAnimatingAway(false);
                b2 = shouldAnimateVisibility;
            }
        }
        this.updateNotificationVisibility(b2, false);
    }
    
    @Override
    public void onPanelExpansionChanged(final float n, final boolean b) {
        final boolean collapsedEnoughToHide = n <= 0.9f;
        if (collapsedEnoughToHide != this.collapsedEnoughToHide) {
            final boolean canShowPulsingHuns = this.getCanShowPulsingHuns();
            this.collapsedEnoughToHide = collapsedEnoughToHide;
            if (canShowPulsingHuns && !this.getCanShowPulsingHuns()) {
                this.updateNotificationVisibility(true, true);
                this.mHeadsUpManager.releaseAllImmediately();
            }
        }
    }
    
    @Override
    public void onStateChanged(final int state) {
        this.updateDozeAmountIfBypass();
        if (this.bypassController.getBypassEnabled() && state == 1 && this.state == 2 && (!this.statusBarStateController.isDozing() || this.shouldAnimateVisibility())) {
            this.setNotificationsVisible(true, false, false);
            this.setNotificationsVisible(false, true, false);
        }
        this.state = state;
    }
    
    public final void removeListener(final WakeUpListener o) {
        Intrinsics.checkParameterIsNotNull(o, "listener");
        this.wakeUpListeners.remove(o);
    }
    
    public final void setDozeAmount(final float mLinearDozeAmount, final float n) {
        final boolean b = mLinearDozeAmount != this.mLinearDozeAmount;
        this.mLinearDozeAmount = mLinearDozeAmount;
        this.mDozeAmount = n;
        final NotificationStackScrollLayout mStackScroller = this.mStackScroller;
        if (mStackScroller != null) {
            mStackScroller.setDozeAmount(n);
            this.updateHideAmount();
            if (b && mLinearDozeAmount == 0.0f) {
                this.setNotificationsVisible(false, false, false);
                this.setNotificationsVisibleForExpansion(false, false, false);
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        throw null;
    }
    
    public final void setFullyAwake(final boolean fullyAwake) {
        this.fullyAwake = fullyAwake;
    }
    
    public final void setIconAreaController(final NotificationIconAreaController notificationIconAreaController) {
        Intrinsics.checkParameterIsNotNull(notificationIconAreaController, "<set-?>");
    }
    
    public final void setNotificationsVisibleForExpansion(final boolean mNotificationsVisibleForExpansion, final boolean b, final boolean b2) {
        this.mNotificationsVisibleForExpansion = mNotificationsVisibleForExpansion;
        this.updateNotificationVisibility(b, b2);
        if (!mNotificationsVisibleForExpansion && this.mNotificationsVisible) {
            this.mHeadsUpManager.releaseAllImmediately();
        }
    }
    
    public final float setPulseHeight(float setPulseHeight) {
        final NotificationStackScrollLayout mStackScroller = this.mStackScroller;
        if (mStackScroller != null) {
            setPulseHeight = mStackScroller.setPulseHeight(setPulseHeight);
            if (this.bypassController.getBypassEnabled()) {
                setPulseHeight = 0.0f;
            }
            return setPulseHeight;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        throw null;
    }
    
    public final void setPulsing(final boolean pulsing) {
        this.pulsing = pulsing;
        if (pulsing) {
            this.updateNotificationVisibility(this.shouldAnimateVisibility(), false);
        }
    }
    
    public final void setStackScroller(final NotificationStackScrollLayout mStackScroller) {
        Intrinsics.checkParameterIsNotNull(mStackScroller, "stackScroller");
        this.mStackScroller = mStackScroller;
        this.pulseExpanding = mStackScroller.isPulseExpanding();
        mStackScroller.setOnPulseHeightChangedListener((Runnable)new NotificationWakeUpCoordinator$setStackScroller.NotificationWakeUpCoordinator$setStackScroller$1(this));
    }
    
    public final void setWakingUp(final boolean wakingUp) {
        this.wakingUp = wakingUp;
        this.setWillWakeUp(false);
        if (wakingUp) {
            if (this.mNotificationsVisible && !this.mNotificationsVisibleForExpansion && !this.bypassController.getBypassEnabled()) {
                final NotificationStackScrollLayout mStackScroller = this.mStackScroller;
                if (mStackScroller == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
                    throw null;
                }
                mStackScroller.wakeUpFromPulse();
            }
            if (this.bypassController.getBypassEnabled() && !this.mNotificationsVisible) {
                this.updateNotificationVisibility(this.shouldAnimateVisibility(), false);
            }
        }
    }
    
    public final void setWillWakeUp(final boolean willWakeUp) {
        if (!willWakeUp || this.mDozeAmount != 0.0f) {
            this.willWakeUp = willWakeUp;
        }
    }
    
    public interface WakeUpListener
    {
        default void onFullyHiddenChanged(final boolean b) {
        }
        
        default void onPulseExpansionChanged(final boolean b) {
        }
    }
}
