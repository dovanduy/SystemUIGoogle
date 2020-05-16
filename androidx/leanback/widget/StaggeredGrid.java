// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.collection.CircularIntArray;
import androidx.collection.CircularArray;

abstract class StaggeredGrid extends Grid
{
    protected int mFirstIndex;
    protected CircularArray<Location> mLocations;
    protected Object mPendingItem;
    protected int mPendingItemSize;
    
    StaggeredGrid() {
        this.mLocations = new CircularArray<Location>(64);
        this.mFirstIndex = -1;
    }
    
    private int calculateOffsetAfterLastItem(int i) {
        while (true) {
            for (int j = this.getLastIndex(); j >= this.mFirstIndex; --j) {
                if (this.getLocation(j).row == i) {
                    i = 1;
                    if (i == 0) {
                        j = this.getLastIndex();
                    }
                    if (this.isReversedFlow()) {
                        i = -this.getLocation(j).size - super.mSpacing;
                    }
                    else {
                        i = this.getLocation(j).size + super.mSpacing;
                    }
                    final int n = j + 1;
                    int n2 = i;
                    for (i = n; i <= this.getLastIndex(); ++i) {
                        n2 -= this.getLocation(i).offset;
                    }
                    return n2;
                }
            }
            i = 0;
            continue;
        }
    }
    
    protected final boolean appendVisbleItemsWithCache(final int n, final boolean b) {
        if (this.mLocations.size() == 0) {
            return false;
        }
        final int count = super.mProvider.getCount();
        final int mLastVisibleIndex = super.mLastVisibleIndex;
        int mStartIndex;
        int edge;
        if (mLastVisibleIndex >= 0) {
            mStartIndex = mLastVisibleIndex + 1;
            edge = super.mProvider.getEdge(mLastVisibleIndex);
        }
        else {
            mStartIndex = super.mStartIndex;
            if (mStartIndex == -1) {
                mStartIndex = 0;
            }
            if (mStartIndex > this.getLastIndex() + 1 || mStartIndex < this.getFirstIndex()) {
                this.mLocations.clear();
                return false;
            }
            if (mStartIndex > this.getLastIndex()) {
                return false;
            }
            edge = Integer.MAX_VALUE;
        }
        int lastIndex = this.getLastIndex();
        int edge2 = edge;
        while (mStartIndex < count && mStartIndex <= lastIndex) {
            final Location location = this.getLocation(mStartIndex);
            int n2;
            if ((n2 = edge2) != Integer.MAX_VALUE) {
                n2 = edge2 + location.offset;
            }
            final int row = location.row;
            final int item = super.mProvider.createItem(mStartIndex, true, super.mTmpItem, false);
            if (item != location.size) {
                location.size = item;
                this.mLocations.removeFromEnd(lastIndex - mStartIndex);
                lastIndex = mStartIndex;
            }
            super.mLastVisibleIndex = mStartIndex;
            if (super.mFirstVisibleIndex < 0) {
                super.mFirstVisibleIndex = mStartIndex;
            }
            super.mProvider.addItem(super.mTmpItem[0], mStartIndex, item, row, n2);
            if (!b && this.checkAppendOverLimit(n)) {
                return true;
            }
            if ((edge2 = n2) == Integer.MAX_VALUE) {
                edge2 = super.mProvider.getEdge(mStartIndex);
            }
            if (row == super.mNumRows - 1 && b) {
                return true;
            }
            ++mStartIndex;
        }
        return false;
    }
    
