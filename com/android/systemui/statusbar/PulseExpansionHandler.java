// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.View;
import android.os.SystemClock;
import android.view.ViewConfiguration;
import com.android.systemui.R$dimen;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import android.view.VelocityTracker;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import android.os.PowerManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.Gefingerpoken;

public final class PulseExpansionHandler implements Gefingerpoken
{
    private static final float RUBBERBAND_FACTOR_STATIC = 0.25f;
    private static final int SPRING_BACK_ANIMATION_LENGTH_MS = 375;
    private boolean bouncerShowing;
    private final KeyguardBypassController bypassController;
    private ExpansionCallback expansionCallback;
    private final FalsingManager falsingManager;
    private final HeadsUpManagerPhone headsUpManager;
    private boolean isExpanding;
    private boolean isWakingToShadeLocked;
    private boolean leavingLockscreen;
    private float mEmptyDragAmount;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private final PowerManager mPowerManager;
    private boolean mReachedWakeUpHeight;
    private ExpandableView mStartingChild;
    private final int[] mTemp2;
    private final float mTouchSlop;
    private float mWakeUpHeight;
    private Runnable pulseExpandAbortListener;
    private boolean qsExpanded;
    private final NotificationRoundnessManager roundnessManager;
    private ShadeController shadeController;
    private NotificationStackScrollLayout stackScroller;
    private final StatusBarStateController statusBarStateController;
    private VelocityTracker velocityTracker;
    private final NotificationWakeUpCoordinator wakeUpCoordinator;
    
    public PulseExpansionHandler(final Context context, final NotificationWakeUpCoordinator wakeUpCoordinator, final KeyguardBypassController bypassController, final HeadsUpManagerPhone headsUpManager, final NotificationRoundnessManager roundnessManager, final StatusBarStateController statusBarStateController, final FalsingManager falsingManager) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(wakeUpCoordinator, "wakeUpCoordinator");
        Intrinsics.checkParameterIsNotNull(bypassController, "bypassController");
        Intrinsics.checkParameterIsNotNull(headsUpManager, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(roundnessManager, "roundnessManager");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(falsingManager, "falsingManager");
        this.wakeUpCoordinator = wakeUpCoordinator;
        this.bypassController = bypassController;
        this.headsUpManager = headsUpManager;
        this.roundnessManager = roundnessManager;
        this.statusBarStateController = statusBarStateController;
        this.falsingManager = falsingManager;
        this.mTemp2 = new int[2];
        context.getResources().getDimensionPixelSize(R$dimen.keyguard_drag_down_min_distance);
        final ViewConfiguration value = ViewConfiguration.get(context);
        Intrinsics.checkExpressionValueIsNotNull(value, "ViewConfiguration.get(context)");
        this.mTouchSlop = (float)value.getScaledTouchSlop();
        this.mPowerManager = (PowerManager)context.getSystemService((Class)PowerManager.class);
    }
    
