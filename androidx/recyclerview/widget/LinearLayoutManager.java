// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.graphics.PointF;
import java.util.List;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;

public class LinearLayoutManager extends LayoutManager implements ViewDropHandler, ScrollVectorProvider
{
    final AnchorInfo mAnchorInfo;
    private int mInitialPrefetchItemCount;
    private boolean mLastStackFromEnd;
    private final LayoutChunkResult mLayoutChunkResult;
    private LayoutState mLayoutState;
    int mOrientation;
    OrientationHelper mOrientationHelper;
    SavedState mPendingSavedState;
    int mPendingScrollPosition;
    int mPendingScrollPositionOffset;
    private boolean mRecycleChildrenOnDetach;
    private int[] mReusableIntPair;
    private boolean mReverseLayout;
    boolean mShouldReverseLayout;
    private boolean mSmoothScrollbarEnabled;
    private boolean mStackFromEnd;
    
    public LinearLayoutManager(final Context context) {
        this(context, 1, false);
    }
    
    public LinearLayoutManager(final Context context, final int orientation, final boolean reverseLayout) {
        this.mOrientation = 1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mStackFromEnd = false;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialPrefetchItemCount = 2;
        this.mReusableIntPair = new int[2];
        this.setOrientation(orientation);
        this.setReverseLayout(reverseLayout);
    }
    
    public LinearLayoutManager(final Context context, final AttributeSet set, final int n, final int n2) {
        this.mOrientation = 1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mStackFromEnd = false;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialPrefetchItemCount = 2;
        this.mReusableIntPair = new int[2];
        final Properties properties = RecyclerView.LayoutManager.getProperties(context, set, n, n2);
        this.setOrientation(properties.orientation);
        this.setReverseLayout(properties.reverseLayout);
        this.setStackFromEnd(properties.stackFromEnd);
    }
    
