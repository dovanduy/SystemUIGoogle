// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.window.WindowContainerTransaction;
import com.android.internal.policy.DividerSnapAlgorithm$SnapTarget;
import android.content.res.Resources;
import android.util.TypedValue;
import android.content.res.Configuration;
import com.android.internal.policy.DockedDividerUtils;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.systemui.wm.DisplayLayout;
import android.content.Context;
import android.graphics.Rect;

public class SplitDisplayLayout
{
    Rect mAdjustedPrimary;
    Rect mAdjustedSecondary;
    Context mContext;
    DisplayLayout mDisplayLayout;
    int mDividerSize;
    int mDividerSizeInactive;
    private DividerSnapAlgorithm mMinimizedSnapAlgorithm;
    Rect mPrimary;
    boolean mResourcesValid;
    Rect mSecondary;
    private DividerSnapAlgorithm mSnapAlgorithm;
    SplitScreenTaskOrganizer mTiles;
    
    public SplitDisplayLayout(final Context mContext, final DisplayLayout mDisplayLayout, final SplitScreenTaskOrganizer mTiles) {
        this.mResourcesValid = false;
        this.mSnapAlgorithm = null;
        this.mMinimizedSnapAlgorithm = null;
        this.mPrimary = null;
        this.mSecondary = null;
        this.mAdjustedPrimary = null;
        this.mAdjustedSecondary = null;
        this.mTiles = mTiles;
        this.mDisplayLayout = mDisplayLayout;
        this.mContext = mContext;
    }
    
    private void adjustForIME(final DisplayLayout displayLayout, int max, int max2, final int n, final int n2, int n3, final Rect rect, final Rect rect2) {
        if (this.mAdjustedPrimary == null) {
            this.mAdjustedPrimary = new Rect();
            this.mAdjustedSecondary = new Rect();
        }
        final Rect rect3 = new Rect();
        displayLayout.getStableBounds(rect3);
        final float n4 = (max - max2) / (float)(n - max2);
        n3 = (int)(n3 * n4 + n2 * (1.0f - n4));
        final int top = rect3.top;
        final int bottom = this.mPrimary.bottom;
        max2 = Math.max(0, max2 - n - (bottom - (top + (int)((bottom - top) * 0.3f))));
        max = Math.max(0, displayLayout.height() - (max + max2));
        this.mAdjustedPrimary.set(rect);
        final Rect mAdjustedPrimary = this.mAdjustedPrimary;
        max = -max;
        mAdjustedPrimary.offset(0, n2 - n3 + max);
        this.mAdjustedSecondary.set(rect2);
        this.mAdjustedSecondary.offset(0, max);
    }
    
    static int getPrimarySplitSide(final Rect rect, final Rect rect2, final int n) {
        if (n == 1) {
            if (rect2.bottom - rect.bottom - (rect.top - rect2.top) < 0) {
                return 4;
            }
            return 2;
        }
        else {
            if (n != 2) {
                return -1;
            }
            if (rect2.right - rect.right - (rect.left - rect2.left) < 0) {
                return 3;
            }
            return 1;
        }
    }
    
