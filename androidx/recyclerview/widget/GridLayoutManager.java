// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.Arrays;
import android.view.View$MeasureSpec;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.util.Log;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.util.SparseIntArray;
import android.graphics.Rect;

public class GridLayoutManager extends LinearLayoutManager
{
    int[] mCachedBorders;
    final Rect mDecorInsets;
    boolean mPendingSpanCountChange;
    final SparseIntArray mPreLayoutSpanIndexCache;
    final SparseIntArray mPreLayoutSpanSizeCache;
    View[] mSet;
    int mSpanCount;
    SpanSizeLookup mSpanSizeLookup;
    private boolean mUsingSpansToEstimateScrollBarDimensions;
    
    public GridLayoutManager(final Context context, final int spanCount) {
        super(context);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = (SpanSizeLookup)new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        this.setSpanCount(spanCount);
    }
    
    public GridLayoutManager(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = (SpanSizeLookup)new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        this.setSpanCount(RecyclerView.LayoutManager.getProperties(context, set, n, n2).spanCount);
    }
    
    private void assignSpans(final Recycler recycler, final State state, int i, final boolean b) {
        int mSpanIndex = 0;
        int n = -1;
        int n2;
        if (b) {
            n2 = 1;
            final int n3 = 0;
            n = i;
            i = n3;
        }
        else {
            --i;
            n2 = -1;
        }
        while (i != n) {
            final View view = this.mSet[i];
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final int spanSize = this.getSpanSize(recycler, state, ((RecyclerView.LayoutManager)this).getPosition(view));
            layoutParams.mSpanSize = spanSize;
            layoutParams.mSpanIndex = mSpanIndex;
            mSpanIndex += spanSize;
            i += n2;
        }
    }
    
    private void cachePreLayoutSpanMapping() {
        for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i) {
            final LayoutParams layoutParams = (LayoutParams)((RecyclerView.LayoutManager)this).getChildAt(i).getLayoutParams();
            final int viewLayoutPosition = ((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewLayoutPosition, layoutParams.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewLayoutPosition, layoutParams.getSpanIndex());
        }
    }
    