    protected final int appendVisibleItemToRow(final int mFirstVisibleIndex, final int n, final int n2) {
        final int mLastVisibleIndex = super.mLastVisibleIndex;
        if (mLastVisibleIndex >= 0 && (mLastVisibleIndex != this.getLastIndex() || super.mLastVisibleIndex != mFirstVisibleIndex - 1)) {
            throw new IllegalStateException();
        }
        final int mLastVisibleIndex2 = super.mLastVisibleIndex;
        int calculateOffsetAfterLastItem;
        if (mLastVisibleIndex2 < 0) {
            if (this.mLocations.size() > 0 && mFirstVisibleIndex == this.getLastIndex() + 1) {
                calculateOffsetAfterLastItem = this.calculateOffsetAfterLastItem(n);
            }
            else {
                calculateOffsetAfterLastItem = 0;
            }
        }
        else {
            calculateOffsetAfterLastItem = n2 - super.mProvider.getEdge(mLastVisibleIndex2);
        }
        final Location location = new Location(n, calculateOffsetAfterLastItem, 0);
        this.mLocations.addLast(location);
        Object mPendingItem = this.mPendingItem;
        if (mPendingItem != null) {
            location.size = this.mPendingItemSize;
            this.mPendingItem = null;
        }
        else {
            location.size = super.mProvider.createItem(mFirstVisibleIndex, true, super.mTmpItem, false);
            mPendingItem = super.mTmpItem[0];
        }
        if (this.mLocations.size() == 1) {
            super.mLastVisibleIndex = mFirstVisibleIndex;
            super.mFirstVisibleIndex = mFirstVisibleIndex;
            this.mFirstIndex = mFirstVisibleIndex;
        }
        else {
            final int mLastVisibleIndex3 = super.mLastVisibleIndex;
            if (mLastVisibleIndex3 < 0) {
                super.mLastVisibleIndex = mFirstVisibleIndex;
                super.mFirstVisibleIndex = mFirstVisibleIndex;
            }
            else {
                super.mLastVisibleIndex = mLastVisibleIndex3 + 1;
            }
        }
        super.mProvider.addItem(mPendingItem, mFirstVisibleIndex, location.size, n, n2);
        return location.size;
    }
    
    @Override
    protected final boolean appendVisibleItems(final int n, final boolean b) {
        if (super.mProvider.getCount() == 0) {
            return false;
        }
        if (!b && this.checkAppendOverLimit(n)) {
            return false;
        }
        try {
            boolean appendVisibleItemsWithoutCache;
            if (this.appendVisbleItemsWithCache(n, b)) {
                appendVisibleItemsWithoutCache = true;
            }
            else {
                appendVisibleItemsWithoutCache = this.appendVisibleItemsWithoutCache(n, b);
                super.mTmpItem[0] = null;
            }
            return appendVisibleItemsWithoutCache;
        }
        finally {
            super.mTmpItem[0] = null;
            this.mPendingItem = null;
        }
    }
    
    protected abstract boolean appendVisibleItemsWithoutCache(final int p0, final boolean p1);
    
    public final int getFirstIndex() {
        return this.mFirstIndex;
    }
    
    @Override
    public final CircularIntArray[] getItemPositionsInRows(int i, final int n) {
        for (int j = 0; j < super.mNumRows; ++j) {
            super.mTmpItemPositionsInRows[j].clear();
        }
        if (i >= 0) {
            while (i <= n) {
                final CircularIntArray circularIntArray = super.mTmpItemPositionsInRows[this.getLocation(i).row];
                if (circularIntArray.size() > 0 && circularIntArray.getLast() == i - 1) {
                    circularIntArray.popLast();
                    circularIntArray.addLast(i);
                }
                else {
                    circularIntArray.addLast(i);
                    circularIntArray.addLast(i);
                }
                ++i;
            }
        }
        return super.mTmpItemPositionsInRows;
    }
    
    public final int getLastIndex() {
        return this.mFirstIndex + this.mLocations.size() - 1;
    }
    
    public final Location getLocation(int n) {
        n -= this.mFirstIndex;
        if (n >= 0 && n < this.mLocations.size()) {
            return this.mLocations.get(n);
        }
        return null;
    }
    
    @Override
    public void invalidateItemsAfter(final int n) {
        super.invalidateItemsAfter(n);
        this.mLocations.removeFromEnd(this.getLastIndex() - n + 1);
        if (this.mLocations.size() == 0) {
            this.mFirstIndex = -1;
        }
    }
    