    static int getSmallestWidthDpForBounds(final Context context, final DisplayLayout displayLayout, final Rect rect) {
        final int dividerSize = DockedDividerUtils.getDividerSize(context.getResources(), DockedDividerUtils.getDividerInsets(context.getResources()));
        final Rect rect2 = new Rect();
        final Rect rect3 = new Rect();
        final Rect rect4 = new Rect(0, 0, displayLayout.width(), displayLayout.height());
        final DisplayLayout displayLayout2 = new DisplayLayout();
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 4; ++i) {
            displayLayout2.set(displayLayout);
            displayLayout2.rotateTo(context.getResources(), i);
            final DividerSnapAlgorithm initSnapAlgorithmForRotation = initSnapAlgorithmForRotation(context, displayLayout2, dividerSize);
            rect2.set(rect);
            DisplayLayout.rotateBounds(rect2, rect4, i - displayLayout.rotation());
            rect3.set(0, 0, displayLayout2.width(), displayLayout2.height());
            final int primarySplitSide = getPrimarySplitSide(rect2, rect3, displayLayout2.getOrientation());
            DockedDividerUtils.calculateBoundsForPosition(initSnapAlgorithmForRotation.calculateNonDismissingSnapTarget(DockedDividerUtils.calculatePositionForBounds(rect2, primarySplitSide, dividerSize)).position, primarySplitSide, rect2, displayLayout2.width(), displayLayout2.height(), dividerSize);
            final Rect rect5 = new Rect(rect3);
            rect5.inset(displayLayout2.stableInsets());
            rect2.intersect(rect5);
            min = Math.min(rect2.width(), min);
        }
        return (int)(min / displayLayout.density());
    }
    
    static DividerSnapAlgorithm initSnapAlgorithmForRotation(final Context context, final DisplayLayout displayLayout, final int n) {
        final Configuration configuration = new Configuration();
        configuration.unset();
        configuration.orientation = displayLayout.getOrientation();
        final Rect appBounds = new Rect(0, 0, displayLayout.width(), displayLayout.height());
        appBounds.inset(displayLayout.nonDecorInsets());
        configuration.windowConfiguration.setAppBounds(appBounds);
        appBounds.set(0, 0, displayLayout.width(), displayLayout.height());
        appBounds.inset(displayLayout.stableInsets());
        configuration.screenWidthDp = (int)(appBounds.width() / displayLayout.density());
        configuration.screenHeightDp = (int)(appBounds.height() / displayLayout.density());
        return new DividerSnapAlgorithm(context.createConfigurationContext(configuration).getResources(), displayLayout.width(), displayLayout.height(), n, configuration.orientation == 1, displayLayout.stableInsets());
    }
    
    private void updateResources() {
        if (this.mResourcesValid) {
            return;
        }
        this.mResourcesValid = true;
        final Resources resources = this.mContext.getResources();
        this.mDividerSize = DockedDividerUtils.getDividerSize(resources, DockedDividerUtils.getDividerInsets(resources));
        this.mDividerSizeInactive = (int)TypedValue.applyDimension(1, 4.0f, resources.getDisplayMetrics());
    }
    
    Rect calcMinimizedHomeStackBounds() {
        final DividerSnapAlgorithm$SnapTarget middleTarget = this.getMinimizedSnapAlgorithm().getMiddleTarget();
        final Rect rect = new Rect();
        DockedDividerUtils.calculateBoundsForPosition(middleTarget.position, DockedDividerUtils.invertDockSide(this.getPrimarySplitSide()), rect, this.mDisplayLayout.width(), this.mDisplayLayout.height(), this.mDividerSize);
        return rect;
    }
    
    void calcSplitBounds(final int n, final Rect rect, final Rect rect2) {
        final int primarySplitSide = this.getPrimarySplitSide();
        DockedDividerUtils.calculateBoundsForPosition(n, primarySplitSide, rect, this.mDisplayLayout.width(), this.mDisplayLayout.height(), this.mDividerSize);
        DockedDividerUtils.calculateBoundsForPosition(n, DockedDividerUtils.invertDockSide(primarySplitSide), rect2, this.mDisplayLayout.width(), this.mDisplayLayout.height(), this.mDividerSize);
    }
    
    DividerSnapAlgorithm getMinimizedSnapAlgorithm() {
        if (this.mMinimizedSnapAlgorithm == null) {
            this.updateResources();
            this.mMinimizedSnapAlgorithm = new DividerSnapAlgorithm(this.mContext.getResources(), this.mDisplayLayout.width(), this.mDisplayLayout.height(), this.mDividerSize, this.mDisplayLayout.isLandscape() ^ true, this.mDisplayLayout.stableInsets(), this.getPrimarySplitSide(), true);
        }
        return this.mMinimizedSnapAlgorithm;
    }
    
    int getPrimarySplitSide() {
        int n;
        if (this.mDisplayLayout.isLandscape()) {
            n = 1;
        }
        else {
            n = 2;
        }
        return n;
    }
    
    DividerSnapAlgorithm getSnapAlgorithm() {
        if (this.mSnapAlgorithm == null) {
            this.updateResources();
            this.mSnapAlgorithm = new DividerSnapAlgorithm(this.mContext.getResources(), this.mDisplayLayout.width(), this.mDisplayLayout.height(), this.mDividerSize, this.mDisplayLayout.isLandscape() ^ true, this.mDisplayLayout.stableInsets(), this.getPrimarySplitSide());
        }
        return this.mSnapAlgorithm;
    }
    
    void resizeSplits(final int n) {
        Rect mPrimary;
        if ((mPrimary = this.mPrimary) == null) {
            mPrimary = new Rect();
        }
        this.mPrimary = mPrimary;
        Rect mSecondary;
        if ((mSecondary = this.mSecondary) == null) {
            mSecondary = new Rect();
        }
        this.mSecondary = mSecondary;
        this.calcSplitBounds(n, this.mPrimary, mSecondary);
    }
    
    void resizeSplits(final int n, final WindowContainerTransaction windowContainerTransaction) {
        this.resizeSplits(n);
        windowContainerTransaction.setBounds(this.mTiles.mPrimary.token, this.mPrimary);
        windowContainerTransaction.setBounds(this.mTiles.mSecondary.token, this.mSecondary);
        windowContainerTransaction.setSmallestScreenWidthDp(this.mTiles.mPrimary.token, getSmallestWidthDpForBounds(this.mContext, this.mDisplayLayout, this.mPrimary));
        windowContainerTransaction.setSmallestScreenWidthDp(this.mTiles.mSecondary.token, getSmallestWidthDpForBounds(this.mContext, this.mDisplayLayout, this.mSecondary));
    }
    
    void rotateTo(final int n) {
        this.mDisplayLayout.rotateTo(this.mContext.getResources(), n);
        final Configuration configuration = new Configuration();
        configuration.unset();
        configuration.orientation = this.mDisplayLayout.getOrientation();
        final Rect appBounds = new Rect(0, 0, this.mDisplayLayout.width(), this.mDisplayLayout.height());
        appBounds.inset(this.mDisplayLayout.nonDecorInsets());
        configuration.windowConfiguration.setAppBounds(appBounds);
        appBounds.set(0, 0, this.mDisplayLayout.width(), this.mDisplayLayout.height());
        appBounds.inset(this.mDisplayLayout.stableInsets());
        configuration.screenWidthDp = (int)(appBounds.width() / this.mDisplayLayout.density());
        configuration.screenHeightDp = (int)(appBounds.height() / this.mDisplayLayout.density());
        this.mContext = this.mContext.createConfigurationContext(configuration);
        this.mSnapAlgorithm = null;
        this.mMinimizedSnapAlgorithm = null;
        this.mResourcesValid = false;
    }
    
    void updateAdjustedBounds(final int n, final int n2, final int n3) {
        this.adjustForIME(this.mDisplayLayout, n, n2, n3, this.mDividerSize, this.mDividerSizeInactive, this.mPrimary, this.mSecondary);
    }
}
