// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.collection.CircularIntArray;
import androidx.recyclerview.widget.RecyclerView;

class SingleRow extends Grid
{
    private final Location mTmpLocation;
    
    SingleRow() {
        this.mTmpLocation = new Location(0);
        this.setNumRows(1);
    }
    
    @Override
    protected final boolean appendVisibleItems(final int n, final boolean b) {
        if (super.mProvider.getCount() == 0) {
            return false;
        }
        if (!b && this.checkAppendOverLimit(n)) {
            return false;
        }
        int i = this.getStartIndexForAppend();
        final boolean b2 = true;
        boolean b3 = false;
        while (i < super.mProvider.getCount()) {
            final int item = super.mProvider.createItem(i, true, super.mTmpItem, false);
            int n3;
            if (super.mFirstVisibleIndex >= 0 && super.mLastVisibleIndex >= 0) {
                if (super.mReversedFlow) {
                    final Provider mProvider = super.mProvider;
                    final int n2 = i - 1;
                    n3 = mProvider.getEdge(n2) - super.mProvider.getSize(n2) - super.mSpacing;
                }
                else {
                    final Provider mProvider2 = super.mProvider;
                    final int n4 = i - 1;
                    n3 = mProvider2.getEdge(n4) + super.mProvider.getSize(n4) + super.mSpacing;
                }
                super.mLastVisibleIndex = i;
            }
            else {
                if (super.mReversedFlow) {
                    n3 = Integer.MAX_VALUE;
                }
                else {
                    n3 = Integer.MIN_VALUE;
                }
                super.mFirstVisibleIndex = i;
                super.mLastVisibleIndex = i;
            }
            super.mProvider.addItem(super.mTmpItem[0], i, item, 0, n3);
            b3 = b2;
            if (b) {
                break;
            }
            if (this.checkAppendOverLimit(n)) {
                b3 = b2;
                break;
            }
            ++i;
            b3 = true;
        }
        return b3;
    }
    
    @Override
    public void collectAdjacentPrefetchPositions(final int n, int mSpacing, final RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int n2 = 0;
        Label_0156: {
            Label_0077: {
                if (super.mReversedFlow) {
                    if (mSpacing <= 0) {
                        break Label_0077;
                    }
                }
                else if (mSpacing >= 0) {
                    break Label_0077;
                }
                if (this.getFirstVisibleIndex() == 0) {
                    return;
                }
                n2 = this.getStartIndexForPrepend();
                final int edge = super.mProvider.getEdge(super.mFirstVisibleIndex);
                final boolean mReversedFlow = super.mReversedFlow;
                mSpacing = super.mSpacing;
                if (!mReversedFlow) {
                    mSpacing = -mSpacing;
                }
                mSpacing += edge;
                break Label_0156;
            }
            if (this.getLastVisibleIndex() == super.mProvider.getCount() - 1) {
                return;
            }
            n2 = this.getStartIndexForAppend();
            final int n3 = super.mProvider.getSize(super.mLastVisibleIndex) + super.mSpacing;
            final int edge2 = super.mProvider.getEdge(super.mLastVisibleIndex);
            mSpacing = n3;
            if (super.mReversedFlow) {
                mSpacing = -n3;
            }
            mSpacing += edge2;
        }
        layoutPrefetchRegistry.addPosition(n2, Math.abs(mSpacing - n));
    }
    
    @Override
    protected final int findRowMax(final boolean b, int edge, final int[] array) {
        if (array != null) {
            array[0] = 0;
            array[1] = edge;
        }
        if (super.mReversedFlow) {
            edge = super.mProvider.getEdge(edge);
        }
        else {
            edge = super.mProvider.getSize(edge) + super.mProvider.getEdge(edge);
        }
        return edge;
    }
    
    @Override
    protected final int findRowMin(final boolean b, int edge, final int[] array) {
        if (array != null) {
            array[0] = 0;
            array[1] = edge;
        }
        if (super.mReversedFlow) {
            edge = super.mProvider.getEdge(edge) - super.mProvider.getSize(edge);
        }
        else {
            edge = super.mProvider.getEdge(edge);
        }
        return edge;
    }
    
    @Override
    public final CircularIntArray[] getItemPositionsInRows(final int n, final int n2) {
        super.mTmpItemPositionsInRows[0].clear();
        super.mTmpItemPositionsInRows[0].addLast(n);
        super.mTmpItemPositionsInRows[0].addLast(n2);
        return super.mTmpItemPositionsInRows;
    }
    
    @Override
    public final Location getLocation(final int n) {
        return this.mTmpLocation;
    }
    
    int getStartIndexForAppend() {
        final int mLastVisibleIndex = super.mLastVisibleIndex;
        if (mLastVisibleIndex >= 0) {
            return mLastVisibleIndex + 1;
        }
        final int mStartIndex = super.mStartIndex;
        if (mStartIndex != -1) {
            return Math.min(mStartIndex, super.mProvider.getCount() - 1);
        }
        return 0;
    }
    
    int getStartIndexForPrepend() {
        final int mFirstVisibleIndex = super.mFirstVisibleIndex;
        if (mFirstVisibleIndex >= 0) {
            return mFirstVisibleIndex - 1;
        }
        final int mStartIndex = super.mStartIndex;
        if (mStartIndex != -1) {
            return Math.min(mStartIndex, super.mProvider.getCount() - 1);
        }
        return super.mProvider.getCount() - 1;
    }
    
    @Override
    protected final boolean prependVisibleItems(final int n, final boolean b) {
        if (super.mProvider.getCount() == 0) {
            return false;
        }
        if (!b && this.checkPrependOverLimit(n)) {
            return false;
        }
        final int minIndex = super.mProvider.getMinIndex();
        int i = this.getStartIndexForPrepend();
        final boolean b2 = true;
        boolean b3 = false;
        while (i >= minIndex) {
            final int item = super.mProvider.createItem(i, false, super.mTmpItem, false);
            int n2;
            if (super.mFirstVisibleIndex >= 0 && super.mLastVisibleIndex >= 0) {
                if (super.mReversedFlow) {
                    n2 = super.mProvider.getEdge(i + 1) + super.mSpacing + item;
                }
                else {
                    n2 = super.mProvider.getEdge(i + 1) - super.mSpacing - item;
                }
                super.mFirstVisibleIndex = i;
            }
            else {
                if (super.mReversedFlow) {
                    n2 = Integer.MIN_VALUE;
                }
                else {
                    n2 = Integer.MAX_VALUE;
                }
                super.mFirstVisibleIndex = i;
                super.mLastVisibleIndex = i;
            }
            super.mProvider.addItem(super.mTmpItem[0], i, item, 0, n2);
            b3 = b2;
            if (b) {
                break;
            }
            if (this.checkPrependOverLimit(n)) {
                b3 = b2;
                break;
            }
            --i;
            b3 = true;
        }
        return b3;
    }
}
