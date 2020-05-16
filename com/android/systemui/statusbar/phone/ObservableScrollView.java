// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.MotionEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView
{
    private boolean mBlockFlinging;
    private int mLastOverscrollAmount;
    private float mLastX;
    private float mLastY;
    private Listener mListener;
    private boolean mTouchCancelled;
    private boolean mTouchEnabled;
    
    public ObservableScrollView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTouchEnabled = true;
    }
    
    private int getMaxScrollY() {
        final int childCount = this.getChildCount();
        int max = 0;
        if (childCount > 0) {
            max = Math.max(0, this.getChildAt(0).getHeight() - (this.getHeight() - super.mPaddingBottom - super.mPaddingTop));
        }
        return max;
    }
    
    public boolean dispatchTouchEvent(MotionEvent obtain) {
        if (obtain.getAction() == 0) {
            if (!this.mTouchEnabled) {
                this.mTouchCancelled = true;
                return false;
            }
            this.mTouchCancelled = false;
        }
        else {
            if (this.mTouchCancelled) {
                return false;
            }
            if (!this.mTouchEnabled) {
                obtain = MotionEvent.obtain(obtain);
                obtain.setAction(3);
                super.dispatchTouchEvent(obtain);
                obtain.recycle();
                this.mTouchCancelled = true;
                return false;
            }
        }
        return super.dispatchTouchEvent(obtain);
    }
    
    public void fling(final int n) {
        if (!this.mBlockFlinging) {
            super.fling(n);
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        this.mLastX = motionEvent.getX();
        this.mLastY = motionEvent.getY();
        return super.onInterceptTouchEvent(motionEvent);
    }
    
    protected void onOverScrolled(int mLastOverscrollAmount, final int n, final boolean b, final boolean b2) {
        super.onOverScrolled(mLastOverscrollAmount, n, b, b2);
        final Listener mListener = this.mListener;
        if (mListener != null) {
            mLastOverscrollAmount = this.mLastOverscrollAmount;
            if (mLastOverscrollAmount > 0) {
                mListener.onOverscrolled(this.mLastX, this.mLastY, mLastOverscrollAmount);
            }
        }
    }
    
    protected void onScrollChanged(final int n, final int n2, final int n3, final int n4) {
        super.onScrollChanged(n, n2, n3, n4);
        final Listener mListener = this.mListener;
        if (mListener != null) {
            mListener.onScrollChanged();
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        this.mLastX = motionEvent.getX();
        this.mLastY = motionEvent.getY();
        return super.onTouchEvent(motionEvent);
    }
    
    protected boolean overScrollBy(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final boolean b) {
        this.mLastOverscrollAmount = Math.max(0, n4 + n2 - this.getMaxScrollY());
        return super.overScrollBy(n, n2, n3, n4, n5, n6, n7, n8, b);
    }
    
    public interface Listener
    {
        void onOverscrolled(final float p0, final float p1, final int p2);
        
        void onScrollChanged();
    }
}
