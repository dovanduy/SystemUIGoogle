// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

final class StaggeredGridDefault extends StaggeredGrid
{
    private int findRowEdgeLimitSearchIndex(final boolean b) {
        final int n = 0;
        int n2 = 0;
        if (b) {
            int n3;
            for (int i = super.mLastVisibleIndex; i >= super.mFirstVisibleIndex; --i, n2 = n3) {
                final int row = this.getLocation(i).row;
                if (row == 0) {
                    n3 = 1;
                }
                else if ((n3 = n2) != 0) {
                    n3 = n2;
                    if (row == super.mNumRows - 1) {
                        return i;
                    }
                }
            }
        }
        else {
            int j = super.mFirstVisibleIndex;
            int n4 = n;
            while (j <= super.mLastVisibleIndex) {
                final int row2 = this.getLocation(j).row;
                int n5;
                if (row2 == super.mNumRows - 1) {
                    n5 = 1;
                }
                else if ((n5 = n4) != 0) {
                    n5 = n4;
                    if (row2 == 0) {
                        return j;
                    }
                }
                ++j;
                n4 = n5;
            }
        }
        return -1;
    }
    
    @Override
    protected boolean appendVisibleItemsWithoutCache(final int n, final boolean b) {
        final int count = super.mProvider.getCount();
        final int mLastVisibleIndex = super.mLastVisibleIndex;
        int n4;
        int n5;
        int n7;
        int mStartIndex;
        if (mLastVisibleIndex >= 0) {
            if (mLastVisibleIndex < this.getLastIndex()) {
                return false;
            }
            final int mLastVisibleIndex2 = super.mLastVisibleIndex;
            final int n2 = mLastVisibleIndex2 + 1;
            int row = this.getLocation(mLastVisibleIndex2).row;
            final int rowEdgeLimitSearchIndex = this.findRowEdgeLimitSearchIndex(true);
            int n3;
            if (rowEdgeLimitSearchIndex < 0) {
                n3 = Integer.MIN_VALUE;
                for (int i = 0; i < super.mNumRows; ++i) {
                    if (super.mReversedFlow) {
                        n3 = this.getRowMin(i);
                    }
                    else {
                        n3 = this.getRowMax(i);
                    }
                    if (n3 != Integer.MIN_VALUE) {
                        break;
                    }
                }
            }
            else if (super.mReversedFlow) {
                n3 = this.findRowMin(false, rowEdgeLimitSearchIndex, null);
            }
            else {
                n3 = this.findRowMax(true, rowEdgeLimitSearchIndex, null);
            }
            Label_0256: {
                if (super.mReversedFlow) {
                    n4 = row;
                    if (this.getRowMin(row) > (n5 = n3)) {
                        break Label_0256;
                    }
                }
                else {
                    n4 = row;
                    if (this.getRowMax(row) < (n5 = n3)) {
                        break Label_0256;
                    }
                }
                n4 = ++row;
                n5 = n3;
                if (row == super.mNumRows) {
                    int n6;
                    if (super.mReversedFlow) {
                        n6 = this.findRowMin(false, null);
                    }
                    else {
                        n6 = this.findRowMax(true, null);
                    }
                    n4 = 0;
                    n5 = n6;
                }
            }
            n7 = 1;
            mStartIndex = n2;
        }
        else {
            mStartIndex = super.mStartIndex;
            if (mStartIndex == -1) {
                mStartIndex = 0;
            }
            int n8;
            if (super.mLocations.size() > 0) {
                n8 = this.getLocation(this.getLastIndex()).row + 1;
            }
            else {
                n8 = mStartIndex;
            }
            n4 = n8 % super.mNumRows;
            n7 = (n5 = 0);
        }
        boolean b2 = false;
        int n9 = n5;
        int n10 = mStartIndex;
        int n11 = n4;
        while (true) {
            if (n11 < super.mNumRows) {
                if (n10 == count || (!b && this.checkAppendOverLimit(n))) {
                    return b2;
                }
                int n12;
                if (super.mReversedFlow) {
                    n12 = this.getRowMin(n11);
                }
                else {
                    n12 = this.getRowMax(n11);
                }
                int n14 = 0;
                Label_0589: {
                    int mSpacing = 0;
                    Label_0447: {
                        int mSpacing2;
                        if (n12 != Integer.MAX_VALUE && n12 != Integer.MIN_VALUE) {
                            if (!super.mReversedFlow) {
                                mSpacing = super.mSpacing;
                                break Label_0447;
                            }
                            mSpacing2 = super.mSpacing;
                        }
                        else if (n11 == 0) {
                            int n13;
                            if (super.mReversedFlow) {
                                n13 = this.getRowMin(super.mNumRows - 1);
                            }
                            else {
                                n13 = this.getRowMax(super.mNumRows - 1);
                            }
                            n14 = n13;
                            if (n13 == Integer.MAX_VALUE || (n14 = n13) == Integer.MIN_VALUE) {
                                break Label_0589;
                            }
                            if (!super.mReversedFlow) {
                                final int mSpacing3 = super.mSpacing;
                                n12 = n13;
                                mSpacing = mSpacing3;
                                break Label_0447;
                            }
                            final int mSpacing4 = super.mSpacing;
                            n12 = n13;
                            mSpacing2 = mSpacing4;
                        }
                        else {
                            if (super.mReversedFlow) {
                                n14 = this.getRowMax(n11 - 1);
                                break Label_0589;
                            }
                            n14 = this.getRowMin(n11 - 1);
                            break Label_0589;
                        }
                        mSpacing = -mSpacing2;
                    }
                    n14 = n12 + mSpacing;
                }
                int n15 = n10 + 1;
                int n16 = this.appendVisibleItemToRow(n10, n11, n14);
                int n18;
                int n19;
                int n20;
                if (n7 != 0) {
                    int n17 = n14;
                    while (true) {
                        if (super.mReversedFlow) {
                            n18 = n7;
                            n19 = n9;
                            n20 = n15;
                            if (n17 - n16 <= n9) {
                                break;
                            }
                        }
                        else {
                            n18 = n7;
                            n19 = n9;
                            n20 = n15;
                            if (n17 + n16 >= n9) {
                                break;
                            }
                        }
                        if (n15 == count || (!b && this.checkAppendOverLimit(n))) {
                            return true;
                        }
                        int n21;
                        if (super.mReversedFlow) {
                            n21 = -n16 - super.mSpacing;
                        }
                        else {
                            n21 = n16 + super.mSpacing;
                        }
                        n17 += n21;
                        n16 = this.appendVisibleItemToRow(n15, n11, n17);
                        ++n15;
                    }
                }
                else {
                    int n22;
                    if (super.mReversedFlow) {
                        n22 = this.getRowMin(n11);
                    }
                    else {
                        n22 = this.getRowMax(n11);
                    }
                    n18 = 1;
                    n19 = n22;
                    n20 = n15;
                }
                ++n11;
                b2 = true;
                n10 = n20;
                n7 = n18;
                n9 = n19;
            }
            else {
                if (b) {
                    return b2;
                }
                int n23;
                if (super.mReversedFlow) {
                    n23 = this.findRowMin(false, null);
                }
                else {
                    n23 = this.findRowMax(true, null);
                }
                n11 = 0;
                n9 = n23;
            }
        }
    }
    
