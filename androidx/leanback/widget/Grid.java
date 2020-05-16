// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import java.util.Arrays;
import android.util.SparseIntArray;
import androidx.recyclerview.widget.RecyclerView;
import androidx.collection.CircularIntArray;

abstract class Grid
{
    protected int mFirstVisibleIndex;
    protected int mLastVisibleIndex;
    protected int mNumRows;
    protected Provider mProvider;
    protected boolean mReversedFlow;
    protected int mSpacing;
    protected int mStartIndex;
    Object[] mTmpItem;
    protected CircularIntArray[] mTmpItemPositionsInRows;
    
    Grid() {
        this.mTmpItem = new Object[1];
        this.mFirstVisibleIndex = -1;
        this.mLastVisibleIndex = -1;
        this.mStartIndex = -1;
    }
    
    public static Grid createGrid(final int numRows) {
        Grid grid;
        if (numRows == 1) {
            grid = new SingleRow();
        }
        else {
            grid = new StaggeredGridDefault();
            grid.setNumRows(numRows);
        }
        return grid;
    }
    
    private void resetVisibleIndexIfEmpty() {
        if (this.mLastVisibleIndex < this.mFirstVisibleIndex) {
            this.resetVisibleIndex();
        }
    }
    
    public boolean appendOneColumnVisibleItems() {
        int n;
        if (this.mReversedFlow) {
            n = Integer.MAX_VALUE;
        }
        else {
            n = Integer.MIN_VALUE;
        }
        return this.appendVisibleItems(n, true);
    }
    
    public final void appendVisibleItems(final int n) {
        this.appendVisibleItems(n, false);
    }
    
    protected abstract boolean appendVisibleItems(final int p0, final boolean p1);
    
    protected final boolean checkAppendOverLimit(final int n) {
        final int mLastVisibleIndex = this.mLastVisibleIndex;
        boolean b = false;
        if (mLastVisibleIndex < 0) {
            return false;
        }
        if (this.mReversedFlow) {
            if (this.findRowMin(true, null) > n + this.mSpacing) {
                return b;
            }
        }
        else if (this.findRowMax(false, null) < n - this.mSpacing) {
            return b;
        }
        b = true;
        return b;
    }
    
    protected final boolean checkPrependOverLimit(final int n) {
        final int mLastVisibleIndex = this.mLastVisibleIndex;
        boolean b = false;
        if (mLastVisibleIndex < 0) {
            return false;
        }
        if (this.mReversedFlow) {
            if (this.findRowMax(false, null) < n - this.mSpacing) {
                return b;
            }
        }
        else if (this.findRowMin(true, null) > n + this.mSpacing) {
            return b;
        }
        b = true;
        return b;
    }
    
    public void collectAdjacentPrefetchPositions(final int n, final int n2, final RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
    }
    
    public void fillDisappearingItems(final int[] array, int n, final SparseIntArray sparseIntArray) {
        final int lastVisibleIndex = this.getLastVisibleIndex();
        int binarySearch;
        if (lastVisibleIndex >= 0) {
            binarySearch = Arrays.binarySearch(array, 0, n, lastVisibleIndex);
        }
        else {
            binarySearch = 0;
        }
        if (binarySearch < 0) {
            int i = -binarySearch - 1;
            int n2;
            if (this.mReversedFlow) {
                n2 = this.mProvider.getEdge(lastVisibleIndex) - this.mProvider.getSize(lastVisibleIndex) - this.mSpacing;
            }
            else {
                n2 = this.mProvider.getEdge(lastVisibleIndex) + this.mProvider.getSize(lastVisibleIndex) + this.mSpacing;
            }
            while (i < n) {
                final int n3 = array[i];
                int value = sparseIntArray.get(n3);
                if (value < 0) {
                    value = 0;
                }
                final int item = this.mProvider.createItem(n3, true, this.mTmpItem, true);
                this.mProvider.addItem(this.mTmpItem[0], n3, item, value, n2);
                if (this.mReversedFlow) {
                    n2 = n2 - item - this.mSpacing;
                }
                else {
                    n2 = n2 + item + this.mSpacing;
                }
                ++i;
            }
        }
        final int firstVisibleIndex = this.getFirstVisibleIndex();
        if (firstVisibleIndex >= 0) {
            n = Arrays.binarySearch(array, 0, n, firstVisibleIndex);
        }
        else {
            n = 0;
        }
        if (n < 0) {
            int j = -n - 2;
            if (this.mReversedFlow) {
                n = this.mProvider.getEdge(firstVisibleIndex);
            }
            else {
                n = this.mProvider.getEdge(firstVisibleIndex);
            }
            while (j >= 0) {
                final int n4 = array[j];
                int value2 = sparseIntArray.get(n4);
                if (value2 < 0) {
                    value2 = 0;
                }
                final int item2 = this.mProvider.createItem(n4, false, this.mTmpItem, true);
                if (this.mReversedFlow) {
                    n = n + this.mSpacing + item2;
                }
                else {
                    n = n - this.mSpacing - item2;
                }
                this.mProvider.addItem(this.mTmpItem[0], n4, item2, value2, n);
                --j;
            }
        }
    }
    
    protected abstract int findRowMax(final boolean p0, final int p1, final int[] p2);
    
    public final int findRowMax(final boolean b, final int[] array) {
        int n;
        if (this.mReversedFlow) {
            n = this.mFirstVisibleIndex;
        }
        else {
            n = this.mLastVisibleIndex;
        }
        return this.findRowMax(b, n, array);
    }
    
