// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.view.animation.Interpolator;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller;
import android.util.DisplayMetrics;
import androidx.recyclerview.widget.LinearSmoothScroller;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.FocusFinder;
import android.os.Build$VERSION;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.Gravity;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.util.AttributeSet;
import android.content.Context;
import java.util.List;
import java.util.Arrays;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View$MeasureSpec;
import androidx.collection.CircularIntArray;
import androidx.core.view.ViewCompat;
import android.view.ViewGroup;
import android.view.View;
import android.util.SparseIntArray;
import androidx.recyclerview.widget.OrientationHelper;
import java.util.ArrayList;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;

final class GridLayoutManager extends LayoutManager
{
    private static final Rect sTempRect;
    static int[] sTwoInts;
    final BaseGridView mBaseGridView;
    OnChildLaidOutListener mChildLaidOutListener;
    private OnChildSelectedListener mChildSelectedListener;
    private ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners;
    int mChildVisibility;
    final ViewsStateBundle mChildrenStates;
    GridLinearSmoothScroller mCurrentSmoothScroller;
    int[] mDisappearingPositions;
    private int mExtraLayoutSpace;
    int mExtraLayoutSpaceInPreLayout;
    private FacetProviderAdapter mFacetProviderAdapter;
    private int mFixedRowSizeSecondary;
    int mFlag;
    int mFocusPosition;
    private int mFocusPositionOffset;
    private int mFocusScrollStrategy;
    private int mGravity;
    Grid mGrid;
    private Grid.Provider mGridProvider;
    private final ItemAlignment mItemAlignment;
    int mMaxPendingMoves;
    private int mMaxSizeSecondary;
    private int[] mMeasuredDimension;
    int mNumRows;
    private int mNumRowsRequested;
    ArrayList<BaseGridView.OnLayoutCompletedListener> mOnLayoutCompletedListeners;
    int mOrientation;
    private OrientationHelper mOrientationHelper;
    PendingMoveSmoothScroller mPendingMoveSmoothScroller;
    int mPositionDeltaInPreLayout;
    final SparseIntArray mPositionToRowInPostLayout;
    private int mPrimaryScrollExtra;
    Recycler mRecycler;
    private final Runnable mRequestLayoutRunnable;
    private int[] mRowSizeSecondary;
    private int mRowSizeSecondaryRequested;
    int mScrollOffsetSecondary;
    private int mSizePrimary;
    float mSmoothScrollSpeedFactor;
    private int mSpacingPrimary;
    private int mSpacingSecondary;
    State mState;
    int mSubFocusPosition;
    private int mVerticalSpacing;
    final WindowAlignment mWindowAlignment;
    
    static {
        sTempRect = new Rect();
        GridLayoutManager.sTwoInts = new int[2];
    }
    
    public GridLayoutManager(final BaseGridView mBaseGridView) {
        this.mSmoothScrollSpeedFactor = 1.0f;
        this.mMaxPendingMoves = 10;
        this.mOrientation = 0;
        this.mOrientationHelper = OrientationHelper.createHorizontalHelper(this);
        this.mPositionToRowInPostLayout = new SparseIntArray();
        this.mFlag = 221696;
        this.mChildSelectedListener = null;
        this.mChildViewHolderSelectedListeners = null;
        this.mOnLayoutCompletedListeners = null;
        this.mChildLaidOutListener = null;
        this.mFocusPosition = -1;
        this.mSubFocusPosition = 0;
        this.mFocusPositionOffset = 0;
        this.mGravity = 8388659;
        this.mNumRowsRequested = 1;
        this.mFocusScrollStrategy = 0;
        this.mWindowAlignment = new WindowAlignment();
        this.mItemAlignment = new ItemAlignment();
        this.mMeasuredDimension = new int[2];
        this.mChildrenStates = new ViewsStateBundle();
        this.mRequestLayoutRunnable = new Runnable() {
            @Override
            public void run() {
                ((RecyclerView.LayoutManager)GridLayoutManager.this).requestLayout();
            }
        };
        this.mGridProvider = new Grid.Provider() {
            @Override
            public void addItem(final Object o, final int n, int n2, final int n3, int n4) {
                final View view = (View)o;
                int paddingMin;
                if (n4 == Integer.MIN_VALUE || (paddingMin = n4) == Integer.MAX_VALUE) {
                    if (!GridLayoutManager.this.mGrid.isReversedFlow()) {
                        paddingMin = GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMin();
                    }
                    else {
                        paddingMin = GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() - GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMax();
                    }
                }
                if (GridLayoutManager.this.mGrid.isReversedFlow() ^ true) {
                    n4 = n2 + paddingMin;
                    n2 = paddingMin;
                }
                else {
                    n2 = paddingMin - n2;
                    n4 = paddingMin;
                }
                final int rowStartSecondary = GridLayoutManager.this.getRowStartSecondary(n3);
                final int paddingMin2 = GridLayoutManager.this.mWindowAlignment.secondAxis().getPaddingMin();
                final GridLayoutManager this$0 = GridLayoutManager.this;
                final int mScrollOffsetSecondary = this$0.mScrollOffsetSecondary;
                this$0.mChildrenStates.loadView(view, n);
                GridLayoutManager.this.layoutChild(n3, view, n2, n4, rowStartSecondary + paddingMin2 - mScrollOffsetSecondary);
                if (!GridLayoutManager.this.mState.isPreLayout()) {
                    GridLayoutManager.this.updateScrollLimits();
                }
                final GridLayoutManager this$2 = GridLayoutManager.this;
                if ((this$2.mFlag & 0x3) != 0x1) {
                    final PendingMoveSmoothScroller mPendingMoveSmoothScroller = this$2.mPendingMoveSmoothScroller;
                    if (mPendingMoveSmoothScroller != null) {
                        mPendingMoveSmoothScroller.consumePendingMovesAfterLayout();
                    }
                }
                final GridLayoutManager this$3 = GridLayoutManager.this;
                if (this$3.mChildLaidOutListener != null) {
                    final RecyclerView.ViewHolder childViewHolder = this$3.mBaseGridView.getChildViewHolder(view);
                    final GridLayoutManager this$4 = GridLayoutManager.this;
                    final OnChildLaidOutListener mChildLaidOutListener = this$4.mChildLaidOutListener;
                    final BaseGridView mBaseGridView = this$4.mBaseGridView;
                    long itemId;
                    if (childViewHolder == null) {
                        itemId = -1L;
                    }
                    else {
                        itemId = childViewHolder.getItemId();
                    }
                    mChildLaidOutListener.onChildLaidOut(mBaseGridView, view, n, itemId);
                }
            }
            
            @Override
            public int createItem(int mFocusPosition, final boolean b, final Object[] array, final boolean b2) {
                final GridLayoutManager this$0 = GridLayoutManager.this;
                final View viewForPosition = this$0.getViewForPosition(mFocusPosition - this$0.mPositionDeltaInPreLayout);
                if (!((RecyclerView.LayoutParams)viewForPosition.getLayoutParams()).isItemRemoved()) {
                    if (b2) {
                        if (b) {
                            ((RecyclerView.LayoutManager)GridLayoutManager.this).addDisappearingView(viewForPosition);
                        }
                        else {
                            ((RecyclerView.LayoutManager)GridLayoutManager.this).addDisappearingView(viewForPosition, 0);
                        }
                    }
                    else if (b) {
                        ((RecyclerView.LayoutManager)GridLayoutManager.this).addView(viewForPosition);
                    }
                    else {
                        ((RecyclerView.LayoutManager)GridLayoutManager.this).addView(viewForPosition, 0);
                    }
                    final int mChildVisibility = GridLayoutManager.this.mChildVisibility;
                    if (mChildVisibility != -1) {
                        viewForPosition.setVisibility(mChildVisibility);
                    }
                    final PendingMoveSmoothScroller mPendingMoveSmoothScroller = GridLayoutManager.this.mPendingMoveSmoothScroller;
                    if (mPendingMoveSmoothScroller != null) {
                        mPendingMoveSmoothScroller.consumePendingMovesBeforeLayout();
                    }
                    final int subPositionByView = GridLayoutManager.this.getSubPositionByView(viewForPosition, viewForPosition.findFocus());
                    final GridLayoutManager this$2 = GridLayoutManager.this;
                    final int mFlag = this$2.mFlag;
                    if ((mFlag & 0x3) != 0x1) {
                        if (mFocusPosition == this$2.mFocusPosition && subPositionByView == this$2.mSubFocusPosition && this$2.mPendingMoveSmoothScroller == null) {
                            this$2.dispatchChildSelected();
                        }
                    }
                    else if ((mFlag & 0x4) == 0x0) {
                        if ((mFlag & 0x10) == 0x0 && mFocusPosition == this$2.mFocusPosition && subPositionByView == this$2.mSubFocusPosition) {
                            this$2.dispatchChildSelected();
                        }
                        else {
                            final GridLayoutManager this$3 = GridLayoutManager.this;
                            if ((this$3.mFlag & 0x10) != 0x0 && mFocusPosition >= this$3.mFocusPosition && viewForPosition.hasFocusable()) {
                                final GridLayoutManager this$4 = GridLayoutManager.this;
                                this$4.mFocusPosition = mFocusPosition;
                                this$4.mSubFocusPosition = subPositionByView;
                                this$4.mFlag &= 0xFFFFFFEF;
                                this$4.dispatchChildSelected();
                            }
                        }
                    }
                    GridLayoutManager.this.measureChild(viewForPosition);
                }
                array[0] = viewForPosition;
                final GridLayoutManager this$5 = GridLayoutManager.this;
                if (this$5.mOrientation == 0) {
                    mFocusPosition = this$5.getDecoratedMeasuredWidthWithMargin(viewForPosition);
                }
                else {
                    mFocusPosition = this$5.getDecoratedMeasuredHeightWithMargin(viewForPosition);
                }
                return mFocusPosition;
            }
            
            @Override
            public int getCount() {
                return GridLayoutManager.this.mState.getItemCount() + GridLayoutManager.this.mPositionDeltaInPreLayout;
            }
            
            @Override
            public int getEdge(int n) {
                final GridLayoutManager this$0 = GridLayoutManager.this;
                final View viewByPosition = ((RecyclerView.LayoutManager)this$0).findViewByPosition(n - this$0.mPositionDeltaInPreLayout);
                final GridLayoutManager this$2 = GridLayoutManager.this;
                if ((this$2.mFlag & 0x40000) != 0x0) {
                    n = this$2.getViewMax(viewByPosition);
                }
                else {
                    n = this$2.getViewMin(viewByPosition);
                }
                return n;
            }
            
            @Override
            public int getMinIndex() {
                return GridLayoutManager.this.mPositionDeltaInPreLayout;
            }
            
            @Override
            public int getSize(final int n) {
                final GridLayoutManager this$0 = GridLayoutManager.this;
                return this$0.getViewPrimarySize(((RecyclerView.LayoutManager)this$0).findViewByPosition(n - this$0.mPositionDeltaInPreLayout));
            }
            
            @Override
            public void removeItem(final int n) {
                final GridLayoutManager this$0 = GridLayoutManager.this;
                final View viewByPosition = ((RecyclerView.LayoutManager)this$0).findViewByPosition(n - this$0.mPositionDeltaInPreLayout);
                final GridLayoutManager this$2 = GridLayoutManager.this;
                if ((this$2.mFlag & 0x3) == 0x1) {
                    ((RecyclerView.LayoutManager)this$2).detachAndScrapView(viewByPosition, this$2.mRecycler);
                }
                else {
                    ((RecyclerView.LayoutManager)this$2).removeAndRecycleView(viewByPosition, this$2.mRecycler);
                }
            }
        };
        this.mBaseGridView = mBaseGridView;
        this.mChildVisibility = -1;
        ((RecyclerView.LayoutManager)this).setItemPrefetchEnabled(false);
    }
    
    private boolean appendOneColumnVisibleItems() {
        return this.mGrid.appendOneColumnVisibleItems();
    }
    
    private void appendVisibleItems() {
        final Grid mGrid = this.mGrid;
        int n;
        if ((this.mFlag & 0x40000) != 0x0) {
            n = -this.mExtraLayoutSpace - this.mExtraLayoutSpaceInPreLayout;
        }
        else {
            n = this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout;
        }
        mGrid.appendVisibleItems(n);
    }
    
    private void discardLayoutInfo() {
        this.mGrid = null;
        this.mRowSizeSecondary = null;
        this.mFlag &= 0xFFFFFBFF;
    }
    
    private void fastRelayout() {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        int firstVisibleIndex = this.mGrid.getFirstVisibleIndex();
        this.mFlag &= 0xFFFFFFF7;
        final int n = 0;
        int n2 = 0;
        int n3 = 0;
        Label_0256: {
            while (true) {
                n3 = n;
                if (n2 >= childCount) {
                    break Label_0256;
                }
                final View child = ((RecyclerView.LayoutManager)this).getChildAt(n2);
                if (firstVisibleIndex != this.getAdapterPositionByView(child)) {
                    break;
                }
                final Grid.Location location = this.mGrid.getLocation(firstVisibleIndex);
                if (location == null) {
                    break;
                }
                final int rowStartSecondary = this.getRowStartSecondary(location.row);
                final int paddingMin = this.mWindowAlignment.secondAxis().getPaddingMin();
                final int mScrollOffsetSecondary = this.mScrollOffsetSecondary;
                final int viewMin = this.getViewMin(child);
                final int viewPrimarySize = this.getViewPrimarySize(child);
                View viewForPosition = child;
                if (((RecyclerView.LayoutParams)child.getLayoutParams()).viewNeedsUpdate()) {
                    this.mFlag |= 0x8;
                    ((RecyclerView.LayoutManager)this).detachAndScrapView(child, this.mRecycler);
                    viewForPosition = this.getViewForPosition(firstVisibleIndex);
                    ((RecyclerView.LayoutManager)this).addView(viewForPosition, n2);
                }
                this.measureChild(viewForPosition);
                int n4;
                if (this.mOrientation == 0) {
                    n4 = this.getDecoratedMeasuredWidthWithMargin(viewForPosition);
                }
                else {
                    n4 = this.getDecoratedMeasuredHeightWithMargin(viewForPosition);
                }
                this.layoutChild(location.row, viewForPosition, viewMin, viewMin + n4, rowStartSecondary + paddingMin - mScrollOffsetSecondary);
                if (viewPrimarySize != n4) {
                    break;
                }
                ++n2;
                ++firstVisibleIndex;
            }
            n3 = 1;
        }
        if (n3 != 0) {
            final int lastVisibleIndex = this.mGrid.getLastVisibleIndex();
            for (int i = childCount - 1; i >= n2; --i) {
                ((RecyclerView.LayoutManager)this).detachAndScrapView(((RecyclerView.LayoutManager)this).getChildAt(i), this.mRecycler);
            }
            this.mGrid.invalidateItemsAfter(firstVisibleIndex);
            if ((this.mFlag & 0x10000) != 0x0) {
                this.appendVisibleItems();
                final int mFocusPosition = this.mFocusPosition;
                if (mFocusPosition >= 0 && mFocusPosition <= lastVisibleIndex) {
                    while (this.mGrid.getLastVisibleIndex() < this.mFocusPosition) {
                        this.mGrid.appendOneColumnVisibleItems();
                    }
                }
            }
            else {
                while (this.mGrid.appendOneColumnVisibleItems() && this.mGrid.getLastVisibleIndex() < lastVisibleIndex) {}
            }
        }
        this.updateScrollLimits();
        this.updateSecondaryScrollLimits();
    }
    