    public int findRowMax(final boolean b, int n, final int[] array) {
        int edge = super.mProvider.getEdge(n);
        Location location = this.getLocation(n);
        int row = location.row;
        int n6;
        int n7;
        int n8;
        if (super.mReversedFlow) {
            int n2 = row;
            int n3 = 1;
            int n4 = n + 1;
            int n5 = edge;
            while (true) {
                n6 = edge;
                n7 = row;
                n8 = n;
                if (n3 >= super.mNumRows) {
                    break;
                }
                n6 = edge;
                n7 = row;
                n8 = n;
                if (n4 > super.mLastVisibleIndex) {
                    break;
                }
                final Location location2 = this.getLocation(n4);
                final int n9 = n5 + location2.offset;
                final int row2 = location2.row;
                int n10 = edge;
                int n11 = row;
                int n12 = n2;
                int n13 = n3;
                int n14 = n;
                Label_0202: {
                    if (row2 != n2) {
                        n13 = n3 + 1;
                        Label_0187: {
                            if (b) {
                                if (n9 <= edge) {
                                    break Label_0187;
                                }
                            }
                            else if (n9 >= edge) {
                                break Label_0187;
                            }
                            n10 = n9;
                            n14 = n4;
                            n = (n11 = (n12 = row2));
                            break Label_0202;
                        }
                        n12 = row2;
                        n14 = n;
                        n11 = row;
                        n10 = edge;
                    }
                }
                ++n4;
                edge = n10;
                n5 = n9;
                row = n11;
                n2 = n12;
                n3 = n13;
                n = n14;
            }
        }
        else {
            final int size = super.mProvider.getSize(n);
            int n15 = 1;
            final int n16 = n - 1;
            int n17 = edge;
            final int n18 = size + edge;
            final int n19 = row;
            int n20 = n;
            n = n16;
            int n21 = row;
            int n22 = n19;
            int n23 = n18;
            while (true) {
                n6 = n23;
                n7 = n22;
                n8 = n20;
                if (n15 >= super.mNumRows) {
                    break;
                }
                n6 = n23;
                n7 = n22;
                n8 = n20;
                if (n < super.mFirstVisibleIndex) {
                    break;
                }
                final int n24 = n17 - location.offset;
                location = this.getLocation(n);
                final int row3 = location.row;
                int n25 = n23;
                int n26 = n22;
                int n27 = n21;
                int n28 = n15;
                int n29 = n20;
                Label_0452: {
                    if (row3 != n21) {
                        n28 = n15 + 1;
                        n25 = super.mProvider.getSize(n) + n24;
                        Label_0436: {
                            if (b) {
                                if (n25 <= n23) {
                                    break Label_0436;
                                }
                            }
                            else if (n25 >= n23) {
                                break Label_0436;
                            }
                            n29 = n;
                            n27 = (n26 = row3);
                            break Label_0452;
                        }
                        n27 = row3;
                        n29 = n20;
                        n26 = n22;
                        n25 = n23;
                    }
                }
                --n;
                n23 = n25;
                n17 = n24;
                n22 = n26;
                n21 = n27;
                n15 = n28;
                n20 = n29;
            }
        }
        if (array != null) {
            array[0] = n7;
            array[1] = n8;
        }
        return n6;
    }
    