    private int computeScrollExtent(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return 0;
        }
        this.ensureLayoutState();
        return ScrollbarHelper.computeScrollExtent(state, this.mOrientationHelper, this.findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), this.findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled);
    }
    
    private int computeScrollOffset(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return 0;
        }
        this.ensureLayoutState();
        return ScrollbarHelper.computeScrollOffset(state, this.mOrientationHelper, this.findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), this.findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }
    
    private int computeScrollRange(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return 0;
        }
        this.ensureLayoutState();
        return ScrollbarHelper.computeScrollRange(state, this.mOrientationHelper, this.findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), this.findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled);
    }
    
    private View findFirstPartiallyOrCompletelyInvisibleChild() {
        return this.findOnePartiallyOrCompletelyInvisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount());
    }
    
    private View findFirstReferenceChild(final Recycler recycler, final State state) {
        return this.findReferenceChild(recycler, state, 0, ((RecyclerView.LayoutManager)this).getChildCount(), state.getItemCount());
    }
    
    private View findLastPartiallyOrCompletelyInvisibleChild() {
        return this.findOnePartiallyOrCompletelyInvisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1);
    }
    
    private View findLastReferenceChild(final Recycler recycler, final State state) {
        return this.findReferenceChild(recycler, state, ((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, state.getItemCount());
    }
    
    private View findPartiallyOrCompletelyInvisibleChildClosestToEnd() {
        View view;
        if (this.mShouldReverseLayout) {
            view = this.findFirstPartiallyOrCompletelyInvisibleChild();
        }
        else {
            view = this.findLastPartiallyOrCompletelyInvisibleChild();
        }
        return view;
    }
    
    private View findPartiallyOrCompletelyInvisibleChildClosestToStart() {
        View view;
        if (this.mShouldReverseLayout) {
            view = this.findLastPartiallyOrCompletelyInvisibleChild();
        }
        else {
            view = this.findFirstPartiallyOrCompletelyInvisibleChild();
        }
        return view;
    }
    
    private View findReferenceChildClosestToEnd(final Recycler recycler, final State state) {
        View view;
        if (this.mShouldReverseLayout) {
            view = this.findFirstReferenceChild(recycler, state);
        }
        else {
            view = this.findLastReferenceChild(recycler, state);
        }
        return view;
    }
    
    private View findReferenceChildClosestToStart(final Recycler recycler, final State state) {
        View view;
        if (this.mShouldReverseLayout) {
            view = this.findLastReferenceChild(recycler, state);
        }
        else {
            view = this.findFirstReferenceChild(recycler, state);
        }
        return view;
    }
    
    private int fixLayoutEndGap(int n, final Recycler recycler, final State state, final boolean b) {
        final int n2 = this.mOrientationHelper.getEndAfterPadding() - n;
        if (n2 > 0) {
            final int n3 = -this.scrollBy(-n2, recycler, state);
            if (b) {
                n = this.mOrientationHelper.getEndAfterPadding() - (n + n3);
                if (n > 0) {
                    this.mOrientationHelper.offsetChildren(n);
                    return n + n3;
                }
            }
            return n3;
        }
        return 0;
    }
    
    private int fixLayoutStartGap(int n, final Recycler recycler, final State state, final boolean b) {
        final int n2 = n - this.mOrientationHelper.getStartAfterPadding();
        if (n2 > 0) {
            int n4;
            final int n3 = n4 = -this.scrollBy(n2, recycler, state);
            if (b) {
                n = n + n3 - this.mOrientationHelper.getStartAfterPadding();
                n4 = n3;
                if (n > 0) {
                    this.mOrientationHelper.offsetChildren(-n);
                    n4 = n3 - n;
                }
            }
            return n4;
        }
        return 0;
    }
    
    private View getChildClosestToEnd() {
        int n;
        if (this.mShouldReverseLayout) {
            n = 0;
        }
        else {
            n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        }
        return ((RecyclerView.LayoutManager)this).getChildAt(n);
    }
    
    private View getChildClosestToStart() {
        int n;
        if (this.mShouldReverseLayout) {
            n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        }
        else {
            n = 0;
        }
        return ((RecyclerView.LayoutManager)this).getChildAt(n);
    }
    
    private void layoutForPredictiveAnimations(final Recycler recycler, final State state, final int n, final int n2) {
        if (state.willRunPredictiveAnimations() && ((RecyclerView.LayoutManager)this).getChildCount() != 0 && !state.isPreLayout()) {
            if (this.supportsPredictiveItemAnimations()) {
                final List<ViewHolder> scrapList = recycler.getScrapList();
                final int size = scrapList.size();
                final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
                final int n3 = 0;
                int mExtraFillSpace2;
                int mExtraFillSpace = mExtraFillSpace2 = n3;
                for (int i = n3; i < size; ++i) {
                    final ViewHolder viewHolder = scrapList.get(i);
                    if (!viewHolder.isRemoved()) {
                        final int layoutPosition = viewHolder.getLayoutPosition();
                        int n4 = 1;
                        if (layoutPosition < position != this.mShouldReverseLayout) {
                            n4 = -1;
                        }
                        if (n4 == -1) {
                            mExtraFillSpace2 += this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView);
                        }
                        else {
                            mExtraFillSpace += this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView);
                        }
                    }
                }
                this.mLayoutState.mScrapList = scrapList;
                if (mExtraFillSpace2 > 0) {
                    this.updateLayoutStateToFillStart(((RecyclerView.LayoutManager)this).getPosition(this.getChildClosestToStart()), n);
                    final LayoutState mLayoutState = this.mLayoutState;
                    mLayoutState.mExtraFillSpace = mExtraFillSpace2;
                    mLayoutState.mAvailable = 0;
                    mLayoutState.assignPositionFromScrapList();
                    this.fill(recycler, this.mLayoutState, state, false);
                }
                if (mExtraFillSpace > 0) {
                    this.updateLayoutStateToFillEnd(((RecyclerView.LayoutManager)this).getPosition(this.getChildClosestToEnd()), n2);
                    final LayoutState mLayoutState2 = this.mLayoutState;
                    mLayoutState2.mExtraFillSpace = mExtraFillSpace;
                    mLayoutState2.mAvailable = 0;
                    mLayoutState2.assignPositionFromScrapList();
                    this.fill(recycler, this.mLayoutState, state, false);
                }
                this.mLayoutState.mScrapList = null;
            }
        }
    }
    
    private void recycleByLayoutState(final Recycler recycler, final LayoutState layoutState) {
        if (layoutState.mRecycle) {
            if (!layoutState.mInfinite) {
                final int mScrollingOffset = layoutState.mScrollingOffset;
                final int mNoRecycleSpace = layoutState.mNoRecycleSpace;
                if (layoutState.mLayoutDirection == -1) {
                    this.recycleViewsFromEnd(recycler, mScrollingOffset, mNoRecycleSpace);
                }
                else {
                    this.recycleViewsFromStart(recycler, mScrollingOffset, mNoRecycleSpace);
                }
            }
        }
    }
    
    private void recycleChildren(final Recycler recycler, final int n, int i) {
        if (n == i) {
            return;
        }
        int j;
        if (i > (j = n)) {
            --i;
            while (i >= n) {
                ((RecyclerView.LayoutManager)this).removeAndRecycleViewAt(i, recycler);
                --i;
            }
        }
        else {
            while (j > i) {
                ((RecyclerView.LayoutManager)this).removeAndRecycleViewAt(j, recycler);
                --j;
            }
        }
    }
    
    private void recycleViewsFromEnd(final Recycler recycler, int i, int n) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        if (i < 0) {
            return;
        }
        final int n2 = this.mOrientationHelper.getEnd() - i + n;
        if (this.mShouldReverseLayout) {
            View child;
            for (i = 0; i < childCount; ++i) {
                child = ((RecyclerView.LayoutManager)this).getChildAt(i);
                if (this.mOrientationHelper.getDecoratedStart(child) < n2 || this.mOrientationHelper.getTransformedStartWithDecoration(child) < n2) {
                    this.recycleChildren(recycler, 0, i);
                    return;
                }
            }
        }
        else {
            View child2;
            for (n = (i = childCount - 1); i >= 0; --i) {
                child2 = ((RecyclerView.LayoutManager)this).getChildAt(i);
                if (this.mOrientationHelper.getDecoratedStart(child2) < n2 || this.mOrientationHelper.getTransformedStartWithDecoration(child2) < n2) {
                    this.recycleChildren(recycler, n, i);
                    break;
                }
            }
        }
    }
    
    private void recycleViewsFromStart(final Recycler recycler, int i, int childCount) {
        if (i < 0) {
            return;
        }
        final int n = i - childCount;
        childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        if (this.mShouldReverseLayout) {
            View child;
            for (i = --childCount; i >= 0; --i) {
                child = ((RecyclerView.LayoutManager)this).getChildAt(i);
                if (this.mOrientationHelper.getDecoratedEnd(child) > n || this.mOrientationHelper.getTransformedEndWithDecoration(child) > n) {
                    this.recycleChildren(recycler, childCount, i);
                    return;
                }
            }
        }
        else {
            View child2;
            for (i = 0; i < childCount; ++i) {
                child2 = ((RecyclerView.LayoutManager)this).getChildAt(i);
                if (this.mOrientationHelper.getDecoratedEnd(child2) > n || this.mOrientationHelper.getTransformedEndWithDecoration(child2) > n) {
                    this.recycleChildren(recycler, 0, i);
                    break;
                }
            }
        }
    }
    
    private void resolveShouldLayoutReverse() {
        if (this.mOrientation != 1 && this.isLayoutRTL()) {
            this.mShouldReverseLayout = (this.mReverseLayout ^ true);
        }
        else {
            this.mShouldReverseLayout = this.mReverseLayout;
        }
    }
    
    private boolean updateAnchorFromChildren(final Recycler recycler, final State state, final AnchorInfo anchorInfo) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        final boolean b = false;
        if (childCount == 0) {
            return false;
        }
        final View focusedChild = ((RecyclerView.LayoutManager)this).getFocusedChild();
        if (focusedChild != null && anchorInfo.isViewValidAsAnchor(focusedChild, state)) {
            anchorInfo.assignFromViewAndKeepVisibleRect(focusedChild, ((RecyclerView.LayoutManager)this).getPosition(focusedChild));
            return true;
        }
        if (this.mLastStackFromEnd != this.mStackFromEnd) {
            return false;
        }
        View view;
        if (anchorInfo.mLayoutFromEnd) {
            view = this.findReferenceChildClosestToEnd(recycler, state);
        }
        else {
            view = this.findReferenceChildClosestToStart(recycler, state);
        }
        if (view != null) {
            anchorInfo.assignFromView(view, ((RecyclerView.LayoutManager)this).getPosition(view));
            if (!state.isPreLayout() && this.supportsPredictiveItemAnimations()) {
                final int decoratedStart = this.mOrientationHelper.getDecoratedStart(view);
                final int decoratedEnd = this.mOrientationHelper.getDecoratedEnd(view);
                final int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
                final int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
                int n = 0;
                Label_0193: {
                    if (decoratedStart < endAfterPadding || decoratedEnd <= endAfterPadding) {
                        n = (b ? 1 : 0);
                        if (decoratedEnd > startAfterPadding) {
                            break Label_0193;
                        }
                        n = (b ? 1 : 0);
                        if (decoratedStart >= startAfterPadding) {
                            break Label_0193;
                        }
                    }
                    n = 1;
                }
                if (n != 0) {
                    int mCoordinate = startAfterPadding;
                    if (anchorInfo.mLayoutFromEnd) {
                        mCoordinate = endAfterPadding;
                    }
                    anchorInfo.mCoordinate = mCoordinate;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean updateAnchorFromPendingData(final State state, final AnchorInfo anchorInfo) {
        final boolean preLayout = state.isPreLayout();
        boolean mLayoutFromEnd = false;
        if (!preLayout) {
            final int mPendingScrollPosition = this.mPendingScrollPosition;
            if (mPendingScrollPosition != -1) {
                if (mPendingScrollPosition >= 0 && mPendingScrollPosition < state.getItemCount()) {
                    anchorInfo.mPosition = this.mPendingScrollPosition;
                    final SavedState mPendingSavedState = this.mPendingSavedState;
                    if (mPendingSavedState != null && mPendingSavedState.hasValidAnchor()) {
                        final boolean mAnchorLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
                        anchorInfo.mLayoutFromEnd = mAnchorLayoutFromEnd;
                        if (mAnchorLayoutFromEnd) {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset;
                        }
                        else {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset;
                        }
                        return true;
                    }
                    if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
                        final View viewByPosition = this.findViewByPosition(this.mPendingScrollPosition);
                        if (viewByPosition != null) {
                            if (this.mOrientationHelper.getDecoratedMeasurement(viewByPosition) > this.mOrientationHelper.getTotalSpace()) {
                                anchorInfo.assignCoordinateFromPadding();
                                return true;
                            }
                            if (this.mOrientationHelper.getDecoratedStart(viewByPosition) - this.mOrientationHelper.getStartAfterPadding() < 0) {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
                                anchorInfo.mLayoutFromEnd = false;
                                return true;
                            }
                            if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(viewByPosition) < 0) {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
                                return anchorInfo.mLayoutFromEnd = true;
                            }
                            int decoratedStart;
                            if (anchorInfo.mLayoutFromEnd) {
                                decoratedStart = this.mOrientationHelper.getDecoratedEnd(viewByPosition) + this.mOrientationHelper.getTotalSpaceChange();
                            }
                            else {
                                decoratedStart = this.mOrientationHelper.getDecoratedStart(viewByPosition);
                            }
                            anchorInfo.mCoordinate = decoratedStart;
                        }
                        else {
                            if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
                                if (this.mPendingScrollPosition < ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0)) == this.mShouldReverseLayout) {
                                    mLayoutFromEnd = true;
                                }
                                anchorInfo.mLayoutFromEnd = mLayoutFromEnd;
                            }
                            anchorInfo.assignCoordinateFromPadding();
                        }
                        return true;
                    }
                    final boolean mShouldReverseLayout = this.mShouldReverseLayout;
                    anchorInfo.mLayoutFromEnd = mShouldReverseLayout;
                    if (mShouldReverseLayout) {
                        anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset;
                    }
                    else {
                        anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset;
                    }
                    return true;
                }
                else {
                    this.mPendingScrollPosition = -1;
                    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
                }
            }
        }
        return false;
    }
    
    private void updateAnchorInfoForLayout(final Recycler recycler, final State state, final AnchorInfo anchorInfo) {
        if (this.updateAnchorFromPendingData(state, anchorInfo)) {
            return;
        }
        if (this.updateAnchorFromChildren(recycler, state, anchorInfo)) {
            return;
        }
        anchorInfo.assignCoordinateFromPadding();
        int mPosition;
        if (this.mStackFromEnd) {
            mPosition = state.getItemCount() - 1;
        }
        else {
            mPosition = 0;
        }
        anchorInfo.mPosition = mPosition;
    }
    
    private void updateLayoutState(int mScrollingOffset, final int mAvailable, final boolean b, final State state) {
        this.mLayoutState.mInfinite = this.resolveIsInfinite();
        this.mLayoutState.mLayoutDirection = mScrollingOffset;
        final int[] mReusableIntPair = this.mReusableIntPair;
        boolean b2 = false;
        mReusableIntPair[0] = 0;
        final int n = 1;
        final int n2 = 1;
        mReusableIntPair[1] = 0;
        this.calculateExtraLayoutSpace(state, mReusableIntPair);
        int max = Math.max(0, this.mReusableIntPair[0]);
        final int max2 = Math.max(0, this.mReusableIntPair[1]);
        if (mScrollingOffset == 1) {
            b2 = true;
        }
        final LayoutState mLayoutState = this.mLayoutState;
        if (b2) {
            mScrollingOffset = max2;
        }
        else {
            mScrollingOffset = max;
        }
        mLayoutState.mExtraFillSpace = mScrollingOffset;
        final LayoutState mLayoutState2 = this.mLayoutState;
        if (!b2) {
            max = max2;
        }
        mLayoutState2.mNoRecycleSpace = max;
        if (b2) {
            final LayoutState mLayoutState3 = this.mLayoutState;
            mLayoutState3.mExtraFillSpace += this.mOrientationHelper.getEndPadding();
            final View childClosestToEnd = this.getChildClosestToEnd();
            final LayoutState mLayoutState4 = this.mLayoutState;
            mScrollingOffset = n2;
            if (this.mShouldReverseLayout) {
                mScrollingOffset = -1;
            }
            mLayoutState4.mItemDirection = mScrollingOffset;
            final LayoutState mLayoutState5 = this.mLayoutState;
            mScrollingOffset = ((RecyclerView.LayoutManager)this).getPosition(childClosestToEnd);
            final LayoutState mLayoutState6 = this.mLayoutState;
            mLayoutState5.mCurrentPosition = mScrollingOffset + mLayoutState6.mItemDirection;
            mLayoutState6.mOffset = this.mOrientationHelper.getDecoratedEnd(childClosestToEnd);
            mScrollingOffset = this.mOrientationHelper.getDecoratedEnd(childClosestToEnd) - this.mOrientationHelper.getEndAfterPadding();
        }
        else {
            final View childClosestToStart = this.getChildClosestToStart();
            final LayoutState mLayoutState7 = this.mLayoutState;
            mLayoutState7.mExtraFillSpace += this.mOrientationHelper.getStartAfterPadding();
            final LayoutState mLayoutState8 = this.mLayoutState;
            if (this.mShouldReverseLayout) {
                mScrollingOffset = n;
            }
            else {
                mScrollingOffset = -1;
            }
            mLayoutState8.mItemDirection = mScrollingOffset;
            final LayoutState mLayoutState9 = this.mLayoutState;
            mScrollingOffset = ((RecyclerView.LayoutManager)this).getPosition(childClosestToStart);
            final LayoutState mLayoutState10 = this.mLayoutState;
            mLayoutState9.mCurrentPosition = mScrollingOffset + mLayoutState10.mItemDirection;
            mLayoutState10.mOffset = this.mOrientationHelper.getDecoratedStart(childClosestToStart);
            mScrollingOffset = -this.mOrientationHelper.getDecoratedStart(childClosestToStart) + this.mOrientationHelper.getStartAfterPadding();
        }
        final LayoutState mLayoutState11 = this.mLayoutState;
        mLayoutState11.mAvailable = mAvailable;
        if (b) {
            mLayoutState11.mAvailable = mAvailable - mScrollingOffset;
        }
        this.mLayoutState.mScrollingOffset = mScrollingOffset;
    }
    
    private void updateLayoutStateToFillEnd(final int mCurrentPosition, final int mOffset) {
        this.mLayoutState.mAvailable = this.mOrientationHelper.getEndAfterPadding() - mOffset;
        final LayoutState mLayoutState = this.mLayoutState;
        int mItemDirection;
        if (this.mShouldReverseLayout) {
            mItemDirection = -1;
        }
        else {
            mItemDirection = 1;
        }
        mLayoutState.mItemDirection = mItemDirection;
        final LayoutState mLayoutState2 = this.mLayoutState;
        mLayoutState2.mCurrentPosition = mCurrentPosition;
        mLayoutState2.mLayoutDirection = 1;
        mLayoutState2.mOffset = mOffset;
        mLayoutState2.mScrollingOffset = Integer.MIN_VALUE;
    }
    
    private void updateLayoutStateToFillEnd(final AnchorInfo anchorInfo) {
        this.updateLayoutStateToFillEnd(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }
    
    private void updateLayoutStateToFillStart(int n, final int mOffset) {
        this.mLayoutState.mAvailable = mOffset - this.mOrientationHelper.getStartAfterPadding();
        final LayoutState mLayoutState = this.mLayoutState;
        mLayoutState.mCurrentPosition = n;
        if (this.mShouldReverseLayout) {
            n = 1;
        }
        else {
            n = -1;
        }
        mLayoutState.mItemDirection = n;
        final LayoutState mLayoutState2 = this.mLayoutState;
        mLayoutState2.mLayoutDirection = -1;
        mLayoutState2.mOffset = mOffset;
        mLayoutState2.mScrollingOffset = Integer.MIN_VALUE;
    }
    
    private void updateLayoutStateToFillStart(final AnchorInfo anchorInfo) {
        this.updateLayoutStateToFillStart(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }
    
    @Override
    public void assertNotInLayoutOrScroll(final String s) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(s);
        }
    }
    
    protected void calculateExtraLayoutSpace(final State state, final int[] array) {
        int extraLayoutSpace = this.getExtraLayoutSpace(state);
        int n;
        if (this.mLayoutState.mLayoutDirection == -1) {
            n = 0;
        }
        else {
            n = extraLayoutSpace;
            extraLayoutSpace = 0;
        }
        array[0] = extraLayoutSpace;
        array[1] = n;
    }
    
    @Override
    public boolean canScrollHorizontally() {
        return this.mOrientation == 0;
    }
    
    @Override
    public boolean canScrollVertically() {
        final int mOrientation = this.mOrientation;
        boolean b = true;
        if (mOrientation != 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void collectAdjacentPrefetchPositions(int a, int n, final State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        if (this.mOrientation != 0) {
            a = n;
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            if (a != 0) {
                this.ensureLayoutState();
                if (a > 0) {
                    n = 1;
                }
                else {
                    n = -1;
                }
                this.updateLayoutState(n, Math.abs(a), true, state);
                this.collectPrefetchPositionsForLayoutState(state, this.mLayoutState, layoutPrefetchRegistry);
            }
        }
    }
    
    @Override
    public void collectInitialPrefetchPositions(final int n, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        final SavedState mPendingSavedState = this.mPendingSavedState;
        int n2 = -1;
        boolean mAnchorLayoutFromEnd;
        int n3;
        if (mPendingSavedState != null && mPendingSavedState.hasValidAnchor()) {
            final SavedState mPendingSavedState2 = this.mPendingSavedState;
            mAnchorLayoutFromEnd = mPendingSavedState2.mAnchorLayoutFromEnd;
            n3 = mPendingSavedState2.mAnchorPosition;
        }
        else {
            this.resolveShouldLayoutReverse();
            final boolean mShouldReverseLayout = this.mShouldReverseLayout;
            final int n4 = n3 = this.mPendingScrollPosition;
            mAnchorLayoutFromEnd = mShouldReverseLayout;
            if (n4 == -1) {
                if (mShouldReverseLayout) {
                    n3 = n - 1;
                    mAnchorLayoutFromEnd = mShouldReverseLayout;
                }
                else {
                    n3 = 0;
                    mAnchorLayoutFromEnd = mShouldReverseLayout;
                }
            }
        }
        if (!mAnchorLayoutFromEnd) {
            n2 = 1;
        }
        for (int n5 = 0; n5 < this.mInitialPrefetchItemCount && n3 >= 0 && n3 < n; n3 += n2, ++n5) {
            layoutPrefetchRegistry.addPosition(n3, 0);
        }
    }
    
    void collectPrefetchPositionsForLayoutState(final State state, final LayoutState layoutState, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        final int mCurrentPosition = layoutState.mCurrentPosition;
        if (mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount()) {
            layoutPrefetchRegistry.addPosition(mCurrentPosition, Math.max(0, layoutState.mScrollingOffset));
        }
    }
    
    @Override
    public int computeHorizontalScrollExtent(final State state) {
        return this.computeScrollExtent(state);
    }
    
    @Override
    public int computeHorizontalScrollOffset(final State state) {
        return this.computeScrollOffset(state);
    }
    
    @Override
    public int computeHorizontalScrollRange(final State state) {
        return this.computeScrollRange(state);
    }
    
    @Override
    public PointF computeScrollVectorForPosition(int n) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return null;
        }
        boolean b = false;
        final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
        final int n2 = 1;
        if (n < position) {
            b = true;
        }
        n = n2;
        if (b != this.mShouldReverseLayout) {
            n = -1;
        }
        if (this.mOrientation == 0) {
            return new PointF((float)n, 0.0f);
        }
        return new PointF(0.0f, (float)n);
    }
    
    @Override
    public int computeVerticalScrollExtent(final State state) {
        return this.computeScrollExtent(state);
    }
    
    @Override
    public int computeVerticalScrollOffset(final State state) {
        return this.computeScrollOffset(state);
    }
    
    @Override
    public int computeVerticalScrollRange(final State state) {
        return this.computeScrollRange(state);
    }
    
    int convertFocusDirectionToLayoutDirection(int n) {
        int n2 = -1;
        final int n3 = 1;
        final int n4 = 1;
        if (n != 1) {
            if (n != 2) {
                if (n == 17) {
                    if (this.mOrientation != 0) {
                        n2 = Integer.MIN_VALUE;
                    }
                    return n2;
                }
                if (n == 33) {
                    if (this.mOrientation != 1) {
                        n2 = Integer.MIN_VALUE;
                    }
                    return n2;
                }
                if (n == 66) {
                    if (this.mOrientation == 0) {
                        n = n3;
                    }
                    else {
                        n = Integer.MIN_VALUE;
                    }
                    return n;
                }
                if (n != 130) {
                    return Integer.MIN_VALUE;
                }
                if (this.mOrientation == 1) {
                    n = n4;
                }
                else {
                    n = Integer.MIN_VALUE;
                }
                return n;
            }
            else {
                if (this.mOrientation == 1) {
                    return 1;
                }
                if (this.isLayoutRTL()) {
                    return -1;
                }
                return 1;
            }
        }
        else {
            if (this.mOrientation == 1) {
                return -1;
            }
            if (this.isLayoutRTL()) {
                return 1;
            }
            return -1;
        }
    }
    
    LayoutState createLayoutState() {
        return new LayoutState();
    }
    
    void ensureLayoutState() {
        if (this.mLayoutState == null) {
            this.mLayoutState = this.createLayoutState();
        }
    }
    
    int fill(final Recycler recycler, final LayoutState layoutState, final State state, final boolean b) {
        final int mAvailable = layoutState.mAvailable;
        final int mScrollingOffset = layoutState.mScrollingOffset;
        if (mScrollingOffset != Integer.MIN_VALUE) {
            if (mAvailable < 0) {
                layoutState.mScrollingOffset = mScrollingOffset + mAvailable;
            }
            this.recycleByLayoutState(recycler, layoutState);
        }
        int n = layoutState.mAvailable + layoutState.mExtraFillSpace;
        final LayoutChunkResult mLayoutChunkResult = this.mLayoutChunkResult;
        while ((layoutState.mInfinite || n > 0) && layoutState.hasMore(state)) {
            mLayoutChunkResult.resetInternal();
            this.layoutChunk(recycler, state, layoutState, mLayoutChunkResult);
            if (mLayoutChunkResult.mFinished) {
                break;
            }
            layoutState.mOffset += mLayoutChunkResult.mConsumed * layoutState.mLayoutDirection;
            int n2 = 0;
            Label_0175: {
                if (mLayoutChunkResult.mIgnoreConsumed && layoutState.mScrapList == null) {
                    n2 = n;
                    if (state.isPreLayout()) {
                        break Label_0175;
                    }
                }
                final int mAvailable2 = layoutState.mAvailable;
                final int mConsumed = mLayoutChunkResult.mConsumed;
                layoutState.mAvailable = mAvailable2 - mConsumed;
                n2 = n - mConsumed;
            }
            final int mScrollingOffset2 = layoutState.mScrollingOffset;
            if (mScrollingOffset2 != Integer.MIN_VALUE) {
                final int mScrollingOffset3 = mScrollingOffset2 + mLayoutChunkResult.mConsumed;
                layoutState.mScrollingOffset = mScrollingOffset3;
                final int mAvailable3 = layoutState.mAvailable;
                if (mAvailable3 < 0) {
                    layoutState.mScrollingOffset = mScrollingOffset3 + mAvailable3;
                }
                this.recycleByLayoutState(recycler, layoutState);
            }
            n = n2;
            if (!b) {
                continue;
            }
            n = n2;
            if (mLayoutChunkResult.mFocusable) {
                break;
            }
        }
        return mAvailable - layoutState.mAvailable;
    }
    
    View findFirstVisibleChildClosestToEnd(final boolean b, final boolean b2) {
        if (this.mShouldReverseLayout) {
            return this.findOneVisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount(), b, b2);
        }
        return this.findOneVisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, b, b2);
    }
    
    View findFirstVisibleChildClosestToStart(final boolean b, final boolean b2) {
        if (this.mShouldReverseLayout) {
            return this.findOneVisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, b, b2);
        }
        return this.findOneVisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount(), b, b2);
    }
    
    public int findFirstVisibleItemPosition() {
        final View oneVisibleChild = this.findOneVisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount(), false, true);
        int position;
        if (oneVisibleChild == null) {
            position = -1;
        }
        else {
            position = ((RecyclerView.LayoutManager)this).getPosition(oneVisibleChild);
        }
        return position;
    }
    
    public int findLastVisibleItemPosition() {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        int position = -1;
        final View oneVisibleChild = this.findOneVisibleChild(childCount - 1, -1, false, true);
        if (oneVisibleChild != null) {
            position = ((RecyclerView.LayoutManager)this).getPosition(oneVisibleChild);
        }
        return position;
    }
    
    View findOnePartiallyOrCompletelyInvisibleChild(final int n, final int n2) {
        this.ensureLayoutState();
        int n3;
        if (n2 > n) {
            n3 = 1;
        }
        else if (n2 < n) {
            n3 = -1;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            return ((RecyclerView.LayoutManager)this).getChildAt(n);
        }
        int n4;
        int n5;
        if (this.mOrientationHelper.getDecoratedStart(((RecyclerView.LayoutManager)this).getChildAt(n)) < this.mOrientationHelper.getStartAfterPadding()) {
            n4 = 16644;
            n5 = 16388;
        }
        else {
            n4 = 4161;
            n5 = 4097;
        }
        View view;
        if (this.mOrientation == 0) {
            view = super.mHorizontalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n5);
        }
        else {
            view = super.mVerticalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n5);
        }
        return view;
    }
    
    View findOneVisibleChild(final int n, final int n2, final boolean b, final boolean b2) {
        this.ensureLayoutState();
        int n3 = 320;
        int n4;
        if (b) {
            n4 = 24579;
        }
        else {
            n4 = 320;
        }
        if (!b2) {
            n3 = 0;
        }
        View view;
        if (this.mOrientation == 0) {
            view = super.mHorizontalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n3);
        }
        else {
            view = super.mVerticalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n3);
        }
        return view;
    }
    
    View findReferenceChild(final Recycler recycler, final State state, int i, final int n, final int n2) {
        this.ensureLayoutState();
        final int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
        final int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
        int n3;
        if (n > i) {
            n3 = 1;
        }
        else {
            n3 = -1;
        }
        View view = null;
        View view2 = null;
        while (i != n) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final int position = ((RecyclerView.LayoutManager)this).getPosition(child);
            final int decoratedStart = this.mOrientationHelper.getDecoratedStart(child);
            final int decoratedEnd = this.mOrientationHelper.getDecoratedEnd(child);
            View view3 = view;
            View view4 = view2;
            if (position >= 0) {
                view3 = view;
                view4 = view2;
                if (position < n2) {
                    if (((LayoutParams)child.getLayoutParams()).isItemRemoved()) {
                        view3 = view;
                        if ((view4 = view2) == null) {
                            view3 = view;
                            view4 = child;
                        }
                    }
                    else {
                        if ((decoratedStart < endAfterPadding || decoratedEnd <= endAfterPadding) && (decoratedEnd > startAfterPadding || decoratedStart >= startAfterPadding)) {
                            return child;
                        }
                        view3 = view;
                        view4 = view2;
                        if (view == null) {
                            view3 = child;
                            view4 = view2;
                        }
                    }
                }
            }
            i += n3;
            view = view3;
            view2 = view4;
        }
        if (view != null) {
            view2 = view;
        }
        return view2;
    }
    
    @Override
    public View findViewByPosition(final int n) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        if (childCount == 0) {
            return null;
        }
        final int n2 = n - ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
        if (n2 >= 0 && n2 < childCount) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(n2);
            if (((RecyclerView.LayoutManager)this).getPosition(child) == n) {
                return child;
            }
        }
        return super.findViewByPosition(n);
    }
    
    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }
    
    @Deprecated
    protected int getExtraLayoutSpace(final State state) {
        if (state.hasTargetScrollPosition()) {
            return this.mOrientationHelper.getTotalSpace();
        }
        return 0;
    }
    
    public int getOrientation() {
        return this.mOrientation;
    }
    
    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }
    
    protected boolean isLayoutRTL() {
        final int layoutDirection = ((RecyclerView.LayoutManager)this).getLayoutDirection();
        boolean b = true;
        if (layoutDirection != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }
    
    void layoutChunk(final Recycler recycler, final State state, final LayoutState layoutState, final LayoutChunkResult layoutChunkResult) {
        final View next = layoutState.next(recycler);
        if (next == null) {
            layoutChunkResult.mFinished = true;
            return;
        }
        final LayoutParams layoutParams = (LayoutParams)next.getLayoutParams();
        if (layoutState.mScrapList == null) {
            if (this.mShouldReverseLayout == (layoutState.mLayoutDirection == -1)) {
                ((RecyclerView.LayoutManager)this).addView(next);
            }
            else {
                ((RecyclerView.LayoutManager)this).addView(next, 0);
            }
        }
        else if (this.mShouldReverseLayout == (layoutState.mLayoutDirection == -1)) {
            ((RecyclerView.LayoutManager)this).addDisappearingView(next);
        }
        else {
            ((RecyclerView.LayoutManager)this).addDisappearingView(next, 0);
        }
        ((RecyclerView.LayoutManager)this).measureChildWithMargins(next, 0, 0);
        layoutChunkResult.mConsumed = this.mOrientationHelper.getDecoratedMeasurement(next);
        int paddingLeft;
        int n2;
        int n3;
        int n4;
        if (this.mOrientation == 1) {
            int n;
            if (this.isLayoutRTL()) {
                n = ((RecyclerView.LayoutManager)this).getWidth() - ((RecyclerView.LayoutManager)this).getPaddingRight();
                paddingLeft = n - this.mOrientationHelper.getDecoratedMeasurementInOther(next);
            }
            else {
                paddingLeft = ((RecyclerView.LayoutManager)this).getPaddingLeft();
                n = this.mOrientationHelper.getDecoratedMeasurementInOther(next) + paddingLeft;
            }
            if (layoutState.mLayoutDirection == -1) {
                final int mOffset = layoutState.mOffset;
                final int mConsumed = layoutChunkResult.mConsumed;
                n2 = mOffset;
                n3 = n;
                n4 = mOffset - mConsumed;
            }
            else {
                final int mOffset2 = layoutState.mOffset;
                final int mConsumed2 = layoutChunkResult.mConsumed;
                final int n5 = mOffset2;
                final int n6 = n;
                final int n7 = mConsumed2 + mOffset2;
                n4 = n5;
                n3 = n6;
                n2 = n7;
            }
        }
        else {
            final int paddingTop = ((RecyclerView.LayoutManager)this).getPaddingTop();
            final int n8 = this.mOrientationHelper.getDecoratedMeasurementInOther(next) + paddingTop;
            if (layoutState.mLayoutDirection == -1) {
                final int mOffset3 = layoutState.mOffset;
                final int mConsumed3 = layoutChunkResult.mConsumed;
                n3 = mOffset3;
                final int n9 = paddingTop;
                n2 = n8;
                paddingLeft = mOffset3 - mConsumed3;
                n4 = n9;
            }
            else {
                final int mOffset4 = layoutState.mOffset;
                n3 = layoutChunkResult.mConsumed + mOffset4;
                n2 = n8;
                n4 = paddingTop;
                paddingLeft = mOffset4;
            }
        }
        ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(next, paddingLeft, n4, n3, n2);
        if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
            layoutChunkResult.mIgnoreConsumed = true;
        }
        layoutChunkResult.mFocusable = next.hasFocusable();
    }
    
    void onAnchorReady(final Recycler recycler, final State state, final AnchorInfo anchorInfo, final int n) {
    }
    
    @Override
    public void onDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        if (this.mRecycleChildrenOnDetach) {
            ((RecyclerView.LayoutManager)this).removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }
    
    @Override
    public View onFocusSearchFailed(View view, int convertFocusDirectionToLayoutDirection, final Recycler recycler, final State state) {
        this.resolveShouldLayoutReverse();
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return null;
        }
        convertFocusDirectionToLayoutDirection = this.convertFocusDirectionToLayoutDirection(convertFocusDirectionToLayoutDirection);
        if (convertFocusDirectionToLayoutDirection == Integer.MIN_VALUE) {
            return null;
        }
        this.ensureLayoutState();
        this.updateLayoutState(convertFocusDirectionToLayoutDirection, (int)(this.mOrientationHelper.getTotalSpace() * 0.33333334f), false, state);
        final LayoutState mLayoutState = this.mLayoutState;
        mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
        mLayoutState.mRecycle = false;
        this.fill(recycler, mLayoutState, state, true);
        if (convertFocusDirectionToLayoutDirection == -1) {
            view = this.findPartiallyOrCompletelyInvisibleChildClosestToStart();
        }
        else {
            view = this.findPartiallyOrCompletelyInvisibleChildClosestToEnd();
        }
        View view2;
        if (convertFocusDirectionToLayoutDirection == -1) {
            view2 = this.getChildClosestToStart();
        }
        else {
            view2 = this.getChildClosestToEnd();
        }
        if (!view2.hasFocusable()) {
            return view;
        }
        if (view == null) {
            return null;
        }
        return view2;
    }
    
    @Override
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            accessibilityEvent.setFromIndex(this.findFirstVisibleItemPosition());
            accessibilityEvent.setToIndex(this.findLastVisibleItemPosition());
        }
    }
    
    @Override
    public void onLayoutChildren(final Recycler recycler, final State state) {
        final SavedState mPendingSavedState = this.mPendingSavedState;
        int n = -1;
        if ((mPendingSavedState != null || this.mPendingScrollPosition != -1) && state.getItemCount() == 0) {
            ((RecyclerView.LayoutManager)this).removeAndRecycleAllViews(recycler);
            return;
        }
        final SavedState mPendingSavedState2 = this.mPendingSavedState;
        if (mPendingSavedState2 != null && mPendingSavedState2.hasValidAnchor()) {
            this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
        }
        this.ensureLayoutState();
        this.mLayoutState.mRecycle = false;
        this.resolveShouldLayoutReverse();
        final View focusedChild = ((RecyclerView.LayoutManager)this).getFocusedChild();
        if (this.mAnchorInfo.mValid && this.mPendingScrollPosition == -1 && this.mPendingSavedState == null) {
            if (focusedChild != null && (this.mOrientationHelper.getDecoratedStart(focusedChild) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(focusedChild) <= this.mOrientationHelper.getStartAfterPadding())) {
                this.mAnchorInfo.assignFromViewAndKeepVisibleRect(focusedChild, ((RecyclerView.LayoutManager)this).getPosition(focusedChild));
            }
        }
        else {
            this.mAnchorInfo.reset();
            final AnchorInfo mAnchorInfo = this.mAnchorInfo;
            mAnchorInfo.mLayoutFromEnd = (this.mShouldReverseLayout ^ this.mStackFromEnd);
            this.updateAnchorInfoForLayout(recycler, state, mAnchorInfo);
            this.mAnchorInfo.mValid = true;
        }
        final LayoutState mLayoutState = this.mLayoutState;
        int mLayoutDirection;
        if (mLayoutState.mLastScrollDelta >= 0) {
            mLayoutDirection = 1;
        }
        else {
            mLayoutDirection = -1;
        }
        mLayoutState.mLayoutDirection = mLayoutDirection;
        final int[] mReusableIntPair = this.mReusableIntPair;
        mReusableIntPair[1] = (mReusableIntPair[0] = 0);
        this.calculateExtraLayoutSpace(state, mReusableIntPair);
        final int n2 = Math.max(0, this.mReusableIntPair[0]) + this.mOrientationHelper.getStartAfterPadding();
        final int n3 = Math.max(0, this.mReusableIntPair[1]) + this.mOrientationHelper.getEndPadding();
        int mExtraFillSpace = n2;
        int mExtraFillSpace2 = n3;
        if (state.isPreLayout()) {
            final int mPendingScrollPosition = this.mPendingScrollPosition;
            mExtraFillSpace = n2;
            mExtraFillSpace2 = n3;
            if (mPendingScrollPosition != -1) {
                mExtraFillSpace = n2;
                mExtraFillSpace2 = n3;
                if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                    final View viewByPosition = this.findViewByPosition(mPendingScrollPosition);
                    mExtraFillSpace = n2;
                    mExtraFillSpace2 = n3;
                    if (viewByPosition != null) {
                        int mPendingScrollPositionOffset;
                        int mPendingScrollPositionOffset2;
                        if (this.mShouldReverseLayout) {
                            mPendingScrollPositionOffset = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(viewByPosition);
                            mPendingScrollPositionOffset2 = this.mPendingScrollPositionOffset;
                        }
                        else {
                            mPendingScrollPositionOffset2 = this.mOrientationHelper.getDecoratedStart(viewByPosition) - this.mOrientationHelper.getStartAfterPadding();
                            mPendingScrollPositionOffset = this.mPendingScrollPositionOffset;
                        }
                        final int n4 = mPendingScrollPositionOffset - mPendingScrollPositionOffset2;
                        if (n4 > 0) {
                            mExtraFillSpace = n2 + n4;
                            mExtraFillSpace2 = n3;
                        }
                        else {
                            mExtraFillSpace2 = n3 - n4;
                            mExtraFillSpace = n2;
                        }
                    }
                }
            }
        }
        Label_0487: {
            if (this.mAnchorInfo.mLayoutFromEnd) {
                if (!this.mShouldReverseLayout) {
                    break Label_0487;
                }
            }
            else if (this.mShouldReverseLayout) {
                break Label_0487;
            }
            n = 1;
        }
        this.onAnchorReady(recycler, state, this.mAnchorInfo, n);
        ((RecyclerView.LayoutManager)this).detachAndScrapAttachedViews(recycler);
        this.mLayoutState.mInfinite = this.resolveIsInfinite();
        this.mLayoutState.mIsPreLayout = state.isPreLayout();
        this.mLayoutState.mNoRecycleSpace = 0;
        final AnchorInfo mAnchorInfo2 = this.mAnchorInfo;
        int mOffset3;
        int mOffset4;
        if (mAnchorInfo2.mLayoutFromEnd) {
            this.updateLayoutStateToFillStart(mAnchorInfo2);
            final LayoutState mLayoutState2 = this.mLayoutState;
            mLayoutState2.mExtraFillSpace = mExtraFillSpace;
            this.fill(recycler, mLayoutState2, state, false);
            final LayoutState mLayoutState3 = this.mLayoutState;
            final int mOffset = mLayoutState3.mOffset;
            final int mCurrentPosition = mLayoutState3.mCurrentPosition;
            final int mAvailable = mLayoutState3.mAvailable;
            int mExtraFillSpace3 = mExtraFillSpace2;
            if (mAvailable > 0) {
                mExtraFillSpace3 = mExtraFillSpace2 + mAvailable;
            }
            this.updateLayoutStateToFillEnd(this.mAnchorInfo);
            final LayoutState mLayoutState4 = this.mLayoutState;
            mLayoutState4.mExtraFillSpace = mExtraFillSpace3;
            mLayoutState4.mCurrentPosition += mLayoutState4.mItemDirection;
            this.fill(recycler, mLayoutState4, state, false);
            final LayoutState mLayoutState5 = this.mLayoutState;
            final int mOffset2 = mLayoutState5.mOffset;
            final int mAvailable2 = mLayoutState5.mAvailable;
            mOffset3 = mOffset;
            mOffset4 = mOffset2;
            if (mAvailable2 > 0) {
                this.updateLayoutStateToFillStart(mCurrentPosition, mOffset);
                final LayoutState mLayoutState6 = this.mLayoutState;
                mLayoutState6.mExtraFillSpace = mAvailable2;
                this.fill(recycler, mLayoutState6, state, false);
                mOffset3 = this.mLayoutState.mOffset;
                mOffset4 = mOffset2;
            }
        }
        else {
            this.updateLayoutStateToFillEnd(mAnchorInfo2);
            final LayoutState mLayoutState7 = this.mLayoutState;
            mLayoutState7.mExtraFillSpace = mExtraFillSpace2;
            this.fill(recycler, mLayoutState7, state, false);
            final LayoutState mLayoutState8 = this.mLayoutState;
            final int mOffset5 = mLayoutState8.mOffset;
            final int mCurrentPosition2 = mLayoutState8.mCurrentPosition;
            final int mAvailable3 = mLayoutState8.mAvailable;
            int mExtraFillSpace4 = mExtraFillSpace;
            if (mAvailable3 > 0) {
                mExtraFillSpace4 = mExtraFillSpace + mAvailable3;
            }
            this.updateLayoutStateToFillStart(this.mAnchorInfo);
            final LayoutState mLayoutState9 = this.mLayoutState;
            mLayoutState9.mExtraFillSpace = mExtraFillSpace4;
            mLayoutState9.mCurrentPosition += mLayoutState9.mItemDirection;
            this.fill(recycler, mLayoutState9, state, false);
            final LayoutState mLayoutState10 = this.mLayoutState;
            final int mOffset6 = mLayoutState10.mOffset;
            final int mAvailable4 = mLayoutState10.mAvailable;
            mOffset3 = mOffset6;
            mOffset4 = mOffset5;
            if (mAvailable4 > 0) {
                this.updateLayoutStateToFillEnd(mCurrentPosition2, mOffset5);
                final LayoutState mLayoutState11 = this.mLayoutState;
                mLayoutState11.mExtraFillSpace = mAvailable4;
                this.fill(recycler, mLayoutState11, state, false);
                mOffset4 = this.mLayoutState.mOffset;
                mOffset3 = mOffset6;
            }
        }
        int n5 = mOffset3;
        int n6 = mOffset4;
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            int n7;
            int n8;
            int n9;
            if (this.mShouldReverseLayout ^ this.mStackFromEnd) {
                final int fixLayoutEndGap = this.fixLayoutEndGap(mOffset4, recycler, state, true);
                n7 = mOffset3 + fixLayoutEndGap;
                n8 = mOffset4 + fixLayoutEndGap;
                n9 = this.fixLayoutStartGap(n7, recycler, state, false);
            }
            else {
                final int fixLayoutStartGap = this.fixLayoutStartGap(mOffset3, recycler, state, true);
                n7 = mOffset3 + fixLayoutStartGap;
                n8 = mOffset4 + fixLayoutStartGap;
                n9 = this.fixLayoutEndGap(n8, recycler, state, false);
            }
            n5 = n7 + n9;
            n6 = n8 + n9;
        }
        this.layoutForPredictiveAnimations(recycler, state, n5, n6);
        if (!state.isPreLayout()) {
            this.mOrientationHelper.onLayoutComplete();
        }
        else {
            this.mAnchorInfo.reset();
        }
        this.mLastStackFromEnd = this.mStackFromEnd;
    }
    
    @Override
    public void onLayoutCompleted(final State state) {
        super.onLayoutCompleted(state);
        this.mPendingSavedState = null;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mAnchorInfo.reset();
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState mPendingSavedState = (SavedState)parcelable;
            this.mPendingSavedState = mPendingSavedState;
            if (this.mPendingScrollPosition != -1) {
                mPendingSavedState.invalidateAnchor();
            }
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        if (this.mPendingSavedState != null) {
            return (Parcelable)new SavedState(this.mPendingSavedState);
        }
        final SavedState savedState = new SavedState();
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            this.ensureLayoutState();
            final boolean mAnchorLayoutFromEnd = this.mLastStackFromEnd ^ this.mShouldReverseLayout;
            savedState.mAnchorLayoutFromEnd = mAnchorLayoutFromEnd;
            if (mAnchorLayoutFromEnd) {
                final View childClosestToEnd = this.getChildClosestToEnd();
                savedState.mAnchorOffset = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(childClosestToEnd);
                savedState.mAnchorPosition = ((RecyclerView.LayoutManager)this).getPosition(childClosestToEnd);
            }
            else {
                final View childClosestToStart = this.getChildClosestToStart();
                savedState.mAnchorPosition = ((RecyclerView.LayoutManager)this).getPosition(childClosestToStart);
                savedState.mAnchorOffset = this.mOrientationHelper.getDecoratedStart(childClosestToStart) - this.mOrientationHelper.getStartAfterPadding();
            }
        }
        else {
            savedState.invalidateAnchor();
        }
        return (Parcelable)savedState;
    }
    
    @Override
    public void prepareForDrop(final View view, final View view2, int position, int position2) {
        this.assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
        this.ensureLayoutState();
        this.resolveShouldLayoutReverse();
        position = ((RecyclerView.LayoutManager)this).getPosition(view);
        position2 = ((RecyclerView.LayoutManager)this).getPosition(view2);
        if (position < position2) {
            position = 1;
        }
        else {
            position = -1;
        }
        if (this.mShouldReverseLayout) {
            if (position == 1) {
                this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getEndAfterPadding() - (this.mOrientationHelper.getDecoratedStart(view2) + this.mOrientationHelper.getDecoratedMeasurement(view)));
            }
            else {
                this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(view2));
            }
        }
        else if (position == -1) {
            this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getDecoratedStart(view2));
        }
        else {
            this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getDecoratedEnd(view2) - this.mOrientationHelper.getDecoratedMeasurement(view));
        }
    }
    
    boolean resolveIsInfinite() {
        return this.mOrientationHelper.getMode() == 0 && this.mOrientationHelper.getEnd() == 0;
    }
    
    int scrollBy(int n, final Recycler recycler, final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0 || n == 0) {
            return 0;
        }
        this.ensureLayoutState();
        this.mLayoutState.mRecycle = true;
        int n2;
        if (n > 0) {
            n2 = 1;
        }
        else {
            n2 = -1;
        }
        final int abs = Math.abs(n);
        this.updateLayoutState(n2, abs, true, state);
        final LayoutState mLayoutState = this.mLayoutState;
        final int n3 = mLayoutState.mScrollingOffset + this.fill(recycler, mLayoutState, state, false);
        if (n3 < 0) {
            return 0;
        }
        if (abs > n3) {
            n = n2 * n3;
        }
        this.mOrientationHelper.offsetChildren(-n);
        return this.mLayoutState.mLastScrollDelta = n;
    }
    
    @Override
    public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
        if (this.mOrientation == 1) {
            return 0;
        }
        return this.scrollBy(n, recycler, state);
    }
    
    @Override
    public void scrollToPosition(final int mPendingScrollPosition) {
        this.mPendingScrollPosition = mPendingScrollPosition;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        final SavedState mPendingSavedState = this.mPendingSavedState;
        if (mPendingSavedState != null) {
            mPendingSavedState.invalidateAnchor();
        }
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void scrollToPositionWithOffset(final int mPendingScrollPosition, final int mPendingScrollPositionOffset) {
        this.mPendingScrollPosition = mPendingScrollPosition;
        this.mPendingScrollPositionOffset = mPendingScrollPositionOffset;
        final SavedState mPendingSavedState = this.mPendingSavedState;
        if (mPendingSavedState != null) {
            mPendingSavedState.invalidateAnchor();
        }
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
        if (this.mOrientation == 0) {
            return 0;
        }
        return this.scrollBy(n, recycler, state);
    }
    
    public void setOrientation(final int n) {
        if (n != 0 && n != 1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("invalid orientation:");
            sb.append(n);
            throw new IllegalArgumentException(sb.toString());
        }
        this.assertNotInLayoutOrScroll(null);
        if (n != this.mOrientation || this.mOrientationHelper == null) {
            final OrientationHelper orientationHelper = OrientationHelper.createOrientationHelper(this, n);
            this.mOrientationHelper = orientationHelper;
            this.mAnchorInfo.mOrientationHelper = orientationHelper;
            this.mOrientation = n;
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    public void setReverseLayout(final boolean mReverseLayout) {
        this.assertNotInLayoutOrScroll(null);
        if (mReverseLayout == this.mReverseLayout) {
            return;
        }
        this.mReverseLayout = mReverseLayout;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void setStackFromEnd(final boolean mStackFromEnd) {
        this.assertNotInLayoutOrScroll(null);
        if (this.mStackFromEnd == mStackFromEnd) {
            return;
        }
        this.mStackFromEnd = mStackFromEnd;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    boolean shouldMeasureTwice() {
        return ((RecyclerView.LayoutManager)this).getHeightMode() != 1073741824 && ((RecyclerView.LayoutManager)this).getWidthMode() != 1073741824 && ((RecyclerView.LayoutManager)this).hasFlexibleChildInBothOrientations();
    }
    
    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int targetPosition) {
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        ((RecyclerView.SmoothScroller)linearSmoothScroller).setTargetPosition(targetPosition);
        ((RecyclerView.LayoutManager)this).startSmoothScroll(linearSmoothScroller);
    }
    
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && this.mLastStackFromEnd == this.mStackFromEnd;
    }
    
    static class AnchorInfo
    {
        int mCoordinate;
        boolean mLayoutFromEnd;
        OrientationHelper mOrientationHelper;
        int mPosition;
        boolean mValid;
        
        AnchorInfo() {
            this.reset();
        }
        
        void assignCoordinateFromPadding() {
            int mCoordinate;
            if (this.mLayoutFromEnd) {
                mCoordinate = this.mOrientationHelper.getEndAfterPadding();
            }
            else {
                mCoordinate = this.mOrientationHelper.getStartAfterPadding();
            }
            this.mCoordinate = mCoordinate;
        }
        
        public void assignFromView(final View view, final int mPosition) {
            if (this.mLayoutFromEnd) {
                this.mCoordinate = this.mOrientationHelper.getDecoratedEnd(view) + this.mOrientationHelper.getTotalSpaceChange();
            }
            else {
                this.mCoordinate = this.mOrientationHelper.getDecoratedStart(view);
            }
            this.mPosition = mPosition;
        }
        
        public void assignFromViewAndKeepVisibleRect(final View view, int a) {
            final int totalSpaceChange = this.mOrientationHelper.getTotalSpaceChange();
            if (totalSpaceChange >= 0) {
                this.assignFromView(view, a);
                return;
            }
            this.mPosition = a;
            if (this.mLayoutFromEnd) {
                a = this.mOrientationHelper.getEndAfterPadding() - totalSpaceChange - this.mOrientationHelper.getDecoratedEnd(view);
                this.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - a;
                if (a > 0) {
                    final int decoratedMeasurement = this.mOrientationHelper.getDecoratedMeasurement(view);
                    final int mCoordinate = this.mCoordinate;
                    final int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
                    final int n = mCoordinate - decoratedMeasurement - (startAfterPadding + Math.min(this.mOrientationHelper.getDecoratedStart(view) - startAfterPadding, 0));
                    if (n < 0) {
                        this.mCoordinate += Math.min(a, -n);
                    }
                }
            }
            else {
                final int decoratedStart = this.mOrientationHelper.getDecoratedStart(view);
                a = decoratedStart - this.mOrientationHelper.getStartAfterPadding();
                this.mCoordinate = decoratedStart;
                if (a > 0) {
                    final int n2 = this.mOrientationHelper.getEndAfterPadding() - Math.min(0, this.mOrientationHelper.getEndAfterPadding() - totalSpaceChange - this.mOrientationHelper.getDecoratedEnd(view)) - (decoratedStart + this.mOrientationHelper.getDecoratedMeasurement(view));
                    if (n2 < 0) {
                        this.mCoordinate -= Math.min(a, -n2);
                    }
                }
            }
        }
        
        boolean isViewValidAsAnchor(final View view, final State state) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            return !layoutParams.isItemRemoved() && layoutParams.getViewLayoutPosition() >= 0 && layoutParams.getViewLayoutPosition() < state.getItemCount();
        }
        
        void reset() {
            this.mPosition = -1;
            this.mCoordinate = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mValid = false;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("AnchorInfo{mPosition=");
            sb.append(this.mPosition);
            sb.append(", mCoordinate=");
            sb.append(this.mCoordinate);
            sb.append(", mLayoutFromEnd=");
            sb.append(this.mLayoutFromEnd);
            sb.append(", mValid=");
            sb.append(this.mValid);
            sb.append('}');
            return sb.toString();
        }
    }
    
    protected static class LayoutChunkResult
    {
        public int mConsumed;
        public boolean mFinished;
        public boolean mFocusable;
        public boolean mIgnoreConsumed;
        
        void resetInternal() {
            this.mConsumed = 0;
            this.mFinished = false;
            this.mIgnoreConsumed = false;
            this.mFocusable = false;
        }
    }
    
    static class LayoutState
    {
        int mAvailable;
        int mCurrentPosition;
        int mExtraFillSpace;
        boolean mInfinite;
        boolean mIsPreLayout;
        int mItemDirection;
        int mLastScrollDelta;
        int mLayoutDirection;
        int mNoRecycleSpace;
        int mOffset;
        boolean mRecycle;
        List<ViewHolder> mScrapList;
        int mScrollingOffset;
        
        LayoutState() {
            this.mRecycle = true;
            this.mExtraFillSpace = 0;
            this.mNoRecycleSpace = 0;
            this.mScrapList = null;
        }
        
        private View nextViewFromScrapList() {
            for (int size = this.mScrapList.size(), i = 0; i < size; ++i) {
                final View itemView = this.mScrapList.get(i).itemView;
                final LayoutParams layoutParams = (LayoutParams)itemView.getLayoutParams();
                if (!layoutParams.isItemRemoved()) {
                    if (this.mCurrentPosition == layoutParams.getViewLayoutPosition()) {
                        this.assignPositionFromScrapList(itemView);
                        return itemView;
                    }
                }
            }
            return null;
        }
        
        public void assignPositionFromScrapList() {
            this.assignPositionFromScrapList(null);
        }
        
        public void assignPositionFromScrapList(View nextViewInLimitedList) {
            nextViewInLimitedList = this.nextViewInLimitedList(nextViewInLimitedList);
            if (nextViewInLimitedList == null) {
                this.mCurrentPosition = -1;
            }
            else {
                this.mCurrentPosition = ((LayoutParams)nextViewInLimitedList.getLayoutParams()).getViewLayoutPosition();
            }
        }
        
        boolean hasMore(final State state) {
            final int mCurrentPosition = this.mCurrentPosition;
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }
        
        View next(final Recycler recycler) {
            if (this.mScrapList != null) {
                return this.nextViewFromScrapList();
            }
            final View viewForPosition = recycler.getViewForPosition(this.mCurrentPosition);
            this.mCurrentPosition += this.mItemDirection;
            return viewForPosition;
        }
        
        public View nextViewInLimitedList(final View view) {
            final int size = this.mScrapList.size();
            View view2 = null;
            int n = Integer.MAX_VALUE;
            int n2 = 0;
            View view3;
            while (true) {
                view3 = view2;
                if (n2 >= size) {
                    break;
                }
                final View itemView = this.mScrapList.get(n2).itemView;
                final LayoutParams layoutParams = (LayoutParams)itemView.getLayoutParams();
                View view4 = view2;
                int n3 = n;
                if (itemView != view) {
                    if (layoutParams.isItemRemoved()) {
                        view4 = view2;
                        n3 = n;
                    }
                    else {
                        final int n4 = (layoutParams.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
                        if (n4 < 0) {
                            view4 = view2;
                            n3 = n;
                        }
                        else {
                            view4 = view2;
                            if (n4 < (n3 = n)) {
                                final View view5 = itemView;
                                if (n4 == 0) {
                                    view3 = view5;
                                    break;
                                }
                                n3 = n4;
                                view4 = view5;
                            }
                        }
                    }
                }
                ++n2;
                view2 = view4;
                n = n3;
            }
            return view3;
        }
    }
    
    @SuppressLint({ "BanParcelableUsage" })
    public static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean mAnchorLayoutFromEnd;
        int mAnchorOffset;
        int mAnchorPosition;
        
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
        
        public SavedState() {
        }
        
        SavedState(final Parcel parcel) {
            this.mAnchorPosition = parcel.readInt();
            this.mAnchorOffset = parcel.readInt();
            final int int1 = parcel.readInt();
            boolean mAnchorLayoutFromEnd = true;
            if (int1 != 1) {
                mAnchorLayoutFromEnd = false;
            }
            this.mAnchorLayoutFromEnd = mAnchorLayoutFromEnd;
        }
        
        public SavedState(final SavedState savedState) {
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mAnchorOffset = savedState.mAnchorOffset;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
        }
        
        public int describeContents() {
            return 0;
        }
        
        boolean hasValidAnchor() {
            return this.mAnchorPosition >= 0;
        }
        
        void invalidateAnchor() {
            this.mAnchorPosition = -1;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mAnchorOffset);
            parcel.writeInt((int)(this.mAnchorLayoutFromEnd ? 1 : 0));
        }
    }
}