    private int findImmediateChildIndex(View containingItemView) {
        final BaseGridView mBaseGridView = this.mBaseGridView;
        if (mBaseGridView != null && containingItemView != mBaseGridView) {
            containingItemView = ((RecyclerView.LayoutManager)this).findContainingItemView(containingItemView);
            if (containingItemView != null) {
                for (int i = 0; i < ((RecyclerView.LayoutManager)this).getChildCount(); ++i) {
                    if (((RecyclerView.LayoutManager)this).getChildAt(i) == containingItemView) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    private void focusToViewInLayout(final boolean b, final boolean b2, final int n, final int n2) {
        View view = ((RecyclerView.LayoutManager)this).findViewByPosition(this.mFocusPosition);
        if (view != null && b2) {
            this.scrollToView(view, false, n, n2);
        }
        if (view != null && b && !view.hasFocus()) {
            view.requestFocus();
        }
        else if (!b && !this.mBaseGridView.hasFocus()) {
            if (view != null && view.hasFocusable()) {
                this.mBaseGridView.focusableViewAvailable(view);
            }
            else {
                for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i) {
                    view = ((RecyclerView.LayoutManager)this).getChildAt(i);
                    if (view != null && view.hasFocusable()) {
                        this.mBaseGridView.focusableViewAvailable(view);
                        break;
                    }
                }
            }
            if (b2 && view != null && view.hasFocus()) {
                this.scrollToView(view, false, n, n2);
            }
        }
    }
    
    private void forceRequestLayout() {
        ViewCompat.postOnAnimation((View)this.mBaseGridView, this.mRequestLayoutRunnable);
    }
    
    private int getAdapterPositionByIndex(final int n) {
        return this.getAdapterPositionByView(((RecyclerView.LayoutManager)this).getChildAt(n));
    }
    
    private int getAdapterPositionByView(final View view) {
        final int n = -1;
        if (view == null) {
            return -1;
        }
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        int viewAdapterPosition = n;
        if (layoutParams != null) {
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved()) {
                viewAdapterPosition = n;
            }
            else {
                viewAdapterPosition = ((RecyclerView.LayoutParams)layoutParams).getViewAdapterPosition();
            }
        }
        return viewAdapterPosition;
    }
    
    private int getAdjustedPrimaryAlignedScrollDistance(final int n, final View view, final View view2) {
        final int subPositionByView = this.getSubPositionByView(view, view2);
        int n2 = n;
        if (subPositionByView != 0) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            n2 = n + (layoutParams.getAlignMultiple()[subPositionByView] - layoutParams.getAlignMultiple()[0]);
        }
        return n2;
    }
    
    private boolean getAlignedPosition(final View view, final View view2, final int[] array) {
        int n = this.getPrimaryAlignedScrollDistance(view);
        if (view2 != null) {
            n = this.getAdjustedPrimaryAlignedScrollDistance(n, view, view2);
        }
        final int secondaryScrollDistance = this.getSecondaryScrollDistance(view);
        final int n2 = n + this.mPrimaryScrollExtra;
        if (n2 == 0 && secondaryScrollDistance == 0) {
            array[1] = (array[0] = 0);
            return false;
        }
        array[0] = n2;
        array[1] = secondaryScrollDistance;
        return true;
    }
    
    private int getMovement(final int n) {
        final int mOrientation = this.mOrientation;
        int n2 = 0;
        if (mOrientation == 0) {
            if (n != 17) {
                if (n == 33) {
                    return 2;
                }
                if (n != 66) {
                    if (n != 130) {
                        return 17;
                    }
                }
                else {
                    if ((this.mFlag & 0x40000) == 0x0) {
                        return 1;
                    }
                    return n2;
                }
            }
            else {
                if ((this.mFlag & 0x40000) == 0x0) {
                    return n2;
                }
                return 1;
            }
        }
        else {
            if (mOrientation != 1) {
                return 17;
            }
            if (n != 17) {
                if (n == 33) {
                    return n2;
                }
                if (n != 66) {
                    if (n != 130) {
                        return 17;
                    }
                    return 1;
                }
                else if ((this.mFlag & 0x80000) != 0x0) {
                    return 2;
                }
            }
            else if ((this.mFlag & 0x80000) == 0x0) {
                return 2;
            }
        }
        n2 = 3;
        return n2;
        n2 = 2;
        return n2;
        n2 = 1;
        return n2;
        n2 = 17;
        return n2;
    }
    
    private boolean getNoneAlignedPosition(View view, final int[] array) {
        final int adapterPositionByView = this.getAdapterPositionByView(view);
        final int viewMin = this.getViewMin(view);
        final int viewMax = this.getViewMax(view);
        int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
        final int clientSize = this.mWindowAlignment.mainAxis().getClientSize();
        final int rowIndex = this.mGrid.getRowIndex(adapterPositionByView);
        final View view2 = null;
        View viewByPosition2 = null;
        View viewByPosition3 = null;
        Label_0323: {
            View view5;
            if (viewMin < paddingMin) {
                if (this.mFocusScrollStrategy == 2) {
                    View viewByPosition = view;
                    while (this.prependOneColumnVisibleItems()) {
                        final Grid mGrid = this.mGrid;
                        final CircularIntArray circularIntArray = mGrid.getItemPositionsInRows(mGrid.getFirstVisibleIndex(), adapterPositionByView)[rowIndex];
                        viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(circularIntArray.get(0));
                        if (viewMax - this.getViewMin(viewByPosition) > clientSize) {
                            if (circularIntArray.size() > 2) {
                                viewByPosition2 = ((RecyclerView.LayoutManager)this).findViewByPosition(circularIntArray.get(2));
                                viewByPosition3 = null;
                                break Label_0323;
                            }
                            final View view3 = null;
                            viewByPosition2 = viewByPosition;
                            viewByPosition3 = view3;
                            break Label_0323;
                        }
                    }
                    final View view4 = null;
                    viewByPosition2 = viewByPosition;
                    viewByPosition3 = view4;
                    break Label_0323;
                }
                view5 = null;
            }
            else {
                if (viewMax <= clientSize + paddingMin) {
                    viewByPosition3 = null;
                    viewByPosition2 = view2;
                    break Label_0323;
                }
                if (this.mFocusScrollStrategy != 2) {
                    viewByPosition3 = view;
                    viewByPosition2 = view2;
                    break Label_0323;
                }
                do {
                    final Grid mGrid2 = this.mGrid;
                    final CircularIntArray circularIntArray2 = mGrid2.getItemPositionsInRows(adapterPositionByView, mGrid2.getLastVisibleIndex())[rowIndex];
                    viewByPosition3 = ((RecyclerView.LayoutManager)this).findViewByPosition(circularIntArray2.get(circularIntArray2.size() - 1));
                    if (this.getViewMax(viewByPosition3) - viewMin > clientSize) {
                        viewByPosition3 = null;
                        break;
                    }
                } while (this.appendOneColumnVisibleItems());
                if ((view5 = viewByPosition3) != null) {
                    viewByPosition2 = view2;
                    break Label_0323;
                }
            }
            final View view6 = view;
            viewByPosition3 = view5;
            viewByPosition2 = view6;
        }
        int n2 = 0;
        Label_0369: {
            int n;
            if (viewByPosition2 != null) {
                n = this.getViewMin(viewByPosition2);
            }
            else {
                if (viewByPosition3 == null) {
                    n2 = 0;
                    break Label_0369;
                }
                n = this.getViewMax(viewByPosition3);
                paddingMin += clientSize;
            }
            n2 = n - paddingMin;
        }
        if (viewByPosition2 != null) {
            view = viewByPosition2;
        }
        else if (viewByPosition3 != null) {
            view = viewByPosition3;
        }
        final int secondaryScrollDistance = this.getSecondaryScrollDistance(view);
        if (n2 == 0 && secondaryScrollDistance == 0) {
            return false;
        }
        array[0] = n2;
        array[1] = secondaryScrollDistance;
        return true;
    }
    
    private int getPrimaryAlignedScrollDistance(final View view) {
        return this.mWindowAlignment.mainAxis().getScroll(this.getViewCenter(view));
    }
    
    private int getRowSizeSecondary(final int n) {
        final int mFixedRowSizeSecondary = this.mFixedRowSizeSecondary;
        if (mFixedRowSizeSecondary != 0) {
            return mFixedRowSizeSecondary;
        }
        final int[] mRowSizeSecondary = this.mRowSizeSecondary;
        if (mRowSizeSecondary == null) {
            return 0;
        }
        return mRowSizeSecondary[n];
    }
    
    private int getSecondaryScrollDistance(final View view) {
        return this.mWindowAlignment.secondAxis().getScroll(this.getViewCenterSecondary(view));
    }
    
    private int getSizeSecondary() {
        int n;
        if ((this.mFlag & 0x80000) != 0x0) {
            n = 0;
        }
        else {
            n = this.mNumRows - 1;
        }
        return this.getRowStartSecondary(n) + this.getRowSizeSecondary(n);
    }
    
    private int getViewCenter(final View view) {
        int n;
        if (this.mOrientation == 0) {
            n = this.getViewCenterX(view);
        }
        else {
            n = this.getViewCenterY(view);
        }
        return n;
    }
    
    private int getViewCenterSecondary(final View view) {
        int n;
        if (this.mOrientation == 0) {
            n = this.getViewCenterY(view);
        }
        else {
            n = this.getViewCenterX(view);
        }
        return n;
    }
    
    private int getViewCenterX(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        return layoutParams.getOpticalLeft(view) + layoutParams.getAlignX();
    }
    
    private int getViewCenterY(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        return layoutParams.getOpticalTop(view) + layoutParams.getAlignY();
    }
    
    private boolean gridOnRequestFocusInDescendantsAligned(final int n, final Rect rect) {
        final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(this.mFocusPosition);
        return viewByPosition != null && viewByPosition.requestFocus(n, rect);
    }
    
    private boolean gridOnRequestFocusInDescendantsUnaligned(final int n, final Rect rect) {
        int i = ((RecyclerView.LayoutManager)this).getChildCount();
        int n2 = -1;
        int n3;
        if ((n & 0x2) != 0x0) {
            n2 = i;
            i = 0;
            n3 = 1;
        }
        else {
            --i;
            n3 = -1;
        }
        final int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
        final int clientSize = this.mWindowAlignment.mainAxis().getClientSize();
        while (i != n2) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            if (child.getVisibility() == 0 && this.getViewMin(child) >= paddingMin && this.getViewMax(child) <= clientSize + paddingMin && child.requestFocus(n, rect)) {
                return true;
            }
            i += n3;
        }
        return false;
    }
    
    private void initScrollController() {
        this.mWindowAlignment.reset();
        this.mWindowAlignment.horizontal.setSize(((RecyclerView.LayoutManager)this).getWidth());
        this.mWindowAlignment.vertical.setSize(((RecyclerView.LayoutManager)this).getHeight());
        this.mWindowAlignment.horizontal.setPadding(((RecyclerView.LayoutManager)this).getPaddingLeft(), ((RecyclerView.LayoutManager)this).getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(((RecyclerView.LayoutManager)this).getPaddingTop(), ((RecyclerView.LayoutManager)this).getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
        this.mScrollOffsetSecondary = 0;
    }
    
    private boolean layoutInit() {
        final int itemCount = this.mState.getItemCount();
        final boolean b = true;
        if (itemCount == 0) {
            this.mFocusPosition = -1;
            this.mSubFocusPosition = 0;
        }
        else {
            final int mFocusPosition = this.mFocusPosition;
            if (mFocusPosition >= itemCount) {
                this.mFocusPosition = itemCount - 1;
                this.mSubFocusPosition = 0;
            }
            else if (mFocusPosition == -1 && itemCount > 0) {
                this.mFocusPosition = 0;
                this.mSubFocusPosition = 0;
            }
        }
        if (!this.mState.didStructureChange()) {
            final Grid mGrid = this.mGrid;
            if (mGrid != null && mGrid.getFirstVisibleIndex() >= 0 && (this.mFlag & 0x100) == 0x0 && this.mGrid.getNumRows() == this.mNumRows) {
                this.updateScrollController();
                this.updateSecondaryScrollLimits();
                this.mGrid.setSpacing(this.mSpacingPrimary);
                return true;
            }
        }
        this.mFlag &= 0xFFFFFEFF;
        final Grid mGrid2 = this.mGrid;
        if (mGrid2 == null || this.mNumRows != mGrid2.getNumRows() || (this.mFlag & 0x40000) != 0x0 != this.mGrid.isReversedFlow()) {
            (this.mGrid = Grid.createGrid(this.mNumRows)).setProvider(this.mGridProvider);
            this.mGrid.setReversedFlow((0x40000 & this.mFlag) != 0x0 && b);
        }
        this.initScrollController();
        this.updateSecondaryScrollLimits();
        this.mGrid.setSpacing(this.mSpacingPrimary);
        ((RecyclerView.LayoutManager)this).detachAndScrapAttachedViews(this.mRecycler);
        this.mGrid.resetVisibleIndex();
        this.mWindowAlignment.mainAxis().invalidateScrollMin();
        this.mWindowAlignment.mainAxis().invalidateScrollMax();
        return false;
    }
    
    private void leaveContext() {
        this.mRecycler = null;
        this.mState = null;
        this.mPositionDeltaInPreLayout = 0;
        this.mExtraLayoutSpaceInPreLayout = 0;
    }
    
    private void measureScrapChild(int bottomMargin, final int n, final int n2, final int[] array) {
        final View viewForPosition = this.mRecycler.getViewForPosition(bottomMargin);
        if (viewForPosition != null) {
            final LayoutParams layoutParams = (LayoutParams)viewForPosition.getLayoutParams();
            ((RecyclerView.LayoutManager)this).calculateItemDecorationsForChild(viewForPosition, GridLayoutManager.sTempRect);
            final int leftMargin = layoutParams.leftMargin;
            final int rightMargin = layoutParams.rightMargin;
            final Rect sTempRect = GridLayoutManager.sTempRect;
            final int left = sTempRect.left;
            final int right = sTempRect.right;
            final int topMargin = layoutParams.topMargin;
            bottomMargin = layoutParams.bottomMargin;
            viewForPosition.measure(ViewGroup.getChildMeasureSpec(n, ((RecyclerView.LayoutManager)this).getPaddingLeft() + ((RecyclerView.LayoutManager)this).getPaddingRight() + (leftMargin + rightMargin + left + right), layoutParams.width), ViewGroup.getChildMeasureSpec(n2, ((RecyclerView.LayoutManager)this).getPaddingTop() + ((RecyclerView.LayoutManager)this).getPaddingBottom() + (topMargin + bottomMargin + sTempRect.top + sTempRect.bottom), layoutParams.height));
            array[0] = this.getDecoratedMeasuredWidthWithMargin(viewForPosition);
            array[1] = this.getDecoratedMeasuredHeightWithMargin(viewForPosition);
            this.mRecycler.recycleView(viewForPosition);
        }
    }
    
    private void offsetChildrenPrimary(final int n) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        final int mOrientation = this.mOrientation;
        int i = 0;
        final int n2 = 0;
        if (mOrientation == 1) {
            for (int j = n2; j < childCount; ++j) {
                ((RecyclerView.LayoutManager)this).getChildAt(j).offsetTopAndBottom(n);
            }
        }
        else {
            while (i < childCount) {
                ((RecyclerView.LayoutManager)this).getChildAt(i).offsetLeftAndRight(n);
                ++i;
            }
        }
    }
    
    private void offsetChildrenSecondary(final int n) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        final int mOrientation = this.mOrientation;
        int i = 0;
        final int n2 = 0;
        if (mOrientation == 0) {
            for (int j = n2; j < childCount; ++j) {
                ((RecyclerView.LayoutManager)this).getChildAt(j).offsetTopAndBottom(n);
            }
        }
        else {
            while (i < childCount) {
                ((RecyclerView.LayoutManager)this).getChildAt(i).offsetLeftAndRight(n);
                ++i;
            }
        }
    }
    
    private boolean prependOneColumnVisibleItems() {
        return this.mGrid.prependOneColumnVisibleItems();
    }
    
    private void prependVisibleItems() {
        final Grid mGrid = this.mGrid;
        int n;
        if ((this.mFlag & 0x40000) != 0x0) {
            n = this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout;
        }
        else {
            n = -this.mExtraLayoutSpace - this.mExtraLayoutSpaceInPreLayout;
        }
        mGrid.prependVisibleItems(n);
    }
    
