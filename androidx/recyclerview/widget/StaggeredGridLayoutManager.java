// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import java.util.List;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.graphics.PointF;
import java.util.Arrays;
import android.view.View$MeasureSpec;
import java.util.ArrayList;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import java.util.BitSet;

public class StaggeredGridLayoutManager extends LayoutManager implements ScrollVectorProvider
{
    private final AnchorInfo mAnchorInfo;
    private final Runnable mCheckForGapsRunnable;
    private int mFullSizeSpec;
    private int mGapStrategy;
    private boolean mLaidOutInvalidFullSpan;
    private boolean mLastLayoutFromEnd;
    private boolean mLastLayoutRTL;
    private final LayoutState mLayoutState;
    LazySpanLookup mLazySpanLookup;
    private int mOrientation;
    private SavedState mPendingSavedState;
    int mPendingScrollPosition;
    int mPendingScrollPositionOffset;
    private int[] mPrefetchDistances;
    OrientationHelper mPrimaryOrientation;
    private BitSet mRemainingSpans;
    boolean mReverseLayout;
    OrientationHelper mSecondaryOrientation;
    boolean mShouldReverseLayout;
    private int mSizePerSpan;
    private boolean mSmoothScrollbarEnabled;
    private int mSpanCount;
    Span[] mSpans;
    private final Rect mTmpRect;
    
    public StaggeredGridLayoutManager(final Context context, final AttributeSet set, final int n, final int n2) {
        this.mSpanCount = -1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mLazySpanLookup = new LazySpanLookup();
        this.mGapStrategy = 2;
        this.mTmpRect = new Rect();
        this.mAnchorInfo = new AnchorInfo();
        this.mLaidOutInvalidFullSpan = false;
        this.mSmoothScrollbarEnabled = true;
        this.mCheckForGapsRunnable = new Runnable() {
            @Override
            public void run() {
                StaggeredGridLayoutManager.this.checkForGaps();
            }
        };
        final Properties properties = RecyclerView.LayoutManager.getProperties(context, set, n, n2);
        this.setOrientation(properties.orientation);
        this.setSpanCount(properties.spanCount);
        this.setReverseLayout(properties.reverseLayout);
        this.mLayoutState = new LayoutState();
        this.createOrientationHelpers();
    }
    
    private void appendViewToAllSpans(final View view) {
        for (int i = this.mSpanCount - 1; i >= 0; --i) {
            this.mSpans[i].appendToSpan(view);
        }
    }
    
    private void applyPendingSavedState(final AnchorInfo anchorInfo) {
        final SavedState mPendingSavedState = this.mPendingSavedState;
        final int mSpanOffsetsSize = mPendingSavedState.mSpanOffsetsSize;
        if (mSpanOffsetsSize > 0) {
            if (mSpanOffsetsSize == this.mSpanCount) {
                for (int i = 0; i < this.mSpanCount; ++i) {
                    this.mSpans[i].clear();
                    final SavedState mPendingSavedState2 = this.mPendingSavedState;
                    final int n = mPendingSavedState2.mSpanOffsets[i];
                    int line;
                    if ((line = n) != Integer.MIN_VALUE) {
                        int n2;
                        if (mPendingSavedState2.mAnchorLayoutFromEnd) {
                            n2 = this.mPrimaryOrientation.getEndAfterPadding();
                        }
                        else {
                            n2 = this.mPrimaryOrientation.getStartAfterPadding();
                        }
                        line = n + n2;
                    }
                    this.mSpans[i].setLine(line);
                }
            }
            else {
                mPendingSavedState.invalidateSpanInfo();
                final SavedState mPendingSavedState3 = this.mPendingSavedState;
                mPendingSavedState3.mAnchorPosition = mPendingSavedState3.mVisibleAnchorPosition;
            }
        }
        final SavedState mPendingSavedState4 = this.mPendingSavedState;
        this.mLastLayoutRTL = mPendingSavedState4.mLastLayoutRTL;
        this.setReverseLayout(mPendingSavedState4.mReverseLayout);
        this.resolveShouldLayoutReverse();
        final SavedState mPendingSavedState5 = this.mPendingSavedState;
        final int mAnchorPosition = mPendingSavedState5.mAnchorPosition;
        if (mAnchorPosition != -1) {
            this.mPendingScrollPosition = mAnchorPosition;
            anchorInfo.mLayoutFromEnd = mPendingSavedState5.mAnchorLayoutFromEnd;
        }
        else {
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
        }
        final SavedState mPendingSavedState6 = this.mPendingSavedState;
        if (mPendingSavedState6.mSpanLookupSize > 1) {
            final LazySpanLookup mLazySpanLookup = this.mLazySpanLookup;
            mLazySpanLookup.mData = mPendingSavedState6.mSpanLookup;
            mLazySpanLookup.mFullSpanItems = mPendingSavedState6.mFullSpanItems;
        }
    }
    
    private void attachViewToSpans(final View view, final LayoutParams layoutParams, final LayoutState layoutState) {
        if (layoutState.mLayoutDirection == 1) {
            if (layoutParams.mFullSpan) {
                this.appendViewToAllSpans(view);
            }
            else {
                layoutParams.mSpan.appendToSpan(view);
            }
        }
        else if (layoutParams.mFullSpan) {
            this.prependViewToAllSpans(view);
        }
        else {
            layoutParams.mSpan.prependToSpan(view);
        }
    }
    