    private final void cancelExpansion() {
        this.setExpanding(false);
        this.falsingManager.onExpansionFromPulseStopped();
        final ExpandableView mStartingChild = this.mStartingChild;
        if (mStartingChild != null) {
            if (mStartingChild == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            this.reset(mStartingChild);
            this.mStartingChild = null;
        }
        else {
            this.resetClock();
        }
        this.wakeUpCoordinator.setNotificationsVisibleForExpansion(false, true, false);
    }
    
    private final void captureStartingChild(final float n, final float n2) {
        if (this.mStartingChild == null && !this.bypassController.getBypassEnabled()) {
            final ExpandableView view = this.findView(n, n2);
            if ((this.mStartingChild = view) != null) {
                if (view == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                this.setUserLocked(view, true);
            }
        }
    }
    
    private final ExpandableView findView(final float n, final float n2) {
        final NotificationStackScrollLayout stackScroller = this.stackScroller;
        final ExpandableView expandableView = null;
        if (stackScroller == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stackScroller");
            throw null;
        }
        stackScroller.getLocationOnScreen(this.mTemp2);
        final int[] mTemp2 = this.mTemp2;
        final float n3 = (float)mTemp2[0];
        final float n4 = (float)mTemp2[1];
        final NotificationStackScrollLayout stackScroller2 = this.stackScroller;
        if (stackScroller2 != null) {
            final ExpandableView childAtRawPosition = stackScroller2.getChildAtRawPosition(n + n3, n2 + n4);
            ExpandableView expandableView2 = expandableView;
            if (childAtRawPosition != null) {
                expandableView2 = expandableView;
                if (childAtRawPosition.isContentExpandable()) {
                    expandableView2 = childAtRawPosition;
                }
            }
            return expandableView2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("stackScroller");
        throw null;
    }
    
    private final void finishExpansion() {
        this.resetClock();
        final ExpandableView mStartingChild = this.mStartingChild;
        if (mStartingChild != null) {
            if (mStartingChild == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            this.setUserLocked(mStartingChild, false);
            this.mStartingChild = null;
        }
        if (this.statusBarStateController.isDozing()) {
            this.isWakingToShadeLocked = true;
            this.wakeUpCoordinator.setWillWakeUp(true);
            final PowerManager mPowerManager = this.mPowerManager;
            if (mPowerManager == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            mPowerManager.wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:PULSEDRAG");
        }
        final ShadeController shadeController = this.shadeController;
        if (shadeController != null) {
            shadeController.goToLockedShade((View)this.mStartingChild);
            this.leavingLockscreen = true;
            this.setExpanding(false);
            final ExpandableView mStartingChild2 = this.mStartingChild;
            if (mStartingChild2 instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)mStartingChild2;
                if (expandableNotificationRow == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                expandableNotificationRow.onExpandedByGesture(true);
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("shadeController");
        throw null;
    }
    
    private final boolean isFalseTouch() {
        return this.falsingManager.isFalseTouch();
    }
    
    private final boolean maybeStartExpansion(final MotionEvent motionEvent) {
        if (!this.wakeUpCoordinator.getCanShowPulsingHuns() || this.qsExpanded || this.bouncerShowing) {
            return false;
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        final VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
            final float x = motionEvent.getX();
            final float y = motionEvent.getY();
            final int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked != 2) {
                        if (actionMasked == 3) {
                            this.recycleVelocityTracker();
                        }
                    }
                    else {
                        final float n = y - this.mInitialTouchY;
                        if (n > this.mTouchSlop && n > Math.abs(x - this.mInitialTouchX)) {
                            this.falsingManager.onStartExpandingFromPulse();
                            this.setExpanding(true);
                            this.captureStartingChild(this.mInitialTouchX, this.mInitialTouchY);
                            this.mInitialTouchY = y;
                            this.mInitialTouchX = x;
                            this.mWakeUpHeight = this.wakeUpCoordinator.getWakeUpHeight();
                            this.mReachedWakeUpHeight = false;
                            return true;
                        }
                    }
                }
                else {
                    this.recycleVelocityTracker();
                }
            }
            else {
                this.setExpanding(false);
                this.leavingLockscreen = false;
                this.mStartingChild = null;
                this.mInitialTouchY = y;
                this.mInitialTouchX = x;
            }
            return false;
        }
        Intrinsics.throwNpe();
        throw null;
    }
    
    private final void recycleVelocityTracker() {
        final VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.velocityTracker = null;
    }
    
    private final void reset(final ExpandableView expandableView) {
        if (expandableView.getActualHeight() == expandableView.getCollapsedHeight()) {
            this.setUserLocked(expandableView, false);
            return;
        }
        final ObjectAnimator ofInt = ObjectAnimator.ofInt((Object)expandableView, "actualHeight", new int[] { expandableView.getActualHeight(), expandableView.getCollapsedHeight() });
        Intrinsics.checkExpressionValueIsNotNull(ofInt, "anim");
        ofInt.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        ofInt.setDuration((long)PulseExpansionHandler.SPRING_BACK_ANIMATION_LENGTH_MS);
        ofInt.addListener((Animator$AnimatorListener)new PulseExpansionHandler$reset.PulseExpansionHandler$reset$1(this, expandableView));
        ofInt.start();
    }
    
    private final void resetClock() {
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mEmptyDragAmount, 0.0f });
        Intrinsics.checkExpressionValueIsNotNull(ofFloat, "anim");
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.setDuration((long)PulseExpansionHandler.SPRING_BACK_ANIMATION_LENGTH_MS);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new PulseExpansionHandler$resetClock.PulseExpansionHandler$resetClock$1(this));
        ofFloat.start();
    }
    
    private final void setEmptyDragAmount(final float n) {
        this.mEmptyDragAmount = n;
        final ExpansionCallback expansionCallback = this.expansionCallback;
        if (expansionCallback != null) {
            expansionCallback.setEmptyDragAmount(n);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("expansionCallback");
        throw null;
    }
    
    private final void setExpanding(final boolean b) {
        final boolean b2 = this.isExpanding != b;
        this.isExpanding = b;
        this.bypassController.setPulseExpanding(b);
        if (b2) {
            if (b) {
                final NotificationEntry topEntry = this.headsUpManager.getTopEntry();
                if (topEntry != null) {
                    this.roundnessManager.setTrackingHeadsUp(topEntry.getRow());
                }
            }
            else {
                this.roundnessManager.setTrackingHeadsUp(null);
                if (!this.leavingLockscreen) {
                    this.bypassController.maybePerformPendingUnlock();
                    final Runnable pulseExpandAbortListener = this.pulseExpandAbortListener;
                    if (pulseExpandAbortListener != null) {
                        pulseExpandAbortListener.run();
                    }
                }
            }
            this.headsUpManager.unpinAll(true);
        }
    }
    
    private final void setUserLocked(final ExpandableView expandableView, final boolean userLocked) {
        if (expandableView instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow)expandableView).setUserLocked(userLocked);
        }
    }
    
