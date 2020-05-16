// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.MathUtils;
import com.android.systemui.R$dimen;
import android.content.Context;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import java.util.ArrayList;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;

public class AmbientState
{
    private ActivatableNotificationView mActivatedChild;
    private boolean mAppearing;
    private int mBaseZHeight;
    private float mCurrentScrollVelocity;
    private boolean mDimmed;
    private float mDozeAmount;
    private boolean mDozing;
    private ArrayList<ExpandableView> mDraggedViews;
    private int mExpandAnimationTopChange;
    private ExpandableNotificationRow mExpandingNotification;
    private float mExpandingVelocity;
    private boolean mExpansionChanging;
    private HeadsUpManager mHeadUpManager;
    private float mHideAmount;
    private boolean mHideSensitive;
    private int mIntrinsicPadding;
    private ActivatableNotificationView mLastVisibleBackgroundChild;
    private int mLayoutHeight;
    private int mLayoutMinHeight;
    private float mMaxHeadsUpTranslation;
    private int mMaxLayoutHeight;
    private Runnable mOnPulseHeightChangedListener;
    private float mOverScrollBottomAmount;
    private float mOverScrollTopAmount;
    private boolean mPanelFullWidth;
    private boolean mPanelTracking;
    private float mPulseHeight;
    private boolean mPulsing;
    private boolean mQsCustomizerShowing;
    private int mScrollY;
    private final StackScrollAlgorithm.SectionProvider mSectionProvider;
    private boolean mShadeExpanded;
    private NotificationShelf mShelf;
    private int mSpeedBumpIndex;
    private float mStackTranslation;
    private int mStatusBarState;
    private int mTopPadding;
    private boolean mUnlockHintRunning;
    private int mZDistanceBetweenElements;
    
    public AmbientState(final Context context, final StackScrollAlgorithm.SectionProvider mSectionProvider, final HeadsUpManager mHeadUpManager) {
        this.mDraggedViews = new ArrayList<ExpandableView>();
        this.mSpeedBumpIndex = -1;
        this.mPulseHeight = 100000.0f;
        this.mDozeAmount = 0.0f;
        this.mSectionProvider = mSectionProvider;
        this.mHeadUpManager = mHeadUpManager;
        this.reload(context);
    }
    
    private static int getBaseHeight(final int n) {
        return n * 4;
    }
    
    public static int getNotificationLaunchHeight(final Context context) {
        return getBaseHeight(getZDistanceBetweenElements(context)) * 2;
    }
    
    private static int getZDistanceBetweenElements(final Context context) {
        return Math.max(1, context.getResources().getDimensionPixelSize(R$dimen.z_distance_between_notifications));
    }
    
    public ActivatableNotificationView getActivatedChild() {
        return this.mActivatedChild;
    }
    
    public int getBaseZHeight() {
        return this.mBaseZHeight;
    }
    
    public float getCurrentScrollVelocity() {
        return this.mCurrentScrollVelocity;
    }
    
    public ArrayList<ExpandableView> getDraggedViews() {
        return this.mDraggedViews;
    }
    
    public int getExpandAnimationTopChange() {
        return this.mExpandAnimationTopChange;
    }
    
    public ExpandableNotificationRow getExpandingNotification() {
        return this.mExpandingNotification;
    }
    
    public float getExpandingVelocity() {
        return this.mExpandingVelocity;
    }
    
    public float getHideAmount() {
        return this.mHideAmount;
    }
    
    public int getInnerHeight() {
        return this.getInnerHeight(false);
    }
    
    public int getInnerHeight(final boolean b) {
        if (this.mDozeAmount == 1.0f && !this.isPulseExpanding()) {
            return this.mShelf.getHeight();
        }
        final int max = Math.max(this.mLayoutMinHeight, Math.min(this.mLayoutHeight, this.mMaxLayoutHeight) - this.mTopPadding);
        if (b) {
            return max;
        }
        final float mPulseHeight = this.mPulseHeight;
        final float b2 = (float)max;
        return (int)MathUtils.lerp(b2, Math.min(mPulseHeight, b2), this.mDozeAmount);
    }
    
    public int getIntrinsicPadding() {
        return this.mIntrinsicPadding;
    }
    
    public ActivatableNotificationView getLastVisibleBackgroundChild() {
        return this.mLastVisibleBackgroundChild;
    }
    
    public float getMaxHeadsUpTranslation() {
        return this.mMaxHeadsUpTranslation;
    }
    
    public float getOverScrollAmount(final boolean b) {
        float n;
        if (b) {
            n = this.mOverScrollTopAmount;
        }
        else {
            n = this.mOverScrollBottomAmount;
        }
        return n;
    }
    