    private int calculateScrollDirectionForPosition(final int n) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        int n2 = -1;
        if (childCount == 0) {
            if (this.mShouldReverseLayout) {
                n2 = 1;
            }
            return n2;
        }
        if (n < this.getFirstChildPosition() == this.mShouldReverseLayout) {
            n2 = 1;
        }
        return n2;
    }
    
    private boolean checkSpanForGap(final Span span) {
        if (this.mShouldReverseLayout) {
            if (span.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding()) {
                final ArrayList<View> mViews = span.mViews;
                return span.getLayoutParams(mViews.get(mViews.size() - 1)).mFullSpan ^ true;
            }
        }
        else if (span.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
            return span.getLayoutParams(span.mViews.get(0)).mFullSpan ^ true;
        }
        return false;
    }
    
    private int computeScrollExtent(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollExtent(state, this.mPrimaryOrientation, this.findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ true), this.findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ true), this, this.mSmoothScrollbarEnabled);
    }
    
    private int computeScrollOffset(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollOffset(state, this.mPrimaryOrientation, this.findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ true), this.findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }
    
    private int computeScrollRange(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollRange(state, this.mPrimaryOrientation, this.findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ true), this.findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ true), this, this.mSmoothScrollbarEnabled);
    }
    
    private int convertFocusDirectionToLayoutDirection(int n) {
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
    
    private FullSpanItem createFullSpanItemFromEnd(final int n) {
        final FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i = 0; i < this.mSpanCount; ++i) {
            fullSpanItem.mGapPerSpan[i] = n - this.mSpans[i].getEndLine(n);
        }
        return fullSpanItem;
    }
    
    private FullSpanItem createFullSpanItemFromStart(final int n) {
        final FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i = 0; i < this.mSpanCount; ++i) {
            fullSpanItem.mGapPerSpan[i] = this.mSpans[i].getStartLine(n) - n;
        }
        return fullSpanItem;
    }
    
    private void createOrientationHelpers() {
        this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
    }
    
    private int fill(final Recycler recycler, final LayoutState layoutState, final State state) {
        final BitSet mRemainingSpans = this.mRemainingSpans;
        final int mSpanCount = this.mSpanCount;
        int n = 0;
        mRemainingSpans.set(0, mSpanCount, true);
        int n2;
        if (this.mLayoutState.mInfinite) {
            if (layoutState.mLayoutDirection == 1) {
                n2 = Integer.MAX_VALUE;
            }
            else {
                n2 = Integer.MIN_VALUE;
            }
        }
        else if (layoutState.mLayoutDirection == 1) {
            n2 = layoutState.mEndLine + layoutState.mAvailable;
        }
        else {
            n2 = layoutState.mStartLine - layoutState.mAvailable;
        }
        this.updateAllRemainingSpans(layoutState.mLayoutDirection, n2);
        int n3;
        if (this.mShouldReverseLayout) {
            n3 = this.mPrimaryOrientation.getEndAfterPadding();
        }
        else {
            n3 = this.mPrimaryOrientation.getStartAfterPadding();
        }
        boolean b = false;
        while (layoutState.hasMore(state) && (this.mLayoutState.mInfinite || !this.mRemainingSpans.isEmpty())) {
            final View next = layoutState.next(recycler);
            final LayoutParams layoutParams = (LayoutParams)next.getLayoutParams();
            final int viewLayoutPosition = ((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition();
            final int span = this.mLazySpanLookup.getSpan(viewLayoutPosition);
            int n4;
            if (span == -1) {
                n4 = 1;
            }
            else {
                n4 = n;
            }
            Span nextSpan;
            if (n4 != 0) {
                if (layoutParams.mFullSpan) {
                    nextSpan = this.mSpans[n];
                }
                else {
                    nextSpan = this.getNextSpan(layoutState);
                }
                this.mLazySpanLookup.setSpan(viewLayoutPosition, nextSpan);
            }
            else {
                nextSpan = this.mSpans[span];
            }
            layoutParams.mSpan = nextSpan;
            if (layoutState.mLayoutDirection == 1) {
                ((RecyclerView.LayoutManager)this).addView(next);
            }
            else {
                ((RecyclerView.LayoutManager)this).addView(next, n);
            }
            this.measureChildWithDecorationsAndMargin(next, layoutParams, (boolean)(n != 0));
            int n6;
            int n7;
            if (layoutState.mLayoutDirection == 1) {
                int n5;
                if (layoutParams.mFullSpan) {
                    n5 = this.getMaxEnd(n3);
                }
                else {
                    n5 = nextSpan.getEndLine(n3);
                }
                final int decoratedMeasurement = this.mPrimaryOrientation.getDecoratedMeasurement(next);
                if (n4 != 0 && layoutParams.mFullSpan) {
                    final FullSpanItem fullSpanItemFromEnd = this.createFullSpanItemFromEnd(n5);
                    fullSpanItemFromEnd.mGapDir = -1;
                    fullSpanItemFromEnd.mPosition = viewLayoutPosition;
                    this.mLazySpanLookup.addFullSpanItem(fullSpanItemFromEnd);
                }
                n6 = decoratedMeasurement + n5;
                n7 = n5;
            }
            else {
                int n8;
                if (layoutParams.mFullSpan) {
                    n8 = this.getMinStart(n3);
                }
                else {
                    n8 = nextSpan.getStartLine(n3);
                }
                n7 = n8 - this.mPrimaryOrientation.getDecoratedMeasurement(next);
                if (n4 != 0 && layoutParams.mFullSpan) {
                    final FullSpanItem fullSpanItemFromStart = this.createFullSpanItemFromStart(n8);
                    fullSpanItemFromStart.mGapDir = 1;
                    fullSpanItemFromStart.mPosition = viewLayoutPosition;
                    this.mLazySpanLookup.addFullSpanItem(fullSpanItemFromStart);
                }
                n6 = n8;
            }
            if (layoutParams.mFullSpan && layoutState.mItemDirection == -1) {
                if (n4 != 0) {
                    this.mLaidOutInvalidFullSpan = true;
                }
                else {
                    boolean b2;
                    if (layoutState.mLayoutDirection == 1) {
                        b2 = this.areAllEndsEqual();
                    }
                    else {
                        b2 = this.areAllStartsEqual();
                    }
                    if (b2 ^ true) {
                        final FullSpanItem fullSpanItem = this.mLazySpanLookup.getFullSpanItem(viewLayoutPosition);
                        if (fullSpanItem != null) {
                            fullSpanItem.mHasUnwantedGapAfter = true;
                        }
                        this.mLaidOutInvalidFullSpan = true;
                    }
                }
            }
            this.attachViewToSpans(next, layoutParams, layoutState);
            int n10;
            int n11;
            if (this.isLayoutRTL() && this.mOrientation == 1) {
                int endAfterPadding;
                if (layoutParams.mFullSpan) {
                    endAfterPadding = this.mSecondaryOrientation.getEndAfterPadding();
                }
                else {
                    endAfterPadding = this.mSecondaryOrientation.getEndAfterPadding() - (this.mSpanCount - 1 - nextSpan.mIndex) * this.mSizePerSpan;
                }
                final int decoratedMeasurement2 = this.mSecondaryOrientation.getDecoratedMeasurement(next);
                final int n9 = endAfterPadding;
                n10 = endAfterPadding - decoratedMeasurement2;
                n11 = n9;
            }
            else {
                int startAfterPadding;
                if (layoutParams.mFullSpan) {
                    startAfterPadding = this.mSecondaryOrientation.getStartAfterPadding();
                }
                else {
                    startAfterPadding = nextSpan.mIndex * this.mSizePerSpan + this.mSecondaryOrientation.getStartAfterPadding();
                }
                final int decoratedMeasurement3 = this.mSecondaryOrientation.getDecoratedMeasurement(next);
                final int n12 = startAfterPadding;
                n11 = decoratedMeasurement3 + startAfterPadding;
                n10 = n12;
            }
            if (this.mOrientation == 1) {
                ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(next, n10, n7, n11, n6);
            }
            else {
                ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(next, n7, n10, n6, n11);
            }
            if (layoutParams.mFullSpan) {
                this.updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, n2);
            }
            else {
                this.updateRemainingSpans(nextSpan, this.mLayoutState.mLayoutDirection, n2);
            }
            this.recycle(recycler, this.mLayoutState);
            if (this.mLayoutState.mStopInFocusable && next.hasFocusable()) {
                if (layoutParams.mFullSpan) {
                    this.mRemainingSpans.clear();
                }
                else {
                    this.mRemainingSpans.set(nextSpan.mIndex, false);
                }
            }
            n = 0;
            b = true;
        }
        if (!b) {
            this.recycle(recycler, this.mLayoutState);
        }
        int b3;
        if (this.mLayoutState.mLayoutDirection == -1) {
            b3 = this.mPrimaryOrientation.getStartAfterPadding() - this.getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
        }
        else {
            b3 = this.getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding();
        }
        int min;
        if (b3 > 0) {
            min = Math.min(layoutState.mAvailable, b3);
        }
        else {
            min = n;
        }
        return min;
    }
    
    private int findFirstReferenceChildPosition(final int n) {
        for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i) {
            final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(i));
            if (position >= 0 && position < n) {
                return position;
            }
        }
        return 0;
    }
    
    private int findLastReferenceChildPosition(final int n) {
        for (int i = ((RecyclerView.LayoutManager)this).getChildCount() - 1; i >= 0; --i) {
            final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(i));
            if (position >= 0 && position < n) {
                return position;
            }
        }
        return 0;
    }
    
    private void fixEndGap(final Recycler recycler, final State state, final boolean b) {
        final int maxEnd = this.getMaxEnd(Integer.MIN_VALUE);
        if (maxEnd == Integer.MIN_VALUE) {
            return;
        }
        final int n = this.mPrimaryOrientation.getEndAfterPadding() - maxEnd;
        if (n > 0) {
            final int n2 = n - -this.scrollBy(-n, recycler, state);
            if (b && n2 > 0) {
                this.mPrimaryOrientation.offsetChildren(n2);
            }
        }
    }
    
    private void fixStartGap(final Recycler recycler, final State state, final boolean b) {
        final int minStart = this.getMinStart(Integer.MAX_VALUE);
        if (minStart == Integer.MAX_VALUE) {
            return;
        }
        final int n = minStart - this.mPrimaryOrientation.getStartAfterPadding();
        if (n > 0) {
            final int n2 = n - this.scrollBy(n, recycler, state);
            if (b && n2 > 0) {
                this.mPrimaryOrientation.offsetChildren(-n2);
            }
        }
    }
    
    private int getMaxEnd(final int n) {
        int endLine = this.mSpans[0].getEndLine(n);
        int n2;
        for (int i = 1; i < this.mSpanCount; ++i, endLine = n2) {
            final int endLine2 = this.mSpans[i].getEndLine(n);
            if (endLine2 > (n2 = endLine)) {
                n2 = endLine2;
            }
        }
        return endLine;
    }
    
    private int getMaxStart(final int n) {
        int startLine = this.mSpans[0].getStartLine(n);
        int n2;
        for (int i = 1; i < this.mSpanCount; ++i, startLine = n2) {
            final int startLine2 = this.mSpans[i].getStartLine(n);
            if (startLine2 > (n2 = startLine)) {
                n2 = startLine2;
            }
        }
        return startLine;
    }
    
    private int getMinEnd(final int n) {
        int endLine = this.mSpans[0].getEndLine(n);
        int n2;
        for (int i = 1; i < this.mSpanCount; ++i, endLine = n2) {
            final int endLine2 = this.mSpans[i].getEndLine(n);
            if (endLine2 < (n2 = endLine)) {
                n2 = endLine2;
            }
        }
        return endLine;
    }
    
    private int getMinStart(final int n) {
        int startLine = this.mSpans[0].getStartLine(n);
        int n2;
        for (int i = 1; i < this.mSpanCount; ++i, startLine = n2) {
            final int startLine2 = this.mSpans[i].getStartLine(n);
            if (startLine2 < (n2 = startLine)) {
                n2 = startLine2;
            }
        }
        return startLine;
    }
    
    private Span getNextSpan(final LayoutState layoutState) {
        final boolean preferLastSpan = this.preferLastSpan(layoutState.mLayoutDirection);
        int mSpanCount = -1;
        int i;
        int n;
        if (preferLastSpan) {
            i = this.mSpanCount - 1;
            n = -1;
        }
        else {
            i = 0;
            mSpanCount = this.mSpanCount;
            n = 1;
        }
        final int mLayoutDirection = layoutState.mLayoutDirection;
        final Span span = null;
        Span span2 = null;
        if (mLayoutDirection == 1) {
            int n2 = Integer.MAX_VALUE;
            final int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
            while (i != mSpanCount) {
                final Span span3 = this.mSpans[i];
                final int endLine = span3.getEndLine(startAfterPadding);
                int n3;
                if (endLine < (n3 = n2)) {
                    span2 = span3;
                    n3 = endLine;
                }
                i += n;
                n2 = n3;
            }
            return span2;
        }
        int n4 = Integer.MIN_VALUE;
        final int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        Span span4 = span;
        while (i != mSpanCount) {
            final Span span5 = this.mSpans[i];
            final int startLine = span5.getStartLine(endAfterPadding);
            int n5;
            if (startLine > (n5 = n4)) {
                span4 = span5;
                n5 = startLine;
            }
            i += n;
            n4 = n5;
        }
        return span4;
    }
    
    private void handleUpdate(int n, final int n2, final int n3) {
        int n4;
        if (this.mShouldReverseLayout) {
            n4 = this.getLastChildPosition();
        }
        else {
            n4 = this.getFirstChildPosition();
        }
        int n5 = 0;
        int n6 = 0;
        Label_0060: {
            if (n3 == 8) {
                if (n >= n2) {
                    n5 = n + 1;
                    n6 = n2;
                    break Label_0060;
                }
                n5 = n2 + 1;
            }
            else {
                n5 = n + n2;
            }
            n6 = n;
        }
        this.mLazySpanLookup.invalidateAfter(n6);
        if (n3 != 1) {
            if (n3 != 2) {
                if (n3 == 8) {
                    this.mLazySpanLookup.offsetForRemoval(n, 1);
                    this.mLazySpanLookup.offsetForAddition(n2, 1);
                }
            }
            else {
                this.mLazySpanLookup.offsetForRemoval(n, n2);
            }
        }
        else {
            this.mLazySpanLookup.offsetForAddition(n, n2);
        }
        if (n5 <= n4) {
            return;
        }
        if (this.mShouldReverseLayout) {
            n = this.getFirstChildPosition();
        }
        else {
            n = this.getLastChildPosition();
        }
        if (n6 <= n) {
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    private void measureChildWithDecorationsAndMargin(final View view, int updateSpecWithExtra, int updateSpecWithExtra2, final boolean b) {
        ((RecyclerView.LayoutManager)this).calculateItemDecorationsForChild(view, this.mTmpRect);
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int leftMargin = layoutParams.leftMargin;
        final Rect mTmpRect = this.mTmpRect;
        updateSpecWithExtra = this.updateSpecWithExtra(updateSpecWithExtra, leftMargin + mTmpRect.left, layoutParams.rightMargin + mTmpRect.right);
        final int topMargin = layoutParams.topMargin;
        final Rect mTmpRect2 = this.mTmpRect;
        updateSpecWithExtra2 = this.updateSpecWithExtra(updateSpecWithExtra2, topMargin + mTmpRect2.top, layoutParams.bottomMargin + mTmpRect2.bottom);
        boolean b2;
        if (b) {
            b2 = ((RecyclerView.LayoutManager)this).shouldReMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams);
        }
        else {
            b2 = ((RecyclerView.LayoutManager)this).shouldMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams);
        }
        if (b2) {
            view.measure(updateSpecWithExtra, updateSpecWithExtra2);
        }
    }
    
    private void measureChildWithDecorationsAndMargin(final View view, final LayoutParams layoutParams, final boolean b) {
        if (layoutParams.mFullSpan) {
            if (this.mOrientation == 1) {
                this.measureChildWithDecorationsAndMargin(view, this.mFullSizeSpec, RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getHeight(), ((RecyclerView.LayoutManager)this).getHeightMode(), ((RecyclerView.LayoutManager)this).getPaddingTop() + ((RecyclerView.LayoutManager)this).getPaddingBottom(), layoutParams.height, true), b);
            }
            else {
                this.measureChildWithDecorationsAndMargin(view, RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getWidth(), ((RecyclerView.LayoutManager)this).getWidthMode(), ((RecyclerView.LayoutManager)this).getPaddingLeft() + ((RecyclerView.LayoutManager)this).getPaddingRight(), layoutParams.width, true), this.mFullSizeSpec, b);
            }
        }
        else if (this.mOrientation == 1) {
            this.measureChildWithDecorationsAndMargin(view, RecyclerView.LayoutManager.getChildMeasureSpec(this.mSizePerSpan, ((RecyclerView.LayoutManager)this).getWidthMode(), 0, layoutParams.width, false), RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getHeight(), ((RecyclerView.LayoutManager)this).getHeightMode(), ((RecyclerView.LayoutManager)this).getPaddingTop() + ((RecyclerView.LayoutManager)this).getPaddingBottom(), layoutParams.height, true), b);
        }
        else {
            this.measureChildWithDecorationsAndMargin(view, RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getWidth(), ((RecyclerView.LayoutManager)this).getWidthMode(), ((RecyclerView.LayoutManager)this).getPaddingLeft() + ((RecyclerView.LayoutManager)this).getPaddingRight(), layoutParams.width, true), RecyclerView.LayoutManager.getChildMeasureSpec(this.mSizePerSpan, ((RecyclerView.LayoutManager)this).getHeightMode(), 0, layoutParams.height, false), b);
        }
    }
    
    private void onLayoutChildren(final Recycler recycler, final State state, final boolean b) {
        final AnchorInfo mAnchorInfo = this.mAnchorInfo;
        if ((this.mPendingSavedState != null || this.mPendingScrollPosition != -1) && state.getItemCount() == 0) {
            ((RecyclerView.LayoutManager)this).removeAndRecycleAllViews(recycler);
            mAnchorInfo.reset();
            return;
        }
        final boolean mValid = mAnchorInfo.mValid;
        final int n = 1;
        final boolean b2 = !mValid || this.mPendingScrollPosition != -1 || this.mPendingSavedState != null;
        if (b2) {
            mAnchorInfo.reset();
            if (this.mPendingSavedState != null) {
                this.applyPendingSavedState(mAnchorInfo);
            }
            else {
                this.resolveShouldLayoutReverse();
                mAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
            }
            this.updateAnchorInfoForLayout(state, mAnchorInfo);
            mAnchorInfo.mValid = true;
        }
        if (this.mPendingSavedState == null && this.mPendingScrollPosition == -1 && (mAnchorInfo.mLayoutFromEnd != this.mLastLayoutFromEnd || this.isLayoutRTL() != this.mLastLayoutRTL)) {
            this.mLazySpanLookup.clear();
            mAnchorInfo.mInvalidateOffsets = true;
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            final SavedState mPendingSavedState = this.mPendingSavedState;
            if (mPendingSavedState == null || mPendingSavedState.mSpanOffsetsSize < 1) {
                if (mAnchorInfo.mInvalidateOffsets) {
                    for (int i = 0; i < this.mSpanCount; ++i) {
                        this.mSpans[i].clear();
                        final int mOffset = mAnchorInfo.mOffset;
                        if (mOffset != Integer.MIN_VALUE) {
                            this.mSpans[i].setLine(mOffset);
                        }
                    }
                }
                else if (!b2 && this.mAnchorInfo.mSpanReferenceLines != null) {
                    for (int j = 0; j < this.mSpanCount; ++j) {
                        final Span span = this.mSpans[j];
                        span.clear();
                        span.setLine(this.mAnchorInfo.mSpanReferenceLines[j]);
                    }
                }
                else {
                    for (int k = 0; k < this.mSpanCount; ++k) {
                        this.mSpans[k].cacheReferenceLineAndClear(this.mShouldReverseLayout, mAnchorInfo.mOffset);
                    }
                    this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
                }
            }
        }
        ((RecyclerView.LayoutManager)this).detachAndScrapAttachedViews(recycler);
        this.mLayoutState.mRecycle = false;
        this.mLaidOutInvalidFullSpan = false;
        this.updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
        this.updateLayoutState(mAnchorInfo.mPosition, state);
        if (mAnchorInfo.mLayoutFromEnd) {
            this.setLayoutStateDirection(-1);
            this.fill(recycler, this.mLayoutState, state);
            this.setLayoutStateDirection(1);
            final LayoutState mLayoutState = this.mLayoutState;
            mLayoutState.mCurrentPosition = mAnchorInfo.mPosition + mLayoutState.mItemDirection;
            this.fill(recycler, mLayoutState, state);
        }
        else {
            this.setLayoutStateDirection(1);
            this.fill(recycler, this.mLayoutState, state);
            this.setLayoutStateDirection(-1);
            final LayoutState mLayoutState2 = this.mLayoutState;
            mLayoutState2.mCurrentPosition = mAnchorInfo.mPosition + mLayoutState2.mItemDirection;
            this.fill(recycler, mLayoutState2, state);
        }
        this.repositionToWrapContentIfNecessary();
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            if (this.mShouldReverseLayout) {
                this.fixEndGap(recycler, state, true);
                this.fixStartGap(recycler, state, false);
            }
            else {
                this.fixStartGap(recycler, state, true);
                this.fixEndGap(recycler, state, false);
            }
        }
        int n2 = 0;
        Label_0668: {
            if (b && !state.isPreLayout() && (this.mGapStrategy != 0 && ((RecyclerView.LayoutManager)this).getChildCount() > 0 && (this.mLaidOutInvalidFullSpan || this.hasGapsToFix() != null))) {
                ((RecyclerView.LayoutManager)this).removeCallbacks(this.mCheckForGapsRunnable);
                if (this.checkForGaps()) {
                    n2 = n;
                    break Label_0668;
                }
            }
            n2 = 0;
        }
        if (state.isPreLayout()) {
            this.mAnchorInfo.reset();
        }
        this.mLastLayoutFromEnd = mAnchorInfo.mLayoutFromEnd;
        this.mLastLayoutRTL = this.isLayoutRTL();
        if (n2 != 0) {
            this.mAnchorInfo.reset();
            this.onLayoutChildren(recycler, state, false);
        }
    }
    
    private boolean preferLastSpan(final int n) {
        final int mOrientation = this.mOrientation;
        final boolean b = true;
        final boolean b2 = true;
        if (mOrientation == 0) {
            return n == -1 != this.mShouldReverseLayout && b2;
        }
        return n == -1 == this.mShouldReverseLayout == this.isLayoutRTL() && b;
    }
    
    private void prependViewToAllSpans(final View view) {
        for (int i = this.mSpanCount - 1; i >= 0; --i) {
            this.mSpans[i].prependToSpan(view);
        }
    }
    
    private void recycle(final Recycler recycler, final LayoutState layoutState) {
        if (layoutState.mRecycle) {
            if (!layoutState.mInfinite) {
                if (layoutState.mAvailable == 0) {
                    if (layoutState.mLayoutDirection == -1) {
                        this.recycleFromEnd(recycler, layoutState.mEndLine);
                    }
                    else {
                        this.recycleFromStart(recycler, layoutState.mStartLine);
                    }
                }
                else if (layoutState.mLayoutDirection == -1) {
                    final int mStartLine = layoutState.mStartLine;
                    final int a = mStartLine - this.getMaxStart(mStartLine);
                    int mEndLine;
                    if (a < 0) {
                        mEndLine = layoutState.mEndLine;
                    }
                    else {
                        mEndLine = layoutState.mEndLine - Math.min(a, layoutState.mAvailable);
                    }
                    this.recycleFromEnd(recycler, mEndLine);
                }
                else {
                    final int a2 = this.getMinEnd(layoutState.mEndLine) - layoutState.mEndLine;
                    int mStartLine2;
                    if (a2 < 0) {
                        mStartLine2 = layoutState.mStartLine;
                    }
                    else {
                        mStartLine2 = Math.min(a2, layoutState.mAvailable) + layoutState.mStartLine;
                    }
                    this.recycleFromStart(recycler, mStartLine2);
                }
            }
        }
    }
    
    private void recycleFromEnd(final Recycler recycler, final int n) {
        for (int i = ((RecyclerView.LayoutManager)this).getChildCount() - 1; i >= 0; --i) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            if (this.mPrimaryOrientation.getDecoratedStart(child) < n || this.mPrimaryOrientation.getTransformedStartWithDecoration(child) < n) {
                break;
            }
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (layoutParams.mFullSpan) {
                final int n2 = 0;
                int n3 = 0;
                int j;
                while (true) {
                    j = n2;
                    if (n3 >= this.mSpanCount) {
                        break;
                    }
                    if (this.mSpans[n3].mViews.size() == 1) {
                        return;
                    }
                    ++n3;
                }
                while (j < this.mSpanCount) {
                    this.mSpans[j].popEnd();
                    ++j;
                }
            }
            else {
                if (layoutParams.mSpan.mViews.size() == 1) {
                    return;
                }
                layoutParams.mSpan.popEnd();
            }
            ((RecyclerView.LayoutManager)this).removeAndRecycleView(child, recycler);
        }
    }
    
    private void recycleFromStart(final Recycler recycler, final int n) {
        while (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            final int n2 = 0;
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(0);
            if (this.mPrimaryOrientation.getDecoratedEnd(child) > n || this.mPrimaryOrientation.getTransformedEndWithDecoration(child) > n) {
                break;
            }
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (layoutParams.mFullSpan) {
                int n3 = 0;
                int i;
                while (true) {
                    i = n2;
                    if (n3 >= this.mSpanCount) {
                        break;
                    }
                    if (this.mSpans[n3].mViews.size() == 1) {
                        return;
                    }
                    ++n3;
                }
                while (i < this.mSpanCount) {
                    this.mSpans[i].popStart();
                    ++i;
                }
            }
            else {
                if (layoutParams.mSpan.mViews.size() == 1) {
                    return;
                }
                layoutParams.mSpan.popStart();
            }
            ((RecyclerView.LayoutManager)this).removeAndRecycleView(child, recycler);
        }
    }
    
    private void repositionToWrapContentIfNecessary() {
        if (this.mSecondaryOrientation.getMode() == 1073741824) {
            return;
        }
        float max = 0.0f;
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        final int n = 0;
        for (int i = 0; i < childCount; ++i) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final float n2 = (float)this.mSecondaryOrientation.getDecoratedMeasurement(child);
            if (n2 >= max) {
                float b = n2;
                if (((LayoutParams)child.getLayoutParams()).isFullSpan()) {
                    b = n2 * 1.0f / this.mSpanCount;
                }
                max = Math.max(max, b);
            }
        }
        final int mSizePerSpan = this.mSizePerSpan;
        int a = Math.round(max * this.mSpanCount);
        if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
            a = Math.min(a, this.mSecondaryOrientation.getTotalSpace());
        }
        this.updateMeasureSpecs(a);
        int j = n;
        if (this.mSizePerSpan == mSizePerSpan) {
            return;
        }
        while (j < childCount) {
            final View child2 = ((RecyclerView.LayoutManager)this).getChildAt(j);
            final LayoutParams layoutParams = (LayoutParams)child2.getLayoutParams();
            if (!layoutParams.mFullSpan) {
                if (this.isLayoutRTL() && this.mOrientation == 1) {
                    final int mSpanCount = this.mSpanCount;
                    final int mIndex = layoutParams.mSpan.mIndex;
                    child2.offsetLeftAndRight(-(mSpanCount - 1 - mIndex) * this.mSizePerSpan - -(mSpanCount - 1 - mIndex) * mSizePerSpan);
                }
                else {
                    final int mIndex2 = layoutParams.mSpan.mIndex;
                    final int n3 = this.mSizePerSpan * mIndex2;
                    final int n4 = mIndex2 * mSizePerSpan;
                    if (this.mOrientation == 1) {
                        child2.offsetLeftAndRight(n3 - n4);
                    }
                    else {
                        child2.offsetTopAndBottom(n3 - n4);
                    }
                }
            }
            ++j;
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
    
    private void setLayoutStateDirection(int n) {
        final LayoutState mLayoutState = this.mLayoutState;
        mLayoutState.mLayoutDirection = n;
        final boolean mShouldReverseLayout = this.mShouldReverseLayout;
        final int n2 = 1;
        if (mShouldReverseLayout == (n == -1)) {
            n = n2;
        }
        else {
            n = -1;
        }
        mLayoutState.mItemDirection = n;
    }
    
    private void updateAllRemainingSpans(final int n, final int n2) {
        for (int i = 0; i < this.mSpanCount; ++i) {
            if (!this.mSpans[i].mViews.isEmpty()) {
                this.updateRemainingSpans(this.mSpans[i], n, n2);
            }
        }
    }
    
    private boolean updateAnchorFromChildren(final State state, final AnchorInfo anchorInfo) {
        int mPosition;
        if (this.mLastLayoutFromEnd) {
            mPosition = this.findLastReferenceChildPosition(state.getItemCount());
        }
        else {
            mPosition = this.findFirstReferenceChildPosition(state.getItemCount());
        }
        anchorInfo.mPosition = mPosition;
        anchorInfo.mOffset = Integer.MIN_VALUE;
        return true;
    }
    
    private void updateLayoutState(int totalSpace, final State state) {
        final LayoutState mLayoutState = this.mLayoutState;
        final boolean b = false;
        mLayoutState.mAvailable = 0;
        mLayoutState.mCurrentPosition = totalSpace;
        int totalSpace2 = 0;
        Label_0098: {
            if (((RecyclerView.LayoutManager)this).isSmoothScrolling()) {
                final int targetScrollPosition = state.getTargetScrollPosition();
                if (targetScrollPosition != -1) {
                    if (this.mShouldReverseLayout == targetScrollPosition < totalSpace) {
                        totalSpace = this.mPrimaryOrientation.getTotalSpace();
                        totalSpace2 = 0;
                        break Label_0098;
                    }
                    totalSpace2 = this.mPrimaryOrientation.getTotalSpace();
                    totalSpace = 0;
                    break Label_0098;
                }
            }
            totalSpace = (totalSpace2 = 0);
        }
        if (((RecyclerView.LayoutManager)this).getClipToPadding()) {
            this.mLayoutState.mStartLine = this.mPrimaryOrientation.getStartAfterPadding() - totalSpace2;
            this.mLayoutState.mEndLine = this.mPrimaryOrientation.getEndAfterPadding() + totalSpace;
        }
        else {
            this.mLayoutState.mEndLine = this.mPrimaryOrientation.getEnd() + totalSpace;
            this.mLayoutState.mStartLine = -totalSpace2;
        }
        final LayoutState mLayoutState2 = this.mLayoutState;
        mLayoutState2.mStopInFocusable = false;
        mLayoutState2.mRecycle = true;
        boolean mInfinite = b;
        if (this.mPrimaryOrientation.getMode() == 0) {
            mInfinite = b;
            if (this.mPrimaryOrientation.getEnd() == 0) {
                mInfinite = true;
            }
        }
        mLayoutState2.mInfinite = mInfinite;
    }
    
    private void updateRemainingSpans(final Span span, final int n, final int n2) {
        final int deletedSize = span.getDeletedSize();
        if (n == -1) {
            if (span.getStartLine() + deletedSize <= n2) {
                this.mRemainingSpans.set(span.mIndex, false);
            }
        }
        else if (span.getEndLine() - deletedSize >= n2) {
            this.mRemainingSpans.set(span.mIndex, false);
        }
    }
    
    private int updateSpecWithExtra(final int n, final int n2, final int n3) {
        if (n2 == 0 && n3 == 0) {
            return n;
        }
        final int mode = View$MeasureSpec.getMode(n);
        if (mode != Integer.MIN_VALUE && mode != 1073741824) {
            return n;
        }
        return View$MeasureSpec.makeMeasureSpec(Math.max(0, View$MeasureSpec.getSize(n) - n2 - n3), mode);
    }
    
    boolean areAllEndsEqual() {
        final int endLine = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; ++i) {
            if (this.mSpans[i].getEndLine(Integer.MIN_VALUE) != endLine) {
                return false;
            }
        }
        return true;
    }
    
    boolean areAllStartsEqual() {
        final int startLine = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; ++i) {
            if (this.mSpans[i].getStartLine(Integer.MIN_VALUE) != startLine) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void assertNotInLayoutOrScroll(final String s) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(s);
        }
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
    
    boolean checkForGaps() {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0 || this.mGapStrategy == 0 || !((RecyclerView.LayoutManager)this).isAttachedToWindow()) {
            return false;
        }
        int n;
        int n2;
        if (this.mShouldReverseLayout) {
            n = this.getLastChildPosition();
            n2 = this.getFirstChildPosition();
        }
        else {
            n = this.getFirstChildPosition();
            n2 = this.getLastChildPosition();
        }
        if (n == 0 && this.hasGapsToFix() != null) {
            this.mLazySpanLookup.clear();
            ((RecyclerView.LayoutManager)this).requestSimpleAnimationsInNextLayout();
            ((RecyclerView.LayoutManager)this).requestLayout();
            return true;
        }
        if (!this.mLaidOutInvalidFullSpan) {
            return false;
        }
        int n3;
        if (this.mShouldReverseLayout) {
            n3 = -1;
        }
        else {
            n3 = 1;
        }
        final LazySpanLookup mLazySpanLookup = this.mLazySpanLookup;
        ++n2;
        final FullSpanItem firstFullSpanItemInRange = mLazySpanLookup.getFirstFullSpanItemInRange(n, n2, n3, true);
        if (firstFullSpanItemInRange == null) {
            this.mLaidOutInvalidFullSpan = false;
            this.mLazySpanLookup.forceInvalidateAfter(n2);
            return false;
        }
        final FullSpanItem firstFullSpanItemInRange2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(n, firstFullSpanItemInRange.mPosition, n3 * -1, true);
        if (firstFullSpanItemInRange2 == null) {
            this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange.mPosition);
        }
        else {
            this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange2.mPosition + 1);
        }
        ((RecyclerView.LayoutManager)this).requestSimpleAnimationsInNextLayout();
        ((RecyclerView.LayoutManager)this).requestLayout();
        return true;
    }
    
    @Override
    public boolean checkLayoutParams(final RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }
    
    @Override
    public void collectAdjacentPrefetchPositions(int toIndex, int i, final State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        if (this.mOrientation != 0) {
            toIndex = i;
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            if (toIndex != 0) {
                this.prepareLayoutStateForDelta(toIndex, state);
                final int[] mPrefetchDistances = this.mPrefetchDistances;
                if (mPrefetchDistances == null || mPrefetchDistances.length < this.mSpanCount) {
                    this.mPrefetchDistances = new int[this.mSpanCount];
                }
                final int n = 0;
                LayoutState mLayoutState;
                int n2;
                int n3;
                int n4;
                int n5;
                for (i = (toIndex = 0); i < this.mSpanCount; ++i, toIndex = n5) {
                    mLayoutState = this.mLayoutState;
                    if (mLayoutState.mItemDirection == -1) {
                        n2 = mLayoutState.mStartLine;
                        n3 = this.mSpans[i].getStartLine(n2);
                    }
                    else {
                        n2 = this.mSpans[i].getEndLine(mLayoutState.mEndLine);
                        n3 = this.mLayoutState.mEndLine;
                    }
                    n4 = n2 - n3;
                    n5 = toIndex;
                    if (n4 >= 0) {
                        this.mPrefetchDistances[toIndex] = n4;
                        n5 = toIndex + 1;
                    }
                }
                Arrays.sort(this.mPrefetchDistances, 0, toIndex);
                LayoutState mLayoutState2;
                for (i = n; i < toIndex && this.mLayoutState.hasMore(state); ++i) {
                    layoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[i]);
                    mLayoutState2 = this.mLayoutState;
                    mLayoutState2.mCurrentPosition += mLayoutState2.mItemDirection;
                }
            }
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
    public PointF computeScrollVectorForPosition(int calculateScrollDirectionForPosition) {
        calculateScrollDirectionForPosition = this.calculateScrollDirectionForPosition(calculateScrollDirectionForPosition);
        final PointF pointF = new PointF();
        if (calculateScrollDirectionForPosition == 0) {
            return null;
        }
        if (this.mOrientation == 0) {
            pointF.x = (float)calculateScrollDirectionForPosition;
            pointF.y = 0.0f;
        }
        else {
            pointF.x = 0.0f;
            pointF.y = (float)calculateScrollDirectionForPosition;
        }
        return pointF;
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
    
    View findFirstVisibleItemClosestToEnd(final boolean b) {
        final int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        final int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        int i = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        View view = null;
        while (i >= 0) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(child);
            final int decoratedEnd = this.mPrimaryOrientation.getDecoratedEnd(child);
            View view2 = view;
            if (decoratedEnd > startAfterPadding) {
                if (decoratedStart >= endAfterPadding) {
                    view2 = view;
                }
                else {
                    if (decoratedEnd <= endAfterPadding || !b) {
                        return child;
                    }
                    if ((view2 = view) == null) {
                        view2 = child;
                    }
                }
            }
            --i;
            view = view2;
        }
        return view;
    }
    
    View findFirstVisibleItemClosestToStart(final boolean b) {
        final int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        final int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        View view = null;
        View view2;
        for (int i = 0; i < childCount; ++i, view = view2) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(child);
            view2 = view;
            if (this.mPrimaryOrientation.getDecoratedEnd(child) > startAfterPadding) {
                if (decoratedStart >= endAfterPadding) {
                    view2 = view;
                }
                else {
                    if (decoratedStart >= startAfterPadding || !b) {
                        return child;
                    }
                    if ((view2 = view) == null) {
                        view2 = child;
                    }
                }
            }
        }
        return view;
    }
    
    int findFirstVisibleItemPositionInt() {
        View view;
        if (this.mShouldReverseLayout) {
            view = this.findFirstVisibleItemClosestToEnd(true);
        }
        else {
            view = this.findFirstVisibleItemClosestToStart(true);
        }
        int position;
        if (view == null) {
            position = -1;
        }
        else {
            position = ((RecyclerView.LayoutManager)this).getPosition(view);
        }
        return position;
    }
    
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -1);
        }
        return new LayoutParams(-1, -2);
    }
    
    @Override
    public RecyclerView.LayoutParams generateLayoutParams(final Context context, final AttributeSet set) {
        return new LayoutParams(context, set);
    }
    
    @Override
    public RecyclerView.LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
            return new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        return new LayoutParams(viewGroup$LayoutParams);
    }
    
    int getFirstChildPosition() {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        int position = 0;
        if (childCount != 0) {
            position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
        }
        return position;
    }
    
    int getLastChildPosition() {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        int position;
        if (childCount == 0) {
            position = 0;
        }
        else {
            position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(childCount - 1));
        }
        return position;
    }
    
    View hasGapsToFix() {
        int n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        final BitSet set = new BitSet(this.mSpanCount);
        set.set(0, this.mSpanCount, true);
        final int mOrientation = this.mOrientation;
        int n2 = -1;
        int n3;
        if (mOrientation == 1 && this.isLayoutRTL()) {
            n3 = 1;
        }
        else {
            n3 = -1;
        }
        int n4;
        if (this.mShouldReverseLayout) {
            n4 = -1;
        }
        else {
            n4 = n + 1;
            n = 0;
        }
        int i = n;
        if (n < n4) {
            n2 = 1;
            i = n;
        }
        while (i != n4) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (set.get(layoutParams.mSpan.mIndex)) {
                if (this.checkSpanForGap(layoutParams.mSpan)) {
                    return child;
                }
                set.clear(layoutParams.mSpan.mIndex);
            }
            if (!layoutParams.mFullSpan) {
                final int n5 = i + n2;
                if (n5 != n4) {
                    final View child2 = ((RecyclerView.LayoutManager)this).getChildAt(n5);
                    boolean b = false;
                    Label_0277: {
                        Label_0275: {
                            if (this.mShouldReverseLayout) {
                                final int decoratedEnd = this.mPrimaryOrientation.getDecoratedEnd(child);
                                final int decoratedEnd2 = this.mPrimaryOrientation.getDecoratedEnd(child2);
                                if (decoratedEnd < decoratedEnd2) {
                                    return child;
                                }
                                if (decoratedEnd != decoratedEnd2) {
                                    break Label_0275;
                                }
                            }
                            else {
                                final int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(child);
                                final int decoratedStart2 = this.mPrimaryOrientation.getDecoratedStart(child2);
                                if (decoratedStart > decoratedStart2) {
                                    return child;
                                }
                                if (decoratedStart != decoratedStart2) {
                                    break Label_0275;
                                }
                            }
                            b = true;
                            break Label_0277;
                        }
                        b = false;
                    }
                    if (b && layoutParams.mSpan.mIndex - ((LayoutParams)child2.getLayoutParams()).mSpan.mIndex < 0 != n3 < 0) {
                        return child;
                    }
                }
            }
            i += n2;
        }
        return null;
    }
    
    public void invalidateSpanAssignments() {
        this.mLazySpanLookup.clear();
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public boolean isAutoMeasureEnabled() {
        return this.mGapStrategy != 0;
    }
    
    boolean isLayoutRTL() {
        final int layoutDirection = ((RecyclerView.LayoutManager)this).getLayoutDirection();
        boolean b = true;
        if (layoutDirection != 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void offsetChildrenHorizontal(final int n) {
        super.offsetChildrenHorizontal(n);
        for (int i = 0; i < this.mSpanCount; ++i) {
            this.mSpans[i].onOffset(n);
        }
    }
    
    @Override
    public void offsetChildrenVertical(final int n) {
        super.offsetChildrenVertical(n);
        for (int i = 0; i < this.mSpanCount; ++i) {
            this.mSpans[i].onOffset(n);
        }
    }
    
    @Override
    public void onAdapterChanged(final Adapter adapter, final Adapter adapter2) {
        this.mLazySpanLookup.clear();
        for (int i = 0; i < this.mSpanCount; ++i) {
            this.mSpans[i].clear();
        }
    }
    
    @Override
    public void onDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        ((RecyclerView.LayoutManager)this).removeCallbacks(this.mCheckForGapsRunnable);
        for (int i = 0; i < this.mSpanCount; ++i) {
            this.mSpans[i].clear();
        }
        recyclerView.requestLayout();
    }
    
    @Override
    public View onFocusSearchFailed(View containingItemView, int n, final Recycler recycler, final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return null;
        }
        containingItemView = ((RecyclerView.LayoutManager)this).findContainingItemView(containingItemView);
        if (containingItemView == null) {
            return null;
        }
        this.resolveShouldLayoutReverse();
        final int convertFocusDirectionToLayoutDirection = this.convertFocusDirectionToLayoutDirection(n);
        if (convertFocusDirectionToLayoutDirection == Integer.MIN_VALUE) {
            return null;
        }
        final LayoutParams layoutParams = (LayoutParams)containingItemView.getLayoutParams();
        final boolean mFullSpan = layoutParams.mFullSpan;
        final Span mSpan = layoutParams.mSpan;
        if (convertFocusDirectionToLayoutDirection == 1) {
            n = this.getLastChildPosition();
        }
        else {
            n = this.getFirstChildPosition();
        }
        this.updateLayoutState(n, state);
        this.setLayoutStateDirection(convertFocusDirectionToLayoutDirection);
        final LayoutState mLayoutState = this.mLayoutState;
        mLayoutState.mCurrentPosition = mLayoutState.mItemDirection + n;
        mLayoutState.mAvailable = (int)(this.mPrimaryOrientation.getTotalSpace() * 0.33333334f);
        final LayoutState mLayoutState2 = this.mLayoutState;
        mLayoutState2.mStopInFocusable = true;
        final int n2 = 0;
        mLayoutState2.mRecycle = false;
        this.fill(recycler, mLayoutState2, state);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        if (!mFullSpan) {
            final View focusableViewAfter = mSpan.getFocusableViewAfter(n, convertFocusDirectionToLayoutDirection);
            if (focusableViewAfter != null && focusableViewAfter != containingItemView) {
                return focusableViewAfter;
            }
        }
        if (this.preferLastSpan(convertFocusDirectionToLayoutDirection)) {
            for (int i = this.mSpanCount - 1; i >= 0; --i) {
                final View focusableViewAfter2 = this.mSpans[i].getFocusableViewAfter(n, convertFocusDirectionToLayoutDirection);
                if (focusableViewAfter2 != null && focusableViewAfter2 != containingItemView) {
                    return focusableViewAfter2;
                }
            }
        }
        else {
            for (int j = 0; j < this.mSpanCount; ++j) {
                final View focusableViewAfter3 = this.mSpans[j].getFocusableViewAfter(n, convertFocusDirectionToLayoutDirection);
                if (focusableViewAfter3 != null && focusableViewAfter3 != containingItemView) {
                    return focusableViewAfter3;
                }
            }
        }
        final boolean mReverseLayout = this.mReverseLayout;
        if (convertFocusDirectionToLayoutDirection == -1) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (((mReverseLayout ^ true) ? 1 : 0) == n) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (!mFullSpan) {
            int n3;
            if (n != 0) {
                n3 = mSpan.findFirstPartiallyVisibleItemPosition();
            }
            else {
                n3 = mSpan.findLastPartiallyVisibleItemPosition();
            }
            final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(n3);
            if (viewByPosition != null && viewByPosition != containingItemView) {
                return viewByPosition;
            }
        }
        int k = n2;
        if (this.preferLastSpan(convertFocusDirectionToLayoutDirection)) {
            for (int l = this.mSpanCount - 1; l >= 0; --l) {
                if (l != mSpan.mIndex) {
                    int n4;
                    if (n != 0) {
                        n4 = this.mSpans[l].findFirstPartiallyVisibleItemPosition();
                    }
                    else {
                        n4 = this.mSpans[l].findLastPartiallyVisibleItemPosition();
                    }
                    final View viewByPosition2 = ((RecyclerView.LayoutManager)this).findViewByPosition(n4);
                    if (viewByPosition2 != null && viewByPosition2 != containingItemView) {
                        return viewByPosition2;
                    }
                }
            }
        }
        else {
            while (k < this.mSpanCount) {
                int n5;
                if (n != 0) {
                    n5 = this.mSpans[k].findFirstPartiallyVisibleItemPosition();
                }
                else {
                    n5 = this.mSpans[k].findLastPartiallyVisibleItemPosition();
                }
                final View viewByPosition3 = ((RecyclerView.LayoutManager)this).findViewByPosition(n5);
                if (viewByPosition3 != null && viewByPosition3 != containingItemView) {
                    return viewByPosition3;
                }
                ++k;
            }
        }
        return null;
    }
    
    @Override
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            final View firstVisibleItemClosestToStart = this.findFirstVisibleItemClosestToStart(false);
            final View firstVisibleItemClosestToEnd = this.findFirstVisibleItemClosestToEnd(false);
            if (firstVisibleItemClosestToStart != null) {
                if (firstVisibleItemClosestToEnd != null) {
                    final int position = ((RecyclerView.LayoutManager)this).getPosition(firstVisibleItemClosestToStart);
                    final int position2 = ((RecyclerView.LayoutManager)this).getPosition(firstVisibleItemClosestToEnd);
                    if (position < position2) {
                        accessibilityEvent.setFromIndex(position);
                        accessibilityEvent.setToIndex(position2);
                    }
                    else {
                        accessibilityEvent.setFromIndex(position2);
                        accessibilityEvent.setToIndex(position);
                    }
                }
            }
        }
    }
    
    @Override
    public void onItemsAdded(final RecyclerView recyclerView, final int n, final int n2) {
        this.handleUpdate(n, n2, 1);
    }
    
    @Override
    public void onItemsChanged(final RecyclerView recyclerView) {
        this.mLazySpanLookup.clear();
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public void onItemsMoved(final RecyclerView recyclerView, final int n, final int n2, final int n3) {
        this.handleUpdate(n, n2, 8);
    }
    
    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, final int n, final int n2) {
        this.handleUpdate(n, n2, 2);
    }
    
    @Override
    public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2, final Object o) {
        this.handleUpdate(n, n2, 4);
    }
    
    @Override
    public void onLayoutChildren(final Recycler recycler, final State state) {
        this.onLayoutChildren(recycler, state, true);
    }
    
    @Override
    public void onLayoutCompleted(final State state) {
        super.onLayoutCompleted(state);
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo.reset();
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState mPendingSavedState = (SavedState)parcelable;
            this.mPendingSavedState = mPendingSavedState;
            if (this.mPendingScrollPosition != -1) {
                mPendingSavedState.invalidateAnchorPositionInfo();
                this.mPendingSavedState.invalidateSpanInfo();
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
        savedState.mReverseLayout = this.mReverseLayout;
        savedState.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
        savedState.mLastLayoutRTL = this.mLastLayoutRTL;
        final LazySpanLookup mLazySpanLookup = this.mLazySpanLookup;
        int i = 0;
        Label_0102: {
            if (mLazySpanLookup != null) {
                final int[] mData = mLazySpanLookup.mData;
                if (mData != null) {
                    savedState.mSpanLookup = mData;
                    savedState.mSpanLookupSize = mData.length;
                    savedState.mFullSpanItems = mLazySpanLookup.mFullSpanItems;
                    break Label_0102;
                }
            }
            savedState.mSpanLookupSize = 0;
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            int mAnchorPosition;
            if (this.mLastLayoutFromEnd) {
                mAnchorPosition = this.getLastChildPosition();
            }
            else {
                mAnchorPosition = this.getFirstChildPosition();
            }
            savedState.mAnchorPosition = mAnchorPosition;
            savedState.mVisibleAnchorPosition = this.findFirstVisibleItemPositionInt();
            final int mSpanCount = this.mSpanCount;
            savedState.mSpanOffsetsSize = mSpanCount;
            savedState.mSpanOffsets = new int[mSpanCount];
            while (i < this.mSpanCount) {
                int n2 = 0;
                Label_0256: {
                    int n;
                    int n3;
                    if (this.mLastLayoutFromEnd) {
                        n = this.mSpans[i].getEndLine(Integer.MIN_VALUE);
                        if ((n2 = n) == Integer.MIN_VALUE) {
                            break Label_0256;
                        }
                        n3 = this.mPrimaryOrientation.getEndAfterPadding();
                    }
                    else {
                        n = this.mSpans[i].getStartLine(Integer.MIN_VALUE);
                        if ((n2 = n) == Integer.MIN_VALUE) {
                            break Label_0256;
                        }
                        n3 = this.mPrimaryOrientation.getStartAfterPadding();
                    }
                    n2 = n - n3;
                }
                savedState.mSpanOffsets[i] = n2;
                ++i;
            }
        }
        else {
            savedState.mAnchorPosition = -1;
            savedState.mVisibleAnchorPosition = -1;
            savedState.mSpanOffsetsSize = 0;
        }
        return (Parcelable)savedState;
    }
    
    @Override
    public void onScrollStateChanged(final int n) {
        if (n == 0) {
            this.checkForGaps();
        }
    }
    
    void prepareLayoutStateForDelta(final int a, final State state) {
        int n;
        int layoutStateDirection;
        if (a > 0) {
            n = this.getLastChildPosition();
            layoutStateDirection = 1;
        }
        else {
            n = this.getFirstChildPosition();
            layoutStateDirection = -1;
        }
        this.mLayoutState.mRecycle = true;
        this.updateLayoutState(n, state);
        this.setLayoutStateDirection(layoutStateDirection);
        final LayoutState mLayoutState = this.mLayoutState;
        mLayoutState.mCurrentPosition = n + mLayoutState.mItemDirection;
        mLayoutState.mAvailable = Math.abs(a);
    }
    
    int scrollBy(int n, final Recycler recycler, final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0 && n != 0) {
            this.prepareLayoutStateForDelta(n, state);
            final int fill = this.fill(recycler, this.mLayoutState, state);
            if (this.mLayoutState.mAvailable >= fill) {
                if (n < 0) {
                    n = -fill;
                }
                else {
                    n = fill;
                }
            }
            this.mPrimaryOrientation.offsetChildren(-n);
            this.mLastLayoutFromEnd = this.mShouldReverseLayout;
            final LayoutState mLayoutState = this.mLayoutState;
            mLayoutState.mAvailable = 0;
            this.recycle(recycler, mLayoutState);
            return n;
        }
        return 0;
    }
    
    @Override
    public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
        return this.scrollBy(n, recycler, state);
    }
    
    @Override
    public void scrollToPosition(final int mPendingScrollPosition) {
        final SavedState mPendingSavedState = this.mPendingSavedState;
        if (mPendingSavedState != null && mPendingSavedState.mAnchorPosition != mPendingScrollPosition) {
            mPendingSavedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = mPendingScrollPosition;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
        return this.scrollBy(n, recycler, state);
    }
    
    @Override
    public void setMeasuredDimension(final Rect rect, int n, int n2) {
        final int n3 = ((RecyclerView.LayoutManager)this).getPaddingLeft() + ((RecyclerView.LayoutManager)this).getPaddingRight();
        final int n4 = ((RecyclerView.LayoutManager)this).getPaddingTop() + ((RecyclerView.LayoutManager)this).getPaddingBottom();
        if (this.mOrientation == 1) {
            n2 = RecyclerView.LayoutManager.chooseSize(n2, rect.height() + n4, ((RecyclerView.LayoutManager)this).getMinimumHeight());
            n = RecyclerView.LayoutManager.chooseSize(n, this.mSizePerSpan * this.mSpanCount + n3, ((RecyclerView.LayoutManager)this).getMinimumWidth());
        }
        else {
            n = RecyclerView.LayoutManager.chooseSize(n, rect.width() + n3, ((RecyclerView.LayoutManager)this).getMinimumWidth());
            n2 = RecyclerView.LayoutManager.chooseSize(n2, this.mSizePerSpan * this.mSpanCount + n4, ((RecyclerView.LayoutManager)this).getMinimumHeight());
        }
        ((RecyclerView.LayoutManager)this).setMeasuredDimension(n, n2);
    }
    
    public void setOrientation(final int mOrientation) {
        if (mOrientation != 0 && mOrientation != 1) {
            throw new IllegalArgumentException("invalid orientation.");
        }
        this.assertNotInLayoutOrScroll(null);
        if (mOrientation == this.mOrientation) {
            return;
        }
        this.mOrientation = mOrientation;
        final OrientationHelper mPrimaryOrientation = this.mPrimaryOrientation;
        this.mPrimaryOrientation = this.mSecondaryOrientation;
        this.mSecondaryOrientation = mPrimaryOrientation;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void setReverseLayout(final boolean b) {
        this.assertNotInLayoutOrScroll(null);
        final SavedState mPendingSavedState = this.mPendingSavedState;
        if (mPendingSavedState != null && mPendingSavedState.mReverseLayout != b) {
            mPendingSavedState.mReverseLayout = b;
        }
        this.mReverseLayout = b;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void setSpanCount(int i) {
        this.assertNotInLayoutOrScroll(null);
        if (i != this.mSpanCount) {
            this.invalidateSpanAssignments();
            this.mSpanCount = i;
            this.mRemainingSpans = new BitSet(this.mSpanCount);
            this.mSpans = new Span[this.mSpanCount];
            for (i = 0; i < this.mSpanCount; ++i) {
                this.mSpans[i] = new Span(i);
            }
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int targetPosition) {
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        ((RecyclerView.SmoothScroller)linearSmoothScroller).setTargetPosition(targetPosition);
        ((RecyclerView.LayoutManager)this).startSmoothScroll(linearSmoothScroller);
    }
    
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null;
    }
    
    boolean updateAnchorFromPendingData(final State state, final AnchorInfo anchorInfo) {
        final boolean preLayout = state.isPreLayout();
        boolean mLayoutFromEnd = false;
        if (!preLayout) {
            final int mPendingScrollPosition = this.mPendingScrollPosition;
            if (mPendingScrollPosition != -1) {
                if (mPendingScrollPosition >= 0 && mPendingScrollPosition < state.getItemCount()) {
                    final SavedState mPendingSavedState = this.mPendingSavedState;
                    if (mPendingSavedState != null && mPendingSavedState.mAnchorPosition != -1 && mPendingSavedState.mSpanOffsetsSize >= 1) {
                        anchorInfo.mOffset = Integer.MIN_VALUE;
                        anchorInfo.mPosition = this.mPendingScrollPosition;
                    }
                    else {
                        final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(this.mPendingScrollPosition);
                        if (viewByPosition != null) {
                            int mPosition;
                            if (this.mShouldReverseLayout) {
                                mPosition = this.getLastChildPosition();
                            }
                            else {
                                mPosition = this.getFirstChildPosition();
                            }
                            anchorInfo.mPosition = mPosition;
                            if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    anchorInfo.mOffset = this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedEnd(viewByPosition);
                                }
                                else {
                                    anchorInfo.mOffset = this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedStart(viewByPosition);
                                }
                                return true;
                            }
                            if (this.mPrimaryOrientation.getDecoratedMeasurement(viewByPosition) > this.mPrimaryOrientation.getTotalSpace()) {
                                int mOffset;
                                if (anchorInfo.mLayoutFromEnd) {
                                    mOffset = this.mPrimaryOrientation.getEndAfterPadding();
                                }
                                else {
                                    mOffset = this.mPrimaryOrientation.getStartAfterPadding();
                                }
                                anchorInfo.mOffset = mOffset;
                                return true;
                            }
                            final int n = this.mPrimaryOrientation.getDecoratedStart(viewByPosition) - this.mPrimaryOrientation.getStartAfterPadding();
                            if (n < 0) {
                                anchorInfo.mOffset = -n;
                                return true;
                            }
                            final int mOffset2 = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(viewByPosition);
                            if (mOffset2 < 0) {
                                anchorInfo.mOffset = mOffset2;
                                return true;
                            }
                            anchorInfo.mOffset = Integer.MIN_VALUE;
                        }
                        else {
                            final int mPendingScrollPosition2 = this.mPendingScrollPosition;
                            anchorInfo.mPosition = mPendingScrollPosition2;
                            final int mPendingScrollPositionOffset = this.mPendingScrollPositionOffset;
                            if (mPendingScrollPositionOffset == Integer.MIN_VALUE) {
                                if (this.calculateScrollDirectionForPosition(mPendingScrollPosition2) == 1) {
                                    mLayoutFromEnd = true;
                                }
                                anchorInfo.mLayoutFromEnd = mLayoutFromEnd;
                                anchorInfo.assignCoordinateFromPadding();
                            }
                            else {
                                anchorInfo.assignCoordinateFromPadding(mPendingScrollPositionOffset);
                            }
                            anchorInfo.mInvalidateOffsets = true;
                        }
                    }
                    return true;
                }
                this.mPendingScrollPosition = -1;
                this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
            }
        }
        return false;
    }
    
    void updateAnchorInfoForLayout(final State state, final AnchorInfo anchorInfo) {
        if (this.updateAnchorFromPendingData(state, anchorInfo)) {
            return;
        }
        if (this.updateAnchorFromChildren(state, anchorInfo)) {
            return;
        }
        anchorInfo.assignCoordinateFromPadding();
        anchorInfo.mPosition = 0;
    }
    
    void updateMeasureSpecs(final int n) {
        this.mSizePerSpan = n / this.mSpanCount;
        this.mFullSizeSpec = View$MeasureSpec.makeMeasureSpec(n, this.mSecondaryOrientation.getMode());
    }
    
    class AnchorInfo
    {
        boolean mInvalidateOffsets;
        boolean mLayoutFromEnd;
        int mOffset;
        int mPosition;
        int[] mSpanReferenceLines;
        boolean mValid;
        
        AnchorInfo() {
            this.reset();
        }
        
        void assignCoordinateFromPadding() {
            int mOffset;
            if (this.mLayoutFromEnd) {
                mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            }
            else {
                mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            }
            this.mOffset = mOffset;
        }
        
        void assignCoordinateFromPadding(final int n) {
            if (this.mLayoutFromEnd) {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - n;
            }
            else {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + n;
            }
        }
        
        void reset() {
            this.mPosition = -1;
            this.mOffset = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mInvalidateOffsets = false;
            this.mValid = false;
            final int[] mSpanReferenceLines = this.mSpanReferenceLines;
            if (mSpanReferenceLines != null) {
                Arrays.fill(mSpanReferenceLines, -1);
            }
        }
        
        void saveSpanReferenceLines(final Span[] array) {
            final int length = array.length;
            final int[] mSpanReferenceLines = this.mSpanReferenceLines;
            if (mSpanReferenceLines == null || mSpanReferenceLines.length < length) {
                this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length];
            }
            for (int i = 0; i < length; ++i) {
                this.mSpanReferenceLines[i] = array[i].getStartLine(Integer.MIN_VALUE);
            }
        }
    }
    
    public static class LayoutParams extends RecyclerView.LayoutParams
    {
        boolean mFullSpan;
        Span mSpan;
        
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
        
        public boolean isFullSpan() {
            return this.mFullSpan;
        }
    }
    
    static class LazySpanLookup
    {
        int[] mData;
        List<FullSpanItem> mFullSpanItems;
        
        private int invalidateFullSpansAfter(final int n) {
            if (this.mFullSpanItems == null) {
                return -1;
            }
            final FullSpanItem fullSpanItem = this.getFullSpanItem(n);
            if (fullSpanItem != null) {
                this.mFullSpanItems.remove(fullSpanItem);
            }
            final int size = this.mFullSpanItems.size();
            int i = 0;
            while (true) {
                while (i < size) {
                    if (this.mFullSpanItems.get(i).mPosition >= n) {
                        if (i != -1) {
                            final FullSpanItem fullSpanItem2 = this.mFullSpanItems.get(i);
                            this.mFullSpanItems.remove(i);
                            return fullSpanItem2.mPosition;
                        }
                        return -1;
                    }
                    else {
                        ++i;
                    }
                }
                i = -1;
                continue;
            }
        }
        
        private void offsetFullSpansForAddition(final int n, final int n2) {
            final List<FullSpanItem> mFullSpanItems = this.mFullSpanItems;
            if (mFullSpanItems == null) {
                return;
            }
            for (int i = mFullSpanItems.size() - 1; i >= 0; --i) {
                final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                final int mPosition = fullSpanItem.mPosition;
                if (mPosition >= n) {
                    fullSpanItem.mPosition = mPosition + n2;
                }
            }
        }
        
        private void offsetFullSpansForRemoval(final int n, final int n2) {
            final List<FullSpanItem> mFullSpanItems = this.mFullSpanItems;
            if (mFullSpanItems == null) {
                return;
            }
            for (int i = mFullSpanItems.size() - 1; i >= 0; --i) {
                final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                final int mPosition = fullSpanItem.mPosition;
                if (mPosition >= n) {
                    if (mPosition < n + n2) {
                        this.mFullSpanItems.remove(i);
                    }
                    else {
                        fullSpanItem.mPosition = mPosition - n2;
                    }
                }
            }
        }
        
        public void addFullSpanItem(final FullSpanItem fullSpanItem) {
            if (this.mFullSpanItems == null) {
                this.mFullSpanItems = new ArrayList<FullSpanItem>();
            }
            for (int size = this.mFullSpanItems.size(), i = 0; i < size; ++i) {
                final FullSpanItem fullSpanItem2 = this.mFullSpanItems.get(i);
                if (fullSpanItem2.mPosition == fullSpanItem.mPosition) {
                    this.mFullSpanItems.remove(i);
                }
                if (fullSpanItem2.mPosition >= fullSpanItem.mPosition) {
                    this.mFullSpanItems.add(i, fullSpanItem);
                    return;
                }
            }
            this.mFullSpanItems.add(fullSpanItem);
        }
        
        void clear() {
            final int[] mData = this.mData;
            if (mData != null) {
                Arrays.fill(mData, -1);
            }
            this.mFullSpanItems = null;
        }
        
        void ensureSize(final int a) {
            final int[] mData = this.mData;
            if (mData == null) {
                Arrays.fill(this.mData = new int[Math.max(a, 10) + 1], -1);
            }
            else if (a >= mData.length) {
                System.arraycopy(mData, 0, this.mData = new int[this.sizeForPosition(a)], 0, mData.length);
                final int[] mData2 = this.mData;
                Arrays.fill(mData2, mData.length, mData2.length, -1);
            }
        }
        
        int forceInvalidateAfter(final int n) {
            final List<FullSpanItem> mFullSpanItems = this.mFullSpanItems;
            if (mFullSpanItems != null) {
                for (int i = mFullSpanItems.size() - 1; i >= 0; --i) {
                    if (this.mFullSpanItems.get(i).mPosition >= n) {
                        this.mFullSpanItems.remove(i);
                    }
                }
            }
            return this.invalidateAfter(n);
        }
        
        public FullSpanItem getFirstFullSpanItemInRange(final int n, final int n2, final int n3, final boolean b) {
            final List<FullSpanItem> mFullSpanItems = this.mFullSpanItems;
            if (mFullSpanItems == null) {
                return null;
            }
            for (int size = mFullSpanItems.size(), i = 0; i < size; ++i) {
                final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                final int mPosition = fullSpanItem.mPosition;
                if (mPosition >= n2) {
                    return null;
                }
                if (mPosition >= n && (n3 == 0 || fullSpanItem.mGapDir == n3 || (b && fullSpanItem.mHasUnwantedGapAfter))) {
                    return fullSpanItem;
                }
            }
            return null;
        }
        
        public FullSpanItem getFullSpanItem(final int n) {
            final List<FullSpanItem> mFullSpanItems = this.mFullSpanItems;
            if (mFullSpanItems == null) {
                return null;
            }
            for (int i = mFullSpanItems.size() - 1; i >= 0; --i) {
                final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                if (fullSpanItem.mPosition == n) {
                    return fullSpanItem;
                }
            }
            return null;
        }
        
        int getSpan(final int n) {
            final int[] mData = this.mData;
            if (mData != null && n < mData.length) {
                return mData[n];
            }
            return -1;
        }
        
        int invalidateAfter(final int n) {
            final int[] mData = this.mData;
            if (mData == null) {
                return -1;
            }
            if (n >= mData.length) {
                return -1;
            }
            int invalidateFullSpansAfter = this.invalidateFullSpansAfter(n);
            if (invalidateFullSpansAfter == -1) {
                final int[] mData2 = this.mData;
                Arrays.fill(mData2, n, mData2.length, -1);
                return this.mData.length;
            }
            final int[] mData3 = this.mData;
            ++invalidateFullSpansAfter;
            Arrays.fill(mData3, n, invalidateFullSpansAfter, -1);
            return invalidateFullSpansAfter;
        }
        
        void offsetForAddition(final int fromIndex, final int n) {
            final int[] mData = this.mData;
            if (mData != null) {
                if (fromIndex < mData.length) {
                    final int toIndex = fromIndex + n;
                    this.ensureSize(toIndex);
                    final int[] mData2 = this.mData;
                    System.arraycopy(mData2, fromIndex, mData2, toIndex, mData2.length - fromIndex - n);
                    Arrays.fill(this.mData, fromIndex, toIndex, -1);
                    this.offsetFullSpansForAddition(fromIndex, n);
                }
            }
        }
        
        void offsetForRemoval(final int n, final int n2) {
            final int[] mData = this.mData;
            if (mData != null) {
                if (n < mData.length) {
                    final int n3 = n + n2;
                    this.ensureSize(n3);
                    final int[] mData2 = this.mData;
                    System.arraycopy(mData2, n3, mData2, n, mData2.length - n - n2);
                    final int[] mData3 = this.mData;
                    Arrays.fill(mData3, mData3.length - n2, mData3.length, -1);
                    this.offsetFullSpansForRemoval(n, n2);
                }
            }
        }
        
        void setSpan(final int n, final Span span) {
            this.ensureSize(n);
            this.mData[n] = span.mIndex;
        }
        
        int sizeForPosition(final int n) {
            int i;
            for (i = this.mData.length; i <= n; i *= 2) {}
            return i;
        }
        
        @SuppressLint({ "BanParcelableUsage" })
        static class FullSpanItem implements Parcelable
        {
            public static final Parcelable$Creator<FullSpanItem> CREATOR;
            int mGapDir;
            int[] mGapPerSpan;
            boolean mHasUnwantedGapAfter;
            int mPosition;
            
            static {
                CREATOR = (Parcelable$Creator)new Parcelable$Creator<FullSpanItem>() {
                    public FullSpanItem createFromParcel(final Parcel parcel) {
                        return new FullSpanItem(parcel);
                    }
                    
                    public FullSpanItem[] newArray(final int n) {
                        return new FullSpanItem[n];
                    }
                };
            }
            
            FullSpanItem() {
            }
            
            FullSpanItem(final Parcel parcel) {
                this.mPosition = parcel.readInt();
                this.mGapDir = parcel.readInt();
                final int int1 = parcel.readInt();
                boolean mHasUnwantedGapAfter = true;
                if (int1 != 1) {
                    mHasUnwantedGapAfter = false;
                }
                this.mHasUnwantedGapAfter = mHasUnwantedGapAfter;
                final int int2 = parcel.readInt();
                if (int2 > 0) {
                    parcel.readIntArray(this.mGapPerSpan = new int[int2]);
                }
            }
            
            public int describeContents() {
                return 0;
            }
            
            int getGapForSpan(int n) {
                final int[] mGapPerSpan = this.mGapPerSpan;
                if (mGapPerSpan == null) {
                    n = 0;
                }
                else {
                    n = mGapPerSpan[n];
                }
                return n;
            }
            
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("FullSpanItem{mPosition=");
                sb.append(this.mPosition);
                sb.append(", mGapDir=");
                sb.append(this.mGapDir);
                sb.append(", mHasUnwantedGapAfter=");
                sb.append(this.mHasUnwantedGapAfter);
                sb.append(", mGapPerSpan=");
                sb.append(Arrays.toString(this.mGapPerSpan));
                sb.append('}');
                return sb.toString();
            }
            
            public void writeToParcel(final Parcel parcel, final int n) {
                parcel.writeInt(this.mPosition);
                parcel.writeInt(this.mGapDir);
                parcel.writeInt((int)(this.mHasUnwantedGapAfter ? 1 : 0));
                final int[] mGapPerSpan = this.mGapPerSpan;
                if (mGapPerSpan != null && mGapPerSpan.length > 0) {
                    parcel.writeInt(mGapPerSpan.length);
                    parcel.writeIntArray(this.mGapPerSpan);
                }
                else {
                    parcel.writeInt(0);
                }
            }
        }
    }
    
    @SuppressLint({ "BanParcelableUsage" })
    public static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean mAnchorLayoutFromEnd;
        int mAnchorPosition;
        List<FullSpanItem> mFullSpanItems;
        boolean mLastLayoutRTL;
        boolean mReverseLayout;
        int[] mSpanLookup;
        int mSpanLookupSize;
        int[] mSpanOffsets;
        int mSpanOffsetsSize;
        int mVisibleAnchorPosition;
        
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
            this.mVisibleAnchorPosition = parcel.readInt();
            final int int1 = parcel.readInt();
            this.mSpanOffsetsSize = int1;
            if (int1 > 0) {
                parcel.readIntArray(this.mSpanOffsets = new int[int1]);
            }
            final int int2 = parcel.readInt();
            if ((this.mSpanLookupSize = int2) > 0) {
                parcel.readIntArray(this.mSpanLookup = new int[int2]);
            }
            final int int3 = parcel.readInt();
            final boolean b = false;
            this.mReverseLayout = (int3 == 1);
            this.mAnchorLayoutFromEnd = (parcel.readInt() == 1);
            boolean mLastLayoutRTL = b;
            if (parcel.readInt() == 1) {
                mLastLayoutRTL = true;
            }
            this.mLastLayoutRTL = mLastLayoutRTL;
            this.mFullSpanItems = (List<FullSpanItem>)parcel.readArrayList(FullSpanItem.class.getClassLoader());
        }
        
        public SavedState(final SavedState savedState) {
            this.mSpanOffsetsSize = savedState.mSpanOffsetsSize;
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mVisibleAnchorPosition = savedState.mVisibleAnchorPosition;
            this.mSpanOffsets = savedState.mSpanOffsets;
            this.mSpanLookupSize = savedState.mSpanLookupSize;
            this.mSpanLookup = savedState.mSpanLookup;
            this.mReverseLayout = savedState.mReverseLayout;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
            this.mLastLayoutRTL = savedState.mLastLayoutRTL;
            this.mFullSpanItems = savedState.mFullSpanItems;
        }
        
        public int describeContents() {
            return 0;
        }
        
        void invalidateAnchorPositionInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mAnchorPosition = -1;
            this.mVisibleAnchorPosition = -1;
        }
        
        void invalidateSpanInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mSpanLookupSize = 0;
            this.mSpanLookup = null;
            this.mFullSpanItems = null;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mVisibleAnchorPosition);
            parcel.writeInt(this.mSpanOffsetsSize);
            if (this.mSpanOffsetsSize > 0) {
                parcel.writeIntArray(this.mSpanOffsets);
            }
            parcel.writeInt(this.mSpanLookupSize);
            if (this.mSpanLookupSize > 0) {
                parcel.writeIntArray(this.mSpanLookup);
            }
            parcel.writeInt((int)(this.mReverseLayout ? 1 : 0));
            parcel.writeInt((int)(this.mAnchorLayoutFromEnd ? 1 : 0));
            parcel.writeInt((int)(this.mLastLayoutRTL ? 1 : 0));
            parcel.writeList((List)this.mFullSpanItems);
        }
    }
    
    class Span
    {
        int mCachedEnd;
        int mCachedStart;
        int mDeletedSize;
        final int mIndex;
        ArrayList<View> mViews;
        
        Span(final int mIndex) {
            this.mViews = new ArrayList<View>();
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
            this.mDeletedSize = 0;
            this.mIndex = mIndex;
        }
        
        void appendToSpan(final View e) {
            final LayoutParams layoutParams = this.getLayoutParams(e);
            layoutParams.mSpan = this;
            this.mViews.add(e);
            this.mCachedEnd = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(e);
            }
        }
        
        void cacheReferenceLineAndClear(final boolean b, final int n) {
            int n2;
            if (b) {
                n2 = this.getEndLine(Integer.MIN_VALUE);
            }
            else {
                n2 = this.getStartLine(Integer.MIN_VALUE);
            }
            this.clear();
            if (n2 == Integer.MIN_VALUE) {
                return;
            }
            if ((b && n2 < StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding()) || (!b && n2 > StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())) {
                return;
            }
            int n3 = n2;
            if (n != Integer.MIN_VALUE) {
                n3 = n2 + n;
            }
            this.mCachedEnd = n3;
            this.mCachedStart = n3;
        }
        
        void calculateCachedEnd() {
            final ArrayList<View> mViews = this.mViews;
            final View view = mViews.get(mViews.size() - 1);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
            if (layoutParams.mFullSpan) {
                final FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == 1) {
                    this.mCachedEnd += fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }
        
        void calculateCachedStart() {
            final View view = this.mViews.get(0);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
            if (layoutParams.mFullSpan) {
                final FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == -1) {
                    this.mCachedStart -= fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }
        
        void clear() {
            this.mViews.clear();
            this.invalidateCache();
            this.mDeletedSize = 0;
        }
        
        public int findFirstPartiallyVisibleItemPosition() {
            int n;
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
            }
            else {
                n = this.findOnePartiallyVisibleChild(0, this.mViews.size(), true);
            }
            return n;
        }
        
        public int findLastPartiallyVisibleItemPosition() {
            int n;
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOnePartiallyVisibleChild(0, this.mViews.size(), true);
            }
            else {
                n = this.findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
            }
            return n;
        }
        
        int findOnePartiallyOrCompletelyVisibleChild(int i, final int n, final boolean b, final boolean b2, final boolean b3) {
            final int startAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            final int endAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            int n2;
            if (n > i) {
                n2 = 1;
            }
            else {
                n2 = -1;
            }
            while (i != n) {
                final View view = this.mViews.get(i);
                final int decoratedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
                final int decoratedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
                boolean b4 = false;
                final boolean b5 = b3 ? (decoratedStart <= endAfterPadding) : (decoratedStart < endAfterPadding);
                Label_0143: {
                    if (b3) {
                        if (decoratedEnd < startAfterPadding) {
                            break Label_0143;
                        }
                    }
                    else if (decoratedEnd <= startAfterPadding) {
                        break Label_0143;
                    }
                    b4 = true;
                }
                if (b5 && b4) {
                    if (b && b2) {
                        if (decoratedStart >= startAfterPadding && decoratedEnd <= endAfterPadding) {
                            return ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view);
                        }
                    }
                    else {
                        if (b2) {
                            return ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view);
                        }
                        if (decoratedStart < startAfterPadding || decoratedEnd > endAfterPadding) {
                            return ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view);
                        }
                    }
                }
                i += n2;
            }
            return -1;
        }
        
        int findOnePartiallyVisibleChild(final int n, final int n2, final boolean b) {
            return this.findOnePartiallyOrCompletelyVisibleChild(n, n2, false, false, b);
        }
        
        public int getDeletedSize() {
            return this.mDeletedSize;
        }
        
        int getEndLine() {
            final int mCachedEnd = this.mCachedEnd;
            if (mCachedEnd != Integer.MIN_VALUE) {
                return mCachedEnd;
            }
            this.calculateCachedEnd();
            return this.mCachedEnd;
        }
        
        int getEndLine(final int n) {
            final int mCachedEnd = this.mCachedEnd;
            if (mCachedEnd != Integer.MIN_VALUE) {
                return mCachedEnd;
            }
            if (this.mViews.size() == 0) {
                return n;
            }
            this.calculateCachedEnd();
            return this.mCachedEnd;
        }
        
        public View getFocusableViewAfter(final int n, int n2) {
            final View view = null;
            View view2 = null;
            View view3;
            if (n2 == -1) {
                final int size = this.mViews.size();
                n2 = 0;
                while (true) {
                    view3 = view2;
                    if (n2 >= size) {
                        break;
                    }
                    final View view4 = this.mViews.get(n2);
                    final StaggeredGridLayoutManager this$0 = StaggeredGridLayoutManager.this;
                    if (this$0.mReverseLayout) {
                        view3 = view2;
                        if (((RecyclerView.LayoutManager)this$0).getPosition(view4) <= n) {
                            break;
                        }
                    }
                    final StaggeredGridLayoutManager this$2 = StaggeredGridLayoutManager.this;
                    if (!this$2.mReverseLayout && ((RecyclerView.LayoutManager)this$2).getPosition(view4) >= n) {
                        view3 = view2;
                        break;
                    }
                    view3 = view2;
                    if (!view4.hasFocusable()) {
                        break;
                    }
                    ++n2;
                    view2 = view4;
                }
            }
            else {
                n2 = this.mViews.size() - 1;
                View view5 = view;
                while (true) {
                    view3 = view5;
                    if (n2 < 0) {
                        break;
                    }
                    final View view6 = this.mViews.get(n2);
                    final StaggeredGridLayoutManager this$3 = StaggeredGridLayoutManager.this;
                    if (this$3.mReverseLayout) {
                        view3 = view5;
                        if (((RecyclerView.LayoutManager)this$3).getPosition(view6) >= n) {
                            break;
                        }
                    }
                    final StaggeredGridLayoutManager this$4 = StaggeredGridLayoutManager.this;
                    if (!this$4.mReverseLayout && ((RecyclerView.LayoutManager)this$4).getPosition(view6) <= n) {
                        view3 = view5;
                        break;
                    }
                    view3 = view5;
                    if (!view6.hasFocusable()) {
                        break;
                    }
                    --n2;
                    view5 = view6;
                }
            }
            return view3;
        }
        
        LayoutParams getLayoutParams(final View view) {
            return (LayoutParams)view.getLayoutParams();
        }
        
        int getStartLine() {
            final int mCachedStart = this.mCachedStart;
            if (mCachedStart != Integer.MIN_VALUE) {
                return mCachedStart;
            }
            this.calculateCachedStart();
            return this.mCachedStart;
        }
        
        int getStartLine(final int n) {
            final int mCachedStart = this.mCachedStart;
            if (mCachedStart != Integer.MIN_VALUE) {
                return mCachedStart;
            }
            if (this.mViews.size() == 0) {
                return n;
            }
            this.calculateCachedStart();
            return this.mCachedStart;
        }
        
        void invalidateCache() {
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
        }
        
        void onOffset(final int n) {
            final int mCachedStart = this.mCachedStart;
            if (mCachedStart != Integer.MIN_VALUE) {
                this.mCachedStart = mCachedStart + n;
            }
            final int mCachedEnd = this.mCachedEnd;
            if (mCachedEnd != Integer.MIN_VALUE) {
                this.mCachedEnd = mCachedEnd + n;
            }
        }
        
        void popEnd() {
            final int size = this.mViews.size();
            final View view = this.mViews.remove(size - 1);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            layoutParams.mSpan = null;
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            if (size == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            this.mCachedEnd = Integer.MIN_VALUE;
        }
        
        void popStart() {
            final View view = this.mViews.remove(0);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            layoutParams.mSpan = null;
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            this.mCachedStart = Integer.MIN_VALUE;
        }
        
        void prependToSpan(final View element) {
            final LayoutParams layoutParams = this.getLayoutParams(element);
            layoutParams.mSpan = this;
            this.mViews.add(0, element);
            this.mCachedStart = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(element);
            }
        }
        
        void setLine(final int n) {
            this.mCachedStart = n;
            this.mCachedEnd = n;
        }
    }
}
