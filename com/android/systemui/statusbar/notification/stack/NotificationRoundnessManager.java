// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.MathUtils;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import java.util.HashSet;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

public class NotificationRoundnessManager implements OnHeadsUpChangedListener
{
    private HashSet<ExpandableView> mAnimatedChildren;
    private float mAppearFraction;
    private final KeyguardBypassController mBypassController;
    private boolean mExpanded;
    private final ActivatableNotificationView[] mFirstInSectionViews;
    private final ActivatableNotificationView[] mLastInSectionViews;
    private Runnable mRoundingChangedCallback;
    private final ActivatableNotificationView[] mTmpFirstInSectionViews;
    private final ActivatableNotificationView[] mTmpLastInSectionViews;
    private ExpandableNotificationRow mTrackedHeadsUp;
    
    NotificationRoundnessManager(final KeyguardBypassController mBypassController, final NotificationSectionsFeatureManager notificationSectionsFeatureManager) {
        final int numberOfBuckets = notificationSectionsFeatureManager.getNumberOfBuckets();
        this.mFirstInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mLastInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mTmpFirstInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mTmpLastInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mBypassController = mBypassController;
    }
    
    private float getRoundness(final ActivatableNotificationView activatableNotificationView, final boolean b) {
        if ((activatableNotificationView.isPinned() || activatableNotificationView.isHeadsUpAnimatingAway()) && !this.mExpanded) {
            return 1.0f;
        }
        if (this.isFirstInSection(activatableNotificationView, true) && b) {
            return 1.0f;
        }
        if (this.isLastInSection(activatableNotificationView, true) && !b) {
            return 1.0f;
        }
        if (activatableNotificationView == this.mTrackedHeadsUp) {
            return MathUtils.saturate(1.0f - this.mAppearFraction);
        }
        if (activatableNotificationView.showingPulsing() && !this.mBypassController.getBypassEnabled()) {
            return 1.0f;
        }
        return 0.0f;
    }
    
    private boolean handleAddedNewViews(final NotificationSection[] array, final ActivatableNotificationView[] array2, final boolean b) {
        boolean b2 = false;
        boolean b3;
    Label_0142:
        for (int length = array.length, i = (b2 = false) ? 1 : 0; i < length; ++i, b2 = b3) {
            final NotificationSection notificationSection = array[i];
            ActivatableNotificationView o;
            if (b) {
                o = notificationSection.getFirstVisibleChild();
            }
            else {
                o = notificationSection.getLastVisibleChild();
            }
            b3 = b2;
            if (o != null) {
                final int length2 = array2.length;
                int j = 0;
                while (true) {
                    while (j < length2) {
                        if (array2[j] == o) {
                            final boolean b4 = true;
                            b3 = b2;
                            if (!b4) {
                                this.updateViewWithoutCallback(o, o.isShown() && !this.mAnimatedChildren.contains(o));
                                b3 = true;
                            }
                            continue Label_0142;
                        }
                        else {
                            ++j;
                        }
                    }
                    final boolean b4 = false;
                    continue;
                }
            }
        }
        return b2;
    }
    
    private boolean handleRemovedOldViews(final NotificationSection[] array, final ActivatableNotificationView[] array2, final boolean b) {
        boolean b2 = false;
        boolean b3;
    Label_0179:
        for (int length = array2.length, i = (b2 = false) ? 1 : 0; i < length; ++i, b2 = b3) {
            final ActivatableNotificationView activatableNotificationView = array2[i];
            b3 = b2;
            if (activatableNotificationView != null) {
                while (true) {
                    for (final NotificationSection notificationSection : array) {
                        ActivatableNotificationView activatableNotificationView2;
                        if (b) {
                            activatableNotificationView2 = notificationSection.getFirstVisibleChild();
                        }
                        else {
                            activatableNotificationView2 = notificationSection.getLastVisibleChild();
                        }
                        if (activatableNotificationView2 == activatableNotificationView) {
                            int n;
                            int n2;
                            if (activatableNotificationView.isFirstInSection() == this.isFirstInSection(activatableNotificationView, false) && activatableNotificationView.isLastInSection() == this.isLastInSection(activatableNotificationView, false)) {
                                n = 0;
                                n2 = 1;
                            }
                            else {
                                n2 = 1;
                                n = n2;
                            }
                            if (n2 != 0) {
                                b3 = b2;
                                if (n == 0) {
                                    continue Label_0179;
                                }
                            }
                            if (!activatableNotificationView.isRemoved()) {
                                this.updateViewWithoutCallback(activatableNotificationView, activatableNotificationView.isShown());
                            }
                            b3 = true;
                            continue Label_0179;
                        }
                    }
                    int n2 = 0;
                    continue;
                }
            }
        }
        return b2;
    }
    