    public float getPulseHeight() {
        float mPulseHeight;
        if ((mPulseHeight = this.mPulseHeight) == 100000.0f) {
            mPulseHeight = 0.0f;
        }
        return mPulseHeight;
    }
    
    public int getScrollY() {
        return this.mScrollY;
    }
    
    public StackScrollAlgorithm.SectionProvider getSectionProvider() {
        return this.mSectionProvider;
    }
    
    public NotificationShelf getShelf() {
        return this.mShelf;
    }
    
    public int getSpeedBumpIndex() {
        return this.mSpeedBumpIndex;
    }
    
    public float getStackTranslation() {
        return this.mStackTranslation;
    }
    
    public float getTopPadding() {
        return (float)this.mTopPadding;
    }
    
    public int getZDistanceBetweenElements() {
        return this.mZDistanceBetweenElements;
    }
    
    public boolean hasPulsingNotifications() {
        if (this.mPulsing) {
            final HeadsUpManager mHeadUpManager = this.mHeadUpManager;
            if (mHeadUpManager != null && mHeadUpManager.hasNotifications()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAppearing() {
        return this.mAppearing;
    }
    
    public boolean isDimmed() {
        return this.mDimmed && (!this.isPulseExpanding() || this.mDozeAmount != 1.0f);
    }
    
    public boolean isDozing() {
        return this.mDozing;
    }
    
    public boolean isDozingAndNotPulsing(final ExpandableNotificationRow expandableNotificationRow) {
        return this.isDozing() && !this.isPulsing(expandableNotificationRow.getEntry());
    }
    
    public boolean isDozingAndNotPulsing(final ExpandableView expandableView) {
        return expandableView instanceof ExpandableNotificationRow && this.isDozingAndNotPulsing((ExpandableNotificationRow)expandableView);
    }
    
    public boolean isExpansionChanging() {
        return this.mExpansionChanging;
    }
    
    public boolean isFullyAwake() {
        return this.mDozeAmount == 0.0f;
    }
    
    public boolean isFullyHidden() {
        return this.mHideAmount == 1.0f;
    }
    
    public boolean isHiddenAtAll() {
        return this.mHideAmount != 0.0f;
    }
    
    public boolean isHideSensitive() {
        return this.mHideSensitive;
    }
    
    public boolean isOnKeyguard() {
        final int mStatusBarState = this.mStatusBarState;
        boolean b = true;
        if (mStatusBarState != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isPanelFullWidth() {
        return this.mPanelFullWidth;
    }
    
    public boolean isPanelTracking() {
        return this.mPanelTracking;
    }
    
    public boolean isPulseExpanding() {
        return this.mPulseHeight != 100000.0f && this.mDozeAmount != 0.0f && this.mHideAmount != 1.0f;
    }
    
    public boolean isPulsing(final NotificationEntry notificationEntry) {
        if (this.mPulsing) {
            final HeadsUpManager mHeadUpManager = this.mHeadUpManager;
            if (mHeadUpManager != null) {
                return mHeadUpManager.isAlerting(notificationEntry.getKey());
            }
        }
        return false;
    }
    
    public boolean isQsCustomizerShowing() {
        return this.mQsCustomizerShowing;
    }
    
    public boolean isShadeExpanded() {
        return this.mShadeExpanded;
    }
    
    public boolean isUnlockHintRunning() {
        return this.mUnlockHintRunning;
    }
    
    public void onBeginDrag(final ExpandableView e) {
        this.mDraggedViews.add(e);
    }
    
    public void onDragFinished(final View o) {
        this.mDraggedViews.remove(o);
    }
    
    public void reload(final Context context) {
        final int zDistanceBetweenElements = getZDistanceBetweenElements(context);
        this.mZDistanceBetweenElements = zDistanceBetweenElements;
        this.mBaseZHeight = getBaseHeight(zDistanceBetweenElements);
    }
    
    public void setActivatedChild(final ActivatableNotificationView mActivatedChild) {
        this.mActivatedChild = mActivatedChild;
    }
    
    public void setAppearing(final boolean mAppearing) {
        this.mAppearing = mAppearing;
    }
    
    public void setCurrentScrollVelocity(final float mCurrentScrollVelocity) {
        this.mCurrentScrollVelocity = mCurrentScrollVelocity;
    }
    
    public void setDimmed(final boolean mDimmed) {
        this.mDimmed = mDimmed;
    }
    
    public void setDismissAllInProgress(final boolean b) {
    }
    
    public void setDozeAmount(final float mDozeAmount) {
        if (mDozeAmount != this.mDozeAmount) {
            this.mDozeAmount = mDozeAmount;
            if (mDozeAmount == 0.0f || mDozeAmount == 1.0f) {
                this.setPulseHeight(100000.0f);
            }
        }
    }
    
    public void setDozing(final boolean mDozing) {
        this.mDozing = mDozing;
    }
    
    public void setExpandAnimationTopChange(final int mExpandAnimationTopChange) {
        this.mExpandAnimationTopChange = mExpandAnimationTopChange;
    }
    
    public void setExpandingNotification(final ExpandableNotificationRow mExpandingNotification) {
        this.mExpandingNotification = mExpandingNotification;
    }
    
    public void setExpandingVelocity(final float mExpandingVelocity) {
        this.mExpandingVelocity = mExpandingVelocity;
    }
    
    public void setExpansionChanging(final boolean mExpansionChanging) {
        this.mExpansionChanging = mExpansionChanging;
    }
    
    public void setHideAmount(final float mHideAmount) {
        if (mHideAmount == 1.0f && this.mHideAmount != mHideAmount) {
            this.setPulseHeight(100000.0f);
        }
        this.mHideAmount = mHideAmount;
    }
    
    public void setHideSensitive(final boolean mHideSensitive) {
        this.mHideSensitive = mHideSensitive;
    }
    
    public void setIntrinsicPadding(final int mIntrinsicPadding) {
        this.mIntrinsicPadding = mIntrinsicPadding;
    }
    
    public void setLastVisibleBackgroundChild(final ActivatableNotificationView mLastVisibleBackgroundChild) {
        this.mLastVisibleBackgroundChild = mLastVisibleBackgroundChild;
    }
    
    public void setLayoutHeight(final int mLayoutHeight) {
        this.mLayoutHeight = mLayoutHeight;
    }
    
    public void setLayoutMaxHeight(final int mMaxLayoutHeight) {
        this.mMaxLayoutHeight = mMaxLayoutHeight;
    }
    
    public void setLayoutMinHeight(final int mLayoutMinHeight) {
        this.mLayoutMinHeight = mLayoutMinHeight;
    }
    
    public void setMaxHeadsUpTranslation(final float mMaxHeadsUpTranslation) {
        this.mMaxHeadsUpTranslation = mMaxHeadsUpTranslation;
    }
    
    public void setOnPulseHeightChangedListener(final Runnable mOnPulseHeightChangedListener) {
        this.mOnPulseHeightChangedListener = mOnPulseHeightChangedListener;
    }
    
    public void setOverScrollAmount(final float n, final boolean b) {
        if (b) {
            this.mOverScrollTopAmount = n;
        }
        else {
            this.mOverScrollBottomAmount = n;
        }
    }
    
    public void setPanelFullWidth(final boolean mPanelFullWidth) {
        this.mPanelFullWidth = mPanelFullWidth;
    }
    
    public void setPanelTracking(final boolean mPanelTracking) {
        this.mPanelTracking = mPanelTracking;
    }
    
    public void setPulseHeight(final float mPulseHeight) {
        if (mPulseHeight != this.mPulseHeight) {
            this.mPulseHeight = mPulseHeight;
            final Runnable mOnPulseHeightChangedListener = this.mOnPulseHeightChangedListener;
            if (mOnPulseHeightChangedListener != null) {
                mOnPulseHeightChangedListener.run();
            }
        }
    }
    
    public void setPulsing(final boolean mPulsing) {
        this.mPulsing = mPulsing;
    }
    
    public void setQsCustomizerShowing(final boolean mQsCustomizerShowing) {
        this.mQsCustomizerShowing = mQsCustomizerShowing;
    }
    
    public void setScrollY(final int mScrollY) {
        this.mScrollY = mScrollY;
    }
    
    public void setShadeExpanded(final boolean mShadeExpanded) {
        this.mShadeExpanded = mShadeExpanded;
    }
    
    public void setShelf(final NotificationShelf mShelf) {
        this.mShelf = mShelf;
    }
    
    public void setSpeedBumpIndex(final int mSpeedBumpIndex) {
        this.mSpeedBumpIndex = mSpeedBumpIndex;
    }
    
    public void setStackTranslation(final float mStackTranslation) {
        this.mStackTranslation = mStackTranslation;
    }
    
    public void setStatusBarState(final int mStatusBarState) {
        this.mStatusBarState = mStatusBarState;
    }
    
    public void setTopPadding(final int mTopPadding) {
        this.mTopPadding = mTopPadding;
    }
    
    public void setUnlockHintRunning(final boolean mUnlockHintRunning) {
        this.mUnlockHintRunning = mUnlockHintRunning;
    }
}
