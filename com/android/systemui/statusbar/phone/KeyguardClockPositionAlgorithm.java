// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$dimen;
import android.content.res.Resources;
import android.util.MathUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.doze.util.BurnInHelperKt;

public class KeyguardClockPositionAlgorithm
{
    private static float CLOCK_HEIGHT_WEIGHT = 0.7f;
    private int mBurnInPreventionOffsetX;
    private int mBurnInPreventionOffsetY;
    private boolean mBypassEnabled;
    private int mClockNotificationsMargin;
    private int mClockPreferredY;
    private int mContainerTopPadding;
    private float mDarkAmount;
    private float mEmptyDragAmount;
    private boolean mHasCustomClock;
    private boolean mHasVisibleNotifs;
    private int mHeight;
    private int mKeyguardStatusHeight;
    private int mMaxShadeBottom;
    private int mMinTopMargin;
    private int mNotificationStackHeight;
    private float mPanelExpansion;
    private int mUnlockedStackScrollerPadding;
    
    private float burnInPreventionOffsetX() {
        return (float)(BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetX * 2, true) - this.mBurnInPreventionOffsetX);
    }
    
    private float burnInPreventionOffsetY() {
        return (float)(BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetY * 2, false) - this.mBurnInPreventionOffsetY);
    }
    
    private float getClockAlpha(final int n) {
        return MathUtils.lerp(Interpolators.ACCELERATE.getInterpolation(Math.max(0.0f, n / Math.max(1.0f, (float)this.getClockY(1.0f)))), 1.0f, this.mDarkAmount);
    }
    
    private int getClockY(float n) {
        int n2;
        if (this.mHasCustomClock) {
            n2 = this.getPreferredClockY();
        }
        else {
            n2 = this.getMaxClockY();
        }
        final float max = MathUtils.max(0.0f, n2 + this.burnInPreventionOffsetY());
        final float n3 = (float)this.getExpandedPreferredClockY();
        final float n4 = (float)(-this.mKeyguardStatusHeight);
        n = Interpolators.FAST_OUT_LINEAR_IN.getInterpolation(n);
        final float lerp = MathUtils.lerp(n4, n3, n);
        final float lerp2 = MathUtils.lerp(n4, max, n);
        if (this.mBypassEnabled && !this.mHasCustomClock) {
            n = 1.0f;
        }
        else {
            n = this.mDarkAmount;
        }
        return (int)(MathUtils.lerp(lerp, lerp2, n) + this.mEmptyDragAmount);
    }
    
    private int getExpandedPreferredClockY() {
        int n;
        if (this.mHasCustomClock && (!this.mHasVisibleNotifs || this.mBypassEnabled)) {
            n = this.getPreferredClockY();
        }
        else {
            n = this.getExpandedClockPosition();
        }
        return n;
    }
    
    private int getMaxClockY() {
        return this.mHeight / 2 - this.mKeyguardStatusHeight - this.mClockNotificationsMargin;
    }
    
    private int getPreferredClockY() {
        return this.mClockPreferredY;
    }
    
    public int getExpandedClockPosition() {
        final int mMaxShadeBottom = this.mMaxShadeBottom;
        final int mMinTopMargin = this.mMinTopMargin;
        float n;
        if ((n = (mMaxShadeBottom - mMinTopMargin) / 2 + mMinTopMargin - this.mKeyguardStatusHeight * KeyguardClockPositionAlgorithm.CLOCK_HEIGHT_WEIGHT - this.mClockNotificationsMargin - this.mNotificationStackHeight / 2) < mMinTopMargin) {
            n = (float)mMinTopMargin;
        }
        final float n2 = (float)this.getMaxClockY();
        float n3 = n;
        if (n > n2) {
            n3 = n2;
        }
        return (int)n3;
    }
    
    public float getMinStackScrollerPadding() {
        float n;
        if (this.mBypassEnabled) {
            n = (float)this.mUnlockedStackScrollerPadding;
        }
        else {
            n = (float)(this.mMinTopMargin + this.mKeyguardStatusHeight + this.mClockNotificationsMargin);
        }
        return n;
    }
    
    public void loadDimens(final Resources resources) {
        this.mClockNotificationsMargin = resources.getDimensionPixelSize(R$dimen.keyguard_clock_notifications_margin);
        this.mContainerTopPadding = Math.max(resources.getDimensionPixelSize(R$dimen.keyguard_clock_top_margin), resources.getDimensionPixelSize(R$dimen.keyguard_lock_height) + resources.getDimensionPixelSize(R$dimen.keyguard_lock_padding) + resources.getDimensionPixelSize(R$dimen.keyguard_clock_lock_margin));
        this.mBurnInPreventionOffsetX = resources.getDimensionPixelSize(R$dimen.burn_in_prevention_offset_x);
        this.mBurnInPreventionOffsetY = resources.getDimensionPixelSize(R$dimen.burn_in_prevention_offset_y);
    }
    
    public void run(final Result result) {
        final int clockY = this.getClockY(this.mPanelExpansion);
        result.clockY = clockY;
        result.clockAlpha = this.getClockAlpha(clockY);
        int mUnlockedStackScrollerPadding;
        if (this.mBypassEnabled) {
            mUnlockedStackScrollerPadding = this.mUnlockedStackScrollerPadding;
        }
        else {
            mUnlockedStackScrollerPadding = clockY + this.mKeyguardStatusHeight;
        }
        result.stackScrollerPadding = mUnlockedStackScrollerPadding;
        int mUnlockedStackScrollerPadding2;
        if (this.mBypassEnabled) {
            mUnlockedStackScrollerPadding2 = this.mUnlockedStackScrollerPadding;
        }
        else {
            mUnlockedStackScrollerPadding2 = this.getClockY(1.0f) + this.mKeyguardStatusHeight;
        }
        result.stackScrollerPaddingExpanded = mUnlockedStackScrollerPadding2;
        result.clockX = (int)NotificationUtils.interpolate(0.0f, this.burnInPreventionOffsetX(), this.mDarkAmount);
    }
    
    public void setup(final int n, final int mMaxShadeBottom, final int mNotificationStackHeight, final float mPanelExpansion, final int mHeight, final int mKeyguardStatusHeight, final int mClockPreferredY, final boolean mHasCustomClock, final boolean mHasVisibleNotifs, final float mDarkAmount, final float mEmptyDragAmount, final boolean mBypassEnabled, final int mUnlockedStackScrollerPadding) {
        this.mMinTopMargin = n + this.mContainerTopPadding;
        this.mMaxShadeBottom = mMaxShadeBottom;
        this.mNotificationStackHeight = mNotificationStackHeight;
        this.mPanelExpansion = mPanelExpansion;
        this.mHeight = mHeight;
        this.mKeyguardStatusHeight = mKeyguardStatusHeight;
        this.mClockPreferredY = mClockPreferredY;
        this.mHasCustomClock = mHasCustomClock;
        this.mHasVisibleNotifs = mHasVisibleNotifs;
        this.mDarkAmount = mDarkAmount;
        this.mEmptyDragAmount = mEmptyDragAmount;
        this.mBypassEnabled = mBypassEnabled;
        this.mUnlockedStackScrollerPadding = mUnlockedStackScrollerPadding;
    }
    
    public static class Result
    {
        public float clockAlpha;
        public int clockX;
        public int clockY;
        public int stackScrollerPadding;
        public int stackScrollerPaddingExpanded;
    }
}
