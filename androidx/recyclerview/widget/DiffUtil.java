// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

public class DiffUtil
{
    private static final Comparator<Diagonal> DIAGONAL_COMPARATOR;
    
    static {
        DIAGONAL_COMPARATOR = new Comparator<Diagonal>() {
            @Override
            public int compare(final Diagonal diagonal, final Diagonal diagonal2) {
                return diagonal.x - diagonal2.x;
            }
        };
    }
    
    private static Snake backward(final Range range, final Callback callback, final CenteredArray centeredArray, final CenteredArray centeredArray2, final int n) {
        final boolean b = (range.oldSize() - range.newSize()) % 2 == 0;
        final int oldSize = range.oldSize();
        final int size = range.newSize();
        int i;
        for (int n2 = i = -n; i <= n; i += 2) {
            int value;
            int value2;
            if (i != n2 && (i == n || centeredArray2.get(i + 1) >= centeredArray2.get(i - 1))) {
                value = centeredArray2.get(i - 1);
                value2 = value - 1;
            }
            else {
                value = (value2 = centeredArray2.get(i + 1));
            }
            int startY = range.newListEnd - (range.oldListEnd - value2 - i);
            int endY;
            if (n != 0 && value2 == value) {
                endY = startY + 1;
            }
            else {
                endY = startY;
            }
            while (value2 > range.oldListStart && startY > range.newListStart && callback.areItemsTheSame(value2 - 1, startY - 1)) {
                --value2;
                --startY;
            }
            centeredArray2.set(i, value2);
            if (b) {
                final int n3 = oldSize - size - i;
                if (n3 >= n2 && n3 <= n && centeredArray.get(n3) >= value2) {
                    final Snake snake = new Snake();
                    snake.startX = value2;
                    snake.startY = startY;
                    snake.endX = value;
                    snake.endY = endY;
                    snake.reverse = true;
                    return snake;
                }
            }
        }
        return null;
    }
    
    public static DiffResult calculateDiff(final Callback callback) {
        return calculateDiff(callback, true);
    }
    
    public static DiffResult calculateDiff(final Callback callback, final boolean b) {
        final int oldListSize = callback.getOldListSize();
        final int newListSize = callback.getNewListSize();
        final ArrayList<Object> list = new ArrayList<Object>();
        final ArrayList<Range> list2 = new ArrayList<Range>();
        list2.add(new Range(0, oldListSize, 0, newListSize));
        final int n = (oldListSize + newListSize + 1) / 2 * 2 + 1;
        final CenteredArray centeredArray = new CenteredArray(n);
        final CenteredArray centeredArray2 = new CenteredArray(n);
        final ArrayList<Range> list3 = (ArrayList<Range>)new ArrayList<Object>();
        while (!list2.isEmpty()) {
            final Range range = list2.remove(list2.size() - 1);
            final Snake midPoint = midPoint(range, callback, centeredArray, centeredArray2);
            if (midPoint != null) {
                if (midPoint.diagonalSize() > 0) {
                    list.add(midPoint.toDiagonal());
                }
                Range range2;
                if (list3.isEmpty()) {
                    range2 = new Range();
                }
                else {
                    range2 = list3.remove(list3.size() - 1);
                }
                range2.oldListStart = range.oldListStart;
                range2.newListStart = range.newListStart;
                range2.oldListEnd = midPoint.startX;
                range2.newListEnd = midPoint.startY;
                list2.add(range2);
                range.oldListEnd = range.oldListEnd;
                range.newListEnd = range.newListEnd;
                range.oldListStart = midPoint.endX;
                range.newListStart = midPoint.endY;
                list2.add(range);
            }
            else {
                list3.add(range);
            }
        }
        Collections.sort(list, (Comparator<? super Object>)DiffUtil.DIAGONAL_COMPARATOR);
        return new DiffResult(callback, (List<Diagonal>)list, centeredArray.backingData(), centeredArray2.backingData(), b);
    }
    