    protected final boolean prependVisbleItemsWithCache(final int n, final boolean b) {
        if (this.mLocations.size() == 0) {
            return false;
        }
        final int mFirstVisibleIndex = super.mFirstVisibleIndex;
        int n2;
        int n3;
        int i;
        if (mFirstVisibleIndex >= 0) {
            n2 = super.mProvider.getEdge(mFirstVisibleIndex);
            n3 = this.getLocation(super.mFirstVisibleIndex).offset;
            i = super.mFirstVisibleIndex - 1;
        }
        else {
            n2 = Integer.MAX_VALUE;
            i = super.mStartIndex;
            if (i == -1) {
                i = 0;
            }
            if (i > this.getLastIndex() || i < this.getFirstIndex() - 1) {
                this.mLocations.clear();
                return false;
            }
            if (i < this.getFirstIndex()) {
                return false;
            }
            n3 = 0;
        }
        while (i >= Math.max(super.mProvider.getMinIndex(), this.mFirstIndex)) {
            final Location location = this.getLocation(i);
            final int row = location.row;
            final int item = super.mProvider.createItem(i, false, super.mTmpItem, false);
            if (item != location.size) {
                this.mLocations.removeFromStart(i + 1 - this.mFirstIndex);
                this.mFirstIndex = super.mFirstVisibleIndex;
                this.mPendingItem = super.mTmpItem[0];
                this.mPendingItemSize = item;
                return false;
            }
            super.mFirstVisibleIndex = i;
            if (super.mLastVisibleIndex < 0) {
                super.mLastVisibleIndex = i;
            }
            super.mProvider.addItem(super.mTmpItem[0], i, item, row, n2 - n3);
            if (!b && this.checkPrependOverLimit(n)) {
                return true;
            }
            n2 = super.mProvider.getEdge(i);
            n3 = location.offset;
            if (row == 0 && b) {
                return true;
            }
            --i;
        }
        return false;
    }
    
    protected final int prependVisibleItemToRow(final int mLastVisibleIndex, final int n, int n2) {
        final int mFirstVisibleIndex = super.mFirstVisibleIndex;
        if (mFirstVisibleIndex >= 0 && (mFirstVisibleIndex != this.getFirstIndex() || super.mFirstVisibleIndex != mLastVisibleIndex + 1)) {
            throw new IllegalStateException();
        }
        final int mFirstIndex = this.mFirstIndex;
        Location location;
        if (mFirstIndex >= 0) {
            location = this.getLocation(mFirstIndex);
        }
        else {
            location = null;
        }
        final int edge = super.mProvider.getEdge(this.mFirstIndex);
        final Location location2 = new Location(n, 0, 0);
        this.mLocations.addFirst(location2);
        Object mPendingItem = this.mPendingItem;
        if (mPendingItem != null) {
            location2.size = this.mPendingItemSize;
            this.mPendingItem = null;
        }
        else {
            location2.size = super.mProvider.createItem(mLastVisibleIndex, false, super.mTmpItem, false);
            mPendingItem = super.mTmpItem[0];
        }
        super.mFirstVisibleIndex = mLastVisibleIndex;
        this.mFirstIndex = mLastVisibleIndex;
        if (super.mLastVisibleIndex < 0) {
            super.mLastVisibleIndex = mLastVisibleIndex;
        }
        if (!super.mReversedFlow) {
            n2 -= location2.size;
        }
        else {
            n2 += location2.size;
        }
        if (location != null) {
            location.offset = edge - n2;
        }
        super.mProvider.addItem(mPendingItem, mLastVisibleIndex, location2.size, n, n2);
        return location2.size;
    }
    
    @Override
    protected final boolean prependVisibleItems(final int n, final boolean b) {
        if (super.mProvider.getCount() == 0) {
            return false;
        }
        if (!b && this.checkPrependOverLimit(n)) {
            return false;
        }
        try {
            boolean prependVisibleItemsWithoutCache;
            if (this.prependVisbleItemsWithCache(n, b)) {
                prependVisibleItemsWithoutCache = true;
            }
            else {
                prependVisibleItemsWithoutCache = this.prependVisibleItemsWithoutCache(n, b);
                super.mTmpItem[0] = null;
            }
            return prependVisibleItemsWithoutCache;
        }
        finally {
            super.mTmpItem[0] = null;
            this.mPendingItem = null;
        }
    }
    
    protected abstract boolean prependVisibleItemsWithoutCache(final int p0, final boolean p1);
    
    public static class Location extends Grid.Location
    {
        public int offset;
        public int size;
        
        public Location(final int n, final int offset, final int size) {
            super(n);
            this.offset = offset;
            this.size = size;
        }
    }
}