    private final void updateExpansionHeight(float n) {
        float mWakeUpHeight = 0.0f;
        final float max = Math.max(n, 0.0f);
        if (!this.mReachedWakeUpHeight && n > this.mWakeUpHeight) {
            this.mReachedWakeUpHeight = true;
        }
        final ExpandableView mStartingChild = this.mStartingChild;
        if (mStartingChild != null) {
            if (mStartingChild == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            final int min = Math.min((int)(mStartingChild.getCollapsedHeight() + max), mStartingChild.getMaxContentHeight());
            mStartingChild.setActualHeight(min);
            n = Math.max((float)min, max);
        }
        else {
            if (this.mReachedWakeUpHeight) {
                mWakeUpHeight = this.mWakeUpHeight;
            }
            this.wakeUpCoordinator.setNotificationsVisibleForExpansion(n > mWakeUpHeight, true, true);
            n = Math.max(this.mWakeUpHeight, max);
        }
        this.setEmptyDragAmount(this.wakeUpCoordinator.setPulseHeight(n) * PulseExpansionHandler.RUBBERBAND_FACTOR_STATIC);
    }
    
    public final boolean getLeavingLockscreen() {
        return this.leavingLockscreen;
    }
    
    public final boolean isExpanding() {
        return this.isExpanding;
    }
    
    public final boolean isWakingToShadeLocked() {
        return this.isWakingToShadeLocked;
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        Intrinsics.checkParameterIsNotNull(motionEvent, "event");
        return this.maybeStartExpansion(motionEvent);
    }
    
    public final void onStartedWakingUp() {
        this.isWakingToShadeLocked = false;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        Intrinsics.checkParameterIsNotNull(motionEvent, "event");
        if (!this.isExpanding) {
            return this.maybeStartExpansion(motionEvent);
        }
        final VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
            final float n = motionEvent.getY() - this.mInitialTouchY;
            final int actionMasked = motionEvent.getActionMasked();
            boolean b = true;
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked == 3) {
                        this.cancelExpansion();
                        this.recycleVelocityTracker();
                    }
                }
                else {
                    this.updateExpansionHeight(n);
                }
            }
            else {
                final VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                velocityTracker2.computeCurrentVelocity(1000);
                Label_0160: {
                    if (n > 0) {
                        final VelocityTracker velocityTracker3 = this.velocityTracker;
                        if (velocityTracker3 == null) {
                            Intrinsics.throwNpe();
                            throw null;
                        }
                        if (velocityTracker3.getYVelocity() > -1000 && this.statusBarStateController.getState() != 0) {
                            break Label_0160;
                        }
                    }
                    b = false;
                }
                if (!this.falsingManager.isUnlockingDisabled() && !this.isFalseTouch() && b) {
                    this.finishExpansion();
                }
                else {
                    this.cancelExpansion();
                }
                this.recycleVelocityTracker();
            }
            return this.isExpanding;
        }
        Intrinsics.throwNpe();
        throw null;
    }
    
    public final void setBouncerShowing(final boolean bouncerShowing) {
        this.bouncerShowing = bouncerShowing;
    }
    
    public final void setPulseExpandAbortListener(final Runnable pulseExpandAbortListener) {
        this.pulseExpandAbortListener = pulseExpandAbortListener;
    }
    
    public final void setPulsing(final boolean b) {
    }
    
    public final void setQsExpanded(final boolean qsExpanded) {
        this.qsExpanded = qsExpanded;
    }
    
    public final void setUp(final NotificationStackScrollLayout stackScroller, final ExpansionCallback expansionCallback, final ShadeController shadeController) {
        Intrinsics.checkParameterIsNotNull(stackScroller, "stackScroller");
        Intrinsics.checkParameterIsNotNull(expansionCallback, "expansionCallback");
        Intrinsics.checkParameterIsNotNull(shadeController, "shadeController");
        this.expansionCallback = expansionCallback;
        this.shadeController = shadeController;
        this.stackScroller = stackScroller;
    }
    
    public interface ExpansionCallback
    {
        void setEmptyDragAmount(final float p0);
    }
}