    private static Snake forward(final Range range, final Callback callback, final CenteredArray centeredArray, final CenteredArray centeredArray2, final int n) {
        final int abs = Math.abs(range.oldSize() - range.newSize());
        boolean b = true;
        if (abs % 2 != 1) {
            b = false;
        }
        final int oldSize = range.oldSize();
        final int size = range.newSize();
        int i;
        for (int n2 = i = -n; i <= n; i += 2) {
            int value;
            int value2;
            if (i != n2 && (i == n || centeredArray.get(i + 1) <= centeredArray.get(i - 1))) {
                value = centeredArray.get(i - 1);
                value2 = value + 1;
            }
            else {
                value = (value2 = centeredArray.get(i + 1));
            }
            int endY = range.newListStart + (value2 - range.oldListStart) - i;
            int startY;
            if (n != 0 && value2 == value) {
                startY = endY - 1;
            }
            else {
                startY = endY;
            }
            while (value2 < range.oldListEnd && endY < range.newListEnd && callback.areItemsTheSame(value2, endY)) {
                ++value2;
                ++endY;
            }
            centeredArray.set(i, value2);
            if (b) {
                final int n3 = oldSize - size - i;
                if (n3 >= n2 + 1 && n3 <= n - 1 && centeredArray2.get(n3) <= value2) {
                    final Snake snake = new Snake();
                    snake.startX = value;
                    snake.startY = startY;
                    snake.endX = value2;
                    snake.endY = endY;
                    snake.reverse = false;
                    return snake;
                }
            }
        }
        return null;
    }
    
    private static Snake midPoint(final Range range, final Callback callback, final CenteredArray centeredArray, final CenteredArray centeredArray2) {
        if (range.oldSize() >= 1) {
            if (range.newSize() >= 1) {
                final int n = (range.oldSize() + range.newSize() + 1) / 2;
                centeredArray.set(1, range.oldListStart);
                centeredArray2.set(1, range.oldListEnd);
                for (int i = 0; i < n; ++i) {
                    final Snake forward = forward(range, callback, centeredArray, centeredArray2, i);
                    if (forward != null) {
                        return forward;
                    }
                    final Snake backward = backward(range, callback, centeredArray, centeredArray2, i);
                    if (backward != null) {
                        return backward;
                    }
                }
            }
        }
        return null;
    }
    
    public abstract static class Callback
    {
        public abstract boolean areContentsTheSame(final int p0, final int p1);
        
        public abstract boolean areItemsTheSame(final int p0, final int p1);
        
        public Object getChangePayload(final int n, final int n2) {
            return null;
        }
        
        public abstract int getNewListSize();
        
        public abstract int getOldListSize();
    }
    
    static class CenteredArray
    {
        private final int[] mData;
        private final int mMid;
        
        CenteredArray(final int n) {
            final int[] mData = new int[n];
            this.mData = mData;
            this.mMid = mData.length / 2;
        }
        
        int[] backingData() {
            return this.mData;
        }
        
        int get(final int n) {
            return this.mData[n + this.mMid];
        }
        
        void set(final int n, final int n2) {
            this.mData[n + this.mMid] = n2;
        }
    }
    
    static class Diagonal
    {
        public final int size;
        public final int x;
        public final int y;
        
        Diagonal(final int x, final int y, final int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
        
        int endX() {
            return this.x + this.size;
        }
        
        int endY() {
            return this.y + this.size;
        }
    }
    
    public static class DiffResult
    {
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final List<Diagonal> mDiagonals;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;
        