    public int findRowMin(final boolean b, int n, final int[] array) {
        final int edge = super.mProvider.getEdge(n);
        Location location = this.getLocation(n);
        int row = location.row;
        int n9;
        int n10;
        int n11;
        if (super.mReversedFlow) {
            final int size = super.mProvider.getSize(n);
            int n2 = 1;
            final int n3 = n - 1;
            final int n4 = edge - size;
            int n5 = row;
            int n6 = n;
            n = n3;
            int n7 = n4;
            int n8 = edge;
            while (true) {
                n9 = row;
                n10 = n7;
                n11 = n6;
                if (n2 >= super.mNumRows) {
                    break;
                }
                n9 = row;
                n10 = n7;
                n11 = n6;
                if (n < super.mFirstVisibleIndex) {
                    break;
                }
                final int n12 = n8 - location.offset;
                location = this.getLocation(n);
                final int row2 = location.row;
                int n13 = row;
                int n14 = n5;
                int n15 = n7;
                int n16 = n2;
                int n17 = n6;
                Label_0246: {
                    if (row2 != n5) {
                        n16 = n2 + 1;
                        n15 = n12 - super.mProvider.getSize(n);
                        Label_0230: {
                            if (b) {
                                if (n15 <= n7) {
                                    break Label_0230;
                                }
                            }
                            else if (n15 >= n7) {
                                break Label_0230;
                            }
                            n17 = n;
                            n14 = (n13 = row2);
                            break Label_0246;
                        }
                        n14 = row2;
                        n17 = n6;
                        n15 = n7;
                        n13 = row;
                    }
                }
                --n;
                n8 = n12;
                row = n13;
                n5 = n14;
                n7 = n15;
                n2 = n16;
                n6 = n17;
            }
        }
        else {
            int n19;
            int n18 = n19 = row;
            int n20 = 1;
            int n21 = n + 1;
            int n22 = edge;
            int n23 = edge;
            while (n20 < super.mNumRows && n21 <= super.mLastVisibleIndex) {
                final Location location2 = this.getLocation(n21);
                n22 += location2.offset;
                final int row3 = location2.row;
                int n24 = n23;
                int n25 = n18;
                int n26 = n19;
                int n27 = n20;
                int n28 = n;
                Label_0431: {
                    if (row3 != n19) {
                        n27 = n20 + 1;
                        Label_0416: {
                            if (b) {
                                if (n22 <= n23) {
                                    break Label_0416;
                                }
                            }
                            else if (n22 >= n23) {
                                break Label_0416;
                            }
                            n24 = n22;
                            n28 = n21;
                            n = (n25 = (n26 = row3));
                            break Label_0431;
                        }
                        n26 = row3;
                        n28 = n;
                        n25 = n18;
                        n24 = n23;
                    }
                }
                ++n21;
                n23 = n24;
                n18 = n25;
                n19 = n26;
                n20 = n27;
                n = n28;
            }
            n11 = n;
            n10 = n23;
            n9 = n18;
        }
        if (array != null) {
            array[0] = n9;
            array[1] = n11;
        }
        return n10;
    }
    