    private void calculateItemBorders(final int n) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, n);
    }
    
    static int[] calculateItemBorders(final int[] array, final int n, int n2) {
        int i = 1;
        int[] array2 = null;
        Label_0034: {
            if (array != null && array.length == n + 1) {
                array2 = array;
                if (array[array.length - 1] == n2) {
                    break Label_0034;
                }
            }
            array2 = new int[n + 1];
        }
        final int n3 = 0;
        array2[0] = 0;
        final int n4 = n2 / n;
        final int n5 = n2 % n;
        int n6 = 0;
        n2 = n3;
        while (i <= n) {
            n2 += n5;
            int n7;
            if (n2 > 0 && n - n2 < n5) {
                n7 = n4 + 1;
                n2 -= n;
            }
            else {
                n7 = n4;
            }
            n6 += n7;
            array2[i] = n6;
            ++i;
        }
        return array2;
    }
    
    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }
    
    private int computeScrollOffsetWithSpanInfo(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            if (state.getItemCount() != 0) {
                this.ensureLayoutState();
                final boolean smoothScrollbarEnabled = this.isSmoothScrollbarEnabled();
                final View firstVisibleChildClosestToStart = this.findFirstVisibleChildClosestToStart(smoothScrollbarEnabled ^ true, true);
                final View firstVisibleChildClosestToEnd = this.findFirstVisibleChildClosestToEnd(smoothScrollbarEnabled ^ true, true);
                if (firstVisibleChildClosestToStart != null) {
                    if (firstVisibleChildClosestToEnd != null) {
                        final int cachedSpanGroupIndex = this.mSpanSizeLookup.getCachedSpanGroupIndex(((RecyclerView.LayoutManager)this).getPosition(firstVisibleChildClosestToStart), this.mSpanCount);
                        final int cachedSpanGroupIndex2 = this.mSpanSizeLookup.getCachedSpanGroupIndex(((RecyclerView.LayoutManager)this).getPosition(firstVisibleChildClosestToEnd), this.mSpanCount);
                        final int min = Math.min(cachedSpanGroupIndex, cachedSpanGroupIndex2);
                        final int max = Math.max(cachedSpanGroupIndex, cachedSpanGroupIndex2);
                        final int cachedSpanGroupIndex3 = this.mSpanSizeLookup.getCachedSpanGroupIndex(state.getItemCount() - 1, this.mSpanCount);
                        int n;
                        if (super.mShouldReverseLayout) {
                            n = Math.max(0, cachedSpanGroupIndex3 + 1 - max - 1);
                        }
                        else {
                            n = Math.max(0, min);
                        }
                        if (!smoothScrollbarEnabled) {
                            return n;
                        }
                        return Math.round(n * (Math.abs(super.mOrientationHelper.getDecoratedEnd(firstVisibleChildClosestToEnd) - super.mOrientationHelper.getDecoratedStart(firstVisibleChildClosestToStart)) / (float)(this.mSpanSizeLookup.getCachedSpanGroupIndex(((RecyclerView.LayoutManager)this).getPosition(firstVisibleChildClosestToEnd), this.mSpanCount) - this.mSpanSizeLookup.getCachedSpanGroupIndex(((RecyclerView.LayoutManager)this).getPosition(firstVisibleChildClosestToStart), this.mSpanCount) + 1)) + (super.mOrientationHelper.getStartAfterPadding() - super.mOrientationHelper.getDecoratedStart(firstVisibleChildClosestToStart)));
                    }
                }
            }
        }
        return 0;
    }
    
    private int computeScrollRangeWithSpanInfo(final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            if (state.getItemCount() != 0) {
                this.ensureLayoutState();
                final View firstVisibleChildClosestToStart = this.findFirstVisibleChildClosestToStart(this.isSmoothScrollbarEnabled() ^ true, true);
                final View firstVisibleChildClosestToEnd = this.findFirstVisibleChildClosestToEnd(this.isSmoothScrollbarEnabled() ^ true, true);
                if (firstVisibleChildClosestToStart != null) {
                    if (firstVisibleChildClosestToEnd != null) {
                        if (!this.isSmoothScrollbarEnabled()) {
                            return this.mSpanSizeLookup.getCachedSpanGroupIndex(state.getItemCount() - 1, this.mSpanCount) + 1;
                        }
                        return (int)((super.mOrientationHelper.getDecoratedEnd(firstVisibleChildClosestToEnd) - super.mOrientationHelper.getDecoratedStart(firstVisibleChildClosestToStart)) / (float)(this.mSpanSizeLookup.getCachedSpanGroupIndex(((RecyclerView.LayoutManager)this).getPosition(firstVisibleChildClosestToEnd), this.mSpanCount) - this.mSpanSizeLookup.getCachedSpanGroupIndex(((RecyclerView.LayoutManager)this).getPosition(firstVisibleChildClosestToStart), this.mSpanCount) + 1) * (this.mSpanSizeLookup.getCachedSpanGroupIndex(state.getItemCount() - 1, this.mSpanCount) + 1));
                    }
                }
            }
        }
        return 0;
    }
    
    private void ensureAnchorIsInCorrectSpan(final Recycler recycler, final State state, final AnchorInfo anchorInfo, int i) {
        if (i == 1) {
            i = 1;
        }
        else {
            i = 0;
        }
        int j = this.getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (i != 0) {
            while (j > 0) {
                i = anchorInfo.mPosition;
                if (i <= 0) {
                    break;
                }
                --i;
                anchorInfo.mPosition = i;
                j = this.getSpanIndex(recycler, state, i);
            }
        }
        else {
            int itemCount;
            int n;
            int spanIndex;
            for (itemCount = state.getItemCount(), i = anchorInfo.mPosition; i < itemCount - 1; i = n, j = spanIndex) {
                n = i + 1;
                spanIndex = this.getSpanIndex(recycler, state, n);
                if (spanIndex <= j) {
                    break;
                }
            }
            anchorInfo.mPosition = i;
        }
    }
    
    private void ensureViewSet() {
        final View[] mSet = this.mSet;
        if (mSet == null || mSet.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }
    
    private int getSpanGroupIndex(final Recycler recycler, final State state, final int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanGroupIndex(i, this.mSpanCount);
        }
        final int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout == -1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot find span size for pre layout position. ");
            sb.append(i);
            Log.w("GridLayoutManager", sb.toString());
            return 0;
        }
        return this.mSpanSizeLookup.getCachedSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
    }
    
    private int getSpanIndex(final Recycler recycler, final State state, final int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        final int value = this.mPreLayoutSpanIndexCache.get(i, -1);
        if (value != -1) {
            return value;
        }
        final int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout == -1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
            sb.append(i);
            Log.w("GridLayoutManager", sb.toString());
            return 0;
        }
        return this.mSpanSizeLookup.getCachedSpanIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
    }
    
    private int getSpanSize(final Recycler recycler, final State state, final int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        final int value = this.mPreLayoutSpanSizeCache.get(i, -1);
        if (value != -1) {
            return value;
        }
        final int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout == -1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
            sb.append(i);
            Log.w("GridLayoutManager", sb.toString());
            return 1;
        }
        return this.mSpanSizeLookup.getSpanSize(convertPreLayoutPositionToPostLayout);
    }
    
    private void guessMeasurement(final float n, final int b) {
        this.calculateItemBorders(Math.max(Math.round(n * this.mSpanCount), b));
    }
    
    private void measureChild(final View view, int n, final boolean b) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final Rect mDecorInsets = layoutParams.mDecorInsets;
        final int n2 = mDecorInsets.top + mDecorInsets.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
        final int n3 = mDecorInsets.left + mDecorInsets.right + layoutParams.leftMargin + layoutParams.rightMargin;
        final int spaceForSpanRange = this.getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
        int n4;
        if (super.mOrientation == 1) {
            n4 = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, n, n3, layoutParams.width, false);
            n = RecyclerView.LayoutManager.getChildMeasureSpec(super.mOrientationHelper.getTotalSpace(), ((RecyclerView.LayoutManager)this).getHeightMode(), n2, layoutParams.height, true);
        }
        else {
            n = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, n, n2, layoutParams.height, false);
            n4 = RecyclerView.LayoutManager.getChildMeasureSpec(super.mOrientationHelper.getTotalSpace(), ((RecyclerView.LayoutManager)this).getWidthMode(), n3, layoutParams.width, true);
        }
        this.measureChildWithDecorationsAndMargin(view, n4, n, b);
    }
    
    private void measureChildWithDecorationsAndMargin(final View view, final int n, final int n2, final boolean b) {
        final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
        boolean b2;
        if (b) {
            b2 = ((RecyclerView.LayoutManager)this).shouldReMeasureChild(view, n, n2, layoutParams);
        }
        else {
            b2 = ((RecyclerView.LayoutManager)this).shouldMeasureChild(view, n, n2, layoutParams);
        }
        if (b2) {
            view.measure(n, n2);
        }
    }
    
    private void updateMeasurements() {
        int n;
        int n2;
        if (this.getOrientation() == 1) {
            n = ((RecyclerView.LayoutManager)this).getWidth() - ((RecyclerView.LayoutManager)this).getPaddingRight();
            n2 = ((RecyclerView.LayoutManager)this).getPaddingLeft();
        }
        else {
            n = ((RecyclerView.LayoutManager)this).getHeight() - ((RecyclerView.LayoutManager)this).getPaddingBottom();
            n2 = ((RecyclerView.LayoutManager)this).getPaddingTop();
        }
        this.calculateItemBorders(n - n2);
    }
    
    @Override
    public boolean checkLayoutParams(final RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }
    
    @Override
    void collectPrefetchPositionsForLayoutState(final State state, final LayoutState layoutState, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int mCurrentPosition;
        for (int mSpanCount = this.mSpanCount, n = 0; n < this.mSpanCount && layoutState.hasMore(state) && mSpanCount > 0; mSpanCount -= this.mSpanSizeLookup.getSpanSize(mCurrentPosition), layoutState.mCurrentPosition += layoutState.mItemDirection, ++n) {
            mCurrentPosition = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(mCurrentPosition, Math.max(0, layoutState.mScrollingOffset));
        }
    }
    
    @Override
    public int computeHorizontalScrollOffset(final State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return this.computeScrollOffsetWithSpanInfo(state);
        }
        return super.computeHorizontalScrollOffset(state);
    }
    
    @Override
    public int computeHorizontalScrollRange(final State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return this.computeScrollRangeWithSpanInfo(state);
        }
        return super.computeHorizontalScrollRange(state);
    }
    
    @Override
    public int computeVerticalScrollOffset(final State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return this.computeScrollOffsetWithSpanInfo(state);
        }
        return super.computeVerticalScrollOffset(state);
    }
    
    @Override
    public int computeVerticalScrollRange(final State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return this.computeScrollRangeWithSpanInfo(state);
        }
        return super.computeVerticalScrollRange(state);
    }
    
    @Override
    View findReferenceChild(final Recycler recycler, final State state, int i, final int n, final int n2) {
        this.ensureLayoutState();
        final int startAfterPadding = super.mOrientationHelper.getStartAfterPadding();
        final int endAfterPadding = super.mOrientationHelper.getEndAfterPadding();
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
            View view3 = view;
            View view4 = view2;
            if (position >= 0) {
                view3 = view;
                view4 = view2;
                if (position < n2) {
                    if (this.getSpanIndex(recycler, state, position) != 0) {
                        view3 = view;
                        view4 = view2;
                    }
                    else if (((RecyclerView.LayoutParams)child.getLayoutParams()).isItemRemoved()) {
                        view3 = view;
                        if ((view4 = view2) == null) {
                            view4 = child;
                            view3 = view;
                        }
                    }
                    else {
                        if (super.mOrientationHelper.getDecoratedStart(child) < endAfterPadding && super.mOrientationHelper.getDecoratedEnd(child) >= startAfterPadding) {
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
        if (view == null) {
            view = view2;
        }
        return view;
    }
    
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (super.mOrientation == 0) {
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
    
    @Override
    public int getColumnCountForAccessibility(final Recycler recycler, final State state) {
        if (super.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return this.getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }
    
    @Override
    public int getRowCountForAccessibility(final Recycler recycler, final State state) {
        if (super.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return this.getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }
    
    int getSpaceForSpanRange(final int n, final int n2) {
        if (super.mOrientation == 1 && this.isLayoutRTL()) {
            final int[] mCachedBorders = this.mCachedBorders;
            final int mSpanCount = this.mSpanCount;
            return mCachedBorders[mSpanCount - n] - mCachedBorders[mSpanCount - n - n2];
        }
        final int[] mCachedBorders2 = this.mCachedBorders;
        return mCachedBorders2[n2 + n] - mCachedBorders2[n];
    }
    
    @Override
    void layoutChunk(final Recycler recycler, final State state, final LayoutState layoutState, final LayoutChunkResult layoutChunkResult) {
        final int modeInOther = super.mOrientationHelper.getModeInOther();
        final boolean b = modeInOther != 1073741824;
        int n;
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            n = this.mCachedBorders[this.mSpanCount];
        }
        else {
            n = 0;
        }
        if (b) {
            this.updateMeasurements();
        }
        final boolean b2 = layoutState.mItemDirection == 1;
        int mSpanCount = this.mSpanCount;
        if (!b2) {
            mSpanCount = this.getSpanIndex(recycler, state, layoutState.mCurrentPosition) + this.getSpanSize(recycler, state, layoutState.mCurrentPosition);
        }
        int n2;
        for (n2 = 0; n2 < this.mSpanCount && layoutState.hasMore(state) && mSpanCount > 0; ++n2) {
            final int mCurrentPosition = layoutState.mCurrentPosition;
            final int spanSize = this.getSpanSize(recycler, state, mCurrentPosition);
            if (spanSize > this.mSpanCount) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Item at position ");
                sb.append(mCurrentPosition);
                sb.append(" requires ");
                sb.append(spanSize);
                sb.append(" spans but GridLayoutManager has only ");
                sb.append(this.mSpanCount);
                sb.append(" spans.");
                throw new IllegalArgumentException(sb.toString());
            }
            mSpanCount -= spanSize;
            if (mSpanCount < 0) {
                break;
            }
            final View next = layoutState.next(recycler);
            if (next == null) {
                break;
            }
            this.mSet[n2] = next;
        }
        if (n2 == 0) {
            layoutChunkResult.mFinished = true;
            return;
        }
        float n3 = 0.0f;
        this.assignSpans(recycler, state, n2, b2);
        int n4;
        int n5;
        float n7;
        for (int i = n4 = 0; i < n2; ++i, n4 = n5, n3 = n7) {
            final View view = this.mSet[i];
            if (layoutState.mScrapList == null) {
                if (b2) {
                    ((RecyclerView.LayoutManager)this).addView(view);
                }
                else {
                    ((RecyclerView.LayoutManager)this).addView(view, 0);
                }
            }
            else if (b2) {
                ((RecyclerView.LayoutManager)this).addDisappearingView(view);
            }
            else {
                ((RecyclerView.LayoutManager)this).addDisappearingView(view, 0);
            }
            ((RecyclerView.LayoutManager)this).calculateItemDecorationsForChild(view, this.mDecorInsets);
            this.measureChild(view, modeInOther, false);
            final int decoratedMeasurement = super.mOrientationHelper.getDecoratedMeasurement(view);
            n5 = n4;
            if (decoratedMeasurement > n4) {
                n5 = decoratedMeasurement;
            }
            final float n6 = super.mOrientationHelper.getDecoratedMeasurementInOther(view) * 1.0f / ((LayoutParams)view.getLayoutParams()).mSpanSize;
            n7 = n3;
            if (n6 > n3) {
                n7 = n6;
            }
        }
        int mConsumed = n4;
        if (b) {
            this.guessMeasurement(n3, n);
            int n8 = 0;
            int n9 = 0;
            while (true) {
                mConsumed = n9;
                if (n8 >= n2) {
                    break;
                }
                final View view2 = this.mSet[n8];
                this.measureChild(view2, 1073741824, true);
                final int decoratedMeasurement2 = super.mOrientationHelper.getDecoratedMeasurement(view2);
                int n10;
                if (decoratedMeasurement2 > (n10 = n9)) {
                    n10 = decoratedMeasurement2;
                }
                ++n8;
                n9 = n10;
            }
        }
        for (int j = 0; j < n2; ++j) {
            final View view3 = this.mSet[j];
            if (super.mOrientationHelper.getDecoratedMeasurement(view3) != mConsumed) {
                final LayoutParams layoutParams = (LayoutParams)view3.getLayoutParams();
                final Rect mDecorInsets = layoutParams.mDecorInsets;
                final int n11 = mDecorInsets.top + mDecorInsets.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
                final int n12 = mDecorInsets.left + mDecorInsets.right + layoutParams.leftMargin + layoutParams.rightMargin;
                final int spaceForSpanRange = this.getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
                int n13;
                int n14;
                if (super.mOrientation == 1) {
                    n13 = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, 1073741824, n12, layoutParams.width, false);
                    n14 = View$MeasureSpec.makeMeasureSpec(mConsumed - n11, 1073741824);
                }
                else {
                    n13 = View$MeasureSpec.makeMeasureSpec(mConsumed - n12, 1073741824);
                    n14 = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, 1073741824, n11, layoutParams.height, false);
                }
                this.measureChildWithDecorationsAndMargin(view3, n13, n14, true);
            }
        }
        int k = 0;
        layoutChunkResult.mConsumed = mConsumed;
        int mOffset;
        int n15;
        int mOffset4;
        int mOffset3;
        if (super.mOrientation == 1) {
            if (layoutState.mLayoutDirection == -1) {
                mOffset = layoutState.mOffset;
                n15 = mOffset - mConsumed;
            }
            else {
                final int mOffset2;
                final int n16 = mConsumed + (mOffset2 = layoutState.mOffset);
                n15 = mOffset2;
                mOffset = n16;
            }
            mOffset3 = (mOffset4 = 0);
        }
        else if (layoutState.mLayoutDirection == -1) {
            mOffset3 = layoutState.mOffset;
            mOffset4 = mOffset3 - mConsumed;
            n15 = (mOffset = 0);
        }
        else {
            mOffset4 = layoutState.mOffset;
            mOffset3 = mConsumed + mOffset4;
            mOffset = (n15 = 0);
        }
        while (k < n2) {
            final View view4 = this.mSet[k];
            final LayoutParams layoutParams2 = (LayoutParams)view4.getLayoutParams();
            int n18;
            int n21;
            int n22;
            int n23;
            if (super.mOrientation == 1) {
                int n19;
                if (this.isLayoutRTL()) {
                    final int n17 = ((RecyclerView.LayoutManager)this).getPaddingLeft() + this.mCachedBorders[this.mSpanCount - layoutParams2.mSpanIndex];
                    final int decoratedMeasurementInOther = super.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                    n18 = n17;
                    n19 = n17 - decoratedMeasurementInOther;
                }
                else {
                    final int n20 = ((RecyclerView.LayoutManager)this).getPaddingLeft() + this.mCachedBorders[layoutParams2.mSpanIndex];
                    final int decoratedMeasurementInOther2 = super.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                    n19 = n20;
                    n18 = decoratedMeasurementInOther2 + n20;
                }
                n21 = mOffset;
                n22 = n15;
                n23 = n19;
            }
            else {
                final int n24 = ((RecyclerView.LayoutManager)this).getPaddingTop() + this.mCachedBorders[layoutParams2.mSpanIndex];
                final int decoratedMeasurementInOther3 = super.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                n22 = n24;
                n23 = mOffset4;
                n21 = decoratedMeasurementInOther3 + n24;
                n18 = mOffset3;
            }
            ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(view4, n23, n22, n18, n21);
            if (((RecyclerView.LayoutParams)layoutParams2).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams2).isItemChanged()) {
                layoutChunkResult.mIgnoreConsumed = true;
            }
            layoutChunkResult.mFocusable |= view4.hasFocusable();
            ++k;
            mOffset3 = n18;
            mOffset4 = n23;
            n15 = n22;
            mOffset = n21;
        }
        Arrays.fill(this.mSet, null);
    }
    
    @Override
    void onAnchorReady(final Recycler recycler, final State state, final AnchorInfo anchorInfo, final int n) {
        super.onAnchorReady(recycler, state, anchorInfo, n);
        this.updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            this.ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, n);
        }
        this.ensureViewSet();
    }
    
    @Override
    public View onFocusSearchFailed(View view, int n, final Recycler recycler, final State state) {
        final View containingItemView = ((RecyclerView.LayoutManager)this).findContainingItemView(view);
        View view2 = null;
        if (containingItemView == null) {
            return null;
        }
        final LayoutParams layoutParams = (LayoutParams)containingItemView.getLayoutParams();
        final int mSpanIndex = layoutParams.mSpanIndex;
        final int b = layoutParams.mSpanSize + mSpanIndex;
        if (super.onFocusSearchFailed(view, n, recycler, state) == null) {
            return null;
        }
        if (this.convertFocusDirectionToLayoutDirection(n) == 1 != super.mShouldReverseLayout) {
            n = 1;
        }
        else {
            n = 0;
        }
        int n2;
        int childCount;
        if (n != 0) {
            n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
            childCount = (n2 = -1);
        }
        else {
            childCount = ((RecyclerView.LayoutManager)this).getChildCount();
            n2 = 1;
            n = 0;
        }
        final boolean b2 = super.mOrientation == 1 && this.isLayoutRTL();
        final int spanGroupIndex = this.getSpanGroupIndex(recycler, state, n);
        int mSpanIndex3;
        int mSpanIndex2 = mSpanIndex3 = -1;
        final int n3 = 0;
        final int n4 = 0;
        int i = n;
        view = null;
        n = n4;
        final int n5 = childCount;
        int n6 = n3;
        while (i != n5) {
            final int spanGroupIndex2 = this.getSpanGroupIndex(recycler, state, i);
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            if (child == containingItemView) {
                break;
            }
            if (child.hasFocusable() && spanGroupIndex2 != spanGroupIndex) {
                if (view2 != null) {
                    break;
                }
            }
            else {
                final LayoutParams layoutParams2 = (LayoutParams)child.getLayoutParams();
                final int mSpanIndex4 = layoutParams2.mSpanIndex;
                final int a = layoutParams2.mSpanSize + mSpanIndex4;
                if (child.hasFocusable() && mSpanIndex4 == mSpanIndex && a == b) {
                    return child;
                }
                int n10 = 0;
                Label_0479: {
                    Label_0326: {
                        if ((child.hasFocusable() || view2 != null) && (child.hasFocusable() || view != null)) {
                            final int n7 = Math.min(a, b) - Math.max(mSpanIndex4, mSpanIndex);
                            if (child.hasFocusable()) {
                                if (n7 > n6 || (n7 == n6 && b2 == mSpanIndex4 > mSpanIndex2)) {
                                    break Label_0326;
                                }
                            }
                            else if (view2 == null) {
                                final int n8 = 1;
                                boolean b3 = true;
                                if (((RecyclerView.LayoutManager)this).isViewPartiallyVisible(child, false, true)) {
                                    final int n9 = n;
                                    if (n7 > n9) {
                                        n10 = n8;
                                        break Label_0479;
                                    }
                                    if (n7 == n9) {
                                        if (mSpanIndex4 <= mSpanIndex3) {
                                            b3 = false;
                                        }
                                        if (b2 == b3) {
                                            break Label_0326;
                                        }
                                    }
                                }
                            }
                            n10 = 0;
                            break Label_0479;
                        }
                    }
                    n10 = 1;
                }
                if (n10 != 0) {
                    if (child.hasFocusable()) {
                        mSpanIndex2 = layoutParams2.mSpanIndex;
                        final int min = Math.min(a, b);
                        final int max = Math.max(mSpanIndex4, mSpanIndex);
                        view2 = child;
                        n6 = min - max;
                    }
                    else {
                        mSpanIndex3 = layoutParams2.mSpanIndex;
                        n = Math.min(a, b) - Math.max(mSpanIndex4, mSpanIndex);
                        view = child;
                    }
                }
            }
            i += n2;
        }
        if (view2 != null) {
            view = view2;
        }
        return view;
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof LayoutParams)) {
            super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
            return;
        }
        final LayoutParams layoutParams2 = (LayoutParams)layoutParams;
        final int spanGroupIndex = this.getSpanGroupIndex(recycler, state, ((RecyclerView.LayoutParams)layoutParams2).getViewLayoutPosition());
        if (super.mOrientation == 0) {
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), spanGroupIndex, 1, false, false));
        }
        else {
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(spanGroupIndex, 1, layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), false, false));
        }
    }
    
    @Override
    public void onItemsAdded(final RecyclerView recyclerView, final int n, final int n2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }
    
    @Override
    public void onItemsChanged(final RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }
    
    @Override
    public void onItemsMoved(final RecyclerView recyclerView, final int n, final int n2, final int n3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }
    
    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, final int n, final int n2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }
    
    @Override
    public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2, final Object o) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }
    
    @Override
    public void onLayoutChildren(final Recycler recycler, final State state) {
        if (state.isPreLayout()) {
            this.cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        this.clearPreLayoutSpanMappingCache();
    }
    
    @Override
    public void onLayoutCompleted(final State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }
    
    @Override
    public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
        this.updateMeasurements();
        this.ensureViewSet();
        return super.scrollHorizontallyBy(n, recycler, state);
    }
    
    @Override
    public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
        this.updateMeasurements();
        this.ensureViewSet();
        return super.scrollVerticallyBy(n, recycler, state);
    }
    
    @Override
    public void setMeasuredDimension(final Rect rect, int chooseSize, int chooseSize2) {
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(rect, chooseSize, chooseSize2);
        }
        final int n = ((RecyclerView.LayoutManager)this).getPaddingLeft() + ((RecyclerView.LayoutManager)this).getPaddingRight();
        final int n2 = ((RecyclerView.LayoutManager)this).getPaddingTop() + ((RecyclerView.LayoutManager)this).getPaddingBottom();
        if (super.mOrientation == 1) {
            chooseSize2 = RecyclerView.LayoutManager.chooseSize(chooseSize2, rect.height() + n2, ((RecyclerView.LayoutManager)this).getMinimumHeight());
            final int[] mCachedBorders = this.mCachedBorders;
            final int chooseSize3 = RecyclerView.LayoutManager.chooseSize(chooseSize, mCachedBorders[mCachedBorders.length - 1] + n, ((RecyclerView.LayoutManager)this).getMinimumWidth());
            chooseSize = chooseSize2;
            chooseSize2 = chooseSize3;
        }
        else {
            chooseSize = RecyclerView.LayoutManager.chooseSize(chooseSize, rect.width() + n, ((RecyclerView.LayoutManager)this).getMinimumWidth());
            final int[] mCachedBorders2 = this.mCachedBorders;
            final int chooseSize4 = RecyclerView.LayoutManager.chooseSize(chooseSize2, mCachedBorders2[mCachedBorders2.length - 1] + n2, ((RecyclerView.LayoutManager)this).getMinimumHeight());
            chooseSize2 = chooseSize;
            chooseSize = chooseSize4;
        }
        ((RecyclerView.LayoutManager)this).setMeasuredDimension(chooseSize2, chooseSize);
    }
    
    public void setSpanCount(final int n) {
        if (n == this.mSpanCount) {
            return;
        }
        this.mPendingSpanCountChange = true;
        if (n >= 1) {
            this.mSpanCount = n;
            this.mSpanSizeLookup.invalidateSpanIndexCache();
            ((RecyclerView.LayoutManager)this).requestLayout();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Span count should be at least 1. Provided ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void setSpanSizeLookup(final SpanSizeLookup mSpanSizeLookup) {
        this.mSpanSizeLookup = mSpanSizeLookup;
    }
    
    @Override
    public void setStackFromEnd(final boolean b) {
        if (!b) {
            super.setStackFromEnd(false);
            return;
        }
        throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }
    
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return super.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }
    
    public static final class DefaultSpanSizeLookup extends SpanSizeLookup
    {
        @Override
        public int getSpanIndex(final int n, final int n2) {
            return n % n2;
        }
        
        @Override
        public int getSpanSize(final int n) {
            return 1;
        }
    }
    
    public static class LayoutParams extends RecyclerView.LayoutParams
    {
        int mSpanIndex;
        int mSpanSize;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public int getSpanIndex() {
            return this.mSpanIndex;
        }
        
        public int getSpanSize() {
            return this.mSpanSize;
        }
    }
    
    public abstract static class SpanSizeLookup
    {
        private boolean mCacheSpanGroupIndices;
        private boolean mCacheSpanIndices;
        final SparseIntArray mSpanGroupIndexCache;
        final SparseIntArray mSpanIndexCache;
        
        public SpanSizeLookup() {
            this.mSpanIndexCache = new SparseIntArray();
            this.mSpanGroupIndexCache = new SparseIntArray();
            this.mCacheSpanIndices = false;
            this.mCacheSpanGroupIndices = false;
        }
        
        static int findFirstKeyLessThan(final SparseIntArray sparseIntArray, int n) {
            int n2 = sparseIntArray.size() - 1;
            int i = 0;
            while (i <= n2) {
                final int n3 = i + n2 >>> 1;
                if (sparseIntArray.keyAt(n3) < n) {
                    i = n3 + 1;
                }
                else {
                    n2 = n3 - 1;
                }
            }
            n = i - 1;
            if (n >= 0 && n < sparseIntArray.size()) {
                return sparseIntArray.keyAt(n);
            }
            return -1;
        }
        
        int getCachedSpanGroupIndex(final int n, int spanGroupIndex) {
            if (!this.mCacheSpanGroupIndices) {
                return this.getSpanGroupIndex(n, spanGroupIndex);
            }
            final int value = this.mSpanGroupIndexCache.get(n, -1);
            if (value != -1) {
                return value;
            }
            spanGroupIndex = this.getSpanGroupIndex(n, spanGroupIndex);
            this.mSpanGroupIndexCache.put(n, spanGroupIndex);
            return spanGroupIndex;
        }
        
        int getCachedSpanIndex(final int n, int spanIndex) {
            if (!this.mCacheSpanIndices) {
                return this.getSpanIndex(n, spanIndex);
            }
            final int value = this.mSpanIndexCache.get(n, -1);
            if (value != -1) {
                return value;
            }
            spanIndex = this.getSpanIndex(n, spanIndex);
            this.mSpanIndexCache.put(n, spanIndex);
            return spanIndex;
        }
        
        public int getSpanGroupIndex(int n, final int n2) {
            int n5 = 0;
            int n6 = 0;
            int n7 = 0;
            Label_0091: {
                if (this.mCacheSpanGroupIndices) {
                    final int firstKeyLessThan = findFirstKeyLessThan(this.mSpanGroupIndexCache, n);
                    if (firstKeyLessThan != -1) {
                        final int value = this.mSpanGroupIndexCache.get(firstKeyLessThan);
                        final int n3 = firstKeyLessThan + 1;
                        final int n4 = this.getCachedSpanIndex(firstKeyLessThan, n2) + this.getSpanSize(firstKeyLessThan);
                        n5 = value;
                        n6 = n3;
                        if ((n7 = n4) == n2) {
                            n5 = value + 1;
                            n7 = 0;
                            n6 = n3;
                        }
                        break Label_0091;
                    }
                }
                n5 = 0;
                n6 = (n7 = n5);
            }
            final int spanSize = this.getSpanSize(n);
            int n8 = n7;
            int i = n6;
            int n9 = n5;
            while (i < n) {
                final int spanSize2 = this.getSpanSize(i);
                final int n10 = n8 + spanSize2;
                int n11;
                int n12;
                if (n10 == n2) {
                    n11 = n9 + 1;
                    n12 = 0;
                }
                else {
                    n11 = n9;
                    if ((n12 = n10) > n2) {
                        n11 = n9 + 1;
                        n12 = spanSize2;
                    }
                }
                ++i;
                n9 = n11;
                n8 = n12;
            }
            n = n9;
            if (n8 + spanSize > n2) {
                n = n9 + 1;
            }
            return n;
        }
        
        public int getSpanIndex(final int n, final int n2) {
            final int spanSize = this.getSpanSize(n);
            if (spanSize == n2) {
                return 0;
            }
            while (true) {
                int firstKeyLessThan = 0;
                Label_0122: {
                    if (this.mCacheSpanIndices) {
                        firstKeyLessThan = findFirstKeyLessThan(this.mSpanIndexCache, n);
                        if (firstKeyLessThan >= 0) {
                            final int n3 = this.mSpanIndexCache.get(firstKeyLessThan) + this.getSpanSize(firstKeyLessThan);
                            break Label_0122;
                        }
                    }
                    int n3;
                    final int n4 = n3 = 0;
                    if (n4 < n) {
                        final int spanSize2 = this.getSpanSize(n4);
                        final int n5 = n3 + spanSize2;
                        if (n5 == n2) {
                            n3 = 0;
                            firstKeyLessThan = n4;
                        }
                        else {
                            firstKeyLessThan = n4;
                            if ((n3 = n5) > n2) {
                                n3 = spanSize2;
                                firstKeyLessThan = n4;
                            }
                        }
                    }
                    else {
                        if (spanSize + n3 <= n2) {
                            return n3;
                        }
                        return 0;
                    }
                }
                final int n4 = firstKeyLessThan + 1;
                continue;
            }
        }
        
        public abstract int getSpanSize(final int p0);
        
        public void invalidateSpanGroupIndexCache() {
            this.mSpanGroupIndexCache.clear();
        }
        
        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }
    }
}