        DiffResult(final Callback mCallback, final List<Diagonal> mDiagonals, final int[] array, final int[] mNewItemStatuses, final boolean mDetectMoves) {
            this.mDiagonals = mDiagonals;
            this.mOldItemStatuses = array;
            this.mNewItemStatuses = mNewItemStatuses;
            Arrays.fill(array, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = mCallback;
            this.mOldListSize = mCallback.getOldListSize();
            this.mNewListSize = mCallback.getNewListSize();
            this.mDetectMoves = mDetectMoves;
            this.addEdgeDiagonals();
            this.findMatchingItems();
        }
        
        private void addEdgeDiagonals() {
            Diagonal diagonal;
            if (this.mDiagonals.isEmpty()) {
                diagonal = null;
            }
            else {
                diagonal = this.mDiagonals.get(0);
            }
            if (diagonal == null || diagonal.x != 0 || diagonal.y != 0) {
                this.mDiagonals.add(0, new Diagonal(0, 0, 0));
            }
            this.mDiagonals.add(new Diagonal(this.mOldListSize, this.mNewListSize, 0));
        }
        
        private void findMatchingAddition(final int n) {
            final int size = this.mDiagonals.size();
            int i = 0;
            int j = 0;
            while (i < size) {
                Diagonal diagonal;
                for (diagonal = this.mDiagonals.get(i); j < diagonal.y; ++j) {
                    if (this.mNewItemStatuses[j] == 0 && this.mCallback.areItemsTheSame(n, j)) {
                        int n2;
                        if (this.mCallback.areContentsTheSame(n, j)) {
                            n2 = 8;
                        }
                        else {
                            n2 = 4;
                        }
                        this.mOldItemStatuses[n] = (j << 4 | n2);
                        this.mNewItemStatuses[j] = (n << 4 | n2);
                        return;
                    }
                }
                j = diagonal.endY();
                ++i;
            }
        }
        
        private void findMatchingItems() {
            for (final Diagonal diagonal : this.mDiagonals) {
                for (int i = 0; i < diagonal.size; ++i) {
                    final int n = diagonal.x + i;
                    final int n2 = diagonal.y + i;
                    int n3;
                    if (this.mCallback.areContentsTheSame(n, n2)) {
                        n3 = 1;
                    }
                    else {
                        n3 = 2;
                    }
                    this.mOldItemStatuses[n] = (n2 << 4 | n3);
                    this.mNewItemStatuses[n2] = (n << 4 | n3);
                }
            }
            if (this.mDetectMoves) {
                this.findMoveMatches();
            }
        }
        
        private void findMoveMatches() {
            final Iterator<Diagonal> iterator = this.mDiagonals.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Diagonal diagonal;
                for (diagonal = iterator.next(); i < diagonal.x; ++i) {
                    if (this.mOldItemStatuses[i] == 0) {
                        this.findMatchingAddition(i);
                    }
                }
                i = diagonal.endX();
            }
        }
        
        private static PostponedUpdate getPostponedUpdate(final Collection<PostponedUpdate> collection, final int n, final boolean b) {
            final Iterator<PostponedUpdate> iterator = collection.iterator();
            while (true) {
                while (iterator.hasNext()) {
                    final PostponedUpdate postponedUpdate = iterator.next();
                    if (postponedUpdate.posInOwnerList == n && postponedUpdate.removal == b) {
                        iterator.remove();
                        while (iterator.hasNext()) {
                            final PostponedUpdate postponedUpdate2 = iterator.next();
                            if (b) {
                                --postponedUpdate2.currentPos;
                            }
                            else {
                                ++postponedUpdate2.currentPos;
                            }
                        }
                        return postponedUpdate;
                    }
                }
                final PostponedUpdate postponedUpdate = null;
                continue;
            }
        }
        