    private boolean processRowSizeSecondary(final boolean b) {
        if (this.mFixedRowSizeSecondary == 0 && this.mRowSizeSecondary != null) {
            final Grid mGrid = this.mGrid;
            CircularIntArray[] itemPositionsInRows;
            if (mGrid == null) {
                itemPositionsInRows = null;
            }
            else {
                itemPositionsInRows = mGrid.getItemPositionsInRows();
            }
            boolean b2;
            int i = (b2 = false) ? 1 : 0;
            int n = -1;
            while (i < this.mNumRows) {
                CircularIntArray circularIntArray;
                if (itemPositionsInRows == null) {
                    circularIntArray = null;
                }
                else {
                    circularIntArray = itemPositionsInRows[i];
                }
                int size;
                if (circularIntArray == null) {
                    size = 0;
                }
                else {
                    size = circularIntArray.size();
                }
                int j = 0;
                int n2 = -1;
                while (j < size) {
                    int n3;
                    for (int k = circularIntArray.get(j); k <= circularIntArray.get(j + 1); ++k, n2 = n3) {
                        final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(k - this.mPositionDeltaInPreLayout);
                        if (viewByPosition == null) {
                            n3 = n2;
                        }
                        else {
                            if (b) {
                                this.measureChild(viewByPosition);
                            }
                            int n4;
                            if (this.mOrientation == 0) {
                                n4 = this.getDecoratedMeasuredHeightWithMargin(viewByPosition);
                            }
                            else {
                                n4 = this.getDecoratedMeasuredWidthWithMargin(viewByPosition);
                            }
                            n3 = n2;
                            if (n4 > n2) {
                                n3 = n4;
                            }
                        }
                    }
                    j += 2;
                }
                final int itemCount = this.mState.getItemCount();
                int n5 = n;
                int n6 = n2;
                if (!this.mBaseGridView.hasFixedSize()) {
                    n5 = n;
                    n6 = n2;
                    if (b) {
                        n5 = n;
                        if ((n6 = n2) < 0) {
                            n5 = n;
                            n6 = n2;
                            if (itemCount > 0) {
                                int n7;
                                if ((n7 = n) < 0) {
                                    final int mFocusPosition = this.mFocusPosition;
                                    int n8;
                                    if (mFocusPosition < 0) {
                                        n8 = 0;
                                    }
                                    else if ((n8 = mFocusPosition) >= itemCount) {
                                        n8 = itemCount - 1;
                                    }
                                    int n9 = n8;
                                    if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
                                        final int layoutPosition = this.mBaseGridView.getChildViewHolder(((RecyclerView.LayoutManager)this).getChildAt(0)).getLayoutPosition();
                                        final int layoutPosition2 = this.mBaseGridView.getChildViewHolder(((RecyclerView.LayoutManager)this).getChildAt(((RecyclerView.LayoutManager)this).getChildCount() - 1)).getLayoutPosition();
                                        if ((n9 = n8) >= layoutPosition && (n9 = n8) <= layoutPosition2) {
                                            int n10;
                                            if (n8 - layoutPosition <= layoutPosition2 - n8) {
                                                n10 = layoutPosition - 1;
                                            }
                                            else {
                                                n10 = layoutPosition2 + 1;
                                            }
                                            if (n10 < 0 && layoutPosition2 < itemCount - 1) {
                                                n9 = layoutPosition2 + 1;
                                            }
                                            else if ((n9 = n10) >= itemCount) {
                                                n9 = n10;
                                                if (layoutPosition > 0) {
                                                    n9 = layoutPosition - 1;
                                                }
                                            }
                                        }
                                    }
                                    n7 = n;
                                    if (n9 >= 0) {
                                        n7 = n;
                                        if (n9 < itemCount) {
                                            this.measureScrapChild(n9, View$MeasureSpec.makeMeasureSpec(0, 0), View$MeasureSpec.makeMeasureSpec(0, 0), this.mMeasuredDimension);
                                            if (this.mOrientation == 0) {
                                                n7 = this.mMeasuredDimension[1];
                                            }
                                            else {
                                                n7 = this.mMeasuredDimension[0];
                                            }
                                        }
                                    }
                                }
                                n5 = n7;
                                n6 = n2;
                                if (n7 >= 0) {
                                    n6 = n7;
                                    n5 = n7;
                                }
                            }
                        }
                    }
                }
                int n11;
                if ((n11 = n6) < 0) {
                    n11 = 0;
                }
                final int[] mRowSizeSecondary = this.mRowSizeSecondary;
                if (mRowSizeSecondary[i] != n11) {
                    mRowSizeSecondary[i] = n11;
                    b2 = true;
                }
                ++i;
                n = n5;
            }
            return b2;
        }
        return false;
    }
    
    private void removeInvisibleViewsAtEnd() {
        final int mFlag = this.mFlag;
        if ((0x10040 & mFlag) == 0x10000) {
            final Grid mGrid = this.mGrid;
            final int mFocusPosition = this.mFocusPosition;
            int n;
            if ((mFlag & 0x40000) != 0x0) {
                n = -this.mExtraLayoutSpace;
            }
            else {
                n = this.mExtraLayoutSpace + this.mSizePrimary;
            }
            mGrid.removeInvisibleItemsAtEnd(mFocusPosition, n);
        }
    }
    
    private void removeInvisibleViewsAtFront() {
        final int mFlag = this.mFlag;
        if ((0x10040 & mFlag) == 0x10000) {
            final Grid mGrid = this.mGrid;
            final int mFocusPosition = this.mFocusPosition;
            int n;
            if ((mFlag & 0x40000) != 0x0) {
                n = this.mSizePrimary + this.mExtraLayoutSpace;
            }
            else {
                n = -this.mExtraLayoutSpace;
            }
            mGrid.removeInvisibleItemsAtFront(mFocusPosition, n);
        }
    }
    
    private void saveContext(final Recycler mRecycler, final State mState) {
        if (this.mRecycler != null || this.mState != null) {
            Log.e("GridLayoutManager", "Recycler information was not released, bug!");
        }
        this.mRecycler = mRecycler;
        this.mState = mState;
        this.mPositionDeltaInPreLayout = 0;
        this.mExtraLayoutSpaceInPreLayout = 0;
    }
    
    private int scrollDirectionPrimary(int childCount) {
        final int mFlag = this.mFlag;
        boolean b = true;
        int n = childCount;
        Label_0118: {
            if ((mFlag & 0x40) == 0x0) {
                n = childCount;
                if ((mFlag & 0x3) != 0x1) {
                    if (childCount > 0) {
                        n = childCount;
                        if (this.mWindowAlignment.mainAxis().isMaxUnknown()) {
                            break Label_0118;
                        }
                        final int maxScroll = this.mWindowAlignment.mainAxis().getMaxScroll();
                        if ((n = childCount) <= maxScroll) {
                            break Label_0118;
                        }
                        childCount = maxScroll;
                    }
                    else {
                        if ((n = childCount) >= 0) {
                            break Label_0118;
                        }
                        n = childCount;
                        if (this.mWindowAlignment.mainAxis().isMinUnknown()) {
                            break Label_0118;
                        }
                        final int minScroll = this.mWindowAlignment.mainAxis().getMinScroll();
                        if ((n = childCount) >= minScroll) {
                            break Label_0118;
                        }
                        childCount = minScroll;
                    }
                    n = childCount;
                }
            }
        }
        if (n == 0) {
            return 0;
        }
        this.offsetChildrenPrimary(-n);
        if ((this.mFlag & 0x3) == 0x1) {
            this.updateScrollLimits();
            return n;
        }
        childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        Label_0188: {
            Label_0184: {
                if ((this.mFlag & 0x40000) != 0x0) {
                    if (n <= 0) {
                        break Label_0184;
                    }
                }
                else if (n >= 0) {
                    break Label_0184;
                }
                this.prependVisibleItems();
                break Label_0188;
            }
            this.appendVisibleItems();
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() > childCount) {
            childCount = 1;
        }
        else {
            childCount = 0;
        }
        final int childCount2 = ((RecyclerView.LayoutManager)this).getChildCount();
        Label_0242: {
            Label_0238: {
                if ((0x40000 & this.mFlag) != 0x0) {
                    if (n <= 0) {
                        break Label_0238;
                    }
                }
                else if (n >= 0) {
                    break Label_0238;
                }
                this.removeInvisibleViewsAtEnd();
                break Label_0242;
            }
            this.removeInvisibleViewsAtFront();
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() >= childCount2) {
            b = false;
        }
        if ((childCount | (b ? 1 : 0)) != 0x0) {
            this.updateRowSecondarySizeRefresh();
        }
        this.mBaseGridView.invalidate();
        this.updateScrollLimits();
        return n;
    }
    
    private int scrollDirectionSecondary(final int n) {
        if (n == 0) {
            return 0;
        }
        this.offsetChildrenSecondary(-n);
        this.mScrollOffsetSecondary += n;
        this.updateSecondaryScrollLimits();
        this.mBaseGridView.invalidate();
        return n;
    }
    
    private void scrollGrid(int n, int n2, final boolean b) {
        if ((this.mFlag & 0x3) == 0x1) {
            this.scrollDirectionPrimary(n);
            this.scrollDirectionSecondary(n2);
        }
        else {
            if (this.mOrientation != 0) {
                final int n3 = n2;
                n2 = n;
                n = n3;
            }
            if (b) {
                this.mBaseGridView.smoothScrollBy(n, n2);
            }
            else {
                this.mBaseGridView.scrollBy(n, n2);
                this.dispatchChildSelectedAndPositioned();
            }
        }
    }
    
    private void scrollToView(final View view, final View view2, final boolean b) {
        this.scrollToView(view, view2, b, 0, 0);
    }
    
    private void scrollToView(final View view, final View view2, final boolean b, final int n, final int n2) {
        if ((this.mFlag & 0x40) != 0x0) {
            return;
        }
        final int adapterPositionByView = this.getAdapterPositionByView(view);
        final int subPositionByView = this.getSubPositionByView(view, view2);
        if (adapterPositionByView != this.mFocusPosition || subPositionByView != this.mSubFocusPosition) {
            this.mFocusPosition = adapterPositionByView;
            this.mSubFocusPosition = subPositionByView;
            this.mFocusPositionOffset = 0;
            if ((this.mFlag & 0x3) != 0x1) {
                this.dispatchChildSelected();
            }
            if (this.mBaseGridView.isChildrenDrawingOrderEnabledInternal()) {
                this.mBaseGridView.invalidate();
            }
        }
        if (view == null) {
            return;
        }
        if (!view.hasFocus() && this.mBaseGridView.hasFocus()) {
            view.requestFocus();
        }
        if ((this.mFlag & 0x20000) == 0x0 && b) {
            return;
        }
        if (this.getScrollPosition(view, view2, GridLayoutManager.sTwoInts) || n != 0 || n2 != 0) {
            final int[] sTwoInts = GridLayoutManager.sTwoInts;
            this.scrollGrid(sTwoInts[0] + n, sTwoInts[1] + n2, b);
        }
    }
    
    private void updateChildAlignments(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.getItemAlignmentFacet() == null) {
            layoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(view));
            layoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(view));
        }
        else {
            layoutParams.calculateItemAlignments(this.mOrientation, view);
            if (this.mOrientation == 0) {
                layoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(view));
            }
            else {
                layoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(view));
            }
        }
    }
    
    private void updateRowSecondarySizeRefresh() {
        final int mFlag = this.mFlag;
        int n = 0;
        if (this.processRowSizeSecondary(false)) {
            n = 1024;
        }
        final int mFlag2 = (mFlag & 0xFFFFFBFF) | n;
        this.mFlag = mFlag2;
        if ((mFlag2 & 0x400) != 0x0) {
            this.forceRequestLayout();
        }
    }
    
    private void updateScrollController() {
        this.mWindowAlignment.horizontal.setSize(((RecyclerView.LayoutManager)this).getWidth());
        this.mWindowAlignment.vertical.setSize(((RecyclerView.LayoutManager)this).getHeight());
        this.mWindowAlignment.horizontal.setPadding(((RecyclerView.LayoutManager)this).getPaddingLeft(), ((RecyclerView.LayoutManager)this).getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(((RecyclerView.LayoutManager)this).getPaddingTop(), ((RecyclerView.LayoutManager)this).getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
    }
    
    private void updateSecondaryScrollLimits() {
        final WindowAlignment.Axis secondAxis = this.mWindowAlignment.secondAxis();
        final int n = secondAxis.getPaddingMin() - this.mScrollOffsetSecondary;
        final int n2 = this.getSizeSecondary() + n;
        secondAxis.updateMinMax(n, n2, n, n2);
    }
    
    @Override
    public boolean canScrollHorizontally() {
        final int mOrientation = this.mOrientation;
        boolean b = true;
        if (mOrientation != 0) {
            b = (this.mNumRows > 1 && b);
        }
        return b;
    }
    
    boolean canScrollTo(final View view) {
        return view.getVisibility() == 0 && (!((RecyclerView.LayoutManager)this).hasFocus() || view.hasFocusable());
    }
    
    @Override
    public boolean canScrollVertically() {
        final int mOrientation = this.mOrientation;
        boolean b = true;
        if (mOrientation != 1) {
            b = (this.mNumRows > 1 && b);
        }
        return b;
    }
    
    @Override
    public void collectAdjacentPrefetchPositions(int n, int n2, final State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        try {
            this.saveContext(null, state);
            if (this.mOrientation != 0) {
                n = n2;
            }
            if (((RecyclerView.LayoutManager)this).getChildCount() != 0 && n != 0) {
                if (n < 0) {
                    n2 = -this.mExtraLayoutSpace;
                }
                else {
                    n2 = this.mSizePrimary + this.mExtraLayoutSpace;
                }
                this.mGrid.collectAdjacentPrefetchPositions(n2, n, layoutPrefetchRegistry);
            }
        }
        finally {
            this.leaveContext();
        }
    }
    
    @Override
    public void collectInitialPrefetchPositions(final int n, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        final int mInitialPrefetchItemCount = this.mBaseGridView.mInitialPrefetchItemCount;
        if (n != 0 && mInitialPrefetchItemCount != 0) {
            int max;
            for (int n2 = max = Math.max(0, Math.min(this.mFocusPosition - (mInitialPrefetchItemCount - 1) / 2, n - mInitialPrefetchItemCount)); max < n && max < n2 + mInitialPrefetchItemCount; ++max) {
                layoutPrefetchRegistry.addPosition(max, 0);
            }
        }
    }
    
    void dispatchChildSelected() {
        if (this.mChildSelectedListener == null && !this.hasOnChildViewHolderSelectedListener()) {
            return;
        }
        final int mFocusPosition = this.mFocusPosition;
        View viewByPosition;
        if (mFocusPosition == -1) {
            viewByPosition = null;
        }
        else {
            viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(mFocusPosition);
        }
        int i = 0;
        if (viewByPosition != null) {
            final RecyclerView.ViewHolder childViewHolder = this.mBaseGridView.getChildViewHolder(viewByPosition);
            final OnChildSelectedListener mChildSelectedListener = this.mChildSelectedListener;
            if (mChildSelectedListener != null) {
                final BaseGridView mBaseGridView = this.mBaseGridView;
                final int mFocusPosition2 = this.mFocusPosition;
                long itemId;
                if (childViewHolder == null) {
                    itemId = -1L;
                }
                else {
                    itemId = childViewHolder.getItemId();
                }
                mChildSelectedListener.onChildSelected(mBaseGridView, viewByPosition, mFocusPosition2, itemId);
            }
            this.fireOnChildViewHolderSelected(this.mBaseGridView, childViewHolder, this.mFocusPosition, this.mSubFocusPosition);
        }
        else {
            final OnChildSelectedListener mChildSelectedListener2 = this.mChildSelectedListener;
            if (mChildSelectedListener2 != null) {
                mChildSelectedListener2.onChildSelected(this.mBaseGridView, null, -1, -1L);
            }
            this.fireOnChildViewHolderSelected(this.mBaseGridView, null, -1, 0);
        }
        if ((this.mFlag & 0x3) != 0x1 && !this.mBaseGridView.isLayoutRequested()) {
            while (i < ((RecyclerView.LayoutManager)this).getChildCount()) {
                if (((RecyclerView.LayoutManager)this).getChildAt(i).isLayoutRequested()) {
                    this.forceRequestLayout();
                    break;
                }
                ++i;
            }
        }
    }
    
    void dispatchChildSelectedAndPositioned() {
        if (!this.hasOnChildViewHolderSelectedListener()) {
            return;
        }
        final int mFocusPosition = this.mFocusPosition;
        View viewByPosition;
        if (mFocusPosition == -1) {
            viewByPosition = null;
        }
        else {
            viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(mFocusPosition);
        }
        if (viewByPosition != null) {
            this.fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, this.mBaseGridView.getChildViewHolder(viewByPosition), this.mFocusPosition, this.mSubFocusPosition);
        }
        else {
            final OnChildSelectedListener mChildSelectedListener = this.mChildSelectedListener;
            if (mChildSelectedListener != null) {
                mChildSelectedListener.onChildSelected(this.mBaseGridView, null, -1, -1L);
            }
            this.fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, null, -1, 0);
        }
    }
    
    void fillScrapViewsInPostLayout() {
        final List<ViewHolder> scrapList = this.mRecycler.getScrapList();
        final int size = scrapList.size();
        if (size == 0) {
            return;
        }
        final int[] mDisappearingPositions = this.mDisappearingPositions;
        if (mDisappearingPositions == null || size > mDisappearingPositions.length) {
            final int[] mDisappearingPositions2 = this.mDisappearingPositions;
            int i;
            if (mDisappearingPositions2 == null) {
                i = 16;
            }
            else {
                i = mDisappearingPositions2.length;
            }
            while (i < size) {
                i <<= 1;
            }
            this.mDisappearingPositions = new int[i];
        }
        int toIndex;
        int n;
        for (int j = toIndex = 0; j < size; ++j, toIndex = n) {
            final int absoluteAdapterPosition = scrapList.get(j).getAbsoluteAdapterPosition();
            n = toIndex;
            if (absoluteAdapterPosition >= 0) {
                this.mDisappearingPositions[toIndex] = absoluteAdapterPosition;
                n = toIndex + 1;
            }
        }
        if (toIndex > 0) {
            Arrays.sort(this.mDisappearingPositions, 0, toIndex);
            this.mGrid.fillDisappearingItems(this.mDisappearingPositions, toIndex, this.mPositionToRowInPostLayout);
        }
        this.mPositionToRowInPostLayout.clear();
    }
    
    void fireOnChildViewHolderSelected(final RecyclerView recyclerView, final ViewHolder viewHolder, final int n, final int n2) {
        final ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = this.mChildViewHolderSelectedListeners;
        if (mChildViewHolderSelectedListeners == null) {
            return;
        }
        for (int i = mChildViewHolderSelectedListeners.size() - 1; i >= 0; --i) {
            this.mChildViewHolderSelectedListeners.get(i).onChildViewHolderSelected(recyclerView, viewHolder, n, n2);
        }
    }
    
    void fireOnChildViewHolderSelectedAndPositioned(final RecyclerView recyclerView, final ViewHolder viewHolder, final int n, final int n2) {
        final ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = this.mChildViewHolderSelectedListeners;
        if (mChildViewHolderSelectedListeners == null) {
            return;
        }
        for (int i = mChildViewHolderSelectedListeners.size() - 1; i >= 0; --i) {
            this.mChildViewHolderSelectedListeners.get(i).onChildViewHolderSelectedAndPositioned(recyclerView, viewHolder, n, n2);
        }
    }
    
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }
    
    @Override
    public RecyclerView.LayoutParams generateLayoutParams(final Context context, final AttributeSet set) {
        return new LayoutParams(context, set);
    }
    
    @Override
    public RecyclerView.LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        if (viewGroup$LayoutParams instanceof RecyclerView.LayoutParams) {
            return new LayoutParams((RecyclerView.LayoutParams)viewGroup$LayoutParams);
        }
        if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
            return new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        return new LayoutParams(viewGroup$LayoutParams);
    }
    
    int getChildDrawingOrder(final RecyclerView recyclerView, final int n, final int n2) {
        final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(this.mFocusPosition);
        if (viewByPosition == null) {
            return n2;
        }
        final int indexOfChild = recyclerView.indexOfChild(viewByPosition);
        if (n2 < indexOfChild) {
            return n2;
        }
        int n3 = indexOfChild;
        if (n2 < n - 1) {
            n3 = indexOfChild + n - 1 - n2;
        }
        return n3;
    }
    
    @Override
    public int getColumnCountForAccessibility(final Recycler recycler, final State state) {
        if (this.mOrientation == 1) {
            final Grid mGrid = this.mGrid;
            if (mGrid != null) {
                return mGrid.getNumRows();
            }
        }
        return super.getColumnCountForAccessibility(recycler, state);
    }
    
    @Override
    public int getDecoratedBottom(final View view) {
        return super.getDecoratedBottom(view) - ((LayoutParams)view.getLayoutParams()).mBottomInset;
    }
    
    @Override
    public void getDecoratedBoundsWithMargins(final View view, final Rect rect) {
        super.getDecoratedBoundsWithMargins(view, rect);
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        rect.left += layoutParams.mLeftInset;
        rect.top += layoutParams.mTopInset;
        rect.right -= layoutParams.mRightInset;
        rect.bottom -= layoutParams.mBottomInset;
    }
    
    @Override
    public int getDecoratedLeft(final View view) {
        return super.getDecoratedLeft(view) + ((LayoutParams)view.getLayoutParams()).mLeftInset;
    }
    
    int getDecoratedMeasuredHeightWithMargin(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        return ((RecyclerView.LayoutManager)this).getDecoratedMeasuredHeight(view) + layoutParams.topMargin + layoutParams.bottomMargin;
    }
    
    int getDecoratedMeasuredWidthWithMargin(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        return ((RecyclerView.LayoutManager)this).getDecoratedMeasuredWidth(view) + layoutParams.leftMargin + layoutParams.rightMargin;
    }
    
    @Override
    public int getDecoratedRight(final View view) {
        return super.getDecoratedRight(view) - ((LayoutParams)view.getLayoutParams()).mRightInset;
    }
    
    @Override
    public int getDecoratedTop(final View view) {
        return super.getDecoratedTop(view) + ((LayoutParams)view.getLayoutParams()).mTopInset;
    }
    
     <E> E getFacet(final ViewHolder viewHolder, final Class<? extends E> clazz) {
        Object facet;
        if (viewHolder instanceof FacetProvider) {
            facet = ((FacetProvider)viewHolder).getFacet(clazz);
        }
        else {
            facet = null;
        }
        Object facet2 = facet;
        if (facet == null) {
            final FacetProviderAdapter mFacetProviderAdapter = this.mFacetProviderAdapter;
            facet2 = facet;
            if (mFacetProviderAdapter != null) {
                final FacetProvider facetProvider = mFacetProviderAdapter.getFacetProvider(viewHolder.getItemViewType());
                facet2 = facet;
                if (facetProvider != null) {
                    facet2 = facetProvider.getFacet(clazz);
                }
            }
        }
        return (E)facet2;
    }
    
    final int getOpticalLeft(final View view) {
        return ((LayoutParams)view.getLayoutParams()).getOpticalLeft(view);
    }
    
    final int getOpticalRight(final View view) {
        return ((LayoutParams)view.getLayoutParams()).getOpticalRight(view);
    }
    
    @Override
    public int getRowCountForAccessibility(final Recycler recycler, final State state) {
        if (this.mOrientation == 0) {
            final Grid mGrid = this.mGrid;
            if (mGrid != null) {
                return mGrid.getNumRows();
            }
        }
        return super.getRowCountForAccessibility(recycler, state);
    }
    
    int getRowStartSecondary(final int n) {
        final int mFlag = this.mFlag;
        int i = 0;
        int n2 = 0;
        int n4;
        if ((mFlag & 0x80000) != 0x0) {
            int n3 = this.mNumRows - 1;
            while (true) {
                n4 = n2;
                if (n3 <= n) {
                    break;
                }
                n2 += this.getRowSizeSecondary(n3) + this.mSpacingSecondary;
                --n3;
            }
        }
        else {
            int n5 = 0;
            while (i < n) {
                n5 += this.getRowSizeSecondary(i) + this.mSpacingSecondary;
                ++i;
            }
            n4 = n5;
        }
        return n4;
    }
    
    boolean getScrollPosition(final View view, final View view2, final int[] array) {
        final int mFocusScrollStrategy = this.mFocusScrollStrategy;
        if (mFocusScrollStrategy != 1 && mFocusScrollStrategy != 2) {
            return this.getAlignedPosition(view, view2, array);
        }
        return this.getNoneAlignedPosition(view, array);
    }
    
    public int getSelection() {
        return this.mFocusPosition;
    }
    
    int getSlideOutDistance() {
        int n;
        int n2;
        if (this.mOrientation == 1) {
            int width;
            n = (width = -((RecyclerView.LayoutManager)this).getHeight());
            if (((RecyclerView.LayoutManager)this).getChildCount() <= 0) {
                return width;
            }
            final int top = ((RecyclerView.LayoutManager)this).getChildAt(0).getTop();
            width = n;
            if (top >= 0) {
                return width;
            }
            n2 = top;
        }
        else if ((this.mFlag & 0x40000) != 0x0) {
            int width = ((RecyclerView.LayoutManager)this).getWidth();
            if (((RecyclerView.LayoutManager)this).getChildCount() <= 0) {
                return width;
            }
            final int right = ((RecyclerView.LayoutManager)this).getChildAt(0).getRight();
            if (right > (width = width)) {
                width = right;
                return width;
            }
            return width;
        }
        else {
            int width;
            n = (width = -((RecyclerView.LayoutManager)this).getWidth());
            if (((RecyclerView.LayoutManager)this).getChildCount() <= 0) {
                return width;
            }
            final int left = ((RecyclerView.LayoutManager)this).getChildAt(0).getLeft();
            width = n;
            if (left >= 0) {
                return width;
            }
            n2 = left;
        }
        return n + n2;
    }
    
    int getSubPositionByView(final View view, View view2) {
        if (view != null) {
            if (view2 != null) {
                final ItemAlignmentFacet itemAlignmentFacet = ((LayoutParams)view.getLayoutParams()).getItemAlignmentFacet();
                if (itemAlignmentFacet != null) {
                    final ItemAlignmentFacet.ItemAlignmentDef[] alignmentDefs = itemAlignmentFacet.getAlignmentDefs();
                    if (alignmentDefs.length > 1) {
                        while (view2 != view) {
                            final int id = view2.getId();
                            if (id != -1) {
                                for (int i = 1; i < alignmentDefs.length; ++i) {
                                    if (alignmentDefs[i].getItemAlignmentFocusViewId() == id) {
                                        return i;
                                    }
                                }
                            }
                            view2 = (View)view2.getParent();
                        }
                    }
                }
            }
        }
        return 0;
    }
    
    String getTag() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GridLayoutManager:");
        sb.append(this.mBaseGridView.getId());
        return sb.toString();
    }
    
    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }
    
    protected View getViewForPosition(final int n) {
        final View viewForPosition = this.mRecycler.getViewForPosition(n);
        ((LayoutParams)viewForPosition.getLayoutParams()).setItemAlignmentFacet((ItemAlignmentFacet)this.getFacet(this.mBaseGridView.getChildViewHolder(viewForPosition), (Class<?>)ItemAlignmentFacet.class));
        return viewForPosition;
    }
    
    int getViewMax(final View view) {
        return this.mOrientationHelper.getDecoratedEnd(view);
    }
    
    int getViewMin(final View view) {
        return this.mOrientationHelper.getDecoratedStart(view);
    }
    
    int getViewPrimarySize(final View view) {
        this.getDecoratedBoundsWithMargins(view, GridLayoutManager.sTempRect);
        int n;
        if (this.mOrientation == 0) {
            n = GridLayoutManager.sTempRect.width();
        }
        else {
            n = GridLayoutManager.sTempRect.height();
        }
        return n;
    }
    
    boolean gridOnRequestFocusInDescendants(final RecyclerView recyclerView, final int n, final Rect rect) {
        final int mFocusScrollStrategy = this.mFocusScrollStrategy;
        if (mFocusScrollStrategy != 1 && mFocusScrollStrategy != 2) {
            return this.gridOnRequestFocusInDescendantsAligned(n, rect);
        }
        return this.gridOnRequestFocusInDescendantsUnaligned(n, rect);
    }
    
    boolean hasCreatedFirstItem() {
        final int itemCount = ((RecyclerView.LayoutManager)this).getItemCount();
        boolean b = false;
        if (itemCount == 0 || this.mBaseGridView.findViewHolderForAdapterPosition(0) != null) {
            b = true;
        }
        return b;
    }
    
    boolean hasCreatedLastItem() {
        final int itemCount = ((RecyclerView.LayoutManager)this).getItemCount();
        boolean b = true;
        if (itemCount != 0) {
            b = (this.mBaseGridView.findViewHolderForAdapterPosition(itemCount - 1) != null && b);
        }
        return b;
    }
    
    protected boolean hasDoneFirstLayout() {
        return this.mGrid != null;
    }
    
    boolean hasOnChildViewHolderSelectedListener() {
        final ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = this.mChildViewHolderSelectedListeners;
        return mChildViewHolderSelectedListeners != null && mChildViewHolderSelectedListeners.size() > 0;
    }
    
    boolean isItemFullyVisible(final int n) {
        final RecyclerView.ViewHolder viewHolderForAdapterPosition = this.mBaseGridView.findViewHolderForAdapterPosition(n);
        final boolean b = false;
        if (viewHolderForAdapterPosition == null) {
            return false;
        }
        boolean b2 = b;
        if (viewHolderForAdapterPosition.itemView.getLeft() >= 0) {
            b2 = b;
            if (viewHolderForAdapterPosition.itemView.getRight() <= this.mBaseGridView.getWidth()) {
                b2 = b;
                if (viewHolderForAdapterPosition.itemView.getTop() >= 0) {
                    b2 = b;
                    if (viewHolderForAdapterPosition.itemView.getBottom() <= this.mBaseGridView.getHeight()) {
                        b2 = true;
                    }
                }
            }
        }
        return b2;
    }
    
    public boolean isScrollEnabled() {
        return (this.mFlag & 0x20000) != 0x0;
    }
    
    boolean isSlidingChildViews() {
        return (this.mFlag & 0x40) != 0x0;
    }
    
    void layoutChild(int n, final View view, int n2, final int n3, int n4) {
        int a;
        if (this.mOrientation == 0) {
            a = this.getDecoratedMeasuredHeightWithMargin(view);
        }
        else {
            a = this.getDecoratedMeasuredWidthWithMargin(view);
        }
        final int mFixedRowSizeSecondary = this.mFixedRowSizeSecondary;
        int min = a;
        if (mFixedRowSizeSecondary > 0) {
            min = Math.min(a, mFixedRowSizeSecondary);
        }
        final int mGravity = this.mGravity;
        final int n5 = mGravity & 0x70;
        int absoluteGravity;
        if ((this.mFlag & 0xC0000) != 0x0) {
            absoluteGravity = Gravity.getAbsoluteGravity(mGravity & 0x800007, 1);
        }
        else {
            absoluteGravity = (mGravity & 0x7);
        }
        int n6 = 0;
        Label_0229: {
            if (this.mOrientation == 0) {
                n6 = n4;
                if (n5 == 48) {
                    break Label_0229;
                }
            }
            if (this.mOrientation == 1 && absoluteGravity == 3) {
                n6 = n4;
            }
            else {
                if ((this.mOrientation == 0 && n5 == 80) || (this.mOrientation == 1 && absoluteGravity == 5)) {
                    n = this.getRowSizeSecondary(n) - min;
                }
                else {
                    if (this.mOrientation != 0 || n5 != 16) {
                        n6 = n4;
                        if (this.mOrientation != 1) {
                            break Label_0229;
                        }
                        n6 = n4;
                        if (absoluteGravity != 1) {
                            break Label_0229;
                        }
                    }
                    n = (this.getRowSizeSecondary(n) - min) / 2;
                }
                n6 = n4 + n;
            }
        }
        if (this.mOrientation == 0) {
            n4 = min + n6;
            n = n2;
            n2 = n3;
        }
        else {
            n = n6;
            n4 = min + n6;
            n6 = n2;
            n2 = n4;
            n4 = n3;
        }
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(view, n, n6, n2, n4);
        super.getDecoratedBoundsWithMargins(view, GridLayoutManager.sTempRect);
        final Rect sTempRect = GridLayoutManager.sTempRect;
        layoutParams.setOpticalInsets(n - sTempRect.left, n6 - sTempRect.top, sTempRect.right - n2, sTempRect.bottom - n4);
        this.updateChildAlignments(view);
    }
    
    void measureChild(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        ((RecyclerView.LayoutManager)this).calculateItemDecorationsForChild(view, GridLayoutManager.sTempRect);
        final int leftMargin = layoutParams.leftMargin;
        final int rightMargin = layoutParams.rightMargin;
        final Rect sTempRect = GridLayoutManager.sTempRect;
        final int n = leftMargin + rightMargin + sTempRect.left + sTempRect.right;
        final int n2 = layoutParams.topMargin + layoutParams.bottomMargin + sTempRect.top + sTempRect.bottom;
        int n3;
        if (this.mRowSizeSecondaryRequested == -2) {
            n3 = View$MeasureSpec.makeMeasureSpec(0, 0);
        }
        else {
            n3 = View$MeasureSpec.makeMeasureSpec(this.mFixedRowSizeSecondary, 1073741824);
        }
        int childMeasureSpec;
        int childMeasureSpec2;
        if (this.mOrientation == 0) {
            childMeasureSpec = ViewGroup.getChildMeasureSpec(View$MeasureSpec.makeMeasureSpec(0, 0), n, layoutParams.width);
            childMeasureSpec2 = ViewGroup.getChildMeasureSpec(n3, n2, layoutParams.height);
        }
        else {
            final int childMeasureSpec3 = ViewGroup.getChildMeasureSpec(View$MeasureSpec.makeMeasureSpec(0, 0), n2, layoutParams.height);
            final int childMeasureSpec4 = ViewGroup.getChildMeasureSpec(n3, n, layoutParams.width);
            childMeasureSpec2 = childMeasureSpec3;
            childMeasureSpec = childMeasureSpec4;
        }
        view.measure(childMeasureSpec, childMeasureSpec2);
    }
    
    @Override
    public void onAdapterChanged(final Adapter adapter, final Adapter adapter2) {
        if (adapter != null) {
            this.discardLayoutInfo();
            this.mFocusPosition = -1;
            this.mFocusPositionOffset = 0;
            this.mChildrenStates.clear();
        }
        if (adapter2 instanceof FacetProviderAdapter) {
            this.mFacetProviderAdapter = (FacetProviderAdapter)adapter2;
        }
        else {
            this.mFacetProviderAdapter = null;
        }
        super.onAdapterChanged(adapter, adapter2);
    }
    
    @Override
    public boolean onAddFocusables(final RecyclerView e, final ArrayList<View> list, final int n, final int n2) {
        if ((this.mFlag & 0x8000) != 0x0) {
            return true;
        }
        if (e.hasFocus()) {
            if (this.mPendingMoveSmoothScroller != null) {
                return true;
            }
            final int movement = this.getMovement(n);
            final int immediateChildIndex = this.findImmediateChildIndex(e.findFocus());
            final int adapterPositionByIndex = this.getAdapterPositionByIndex(immediateChildIndex);
            View viewByPosition;
            if (adapterPositionByIndex == -1) {
                viewByPosition = null;
            }
            else {
                viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(adapterPositionByIndex);
            }
            if (viewByPosition != null) {
                viewByPosition.addFocusables((ArrayList)list, n, n2);
            }
            if (this.mGrid == null || ((RecyclerView.LayoutManager)this).getChildCount() == 0) {
                return true;
            }
            if ((movement == 3 || movement == 2) && this.mGrid.getNumRows() <= 1) {
                return true;
            }
            final Grid mGrid = this.mGrid;
            int row;
            if (mGrid != null && viewByPosition != null) {
                row = mGrid.getLocation(adapterPositionByIndex).row;
            }
            else {
                row = -1;
            }
            final int size = list.size();
            int n3;
            if (movement != 1 && movement != 3) {
                n3 = -1;
            }
            else {
                n3 = 1;
            }
            int n4;
            if (n3 > 0) {
                n4 = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
            }
            else {
                n4 = 0;
            }
            int n5;
            if (immediateChildIndex == -1) {
                if (n3 > 0) {
                    n5 = 0;
                }
                else {
                    n5 = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
                }
            }
            else {
                n5 = immediateChildIndex + n3;
            }
            while (true) {
                if (n3 > 0) {
                    if (n5 > n4) {
                        break;
                    }
                }
                else if (n5 < n4) {
                    break;
                }
                final View child = ((RecyclerView.LayoutManager)this).getChildAt(n5);
                if (child.getVisibility() == 0) {
                    if (child.hasFocusable()) {
                        if (viewByPosition == null) {
                            child.addFocusables((ArrayList)list, n, n2);
                            if (list.size() > size) {
                                break;
                            }
                        }
                        else {
                            final int adapterPositionByIndex2 = this.getAdapterPositionByIndex(n5);
                            final Grid.Location location = this.mGrid.getLocation(adapterPositionByIndex2);
                            if (location != null) {
                                if (movement == 1) {
                                    if (location.row == row && adapterPositionByIndex2 > adapterPositionByIndex) {
                                        child.addFocusables((ArrayList)list, n, n2);
                                        if (list.size() > size) {
                                            break;
                                        }
                                    }
                                }
                                else if (movement == 0) {
                                    if (location.row == row && adapterPositionByIndex2 < adapterPositionByIndex) {
                                        child.addFocusables((ArrayList)list, n, n2);
                                        if (list.size() > size) {
                                            break;
                                        }
                                    }
                                }
                                else if (movement == 3) {
                                    final int row2 = location.row;
                                    if (row2 != row) {
                                        if (row2 < row) {
                                            break;
                                        }
                                        child.addFocusables((ArrayList)list, n, n2);
                                    }
                                }
                                else if (movement == 2) {
                                    final int row3 = location.row;
                                    if (row3 != row) {
                                        if (row3 > row) {
                                            break;
                                        }
                                        child.addFocusables((ArrayList)list, n, n2);
                                    }
                                }
                            }
                        }
                    }
                }
                n5 += n3;
            }
        }
        else {
            final int size2 = list.size();
            if (this.mFocusScrollStrategy != 0) {
                final int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
                final int clientSize = this.mWindowAlignment.mainAxis().getClientSize();
                for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i) {
                    final View child2 = ((RecyclerView.LayoutManager)this).getChildAt(i);
                    if (child2.getVisibility() == 0 && this.getViewMin(child2) >= paddingMin && this.getViewMax(child2) <= clientSize + paddingMin) {
                        child2.addFocusables((ArrayList)list, n, n2);
                    }
                }
                if (list.size() == size2) {
                    for (int childCount2 = ((RecyclerView.LayoutManager)this).getChildCount(), j = 0; j < childCount2; ++j) {
                        final View child3 = ((RecyclerView.LayoutManager)this).getChildAt(j);
                        if (child3.getVisibility() == 0) {
                            child3.addFocusables((ArrayList)list, n, n2);
                        }
                    }
                }
            }
            else {
                final View viewByPosition2 = ((RecyclerView.LayoutManager)this).findViewByPosition(this.mFocusPosition);
                if (viewByPosition2 != null) {
                    viewByPosition2.addFocusables((ArrayList)list, n, n2);
                }
            }
            if (list.size() != size2) {
                return true;
            }
            if (e.isFocusable()) {
                list.add((View)e);
            }
        }
        return true;
    }
    
    void onChildRecycled(final ViewHolder viewHolder) {
        final int absoluteAdapterPosition = viewHolder.getAbsoluteAdapterPosition();
        if (absoluteAdapterPosition != -1) {
            this.mChildrenStates.saveOffscreenView(viewHolder.itemView, absoluteAdapterPosition);
        }
    }
    
    void onFocusChanged(final boolean b, int mFocusPosition, final Rect rect) {
        if (b) {
            mFocusPosition = this.mFocusPosition;
            while (true) {
                final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(mFocusPosition);
                if (viewByPosition == null) {
                    break;
                }
                if (viewByPosition.getVisibility() == 0 && viewByPosition.hasFocusable()) {
                    viewByPosition.requestFocus();
                    break;
                }
                ++mFocusPosition;
            }
        }
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfo(final Recycler recycler, final State state, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        this.saveContext(recycler, state);
        final int itemCount = state.getItemCount();
        final boolean b = (this.mFlag & 0x40000) != 0x0;
        if (itemCount > 1 && !this.isItemFullyVisible(0)) {
            if (sdk_INT >= 23) {
                if (this.mOrientation == 0) {
                    AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat;
                    if (b) {
                        accessibilityActionCompat = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT;
                    }
                    else {
                        accessibilityActionCompat = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT;
                    }
                    accessibilityNodeInfoCompat.addAction(accessibilityActionCompat);
                }
                else {
                    accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP);
                }
            }
            else {
                accessibilityNodeInfoCompat.addAction(8192);
            }
            accessibilityNodeInfoCompat.setScrollable(true);
        }
        if (itemCount > 1 && !this.isItemFullyVisible(itemCount - 1)) {
            if (sdk_INT >= 23) {
                if (this.mOrientation == 0) {
                    AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat2;
                    if (b) {
                        accessibilityActionCompat2 = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT;
                    }
                    else {
                        accessibilityActionCompat2 = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT;
                    }
                    accessibilityNodeInfoCompat.addAction(accessibilityActionCompat2);
                }
                else {
                    accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN);
                }
            }
            else {
                accessibilityNodeInfoCompat.addAction(4096);
            }
            accessibilityNodeInfoCompat.setScrollable(true);
        }
        accessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(this.getRowCountForAccessibility(recycler, state), this.getColumnCountForAccessibility(recycler, state), ((RecyclerView.LayoutManager)this).isLayoutHierarchical(recycler, state), ((RecyclerView.LayoutManager)this).getSelectionModeForAccessibility(recycler, state)));
        this.leaveContext();
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        if (this.mGrid != null) {
            if (layoutParams instanceof LayoutParams) {
                final int viewAdapterPosition = ((RecyclerView.LayoutParams)layoutParams).getViewAdapterPosition();
                int rowIndex;
                if (viewAdapterPosition >= 0) {
                    rowIndex = this.mGrid.getRowIndex(viewAdapterPosition);
                }
                else {
                    rowIndex = -1;
                }
                if (rowIndex < 0) {
                    return;
                }
                final int n = viewAdapterPosition / this.mGrid.getNumRows();
                if (this.mOrientation == 0) {
                    accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(rowIndex, 1, n, 1, false, false));
                }
                else {
                    accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(n, 1, rowIndex, 1, false, false));
                }
            }
        }
    }
    
    @Override
    public View onInterceptFocusSearch(View mBaseGridView, final int n) {
        if ((this.mFlag & 0x8000) != 0x0) {
            return mBaseGridView;
        }
        final FocusFinder instance = FocusFinder.getInstance();
        View view = null;
        if (n != 2 && n != 1) {
            view = instance.findNextFocus((ViewGroup)this.mBaseGridView, mBaseGridView, n);
        }
        else {
            if (this.canScrollVertically()) {
                int n2;
                if (n == 2) {
                    n2 = 130;
                }
                else {
                    n2 = 33;
                }
                view = instance.findNextFocus((ViewGroup)this.mBaseGridView, mBaseGridView, n2);
            }
            if (this.canScrollHorizontally()) {
                int n3;
                if (((RecyclerView.LayoutManager)this).getLayoutDirection() == 1 ^ n == 2) {
                    n3 = 66;
                }
                else {
                    n3 = 17;
                }
                view = instance.findNextFocus((ViewGroup)this.mBaseGridView, mBaseGridView, n3);
            }
        }
        if (view != null) {
            return view;
        }
        if (this.mBaseGridView.getDescendantFocusability() == 393216) {
            return this.mBaseGridView.getParent().focusSearch(mBaseGridView, n);
        }
        final int movement = this.getMovement(n);
        final boolean b = this.mBaseGridView.getScrollState() != 0;
        View view2 = null;
        Label_0391: {
            if (movement == 1) {
                if (b || (this.mFlag & 0x1000) == 0x0) {
                    view = mBaseGridView;
                }
                view2 = view;
                if ((this.mFlag & 0x20000) == 0x0) {
                    break Label_0391;
                }
                view2 = view;
                if (this.hasCreatedLastItem()) {
                    break Label_0391;
                }
                this.processPendingMovement(true);
            }
            else if (movement == 0) {
                if (b || (this.mFlag & 0x800) == 0x0) {
                    view = mBaseGridView;
                }
                view2 = view;
                if ((this.mFlag & 0x20000) == 0x0) {
                    break Label_0391;
                }
                view2 = view;
                if (this.hasCreatedFirstItem()) {
                    break Label_0391;
                }
                this.processPendingMovement(false);
            }
            else if (movement == 3) {
                if (!b) {
                    view2 = view;
                    if ((this.mFlag & 0x4000) != 0x0) {
                        break Label_0391;
                    }
                }
            }
            else {
                view2 = view;
                if (movement != 2) {
                    break Label_0391;
                }
                if (!b) {
                    view2 = view;
                    if ((this.mFlag & 0x2000) != 0x0) {
                        break Label_0391;
                    }
                }
            }
            view2 = mBaseGridView;
        }
        if (view2 != null) {
            return view2;
        }
        final View focusSearch = this.mBaseGridView.getParent().focusSearch(mBaseGridView, n);
        if (focusSearch != null) {
            return focusSearch;
        }
        if (mBaseGridView == null) {
            mBaseGridView = (View)this.mBaseGridView;
        }
        return mBaseGridView;
    }
    
    @Override
    public void onItemsAdded(final RecyclerView recyclerView, final int n, final int n2) {
        if (this.mFocusPosition != -1) {
            final Grid mGrid = this.mGrid;
            if (mGrid != null && mGrid.getFirstVisibleIndex() >= 0) {
                final int mFocusPositionOffset = this.mFocusPositionOffset;
                if (mFocusPositionOffset != Integer.MIN_VALUE && n <= this.mFocusPosition + mFocusPositionOffset) {
                    this.mFocusPositionOffset = mFocusPositionOffset + n2;
                }
            }
        }
        this.mChildrenStates.clear();
    }
    
    @Override
    public void onItemsChanged(final RecyclerView recyclerView) {
        this.mFocusPositionOffset = 0;
        this.mChildrenStates.clear();
    }
    
    @Override
    public void onItemsMoved(final RecyclerView recyclerView, final int n, final int n2, final int n3) {
        final int mFocusPosition = this.mFocusPosition;
        if (mFocusPosition != -1) {
            final int mFocusPositionOffset = this.mFocusPositionOffset;
            if (mFocusPositionOffset != Integer.MIN_VALUE) {
                final int n4 = mFocusPosition + mFocusPositionOffset;
                if (n <= n4 && n4 < n + n3) {
                    this.mFocusPositionOffset = mFocusPositionOffset + (n2 - n);
                }
                else if (n < n4 && n2 > n4 - n3) {
                    this.mFocusPositionOffset -= n3;
                }
                else if (n > n4 && n2 < n4) {
                    this.mFocusPositionOffset += n3;
                }
            }
        }
        this.mChildrenStates.clear();
    }
    
    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, int mFocusPositionOffset, final int n) {
        if (this.mFocusPosition != -1) {
            final Grid mGrid = this.mGrid;
            if (mGrid != null && mGrid.getFirstVisibleIndex() >= 0) {
                final int mFocusPositionOffset2 = this.mFocusPositionOffset;
                if (mFocusPositionOffset2 != Integer.MIN_VALUE) {
                    final int mFocusPosition = this.mFocusPosition;
                    final int n2 = mFocusPosition + mFocusPositionOffset2;
                    if (mFocusPositionOffset <= n2) {
                        if (mFocusPositionOffset + n > n2) {
                            mFocusPositionOffset = mFocusPositionOffset2 + (mFocusPositionOffset - n2);
                            this.mFocusPositionOffset = mFocusPositionOffset;
                            this.mFocusPosition = mFocusPosition + mFocusPositionOffset;
                            this.mFocusPositionOffset = Integer.MIN_VALUE;
                        }
                        else {
                            this.mFocusPositionOffset = mFocusPositionOffset2 - n;
                        }
                    }
                }
            }
        }
        this.mChildrenStates.clear();
    }
    
    @Override
    public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2) {
        for (int i = n; i < n2 + n; ++i) {
            this.mChildrenStates.remove(i);
        }
    }
    
    @Override
    public void onLayoutChildren(final Recycler recycler, final State state) {
        if (this.mNumRows == 0) {
            return;
        }
        if (state.getItemCount() < 0) {
            return;
        }
        if ((this.mFlag & 0x40) != 0x0 && ((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            this.mFlag |= 0x80;
            return;
        }
        final int mFlag = this.mFlag;
        if ((mFlag & 0x200) == 0x0) {
            this.discardLayoutInfo();
            this.removeAndRecycleAllViews(recycler);
            return;
        }
        boolean b = true;
        this.mFlag = ((mFlag & 0xFFFFFFFC) | 0x1);
        this.saveContext(recycler, state);
        final boolean preLayout = state.isPreLayout();
        int a = Integer.MIN_VALUE;
        int n = 0;
        int i = 0;
        if (preLayout) {
            this.updatePositionDeltaInPreLayout();
            final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
            if (this.mGrid != null && childCount > 0) {
                int a2 = Integer.MAX_VALUE;
                final int oldPosition = this.mBaseGridView.getChildViewHolder(((RecyclerView.LayoutManager)this).getChildAt(0)).getOldPosition();
                final int oldPosition2 = this.mBaseGridView.getChildViewHolder(((RecyclerView.LayoutManager)this).getChildAt(childCount - 1)).getOldPosition();
                while (i < childCount) {
                    final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
                    final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                    final int childAdapterPosition = this.mBaseGridView.getChildAdapterPosition(child);
                    int max = 0;
                    int min = 0;
                    Label_0306: {
                        if (!((RecyclerView.LayoutParams)layoutParams).isItemChanged() && !((RecyclerView.LayoutParams)layoutParams).isItemRemoved() && !child.isLayoutRequested() && (child.hasFocus() || this.mFocusPosition != ((RecyclerView.LayoutParams)layoutParams).getViewAdapterPosition()) && (!child.hasFocus() || this.mFocusPosition == ((RecyclerView.LayoutParams)layoutParams).getViewAdapterPosition()) && childAdapterPosition >= oldPosition) {
                            max = a;
                            min = a2;
                            if (childAdapterPosition <= oldPosition2) {
                                break Label_0306;
                            }
                        }
                        min = Math.min(a2, this.getViewMin(child));
                        max = Math.max(a, this.getViewMax(child));
                    }
                    ++i;
                    a = max;
                    a2 = min;
                }
                if (a > a2) {
                    this.mExtraLayoutSpaceInPreLayout = a - a2;
                }
                this.appendVisibleItems();
                this.prependVisibleItems();
            }
            this.mFlag &= 0xFFFFFFFC;
            this.leaveContext();
            return;
        }
        if (state.willRunPredictiveAnimations()) {
            this.updatePositionToRowMapInPostLayout();
        }
        if (((RecyclerView.LayoutManager)this).isSmoothScrolling() || this.mFocusScrollStrategy != 0) {
            b = false;
        }
        final int mFocusPosition = this.mFocusPosition;
        if (mFocusPosition != -1) {
            final int mFocusPositionOffset = this.mFocusPositionOffset;
            if (mFocusPositionOffset != Integer.MIN_VALUE) {
                this.mFocusPosition = mFocusPosition + mFocusPositionOffset;
                this.mSubFocusPosition = 0;
            }
        }
        this.mFocusPositionOffset = 0;
        final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(this.mFocusPosition);
        final int mFocusPosition2 = this.mFocusPosition;
        final int mSubFocusPosition = this.mSubFocusPosition;
        final boolean hasFocus = this.mBaseGridView.hasFocus();
        final Grid mGrid = this.mGrid;
        int firstVisibleIndex;
        if (mGrid != null) {
            firstVisibleIndex = mGrid.getFirstVisibleIndex();
        }
        else {
            firstVisibleIndex = -1;
        }
        final Grid mGrid2 = this.mGrid;
        int lastVisibleIndex;
        if (mGrid2 != null) {
            lastVisibleIndex = mGrid2.getLastVisibleIndex();
        }
        else {
            lastVisibleIndex = -1;
        }
        int n2;
        int n3;
        if (this.mOrientation == 0) {
            n2 = state.getRemainingScrollHorizontal();
            n3 = state.getRemainingScrollVertical();
        }
        else {
            n3 = state.getRemainingScrollHorizontal();
            n2 = state.getRemainingScrollVertical();
        }
        if (this.layoutInit()) {
            this.mFlag |= 0x4;
            this.mGrid.setStart(this.mFocusPosition);
            this.fastRelayout();
        }
        else {
            final int mFlag2 = this.mFlag & 0xFFFFFFFB;
            this.mFlag = mFlag2;
            if (b) {
                n = 16;
            }
            this.mFlag = (n | (mFlag2 & 0xFFFFFFEF));
            int start = firstVisibleIndex;
            int mFocusPosition3 = lastVisibleIndex;
            Label_0666: {
                if (b) {
                    if (firstVisibleIndex >= 0) {
                        final int mFocusPosition4 = this.mFocusPosition;
                        if (mFocusPosition4 <= lastVisibleIndex) {
                            start = firstVisibleIndex;
                            mFocusPosition3 = lastVisibleIndex;
                            if (mFocusPosition4 >= firstVisibleIndex) {
                                break Label_0666;
                            }
                        }
                    }
                    start = (mFocusPosition3 = this.mFocusPosition);
                }
            }
            this.mGrid.setStart(start);
            if (mFocusPosition3 != -1) {
                while (this.appendOneColumnVisibleItems() && ((RecyclerView.LayoutManager)this).findViewByPosition(mFocusPosition3) == null) {}
            }
        }
        int firstVisibleIndex2;
        int lastVisibleIndex2;
        do {
            this.updateScrollLimits();
            firstVisibleIndex2 = this.mGrid.getFirstVisibleIndex();
            lastVisibleIndex2 = this.mGrid.getLastVisibleIndex();
            this.focusToViewInLayout(hasFocus, b, -n2, -n3);
            this.appendVisibleItems();
            this.prependVisibleItems();
        } while (this.mGrid.getFirstVisibleIndex() != firstVisibleIndex2 || this.mGrid.getLastVisibleIndex() != lastVisibleIndex2);
        this.removeInvisibleViewsAtFront();
        this.removeInvisibleViewsAtEnd();
        if (state.willRunPredictiveAnimations()) {
            this.fillScrapViewsInPostLayout();
        }
        final int mFlag3 = this.mFlag;
        if ((mFlag3 & 0x400) != 0x0) {
            this.mFlag = (mFlag3 & 0xFFFFFBFF);
        }
        else {
            this.updateRowSecondarySizeRefresh();
        }
        Label_0885: {
            if ((this.mFlag & 0x4) != 0x0) {
                final int mFocusPosition5 = this.mFocusPosition;
                if (mFocusPosition5 != mFocusPosition2 || this.mSubFocusPosition != mSubFocusPosition || ((RecyclerView.LayoutManager)this).findViewByPosition(mFocusPosition5) != viewByPosition || (this.mFlag & 0x8) != 0x0) {
                    this.dispatchChildSelected();
                    break Label_0885;
                }
            }
            if ((this.mFlag & 0x14) == 0x10) {
                this.dispatchChildSelected();
            }
        }
        this.dispatchChildSelectedAndPositioned();
        if ((this.mFlag & 0x40) != 0x0) {
            this.scrollDirectionPrimary(this.getSlideOutDistance());
        }
        this.mFlag &= 0xFFFFFFFC;
        this.leaveContext();
    }
    
    @Override
    public void onLayoutCompleted(final State state) {
        final ArrayList<BaseGridView.OnLayoutCompletedListener> mOnLayoutCompletedListeners = this.mOnLayoutCompletedListeners;
        if (mOnLayoutCompletedListeners != null) {
            for (int i = mOnLayoutCompletedListeners.size() - 1; i >= 0; --i) {
                this.mOnLayoutCompletedListeners.get(i).onLayoutCompleted(state);
            }
        }
    }
    
    @Override
    public void onMeasure(final Recycler recycler, final State state, int n, int mMaxSizeSecondary) {
        this.saveContext(recycler, state);
        int size;
        int n2;
        int n3;
        if (this.mOrientation == 0) {
            size = View$MeasureSpec.getSize(n);
            final int size2 = View$MeasureSpec.getSize(mMaxSizeSecondary);
            n2 = View$MeasureSpec.getMode(mMaxSizeSecondary);
            n3 = ((RecyclerView.LayoutManager)this).getPaddingTop();
            n = ((RecyclerView.LayoutManager)this).getPaddingBottom();
            mMaxSizeSecondary = size2;
        }
        else {
            final int size3 = View$MeasureSpec.getSize(n);
            final int size4 = View$MeasureSpec.getSize(mMaxSizeSecondary);
            n2 = View$MeasureSpec.getMode(n);
            n3 = ((RecyclerView.LayoutManager)this).getPaddingLeft();
            n = ((RecyclerView.LayoutManager)this).getPaddingRight();
            mMaxSizeSecondary = size3;
            size = size4;
        }
        final int n4 = n3 + n;
        this.mMaxSizeSecondary = mMaxSizeSecondary;
        final int mRowSizeSecondaryRequested = this.mRowSizeSecondaryRequested;
        Label_0531: {
            if (mRowSizeSecondaryRequested == -2) {
                mMaxSizeSecondary = this.mNumRowsRequested;
                if ((n = mMaxSizeSecondary) == 0) {
                    n = 1;
                }
                this.mNumRows = n;
                this.mFixedRowSizeSecondary = 0;
                final int[] mRowSizeSecondary = this.mRowSizeSecondary;
                if (mRowSizeSecondary == null || mRowSizeSecondary.length != n) {
                    this.mRowSizeSecondary = new int[this.mNumRows];
                }
                if (this.mState.isPreLayout()) {
                    this.updatePositionDeltaInPreLayout();
                }
                this.processRowSizeSecondary(true);
                if (n2 != Integer.MIN_VALUE) {
                    if (n2 != 0) {
                        if (n2 != 1073741824) {
                            throw new IllegalStateException("wrong spec");
                        }
                        n = this.mMaxSizeSecondary;
                    }
                    else {
                        n = this.getSizeSecondary() + n4;
                    }
                }
                else {
                    n = Math.min(this.getSizeSecondary() + n4, this.mMaxSizeSecondary);
                }
            }
            else {
                if (n2 != Integer.MIN_VALUE) {
                    if (n2 == 0) {
                        if ((n = mRowSizeSecondaryRequested) == 0) {
                            n = mMaxSizeSecondary - n4;
                        }
                        this.mFixedRowSizeSecondary = n;
                        mMaxSizeSecondary = this.mNumRowsRequested;
                        if ((n = mMaxSizeSecondary) == 0) {
                            n = 1;
                        }
                        this.mNumRows = n;
                        n = this.mFixedRowSizeSecondary * n + this.mSpacingSecondary * (n - 1) + n4;
                        break Label_0531;
                    }
                    if (n2 != 1073741824) {
                        throw new IllegalStateException("wrong spec");
                    }
                }
                if (this.mNumRowsRequested == 0 && this.mRowSizeSecondaryRequested == 0) {
                    this.mNumRows = 1;
                    this.mFixedRowSizeSecondary = mMaxSizeSecondary - n4;
                }
                else {
                    final int mNumRowsRequested = this.mNumRowsRequested;
                    if (mNumRowsRequested == 0) {
                        final int mRowSizeSecondaryRequested2 = this.mRowSizeSecondaryRequested;
                        this.mFixedRowSizeSecondary = mRowSizeSecondaryRequested2;
                        n = this.mSpacingSecondary;
                        this.mNumRows = (mMaxSizeSecondary + n) / (mRowSizeSecondaryRequested2 + n);
                    }
                    else {
                        n = this.mRowSizeSecondaryRequested;
                        if (n == 0) {
                            this.mNumRows = mNumRowsRequested;
                            this.mFixedRowSizeSecondary = (mMaxSizeSecondary - n4 - this.mSpacingSecondary * (mNumRowsRequested - 1)) / mNumRowsRequested;
                        }
                        else {
                            this.mNumRows = mNumRowsRequested;
                            this.mFixedRowSizeSecondary = n;
                        }
                    }
                }
                n = mMaxSizeSecondary;
                if (n2 == Integer.MIN_VALUE) {
                    n = this.mFixedRowSizeSecondary;
                    final int mNumRows = this.mNumRows;
                    final int n5 = n * mNumRows + this.mSpacingSecondary * (mNumRows - 1) + n4;
                    if (n5 < (n = mMaxSizeSecondary)) {
                        n = n5;
                    }
                }
            }
        }
        if (this.mOrientation == 0) {
            ((RecyclerView.LayoutManager)this).setMeasuredDimension(size, n);
        }
        else {
            ((RecyclerView.LayoutManager)this).setMeasuredDimension(n, size);
        }
        this.leaveContext();
    }
    
    @Override
    public boolean onRequestChildFocus(final RecyclerView recyclerView, final View view, final View view2) {
        if ((this.mFlag & 0x8000) != 0x0) {
            return true;
        }
        if (this.getAdapterPositionByView(view) == -1) {
            return true;
        }
        if ((this.mFlag & 0x23) == 0x0) {
            this.scrollToView(view, view2, true);
        }
        return true;
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        this.mFocusPosition = savedState.index;
        this.mFocusPositionOffset = 0;
        this.mChildrenStates.loadFromBundle(savedState.childStates);
        this.mFlag |= 0x100;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void onRtlPropertiesChanged(final int n) {
        final int mOrientation = this.mOrientation;
        boolean reversedFlow = false;
        int n2;
        if ((mOrientation != 0) ? (n == 1) : (n == 1)) {
            n2 = 262144;
        }
        else {
            n2 = 0;
        }
        final int mFlag = this.mFlag;
        if ((0xC0000 & mFlag) == n2) {
            return;
        }
        final int mFlag2 = n2 | (mFlag & 0xFFF3FFFF);
        this.mFlag = mFlag2;
        this.mFlag = (mFlag2 | 0x100);
        final WindowAlignment.Axis horizontal = this.mWindowAlignment.horizontal;
        if (n == 1) {
            reversedFlow = true;
        }
        horizontal.setReversedFlow(reversedFlow);
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState();
        savedState.index = this.getSelection();
        Bundle saveAsBundle = this.mChildrenStates.saveAsBundle();
        Bundle saveOnScreenView;
        for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i, saveAsBundle = saveOnScreenView) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final int adapterPositionByView = this.getAdapterPositionByView(child);
            saveOnScreenView = saveAsBundle;
            if (adapterPositionByView != -1) {
                saveOnScreenView = this.mChildrenStates.saveOnScreenView(saveAsBundle, child, adapterPositionByView);
            }
        }
        savedState.childStates = saveAsBundle;
        return (Parcelable)savedState;
    }
    
    @Override
    public boolean performAccessibilityAction(final Recycler recycler, final State state, final int n, final Bundle bundle) {
        if (!this.isScrollEnabled()) {
            return true;
        }
        this.saveContext(recycler, state);
        final boolean b = (this.mFlag & 0x40000) != 0x0;
        int n2 = n;
        Label_0133: {
            if (Build$VERSION.SDK_INT >= 23) {
                Label_0075: {
                    if (this.mOrientation == 0) {
                        if (n == AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT.getId()) {
                            if (!b) {
                                break Label_0075;
                            }
                        }
                        else {
                            if ((n2 = n) != AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT.getId()) {
                                break Label_0133;
                            }
                            if (b) {
                                break Label_0075;
                            }
                        }
                    }
                    else {
                        if (n == AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP.getId()) {
                            break Label_0075;
                        }
                        if ((n2 = n) != AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN.getId()) {
                            break Label_0133;
                        }
                    }
                    n2 = 4096;
                    break Label_0133;
                }
                n2 = 8192;
            }
        }
        if (n2 != 4096) {
            if (n2 == 8192) {
                this.processPendingMovement(false);
                this.processSelectionMoves(false, -1);
            }
        }
        else {
            this.processPendingMovement(true);
            this.processSelectionMoves(false, 1);
        }
        this.leaveContext();
        return true;
    }
    
    void processPendingMovement(final boolean b) {
        Label_0022: {
            if (b) {
                if (!this.hasCreatedLastItem()) {
                    break Label_0022;
                }
            }
            else if (!this.hasCreatedFirstItem()) {
                break Label_0022;
            }
            return;
        }
        final PendingMoveSmoothScroller mPendingMoveSmoothScroller = this.mPendingMoveSmoothScroller;
        if (mPendingMoveSmoothScroller == null) {
            final boolean b2 = true;
            int n;
            if (b) {
                n = 1;
            }
            else {
                n = -1;
            }
            final PendingMoveSmoothScroller pendingMoveSmoothScroller = new PendingMoveSmoothScroller(n, this.mNumRows > 1 && b2);
            this.mFocusPositionOffset = 0;
            this.startSmoothScroll(pendingMoveSmoothScroller);
        }
        else if (b) {
            mPendingMoveSmoothScroller.increasePendingMoves();
        }
        else {
            mPendingMoveSmoothScroller.decreasePendingMoves();
        }
    }
    
    int processSelectionMoves(final boolean b, int n) {
        final Grid mGrid = this.mGrid;
        if (mGrid == null) {
            return n;
        }
        int mFocusPosition = this.mFocusPosition;
        int rowIndex;
        if (mFocusPosition != -1) {
            rowIndex = mGrid.getRowIndex(mFocusPosition);
        }
        else {
            rowIndex = -1;
        }
        View view = null;
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        final int n2 = 0;
        int n3 = n;
        n = n2;
        int n4 = rowIndex;
        while (n < childCount && n3 != 0) {
            int n5;
            if (n3 > 0) {
                n5 = n;
            }
            else {
                n5 = childCount - 1 - n;
            }
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(n5);
            int n6 = 0;
            int n7 = 0;
            View view2 = null;
            int n8 = 0;
            Label_0278: {
                if (!this.canScrollTo(child)) {
                    n6 = n4;
                    n7 = mFocusPosition;
                    view2 = view;
                    n8 = n3;
                }
                else {
                    final int adapterPositionByIndex = this.getAdapterPositionByIndex(n5);
                    final int rowIndex2 = this.mGrid.getRowIndex(adapterPositionByIndex);
                    if (n4 == -1) {
                        n7 = adapterPositionByIndex;
                        view2 = child;
                        n6 = rowIndex2;
                        n8 = n3;
                    }
                    else {
                        n6 = n4;
                        n7 = mFocusPosition;
                        view2 = view;
                        n8 = n3;
                        if (rowIndex2 == n4) {
                            if (n3 <= 0 || adapterPositionByIndex <= mFocusPosition) {
                                n6 = n4;
                                n7 = mFocusPosition;
                                view2 = view;
                                if ((n8 = n3) >= 0) {
                                    break Label_0278;
                                }
                                n6 = n4;
                                n7 = mFocusPosition;
                                view2 = view;
                                n8 = n3;
                                if (adapterPositionByIndex >= mFocusPosition) {
                                    break Label_0278;
                                }
                            }
                            int n9;
                            if (n3 > 0) {
                                n9 = n3 - 1;
                            }
                            else {
                                n9 = n3 + 1;
                            }
                            view2 = child;
                            n8 = n9;
                            n7 = adapterPositionByIndex;
                            n6 = n4;
                        }
                    }
                }
            }
            ++n;
            n4 = n6;
            mFocusPosition = n7;
            view = view2;
            n3 = n8;
        }
        if (view != null) {
            if (b) {
                if (((RecyclerView.LayoutManager)this).hasFocus()) {
                    this.mFlag |= 0x20;
                    view.requestFocus();
                    this.mFlag &= 0xFFFFFFDF;
                }
                this.mFocusPosition = mFocusPosition;
                this.mSubFocusPosition = 0;
            }
            else {
                this.scrollToView(view, true);
            }
        }
        return n3;
    }
    
    @Override
    public void removeAndRecycleAllViews(final Recycler recycler) {
        for (int i = ((RecyclerView.LayoutManager)this).getChildCount() - 1; i >= 0; --i) {
            ((RecyclerView.LayoutManager)this).removeAndRecycleViewAt(i, recycler);
        }
    }
    
    @Override
    public boolean requestChildRectangleOnScreen(final RecyclerView recyclerView, final View view, final Rect rect, final boolean b) {
        return false;
    }
    
    @Override
    public int scrollHorizontallyBy(int n, final Recycler recycler, final State state) {
        if ((this.mFlag & 0x200) != 0x0 && this.hasDoneFirstLayout()) {
            this.saveContext(recycler, state);
            this.mFlag = ((this.mFlag & 0xFFFFFFFC) | 0x2);
            if (this.mOrientation == 0) {
                n = this.scrollDirectionPrimary(n);
            }
            else {
                n = this.scrollDirectionSecondary(n);
            }
            this.leaveContext();
            this.mFlag &= 0xFFFFFFFC;
            return n;
        }
        return 0;
    }
    
    @Override
    public void scrollToPosition(final int n) {
        this.setSelection(n, 0, false, 0);
    }
    
    void scrollToSelection(int startPositionSmoothScroller, final int mSubFocusPosition, final boolean b, int mPrimaryScrollExtra) {
        this.mPrimaryScrollExtra = mPrimaryScrollExtra;
        final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(startPositionSmoothScroller);
        mPrimaryScrollExtra = ((((RecyclerView.LayoutManager)this).isSmoothScrolling() ^ true) ? 1 : 0);
        if (mPrimaryScrollExtra != 0 && !this.mBaseGridView.isLayoutRequested() && viewByPosition != null && this.getAdapterPositionByView(viewByPosition) == startPositionSmoothScroller) {
            this.mFlag |= 0x20;
            this.scrollToView(viewByPosition, b);
            this.mFlag &= 0xFFFFFFDF;
        }
        else {
            final int mFlag = this.mFlag;
            if ((mFlag & 0x200) == 0x0 || (mFlag & 0x40) != 0x0) {
                this.mFocusPosition = startPositionSmoothScroller;
                this.mSubFocusPosition = mSubFocusPosition;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
                return;
            }
            if (b && !this.mBaseGridView.isLayoutRequested()) {
                this.mFocusPosition = startPositionSmoothScroller;
                this.mSubFocusPosition = mSubFocusPosition;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
                if (!this.hasDoneFirstLayout()) {
                    Log.w(this.getTag(), "setSelectionSmooth should not be called before first layout pass");
                    return;
                }
                startPositionSmoothScroller = this.startPositionSmoothScroller(startPositionSmoothScroller);
                if (startPositionSmoothScroller != this.mFocusPosition) {
                    this.mFocusPosition = startPositionSmoothScroller;
                    this.mSubFocusPosition = 0;
                }
            }
            else {
                if (mPrimaryScrollExtra == 0) {
                    this.skipSmoothScrollerOnStopInternal();
                    this.mBaseGridView.stopScroll();
                }
                if (!this.mBaseGridView.isLayoutRequested() && viewByPosition != null && this.getAdapterPositionByView(viewByPosition) == startPositionSmoothScroller) {
                    this.mFlag |= 0x20;
                    this.scrollToView(viewByPosition, b);
                    this.mFlag &= 0xFFFFFFDF;
                }
                else {
                    this.mFocusPosition = startPositionSmoothScroller;
                    this.mSubFocusPosition = mSubFocusPosition;
                    this.mFocusPositionOffset = Integer.MIN_VALUE;
                    this.mFlag |= 0x100;
                    ((RecyclerView.LayoutManager)this).requestLayout();
                }
            }
        }
    }
    
    void scrollToView(final View view, final boolean b) {
        View focus;
        if (view == null) {
            focus = null;
        }
        else {
            focus = view.findFocus();
        }
        this.scrollToView(view, focus, b);
    }
    
    void scrollToView(final View view, final boolean b, final int n, final int n2) {
        View focus;
        if (view == null) {
            focus = null;
        }
        else {
            focus = view.findFocus();
        }
        this.scrollToView(view, focus, b, n, n2);
    }
    
    @Override
    public int scrollVerticallyBy(int n, final Recycler recycler, final State state) {
        if ((this.mFlag & 0x200) != 0x0 && this.hasDoneFirstLayout()) {
            this.mFlag = ((this.mFlag & 0xFFFFFFFC) | 0x2);
            this.saveContext(recycler, state);
            if (this.mOrientation == 1) {
                n = this.scrollDirectionPrimary(n);
            }
            else {
                n = this.scrollDirectionSecondary(n);
            }
            this.leaveContext();
            this.mFlag &= 0xFFFFFFFC;
            return n;
        }
        return 0;
    }
    
    public void setFocusOutAllowed(final boolean b, final boolean b2) {
        final int mFlag = this.mFlag;
        int n = 0;
        int n2;
        if (b) {
            n2 = 2048;
        }
        else {
            n2 = 0;
        }
        if (b2) {
            n = 4096;
        }
        this.mFlag = (n2 | (mFlag & 0xFFFFE7FF) | n);
    }
    
    public void setFocusOutSideAllowed(final boolean b, final boolean b2) {
        final int mFlag = this.mFlag;
        int n = 0;
        int n2;
        if (b) {
            n2 = 8192;
        }
        else {
            n2 = 0;
        }
        if (b2) {
            n = 16384;
        }
        this.mFlag = (n2 | (mFlag & 0xFFFF9FFF) | n);
    }
    
    public void setGravity(final int mGravity) {
        this.mGravity = mGravity;
    }
    
    public void setHorizontalSpacing(final int n) {
        if (this.mOrientation == 0) {
            this.mSpacingPrimary = n;
        }
        else {
            this.mSpacingSecondary = n;
        }
    }
    
    public void setNumRows(final int mNumRowsRequested) {
        if (mNumRowsRequested >= 0) {
            this.mNumRowsRequested = mNumRowsRequested;
            return;
        }
        throw new IllegalArgumentException();
    }
    
    public void setOnChildViewHolderSelectedListener(final OnChildViewHolderSelectedListener e) {
        if (e == null) {
            this.mChildViewHolderSelectedListeners = null;
            return;
        }
        final ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = this.mChildViewHolderSelectedListeners;
        if (mChildViewHolderSelectedListeners == null) {
            this.mChildViewHolderSelectedListeners = new ArrayList<OnChildViewHolderSelectedListener>();
        }
        else {
            mChildViewHolderSelectedListeners.clear();
        }
        this.mChildViewHolderSelectedListeners.add(e);
    }
    
    public void setOrientation(final int orientation) {
        if (orientation != 0 && orientation != 1) {
            return;
        }
        this.mOrientation = orientation;
        this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, orientation);
        this.mWindowAlignment.setOrientation(orientation);
        this.mItemAlignment.setOrientation(orientation);
        this.mFlag |= 0x100;
    }
    
    public void setRowHeight(final int n) {
        if (n < 0 && n != -2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid row height: ");
            sb.append(n);
            throw new IllegalArgumentException(sb.toString());
        }
        this.mRowSizeSecondaryRequested = n;
    }
    
    public void setSelection(final int n, final int n2) {
        this.setSelection(n, 0, false, n2);
    }
    
    public void setSelection(final int n, final int n2, final boolean b, final int n3) {
        if ((this.mFocusPosition != n && n != -1) || n2 != this.mSubFocusPosition || n3 != this.mPrimaryScrollExtra) {
            this.scrollToSelection(n, n2, b, n3);
        }
    }
    
    public void setSelectionSmooth(final int n) {
        this.setSelection(n, 0, true, 0);
    }
    
    public void setSelectionWithSub(final int n, final int n2, final int n3) {
        this.setSelection(n, n2, false, n3);
    }
    
    public void setVerticalSpacing(final int n) {
        if (this.mOrientation == 1) {
            this.mVerticalSpacing = n;
            this.mSpacingPrimary = n;
        }
        else {
            this.mVerticalSpacing = n;
            this.mSpacingSecondary = n;
        }
    }
    
    public void setWindowAlignment(final int windowAlignment) {
        this.mWindowAlignment.mainAxis().setWindowAlignment(windowAlignment);
    }
    
    void skipSmoothScrollerOnStopInternal() {
        final GridLinearSmoothScroller mCurrentSmoothScroller = this.mCurrentSmoothScroller;
        if (mCurrentSmoothScroller != null) {
            mCurrentSmoothScroller.mSkipOnStopInternal = true;
        }
    }
    
    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int n) {
        this.setSelection(n, 0, true, 0);
    }
    
    int startPositionSmoothScroller(final int targetPosition) {
        final GridLinearSmoothScroller gridLinearSmoothScroller = new GridLinearSmoothScroller() {
            @Override
            public PointF computeScrollVectorForPosition(int n) {
                if (((RecyclerView.SmoothScroller)this).getChildCount() == 0) {
                    return null;
                }
                final GridLayoutManager this$0 = GridLayoutManager.this;
                boolean b = false;
                final int position = ((RecyclerView.LayoutManager)this$0).getPosition(((RecyclerView.LayoutManager)this$0).getChildAt(0));
                final int mFlag = GridLayoutManager.this.mFlag;
                final int n2 = 1;
                Label_0064: {
                    if ((mFlag & 0x40000) != 0x0) {
                        if (n <= position) {
                            break Label_0064;
                        }
                    }
                    else if (n >= position) {
                        break Label_0064;
                    }
                    b = true;
                }
                n = n2;
                if (b) {
                    n = -1;
                }
                if (GridLayoutManager.this.mOrientation == 0) {
                    return new PointF((float)n, 0.0f);
                }
                return new PointF(0.0f, (float)n);
            }
        };
        ((RecyclerView.SmoothScroller)gridLinearSmoothScroller).setTargetPosition(targetPosition);
        this.startSmoothScroll(gridLinearSmoothScroller);
        return ((RecyclerView.SmoothScroller)gridLinearSmoothScroller).getTargetPosition();
    }
    
    @Override
    public void startSmoothScroll(final SmoothScroller smoothScroller) {
        this.skipSmoothScrollerOnStopInternal();
        super.startSmoothScroll(smoothScroller);
        if (smoothScroller.isRunning() && smoothScroller instanceof GridLinearSmoothScroller) {
            final GridLinearSmoothScroller mCurrentSmoothScroller = (GridLinearSmoothScroller)smoothScroller;
            this.mCurrentSmoothScroller = mCurrentSmoothScroller;
            if (mCurrentSmoothScroller instanceof PendingMoveSmoothScroller) {
                this.mPendingMoveSmoothScroller = (PendingMoveSmoothScroller)mCurrentSmoothScroller;
            }
            else {
                this.mPendingMoveSmoothScroller = null;
            }
        }
        else {
            this.mCurrentSmoothScroller = null;
            this.mPendingMoveSmoothScroller = null;
        }
    }
    
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
    
    void updatePositionDeltaInPreLayout() {
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            this.mPositionDeltaInPreLayout = this.mGrid.getFirstVisibleIndex() - ((RecyclerView.LayoutParams)((RecyclerView.LayoutManager)this).getChildAt(0).getLayoutParams()).getViewLayoutPosition();
        }
        else {
            this.mPositionDeltaInPreLayout = 0;
        }
    }
    
    void updatePositionToRowMapInPostLayout() {
        this.mPositionToRowInPostLayout.clear();
        for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i) {
            final int oldPosition = this.mBaseGridView.getChildViewHolder(((RecyclerView.LayoutManager)this).getChildAt(i)).getOldPosition();
            if (oldPosition >= 0) {
                final Grid.Location location = this.mGrid.getLocation(oldPosition);
                if (location != null) {
                    this.mPositionToRowInPostLayout.put(oldPosition, location.row);
                }
            }
        }
    }
    
    void updateScrollLimits() {
        if (this.mState.getItemCount() == 0) {
            return;
        }
        int n;
        int n2;
        int n3;
        int itemCount;
        if ((this.mFlag & 0x40000) == 0x0) {
            n = this.mGrid.getLastVisibleIndex();
            n2 = this.mState.getItemCount() - 1;
            n3 = this.mGrid.getFirstVisibleIndex();
            itemCount = 0;
        }
        else {
            n = this.mGrid.getFirstVisibleIndex();
            n3 = this.mGrid.getLastVisibleIndex();
            itemCount = this.mState.getItemCount();
            --itemCount;
            n2 = 0;
        }
        if (n >= 0) {
            if (n3 >= 0) {
                final boolean b = n == n2;
                final boolean b2 = n3 == itemCount;
                if (!b && this.mWindowAlignment.mainAxis().isMaxUnknown() && !b2 && this.mWindowAlignment.mainAxis().isMinUnknown()) {
                    return;
                }
                int n4 = Integer.MAX_VALUE;
                int n5;
                if (b) {
                    final int rowMax = this.mGrid.findRowMax(true, GridLayoutManager.sTwoInts);
                    final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(GridLayoutManager.sTwoInts[1]);
                    final int viewCenter = this.getViewCenter(viewByPosition);
                    final int[] alignMultiple = ((LayoutParams)viewByPosition.getLayoutParams()).getAlignMultiple();
                    n4 = rowMax;
                    n5 = viewCenter;
                    if (alignMultiple != null) {
                        n4 = rowMax;
                        n5 = viewCenter;
                        if (alignMultiple.length > 0) {
                            n5 = viewCenter + (alignMultiple[alignMultiple.length - 1] - alignMultiple[0]);
                            n4 = rowMax;
                        }
                    }
                }
                else {
                    n5 = Integer.MAX_VALUE;
                }
                int rowMin = Integer.MIN_VALUE;
                int viewCenter2;
                if (b2) {
                    rowMin = this.mGrid.findRowMin(false, GridLayoutManager.sTwoInts);
                    viewCenter2 = this.getViewCenter(((RecyclerView.LayoutManager)this).findViewByPosition(GridLayoutManager.sTwoInts[1]));
                }
                else {
                    viewCenter2 = Integer.MIN_VALUE;
                }
                this.mWindowAlignment.mainAxis().updateMinMax(rowMin, n4, viewCenter2, n5);
            }
        }
    }
    
    abstract class GridLinearSmoothScroller extends LinearSmoothScroller
    {
        boolean mSkipOnStopInternal;
        
        GridLinearSmoothScroller() {
            super(GridLayoutManager.this.mBaseGridView.getContext());
        }
        
        @Override
        protected float calculateSpeedPerPixel(final DisplayMetrics displayMetrics) {
            return super.calculateSpeedPerPixel(displayMetrics) * GridLayoutManager.this.mSmoothScrollSpeedFactor;
        }
        
        @Override
        protected int calculateTimeForScrolling(final int n) {
            int calculateTimeForScrolling;
            final int n2 = calculateTimeForScrolling = super.calculateTimeForScrolling(n);
            if (GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() > 0) {
                final float n3 = 30.0f / GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() * n;
                calculateTimeForScrolling = n2;
                if (n2 < n3) {
                    calculateTimeForScrolling = (int)n3;
                }
            }
            return calculateTimeForScrolling;
        }
        
        @Override
        protected void onStop() {
            super.onStop();
            if (!this.mSkipOnStopInternal) {
                this.onStopInternal();
            }
            final GridLayoutManager this$0 = GridLayoutManager.this;
            if (this$0.mCurrentSmoothScroller == this) {
                this$0.mCurrentSmoothScroller = null;
            }
            final GridLayoutManager this$2 = GridLayoutManager.this;
            if (this$2.mPendingMoveSmoothScroller == this) {
                this$2.mPendingMoveSmoothScroller = null;
            }
        }
        
        protected void onStopInternal() {
            final View viewByPosition = ((RecyclerView.SmoothScroller)this).findViewByPosition(((RecyclerView.SmoothScroller)this).getTargetPosition());
            if (viewByPosition == null) {
                if (((RecyclerView.SmoothScroller)this).getTargetPosition() >= 0) {
                    GridLayoutManager.this.scrollToSelection(((RecyclerView.SmoothScroller)this).getTargetPosition(), 0, false, 0);
                }
                return;
            }
            if (GridLayoutManager.this.mFocusPosition != ((RecyclerView.SmoothScroller)this).getTargetPosition()) {
                GridLayoutManager.this.mFocusPosition = ((RecyclerView.SmoothScroller)this).getTargetPosition();
            }
            if (((RecyclerView.LayoutManager)GridLayoutManager.this).hasFocus()) {
                final GridLayoutManager this$0 = GridLayoutManager.this;
                this$0.mFlag |= 0x20;
                viewByPosition.requestFocus();
                final GridLayoutManager this$2 = GridLayoutManager.this;
                this$2.mFlag &= 0xFFFFFFDF;
            }
            GridLayoutManager.this.dispatchChildSelected();
            GridLayoutManager.this.dispatchChildSelectedAndPositioned();
        }
        
        @Override
        protected void onTargetFound(final View view, final State state, final Action action) {
            if (GridLayoutManager.this.getScrollPosition(view, null, GridLayoutManager.sTwoInts)) {
                int n;
                int n2;
                if (GridLayoutManager.this.mOrientation == 0) {
                    final int[] sTwoInts = GridLayoutManager.sTwoInts;
                    n = sTwoInts[0];
                    n2 = sTwoInts[1];
                }
                else {
                    final int[] sTwoInts2 = GridLayoutManager.sTwoInts;
                    n = sTwoInts2[1];
                    n2 = sTwoInts2[0];
                }
                action.update(n, n2, this.calculateTimeForDeceleration((int)Math.sqrt(n * n + n2 * n2)), (Interpolator)super.mDecelerateInterpolator);
            }
        }
    }
    
    static final class LayoutParams extends RecyclerView.LayoutParams
    {
        private int[] mAlignMultiple;
        private int mAlignX;
        private int mAlignY;
        private ItemAlignmentFacet mAlignmentFacet;
        int mBottomInset;
        int mLeftInset;
        int mRightInset;
        int mTopInset;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((RecyclerView.LayoutParams)layoutParams);
        }
        
        public LayoutParams(final RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }
        
        void calculateItemAlignments(final int n, final View view) {
            final ItemAlignmentFacet.ItemAlignmentDef[] alignmentDefs = this.mAlignmentFacet.getAlignmentDefs();
            final int[] mAlignMultiple = this.mAlignMultiple;
            if (mAlignMultiple == null || mAlignMultiple.length != alignmentDefs.length) {
                this.mAlignMultiple = new int[alignmentDefs.length];
            }
            for (int i = 0; i < alignmentDefs.length; ++i) {
                this.mAlignMultiple[i] = ItemAlignmentFacetHelper.getAlignmentPosition(view, alignmentDefs[i], n);
            }
            if (n == 0) {
                this.mAlignX = this.mAlignMultiple[0];
            }
            else {
                this.mAlignY = this.mAlignMultiple[0];
            }
        }
        
        int[] getAlignMultiple() {
            return this.mAlignMultiple;
        }
        
        int getAlignX() {
            return this.mAlignX;
        }
        
        int getAlignY() {
            return this.mAlignY;
        }
        
        ItemAlignmentFacet getItemAlignmentFacet() {
            return this.mAlignmentFacet;
        }
        
        int getOpticalHeight(final View view) {
            return view.getHeight() - this.mTopInset - this.mBottomInset;
        }
        
        int getOpticalLeft(final View view) {
            return view.getLeft() + this.mLeftInset;
        }
        
        int getOpticalLeftInset() {
            return this.mLeftInset;
        }
        
        int getOpticalRight(final View view) {
            return view.getRight() - this.mRightInset;
        }
        
        int getOpticalRightInset() {
            return this.mRightInset;
        }
        
        int getOpticalTop(final View view) {
            return view.getTop() + this.mTopInset;
        }
        
        int getOpticalTopInset() {
            return this.mTopInset;
        }
        
        int getOpticalWidth(final View view) {
            return view.getWidth() - this.mLeftInset - this.mRightInset;
        }
        
        void setAlignX(final int mAlignX) {
            this.mAlignX = mAlignX;
        }
        
        void setAlignY(final int mAlignY) {
            this.mAlignY = mAlignY;
        }
        
        void setItemAlignmentFacet(final ItemAlignmentFacet mAlignmentFacet) {
            this.mAlignmentFacet = mAlignmentFacet;
        }
        
        void setOpticalInsets(final int mLeftInset, final int mTopInset, final int mRightInset, final int mBottomInset) {
            this.mLeftInset = mLeftInset;
            this.mTopInset = mTopInset;
            this.mRightInset = mRightInset;
            this.mBottomInset = mBottomInset;
        }
    }
    
    final class PendingMoveSmoothScroller extends GridLinearSmoothScroller
    {
        private int mPendingMoves;
        private final boolean mStaggeredGrid;
        
        PendingMoveSmoothScroller(final int mPendingMoves, final boolean mStaggeredGrid) {
            this.mPendingMoves = mPendingMoves;
            this.mStaggeredGrid = mStaggeredGrid;
            ((RecyclerView.SmoothScroller)this).setTargetPosition(-2);
        }
        
        @Override
        public PointF computeScrollVectorForPosition(int mPendingMoves) {
            mPendingMoves = this.mPendingMoves;
            if (mPendingMoves == 0) {
                return null;
            }
            mPendingMoves = ((((GridLayoutManager.this.mFlag & 0x40000) != 0x0) ? (mPendingMoves > 0) : (mPendingMoves < 0)) ? -1 : 1);
            if (GridLayoutManager.this.mOrientation == 0) {
                return new PointF((float)mPendingMoves, 0.0f);
            }
            return new PointF(0.0f, (float)mPendingMoves);
        }
        
        void consumePendingMovesAfterLayout() {
            if (this.mStaggeredGrid) {
                final int mPendingMoves = this.mPendingMoves;
                if (mPendingMoves != 0) {
                    this.mPendingMoves = GridLayoutManager.this.processSelectionMoves(true, mPendingMoves);
                }
            }
            final int mPendingMoves2 = this.mPendingMoves;
            if (mPendingMoves2 == 0 || (mPendingMoves2 > 0 && GridLayoutManager.this.hasCreatedLastItem()) || (this.mPendingMoves < 0 && GridLayoutManager.this.hasCreatedFirstItem())) {
                ((RecyclerView.SmoothScroller)this).setTargetPosition(GridLayoutManager.this.mFocusPosition);
                ((RecyclerView.SmoothScroller)this).stop();
            }
        }
        
        void consumePendingMovesBeforeLayout() {
            if (!this.mStaggeredGrid) {
                final int mPendingMoves = this.mPendingMoves;
                if (mPendingMoves != 0) {
                    final View view = null;
                    View view2 = null;
                Label_0043_Outer:
                    while (true) {
                        if (mPendingMoves <= 0) {
                            final GridLayoutManager this$0 = GridLayoutManager.this;
                            final int mFocusPosition = this$0.mFocusPosition;
                            final int n = this$0.mNumRows;
                            view2 = view;
                            break Label_0069;
                        }
                        final GridLayoutManager this$2 = GridLayoutManager.this;
                        int mFocusPosition = this$2.mFocusPosition;
                        int n2 = this$2.mNumRows;
                        while (true) {
                            mFocusPosition += n2;
                            Label_0074: {
                                break Label_0074;
                                final int n;
                                mFocusPosition -= n;
                            }
                            if (this.mPendingMoves != 0) {
                                final View viewByPosition = ((RecyclerView.SmoothScroller)this).findViewByPosition(mFocusPosition);
                                if (viewByPosition != null) {
                                    if (GridLayoutManager.this.canScrollTo(viewByPosition)) {
                                        final GridLayoutManager this$3 = GridLayoutManager.this;
                                        this$3.mFocusPosition = mFocusPosition;
                                        this$3.mSubFocusPosition = 0;
                                        final int mPendingMoves2 = this.mPendingMoves;
                                        if (mPendingMoves2 > 0) {
                                            this.mPendingMoves = mPendingMoves2 - 1;
                                        }
                                        else {
                                            this.mPendingMoves = mPendingMoves2 + 1;
                                        }
                                        view2 = viewByPosition;
                                    }
                                    if (this.mPendingMoves > 0) {
                                        n2 = GridLayoutManager.this.mNumRows;
                                        continue;
                                    }
                                    final int n = GridLayoutManager.this.mNumRows;
                                    continue Label_0043_Outer;
                                }
                            }
                            break;
                        }
                        break;
                    }
                    if (view2 != null && ((RecyclerView.LayoutManager)GridLayoutManager.this).hasFocus()) {
                        final GridLayoutManager this$4 = GridLayoutManager.this;
                        this$4.mFlag |= 0x20;
                        view2.requestFocus();
                        final GridLayoutManager this$5 = GridLayoutManager.this;
                        this$5.mFlag &= 0xFFFFFFDF;
                    }
                }
            }
        }
        
        void decreasePendingMoves() {
            final int mPendingMoves = this.mPendingMoves;
            if (mPendingMoves > -GridLayoutManager.this.mMaxPendingMoves) {
                this.mPendingMoves = mPendingMoves - 1;
            }
        }
        
        void increasePendingMoves() {
            final int mPendingMoves = this.mPendingMoves;
            if (mPendingMoves < GridLayoutManager.this.mMaxPendingMoves) {
                this.mPendingMoves = mPendingMoves + 1;
            }
        }
        
        @Override
        protected void onStopInternal() {
            super.onStopInternal();
            this.mPendingMoves = 0;
            final View viewByPosition = ((RecyclerView.SmoothScroller)this).findViewByPosition(((RecyclerView.SmoothScroller)this).getTargetPosition());
            if (viewByPosition != null) {
                GridLayoutManager.this.scrollToView(viewByPosition, true);
            }
        }
    }
    
    @SuppressLint({ "BanParcelableUsage" })
    static final class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Bundle childStates;
        int index;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState() {
            this.childStates = Bundle.EMPTY;
        }
        
        SavedState(final Parcel parcel) {
            this.childStates = Bundle.EMPTY;
            this.index = parcel.readInt();
            this.childStates = parcel.readBundle(GridLayoutManager.class.getClassLoader());
        }
        
        public int describeContents() {
            return 0;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeInt(this.index);
            parcel.writeBundle(this.childStates);
        }
    }
}
