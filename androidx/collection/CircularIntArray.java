// 
// Decompiled by Procyon v0.5.36
// 

package androidx.collection;

public final class CircularIntArray
{
    private int mCapacityBitmask;
    private int[] mElements;
    private int mHead;
    private int mTail;
    
    public CircularIntArray() {
        this(8);
    }
    
    public CircularIntArray(final int i) {
        if (i < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        if (i <= 1073741824) {
            int n = i;
            if (Integer.bitCount(i) != 1) {
                n = Integer.highestOneBit(i - 1) << 1;
            }
            this.mCapacityBitmask = n - 1;
            this.mElements = new int[n];
            return;
        }
        throw new IllegalArgumentException("capacity must be <= 2^30");
    }
    
    private void doubleCapacity() {
        final int[] mElements = this.mElements;
        final int length = mElements.length;
        final int mHead = this.mHead;
        final int n = length - mHead;
        final int n2 = length << 1;
        if (n2 >= 0) {
            final int[] mElements2 = new int[n2];
            System.arraycopy(mElements, mHead, mElements2, 0, n);
            System.arraycopy(this.mElements, 0, mElements2, n, this.mHead);
            this.mElements = mElements2;
            this.mHead = 0;
            this.mTail = length;
            this.mCapacityBitmask = n2 - 1;
            return;
        }
        throw new RuntimeException("Max array capacity exceeded");
    }
    
    public void addLast(int mTail) {
        final int[] mElements = this.mElements;
        final int mTail2 = this.mTail;
        mElements[mTail2] = mTail;
        mTail = (this.mCapacityBitmask & mTail2 + 1);
        this.mTail = mTail;
        if (mTail == this.mHead) {
            this.doubleCapacity();
        }
    }
    
    public void clear() {
        this.mTail = this.mHead;
    }
    
    public int get(final int n) {
        if (n >= 0 && n < this.size()) {
            return this.mElements[this.mCapacityBitmask & this.mHead + n];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int getLast() {
        final int mHead = this.mHead;
        final int mTail = this.mTail;
        if (mHead != mTail) {
            return this.mElements[this.mCapacityBitmask & mTail - 1];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int popLast() {
        final int mHead = this.mHead;
        final int mTail = this.mTail;
        if (mHead != mTail) {
            final int mTail2 = this.mCapacityBitmask & mTail - 1;
            final int n = this.mElements[mTail2];
            this.mTail = mTail2;
            return n;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int size() {
        return this.mCapacityBitmask & this.mTail - this.mHead;
    }
}
