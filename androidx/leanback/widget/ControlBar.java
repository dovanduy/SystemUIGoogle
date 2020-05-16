// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.Rect;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.view.View;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

class ControlBar extends LinearLayout
{
    private int mChildMarginFromCenter;
    boolean mDefaultFocusToMiddle;
    int mLastFocusIndex;
    private OnChildFocusedListener mOnChildFocusedListener;
    
    public ControlBar(final Context context, final AttributeSet set) {
        super(context, set);
        this.mLastFocusIndex = -1;
        this.mDefaultFocusToMiddle = true;
    }
    
    public ControlBar(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mLastFocusIndex = -1;
        this.mDefaultFocusToMiddle = true;
    }
    
    public void addFocusables(final ArrayList<View> list, int mLastFocusIndex, final int n) {
        if (mLastFocusIndex != 33 && mLastFocusIndex != 130) {
            super.addFocusables((ArrayList)list, mLastFocusIndex, n);
        }
        else {
            mLastFocusIndex = this.mLastFocusIndex;
            if (mLastFocusIndex >= 0 && mLastFocusIndex < this.getChildCount()) {
                list.add(this.getChildAt(this.mLastFocusIndex));
            }
            else if (this.getChildCount() > 0) {
                list.add(this.getChildAt(this.getDefaultFocusIndex()));
            }
        }
    }
    
    int getDefaultFocusIndex() {
        int n;
        if (this.mDefaultFocusToMiddle) {
            n = this.getChildCount() / 2;
        }
        else {
            n = 0;
        }
        return n;
    }
    
    protected void onMeasure(int n, int i) {
        super.onMeasure(n, i);
        if (this.mChildMarginFromCenter <= 0) {
            return;
        }
        i = 0;
        n = 0;
        while (i < this.getChildCount() - 1) {
            final View child = this.getChildAt(i);
            ++i;
            final View child2 = this.getChildAt(i);
            final int marginStart = this.mChildMarginFromCenter - (child.getMeasuredWidth() + child2.getMeasuredWidth()) / 2;
            final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)child2.getLayoutParams();
            final int marginStart2 = layoutParams.getMarginStart();
            layoutParams.setMarginStart(marginStart);
            child2.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            n += marginStart - marginStart2;
        }
        this.setMeasuredDimension(this.getMeasuredWidth() + n, this.getMeasuredHeight());
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        if (this.getChildCount() > 0) {
            final int mLastFocusIndex = this.mLastFocusIndex;
            int n2;
            if (mLastFocusIndex >= 0 && mLastFocusIndex < this.getChildCount()) {
                n2 = this.mLastFocusIndex;
            }
            else {
                n2 = this.getDefaultFocusIndex();
            }
            if (this.getChildAt(n2).requestFocus(n, rect)) {
                return true;
            }
        }
        return super.onRequestFocusInDescendants(n, rect);
    }
    
    public void requestChildFocus(final View view, final View view2) {
        super.requestChildFocus(view, view2);
        this.mLastFocusIndex = this.indexOfChild(view);
        final OnChildFocusedListener mOnChildFocusedListener = this.mOnChildFocusedListener;
        if (mOnChildFocusedListener != null) {
            mOnChildFocusedListener.onChildFocusedListener(view, view2);
        }
    }
    
    public interface OnChildFocusedListener
    {
        void onChildFocusedListener(final View p0, final View p1);
    }
}