    private boolean isFirstInSection(final ActivatableNotificationView activatableNotificationView, final boolean b) {
        final boolean b2 = false;
        int n2;
        int n = n2 = 0;
        while (true) {
            final ActivatableNotificationView[] mFirstInSectionViews = this.mFirstInSectionViews;
            if (n >= mFirstInSectionViews.length) {
                return false;
            }
            if (activatableNotificationView == mFirstInSectionViews[n]) {
                if (!b) {
                    final boolean b3 = b2;
                    if (n2 <= 0) {
                        return b3;
                    }
                }
                return true;
            }
            int n3 = n2;
            if (mFirstInSectionViews[n] != null) {
                n3 = n2 + 1;
            }
            ++n;
            n2 = n3;
        }
    }
    
    private boolean isLastInSection(final ActivatableNotificationView activatableNotificationView, final boolean b) {
        final int length = this.mLastInSectionViews.length;
        final boolean b2 = true;
        int i = length - 1;
        int n = 0;
        while (i >= 0) {
            final ActivatableNotificationView[] mLastInSectionViews = this.mLastInSectionViews;
            if (activatableNotificationView == mLastInSectionViews[i]) {
                boolean b3 = b2;
                if (!b) {
                    b3 = (n > 0 && b2);
                }
                return b3;
            }
            int n2 = n;
            if (mLastInSectionViews[i] != null) {
                n2 = n + 1;
            }
            --i;
            n = n2;
        }
        return false;
    }
    
    private void updateView(final ActivatableNotificationView activatableNotificationView, final boolean b) {
        if (this.updateViewWithoutCallback(activatableNotificationView, b)) {
            this.mRoundingChangedCallback.run();
        }
    }
    
    private boolean updateViewWithoutCallback(final ActivatableNotificationView activatableNotificationView, final boolean b) {
        final boolean b2 = true;
        final float roundness = this.getRoundness(activatableNotificationView, true);
        final float roundness2 = this.getRoundness(activatableNotificationView, false);
        final boolean setTopRoundness = activatableNotificationView.setTopRoundness(roundness, b);
        final boolean setBottomRoundness = activatableNotificationView.setBottomRoundness(roundness2, b);
        final boolean firstInSection = this.isFirstInSection(activatableNotificationView, false);
        final boolean lastInSection = this.isLastInSection(activatableNotificationView, false);
        activatableNotificationView.setFirstInSection(firstInSection);
        activatableNotificationView.setLastInSection(lastInSection);
        if (firstInSection || lastInSection) {
            boolean b3 = b2;
            if (setTopRoundness) {
                return b3;
            }
            if (setBottomRoundness) {
                b3 = b2;
                return b3;
            }
        }
        return false;
    }
    
    @Override
    public void onHeadsUpPinned(final NotificationEntry notificationEntry) {
        this.updateView(notificationEntry.getRow(), false);
    }
    
    @Override
    public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
        this.updateView(notificationEntry.getRow(), false);
    }
    
    @Override
    public void onHeadsUpUnPinned(final NotificationEntry notificationEntry) {
        this.updateView(notificationEntry.getRow(), true);
    }
    
    public void onHeadsupAnimatingAwayChanged(final ExpandableNotificationRow expandableNotificationRow, final boolean b) {
        this.updateView(expandableNotificationRow, false);
    }
    
    public void setAnimatedChildren(final HashSet<ExpandableView> mAnimatedChildren) {
        this.mAnimatedChildren = mAnimatedChildren;
    }
    
    public void setExpanded(final float n, final float mAppearFraction) {
        this.mExpanded = (n != 0.0f);
        this.mAppearFraction = mAppearFraction;
        final ExpandableNotificationRow mTrackedHeadsUp = this.mTrackedHeadsUp;
        if (mTrackedHeadsUp != null) {
            this.updateView(mTrackedHeadsUp, true);
        }
    }
    
    public void setOnRoundingChangedCallback(final Runnable mRoundingChangedCallback) {
        this.mRoundingChangedCallback = mRoundingChangedCallback;
    }
    
    public void setTrackingHeadsUp(final ExpandableNotificationRow mTrackedHeadsUp) {
        final ExpandableNotificationRow mTrackedHeadsUp2 = this.mTrackedHeadsUp;
        this.mTrackedHeadsUp = mTrackedHeadsUp;
        if (mTrackedHeadsUp2 != null) {
            this.updateView(mTrackedHeadsUp2, true);
        }
    }
    
    public void updateRoundedChildren(final NotificationSection[] array) {
        for (int i = 0; i < array.length; ++i) {
            final ActivatableNotificationView[] mTmpFirstInSectionViews = this.mTmpFirstInSectionViews;
            final ActivatableNotificationView[] mFirstInSectionViews = this.mFirstInSectionViews;
            mTmpFirstInSectionViews[i] = mFirstInSectionViews[i];
            this.mTmpLastInSectionViews[i] = this.mLastInSectionViews[i];
            mFirstInSectionViews[i] = array[i].getFirstVisibleChild();
            this.mLastInSectionViews[i] = array[i].getLastVisibleChild();
        }
        if (this.handleAddedNewViews(array, this.mTmpLastInSectionViews, false) | (this.handleRemovedOldViews(array, this.mTmpFirstInSectionViews, true) | false | this.handleRemovedOldViews(array, this.mTmpLastInSectionViews, false) | this.handleAddedNewViews(array, this.mTmpFirstInSectionViews, true))) {
            this.mRoundingChangedCallback.run();
        }
    }
}