    protected abstract int findRowMin(final boolean p0, final int p1, final int[] p2);
    
    public final int findRowMin(final boolean b, final int[] array) {
        int n;
        if (this.mReversedFlow) {
            n = this.mLastVisibleIndex;
        }
        else {
            n = this.mFirstVisibleIndex;
        }
        return this.findRowMin(b, n, array);
    }
    
    public final int getFirstVisibleIndex() {
        return this.mFirstVisibleIndex;
    }
    
    public final CircularIntArray[] getItemPositionsInRows() {
        return this.getItemPositionsInRows(this.getFirstVisibleIndex(), this.getLastVisibleIndex());
    }
    
    public abstract CircularIntArray[] getItemPositionsInRows(final int p0, final int p1);
    
    public final int getLastVisibleIndex() {
        return this.mLastVisibleIndex;
    }
    
    public abstract Location getLocation(final int p0);
    
    public int getNumRows() {
        return this.mNumRows;
    }
    
    public final int getRowIndex(final int n) {
        final Location location = this.getLocation(n);
        if (location == null) {
            return -1;
        }
        return location.row;
    }
    
    public void invalidateItemsAfter(final int start) {
        if (start < 0) {
            return;
        }
        final int mLastVisibleIndex = this.mLastVisibleIndex;
        if (mLastVisibleIndex < 0) {
            return;
        }
        if (mLastVisibleIndex >= start) {
            this.mLastVisibleIndex = start - 1;
        }
        this.resetVisibleIndexIfEmpty();
        if (this.getFirstVisibleIndex() < 0) {
            this.setStart(start);
        }
    }
    
    public boolean isReversedFlow() {
        return this.mReversedFlow;
    }
    
    public final boolean prependOneColumnVisibleItems() {
        int n;
        if (this.mReversedFlow) {
            n = Integer.MIN_VALUE;
        }
        else {
            n = Integer.MAX_VALUE;
        }
        return this.prependVisibleItems(n, true);
    }
    
    public final void prependVisibleItems(final int n) {
        this.prependVisibleItems(n, false);
    }
    
    protected abstract boolean prependVisibleItems(final int p0, final boolean p1);
    
    public void removeInvisibleItemsAtEnd(final int n, final int n2) {
        while (true) {
            final int mLastVisibleIndex = this.mLastVisibleIndex;
            if (mLastVisibleIndex < this.mFirstVisibleIndex || mLastVisibleIndex <= n) {
                break;
            }
            final boolean mReversedFlow = this.mReversedFlow;
            boolean b = false;
            Label_0066: {
                if (!mReversedFlow) {
                    if (this.mProvider.getEdge(mLastVisibleIndex) < n2) {
                        break Label_0066;
                    }
                }
                else if (this.mProvider.getEdge(mLastVisibleIndex) > n2) {
                    break Label_0066;
                }
                b = true;
            }
            if (!b) {
                break;
            }
            this.mProvider.removeItem(this.mLastVisibleIndex);
            --this.mLastVisibleIndex;
        }
        this.resetVisibleIndexIfEmpty();
    }
    
    public void removeInvisibleItemsAtFront(final int n, final int n2) {
        while (true) {
            final int mLastVisibleIndex = this.mLastVisibleIndex;
            final int mFirstVisibleIndex = this.mFirstVisibleIndex;
            if (mLastVisibleIndex < mFirstVisibleIndex || mFirstVisibleIndex >= n) {
                break;
            }
            final int size = this.mProvider.getSize(mFirstVisibleIndex);
            final boolean mReversedFlow = this.mReversedFlow;
            boolean b = false;
            Label_0093: {
                if (!mReversedFlow) {
                    if (this.mProvider.getEdge(this.mFirstVisibleIndex) + size > n2) {
                        break Label_0093;
                    }
                }
                else if (this.mProvider.getEdge(this.mFirstVisibleIndex) - size < n2) {
                    break Label_0093;
                }
                b = true;
            }
            if (!b) {
                break;
            }
            this.mProvider.removeItem(this.mFirstVisibleIndex);
            ++this.mFirstVisibleIndex;
        }
        this.resetVisibleIndexIfEmpty();
    }
    
    public void resetVisibleIndex() {
        this.mLastVisibleIndex = -1;
        this.mFirstVisibleIndex = -1;
    }
    
    void setNumRows(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
        if (this.mNumRows == i) {
            return;
        }
        this.mNumRows = i;
        this.mTmpItemPositionsInRows = new CircularIntArray[i];
        for (i = 0; i < this.mNumRows; ++i) {
            this.mTmpItemPositionsInRows[i] = new CircularIntArray();
        }
    }
    
    public void setProvider(final Provider mProvider) {
        this.mProvider = mProvider;
    }
    
    public final void setReversedFlow(final boolean mReversedFlow) {
        this.mReversedFlow = mReversedFlow;
    }
    
    public final void setSpacing(final int mSpacing) {
        this.mSpacing = mSpacing;
    }
    
    public void setStart(final int mStartIndex) {
        this.mStartIndex = mStartIndex;
    }
    
    public static class Location
    {
        public int row;
        
        public Location(final int row) {
            this.row = row;
        }
    }
    
    public interface Provider
    {
        void addItem(final Object p0, final int p1, final int p2, final int p3, final int p4);
        
        int createItem(final int p0, final boolean p1, final Object[] p2, final boolean p3);
        
        int getCount();
        
        int getEdge(final int p0);
        
        int getMinIndex();
        
        int getSize(final int p0);
        
        void removeItem(final int p0);
    }
}
