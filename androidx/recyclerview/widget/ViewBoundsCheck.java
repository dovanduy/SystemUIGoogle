// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.view.View;

class ViewBoundsCheck
{
    BoundFlags mBoundFlags;
    final Callback mCallback;
    
    ViewBoundsCheck(final Callback mCallback) {
        this.mCallback = mCallback;
        this.mBoundFlags = new BoundFlags();
    }
    
    View findOneViewWithinBoundFlags(int i, final int n, final int n2, final int n3) {
        final int parentStart = this.mCallback.getParentStart();
        final int parentEnd = this.mCallback.getParentEnd();
        int n4;
        if (n > i) {
            n4 = 1;
        }
        else {
            n4 = -1;
        }
        View view = null;
        while (i != n) {
            final View child = this.mCallback.getChildAt(i);
            this.mBoundFlags.setBounds(parentStart, parentEnd, this.mCallback.getChildStart(child), this.mCallback.getChildEnd(child));
            if (n2 != 0) {
                this.mBoundFlags.resetFlags();
                this.mBoundFlags.addFlags(n2);
                if (this.mBoundFlags.boundsMatch()) {
                    return child;
                }
            }
            View view2 = view;
            if (n3 != 0) {
                this.mBoundFlags.resetFlags();
                this.mBoundFlags.addFlags(n3);
                view2 = view;
                if (this.mBoundFlags.boundsMatch()) {
                    view2 = child;
                }
            }
            i += n4;
            view = view2;
        }
        return view;
    }
    
    boolean isViewWithinBoundFlags(final View view, final int n) {
        this.mBoundFlags.setBounds(this.mCallback.getParentStart(), this.mCallback.getParentEnd(), this.mCallback.getChildStart(view), this.mCallback.getChildEnd(view));
        if (n != 0) {
            this.mBoundFlags.resetFlags();
            this.mBoundFlags.addFlags(n);
            return this.mBoundFlags.boundsMatch();
        }
        return false;
    }
    
    static class BoundFlags
    {
        int mBoundFlags;
        int mChildEnd;
        int mChildStart;
        int mRvEnd;
        int mRvStart;
        
        BoundFlags() {
            this.mBoundFlags = 0;
        }
        
        void addFlags(final int n) {
            this.mBoundFlags |= n;
        }
        
        boolean boundsMatch() {
            final int mBoundFlags = this.mBoundFlags;
            if ((mBoundFlags & 0x7) != 0x0 && (mBoundFlags & this.compare(this.mChildStart, this.mRvStart) << 0) == 0x0) {
                return false;
            }
            final int mBoundFlags2 = this.mBoundFlags;
            if ((mBoundFlags2 & 0x70) != 0x0 && (mBoundFlags2 & this.compare(this.mChildStart, this.mRvEnd) << 4) == 0x0) {
                return false;
            }
            final int mBoundFlags3 = this.mBoundFlags;
            if ((mBoundFlags3 & 0x700) != 0x0 && (mBoundFlags3 & this.compare(this.mChildEnd, this.mRvStart) << 8) == 0x0) {
                return false;
            }
            final int mBoundFlags4 = this.mBoundFlags;
            return (mBoundFlags4 & 0x7000) == 0x0 || (this.compare(this.mChildEnd, this.mRvEnd) << 12 & mBoundFlags4) != 0x0;
        }
        
        int compare(final int n, final int n2) {
            if (n > n2) {
                return 1;
            }
            if (n == n2) {
                return 2;
            }
            return 4;
        }
        
        void resetFlags() {
            this.mBoundFlags = 0;
        }
        
        void setBounds(final int mRvStart, final int mRvEnd, final int mChildStart, final int mChildEnd) {
            this.mRvStart = mRvStart;
            this.mRvEnd = mRvEnd;
            this.mChildStart = mChildStart;
            this.mChildEnd = mChildEnd;
        }
    }
    
    interface Callback
    {
        View getChildAt(final int p0);
        
        int getChildEnd(final View p0);
        
        int getChildStart(final View p0);
        
        int getParentEnd();
        
        int getParentStart();
    }
}