    int getRowMax(int n) {
        final int mFirstVisibleIndex = super.mFirstVisibleIndex;
        if (mFirstVisibleIndex < 0) {
            return Integer.MIN_VALUE;
        }
        if (!super.mReversedFlow) {
            int edge = super.mProvider.getEdge(super.mLastVisibleIndex);
            Location location = this.getLocation(super.mLastVisibleIndex);
            if (location.row != n) {
                for (int i = super.mLastVisibleIndex - 1; i >= this.getFirstIndex(); --i) {
                    edge -= location.offset;
                    location = this.getLocation(i);
                    if (location.row == n) {
                        n = location.size;
                        return edge + n;
                    }
                }
                return Integer.MIN_VALUE;
            }
            n = location.size;
            return edge + n;
        }
        int edge2 = super.mProvider.getEdge(mFirstVisibleIndex);
        if (this.getLocation(super.mFirstVisibleIndex).row == n) {
            return edge2;
        }
        int mFirstVisibleIndex2 = super.mFirstVisibleIndex;
        while (++mFirstVisibleIndex2 <= this.getLastIndex()) {
            final Location location2 = this.getLocation(mFirstVisibleIndex2);
            edge2 += location2.offset;
            if (location2.row == n) {
                return edge2;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    int getRowMin(int n) {
        final int mFirstVisibleIndex = super.mFirstVisibleIndex;
        if (mFirstVisibleIndex < 0) {
            return Integer.MAX_VALUE;
        }
        if (super.mReversedFlow) {
            int edge = super.mProvider.getEdge(super.mLastVisibleIndex);
            Location location = this.getLocation(super.mLastVisibleIndex);
            if (location.row != n) {
                for (int i = super.mLastVisibleIndex - 1; i >= this.getFirstIndex(); --i) {
                    edge -= location.offset;
                    location = this.getLocation(i);
                    if (location.row == n) {
                        n = location.size;
                        return edge - n;
                    }
                }
                return Integer.MAX_VALUE;
            }
            n = location.size;
            return edge - n;
        }
        int edge2 = super.mProvider.getEdge(mFirstVisibleIndex);
        if (this.getLocation(super.mFirstVisibleIndex).row == n) {
            return edge2;
        }
        int mFirstVisibleIndex2 = super.mFirstVisibleIndex;
        while (++mFirstVisibleIndex2 <= this.getLastIndex()) {
            final Location location2 = this.getLocation(mFirstVisibleIndex2);
            edge2 += location2.offset;
            if (location2.row == n) {
                return edge2;
            }
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    protected boolean prependVisibleItemsWithoutCache(final int n, final boolean b) {
        final int mFirstVisibleIndex = super.mFirstVisibleIndex;
        int n6;
        int n7;
        int mStartIndex;
        int n10;
        if (mFirstVisibleIndex >= 0) {
            if (mFirstVisibleIndex > this.getFirstIndex()) {
                return false;
            }
            final int mFirstVisibleIndex2 = super.mFirstVisibleIndex;
            final int n2 = mFirstVisibleIndex2 - 1;
            int row = this.getLocation(mFirstVisibleIndex2).row;
            final int rowEdgeLimitSearchIndex = this.findRowEdgeLimitSearchIndex(false);
            int n5;
            if (rowEdgeLimitSearchIndex < 0) {
                final int n3 = row - 1;
                int n4 = super.mNumRows - 1;
                n5 = Integer.MAX_VALUE;
                while (true) {
                    row = n3;
                    if (n4 < 0) {
                        break;
                    }
                    if (super.mReversedFlow) {
                        n5 = this.getRowMax(n4);
                    }
                    else {
                        n5 = this.getRowMin(n4);
                    }
                    if (n5 != Integer.MAX_VALUE) {
                        row = n3;
                        break;
                    }
                    --n4;
                }
            }
            else if (super.mReversedFlow) {
                n5 = this.findRowMax(true, rowEdgeLimitSearchIndex, null);
            }
            else {
                n5 = this.findRowMin(false, rowEdgeLimitSearchIndex, null);
            }
            Label_0238: {
                if (super.mReversedFlow) {
                    n6 = row;
                    if (this.getRowMax(row) < (n7 = n5)) {
                        break Label_0238;
                    }
                }
                else {
                    n6 = row;
                    if (this.getRowMin(row) > (n7 = n5)) {
                        break Label_0238;
                    }
                }
                n6 = --row;
                n7 = n5;
                if (row < 0) {
                    n6 = super.mNumRows - 1;
                    int n8;
                    if (super.mReversedFlow) {
                        n8 = this.findRowMax(true, null);
                    }
                    else {
                        n8 = this.findRowMin(false, null);
                    }
                    n7 = n8;
                }
            }
            final int n9 = 1;
            mStartIndex = n2;
            n10 = n9;
        }
        else {
            mStartIndex = super.mStartIndex;
            if (mStartIndex == -1) {
                mStartIndex = 0;
            }
            int n11;
            if (super.mLocations.size() > 0) {
                n11 = this.getLocation(this.getFirstIndex()).row + super.mNumRows - 1;
            }
            else {
                n11 = mStartIndex;
            }
            n6 = n11 % super.mNumRows;
            n10 = (n7 = 0);
        }
        boolean b2 = false;
        int n12 = n7;
        int n13 = mStartIndex;
        int n14 = n6;
        while (true) {
            if (n14 >= 0) {
                if (n13 < 0 || (!b && this.checkPrependOverLimit(n))) {
                    return b2;
                }
                int n15;
                if (super.mReversedFlow) {
                    n15 = this.getRowMax(n14);
                }
                else {
                    n15 = this.getRowMin(n14);
                }
                int n17 = 0;
                Label_0553: {
                    int mSpacing = 0;
                    Label_0423: {
                        int mSpacing2;
                        if (n15 != Integer.MAX_VALUE && n15 != Integer.MIN_VALUE) {
                            if (super.mReversedFlow) {
                                mSpacing = super.mSpacing;
                                break Label_0423;
                            }
                            mSpacing2 = super.mSpacing;
                        }
                        else if (n14 == super.mNumRows - 1) {
                            int n16;
                            if (super.mReversedFlow) {
                                n16 = this.getRowMax(0);
                            }
                            else {
                                n16 = this.getRowMin(0);
                            }
                            n17 = n16;
                            if (n16 == Integer.MAX_VALUE || (n17 = n16) == Integer.MIN_VALUE) {
                                break Label_0553;
                            }
                            if (super.mReversedFlow) {
                                final int mSpacing3 = super.mSpacing;
                                n15 = n16;
                                mSpacing = mSpacing3;
                                break Label_0423;
                            }
                            final int mSpacing4 = super.mSpacing;
                            n15 = n16;
                            mSpacing2 = mSpacing4;
                        }
                        else {
                            if (super.mReversedFlow) {
                                n17 = this.getRowMin(n14 + 1);
                                break Label_0553;
                            }
                            n17 = this.getRowMax(n14 + 1);
                            break Label_0553;
                        }
                        mSpacing = -mSpacing2;
                    }
                    n17 = n15 + mSpacing;
                }
                int n18 = n13 - 1;
                int n19 = this.prependVisibleItemToRow(n13, n14, n17);
                int n21;
                int n22;
                int n23;
                if (n10 != 0) {
                    int n20 = n17;
                    while (true) {
                        if (super.mReversedFlow) {
                            n21 = n10;
                            n22 = n12;
                            n23 = n18;
                            if (n20 + n19 >= n12) {
                                break;
                            }
                        }
                        else {
                            n21 = n10;
                            n22 = n12;
                            n23 = n18;
                            if (n20 - n19 <= n12) {
                                break;
                            }
                        }
                        if (n18 < 0 || (!b && this.checkPrependOverLimit(n))) {
                            return true;
                        }
                        int n24;
                        if (super.mReversedFlow) {
                            n24 = n19 + super.mSpacing;
                        }
                        else {
                            n24 = -n19 - super.mSpacing;
                        }
                        n20 += n24;
                        n19 = this.prependVisibleItemToRow(n18, n14, n20);
                        --n18;
                    }
                }
                else {
                    int n25;
                    if (super.mReversedFlow) {
                        n25 = this.getRowMax(n14);
                    }
                    else {
                        n25 = this.getRowMin(n14);
                    }
                    n21 = 1;
                    n22 = n25;
                    n23 = n18;
                }
                --n14;
                b2 = true;
                n13 = n23;
                n10 = n21;
                n12 = n22;
            }
            else {
                if (b) {
                    return b2;
                }
                int n26;
                if (super.mReversedFlow) {
                    n26 = this.findRowMax(true, null);
                }
                else {
                    n26 = this.findRowMin(false, null);
                }
                n14 = super.mNumRows - 1;
                n12 = n26;
            }
        }
    }
}