        public void dispatchUpdatesTo(final ListUpdateCallback listUpdateCallback) {
            BatchingListUpdateCallback batchingListUpdateCallback;
            if (listUpdateCallback instanceof BatchingListUpdateCallback) {
                batchingListUpdateCallback = (BatchingListUpdateCallback)listUpdateCallback;
            }
            else {
                batchingListUpdateCallback = new BatchingListUpdateCallback(listUpdateCallback);
            }
            int mOldListSize = this.mOldListSize;
            final ArrayDeque<PostponedUpdate> arrayDeque = new ArrayDeque<PostponedUpdate>();
            int n = this.mOldListSize;
            int n2 = this.mNewListSize;
            for (int i = this.mDiagonals.size() - 1; i >= 0; --i) {
                final Diagonal diagonal = this.mDiagonals.get(i);
                final int endX = diagonal.endX();
                final int endY = diagonal.endY();
                int n3 = n;
                int n4 = mOldListSize;
                int n5;
                int j;
                while (true) {
                    n5 = 0;
                    mOldListSize = n4;
                    j = n2;
                    if (n3 <= endX) {
                        break;
                    }
                    final int n6 = n3 - 1;
                    final int n7 = this.mOldItemStatuses[n6];
                    if ((n7 & 0xC) != 0x0) {
                        final int n8 = n7 >> 4;
                        final PostponedUpdate postponedUpdate = getPostponedUpdate(arrayDeque, n8, false);
                        if (postponedUpdate != null) {
                            final int n9 = n4 - postponedUpdate.currentPos - 1;
                            batchingListUpdateCallback.onMoved(n6, n9);
                            n3 = n6;
                            if ((n7 & 0x4) == 0x0) {
                                continue;
                            }
                            batchingListUpdateCallback.onChanged(n9, 1, this.mCallback.getChangePayload(n6, n8));
                            n3 = n6;
                        }
                        else {
                            arrayDeque.add(new PostponedUpdate(n6, n4 - n6 - 1, true));
                            n3 = n6;
                        }
                    }
                    else {
                        batchingListUpdateCallback.onRemoved(n6, 1);
                        --n4;
                        n3 = n6;
                    }
                }
                while (j > endY) {
                    final int n10 = j - 1;
                    final int n11 = this.mNewItemStatuses[n10];
                    if ((n11 & 0xC) != 0x0) {
                        final int n12 = n11 >> 4;
                        final PostponedUpdate postponedUpdate2 = getPostponedUpdate(arrayDeque, n12, true);
                        if (postponedUpdate2 == null) {
                            arrayDeque.add(new PostponedUpdate(n10, mOldListSize - n3, false));
                            j = n10;
                        }
                        else {
                            batchingListUpdateCallback.onMoved(mOldListSize - postponedUpdate2.currentPos - 1, n3);
                            j = n10;
                            if ((n11 & 0x4) == 0x0) {
                                continue;
                            }
                            batchingListUpdateCallback.onChanged(n3, 1, this.mCallback.getChangePayload(n12, n10));
                            j = n10;
                        }
                    }
                    else {
                        batchingListUpdateCallback.onInserted(n3, 1);
                        ++mOldListSize;
                        j = n10;
                    }
                }
                int x = diagonal.x;
                int y = diagonal.y;
                for (int k = n5; k < diagonal.size; ++k) {
                    if ((this.mOldItemStatuses[x] & 0xF) == 0x2) {
                        batchingListUpdateCallback.onChanged(x, 1, this.mCallback.getChangePayload(x, y));
                    }
                    ++x;
                    ++y;
                }
                n = diagonal.x;
                n2 = diagonal.y;
            }
            batchingListUpdateCallback.dispatchLastEvent();
        }
        
        public void dispatchUpdatesTo(final RecyclerView.Adapter adapter) {
            this.dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }
    }
    
    private static class PostponedUpdate
    {
        int currentPos;
        int posInOwnerList;
        boolean removal;
        
        PostponedUpdate(final int posInOwnerList, final int currentPos, final boolean removal) {
            this.posInOwnerList = posInOwnerList;
            this.currentPos = currentPos;
            this.removal = removal;
        }
    }
    
    static class Range
    {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;
        
        public Range() {
        }
        
        public Range(final int oldListStart, final int oldListEnd, final int newListStart, final int newListEnd) {
            this.oldListStart = oldListStart;
            this.oldListEnd = oldListEnd;
            this.newListStart = newListStart;
            this.newListEnd = newListEnd;
        }
        
        int newSize() {
            return this.newListEnd - this.newListStart;
        }
        
        int oldSize() {
            return this.oldListEnd - this.oldListStart;
        }
    }
    
    static class Snake
    {
        public int endX;
        public int endY;
        public boolean reverse;
        public int startX;
        public int startY;
        
        int diagonalSize() {
            return Math.min(this.endX - this.startX, this.endY - this.startY);
        }
        
        boolean hasAdditionOrRemoval() {
            return this.endY - this.startY != this.endX - this.startX;
        }
        
        boolean isAddition() {
            return this.endY - this.startY > this.endX - this.startX;
        }
        
        Diagonal toDiagonal() {
            if (!this.hasAdditionOrRemoval()) {
                final int startX = this.startX;
                return new Diagonal(startX, this.startY, this.endX - startX);
            }
            if (this.reverse) {
                return new Diagonal(this.startX, this.startY, this.diagonalSize());
            }
            if (this.isAddition()) {
                return new Diagonal(this.startX, this.startY + 1, this.diagonalSize());
            }
            return new Diagonal(this.startX + 1, this.startY, this.diagonalSize());
        }
    }
}
