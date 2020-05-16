// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.view.View$MeasureSpec;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.util.TypedValue;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewGroup;

public final class KeyboardShortcutKeysLayout extends ViewGroup
{
    private final Context mContext;
    private int mLineHeight;
    
    public KeyboardShortcutKeysLayout(final Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }
    
    public KeyboardShortcutKeysLayout(final Context mContext, final AttributeSet set) {
        super(mContext, set);
        this.mContext = mContext;
    }
    
    private int getHorizontalVerticalSpacing() {
        return (int)TypedValue.applyDimension(1, 4.0f, this.getResources().getDisplayMetrics());
    }
    
    private boolean isRTL() {
        final int layoutDirection = this.mContext.getResources().getConfiguration().getLayoutDirection();
        boolean b = true;
        if (layoutDirection != 1) {
            b = false;
        }
        return b;
    }
    
    private void layoutChildrenOnRow(final int n, final int n2, final int n3, int measuredWidth, final int n4, int i) {
        int n5 = measuredWidth;
        if (!this.isRTL()) {
            n5 = this.getPaddingLeft() + n3 - measuredWidth + i;
        }
        i = n;
        measuredWidth = n5;
        while (i < n2) {
            final View child = this.getChildAt(i);
            final int measuredWidth2 = child.getMeasuredWidth();
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            int n6 = measuredWidth;
            if (this.isRTL()) {
                n6 = measuredWidth;
                if (i == n) {
                    n6 = n3 - measuredWidth - this.getPaddingRight() - measuredWidth2 - layoutParams.mHorizontalSpacing;
                }
            }
            child.layout(n6, n4, n6 + measuredWidth2, child.getMeasuredHeight() + n4);
            if (this.isRTL()) {
                if (i < n2 - 1) {
                    measuredWidth = this.getChildAt(i + 1).getMeasuredWidth();
                }
                else {
                    measuredWidth = 0;
                }
                measuredWidth = n6 - (measuredWidth + layoutParams.mHorizontalSpacing);
            }
            else {
                measuredWidth = n6 + (measuredWidth2 + layoutParams.mHorizontalSpacing);
            }
            ++i;
        }
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        final int horizontalVerticalSpacing = this.getHorizontalVerticalSpacing();
        return new LayoutParams(horizontalVerticalSpacing, horizontalVerticalSpacing);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        final int horizontalVerticalSpacing = this.getHorizontalVerticalSpacing();
        return new LayoutParams(horizontalVerticalSpacing, horizontalVerticalSpacing, viewGroup$LayoutParams);
    }
    
    protected void onLayout(final boolean b, int i, int paddingTop, int paddingLeft, int n) {
        final int childCount = this.getChildCount();
        final int n2 = paddingLeft - i;
        if (this.isRTL()) {
            i = n2 - this.getPaddingRight();
        }
        else {
            i = this.getPaddingLeft();
        }
        paddingTop = this.getPaddingTop();
        paddingLeft = i;
        i = 0;
        int n3;
        n = (n3 = i);
        while (i < childCount) {
            final View child = this.getChildAt(i);
            int n4 = n;
            int n5 = paddingLeft;
            int mHorizontalSpacing = n3;
            int n6 = paddingTop;
            if (child.getVisibility() != 8) {
                final int measuredWidth = child.getMeasuredWidth();
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                final boolean rtl = this.isRTL();
                boolean b2 = true;
                Label_0151: {
                    if (rtl) {
                        if (paddingLeft - this.getPaddingLeft() - measuredWidth < 0) {
                            break Label_0151;
                        }
                    }
                    else if (paddingLeft + measuredWidth > n2) {
                        break Label_0151;
                    }
                    b2 = false;
                }
                n4 = n;
                int n7 = paddingLeft;
                n6 = paddingTop;
                if (b2) {
                    this.layoutChildrenOnRow(n, i, n2, paddingLeft, paddingTop, n3);
                    if (this.isRTL()) {
                        paddingLeft = n2 - this.getPaddingRight();
                    }
                    else {
                        paddingLeft = this.getPaddingLeft();
                    }
                    n6 = paddingTop + this.mLineHeight;
                    n4 = i;
                    n7 = paddingLeft;
                }
                if (this.isRTL()) {
                    paddingTop = n7 - measuredWidth - layoutParams.mHorizontalSpacing;
                }
                else {
                    paddingTop = n7 + measuredWidth + layoutParams.mHorizontalSpacing;
                }
                mHorizontalSpacing = layoutParams.mHorizontalSpacing;
                n5 = paddingTop;
            }
            ++i;
            n = n4;
            paddingLeft = n5;
            n3 = mHorizontalSpacing;
            paddingTop = n6;
        }
        if (n < childCount) {
            this.layoutChildrenOnRow(n, childCount, n2, paddingLeft, paddingTop, n3);
        }
    }
    
    protected void onMeasure(int paddingTop, final int n) {
        final int n2 = View$MeasureSpec.getSize(paddingTop) - this.getPaddingLeft() - this.getPaddingRight();
        final int childCount = this.getChildCount();
        final int n3 = View$MeasureSpec.getSize(n) - this.getPaddingTop() - this.getPaddingBottom();
        int paddingLeft = this.getPaddingLeft();
        paddingTop = this.getPaddingTop();
        final int mode = View$MeasureSpec.getMode(n);
        int i = 0;
        int n4;
        if (mode == Integer.MIN_VALUE) {
            n4 = View$MeasureSpec.makeMeasureSpec(n3, Integer.MIN_VALUE);
        }
        else {
            n4 = View$MeasureSpec.makeMeasureSpec(0, 0);
        }
        int n5 = 0;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            int n6 = paddingLeft;
            int n7 = paddingTop;
            int n8 = n5;
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                child.measure(View$MeasureSpec.makeMeasureSpec(n2, Integer.MIN_VALUE), n4);
                final int measuredWidth = child.getMeasuredWidth();
                final int max = Math.max(n5, child.getMeasuredHeight() + layoutParams.mVerticalSpacing);
                int paddingLeft2 = paddingLeft;
                int n9 = paddingTop;
                if (paddingLeft + measuredWidth > n2) {
                    paddingLeft2 = this.getPaddingLeft();
                    n9 = paddingTop + max;
                }
                n6 = paddingLeft2 + (measuredWidth + layoutParams.mHorizontalSpacing);
                n8 = max;
                n7 = n9;
            }
            ++i;
            paddingLeft = n6;
            paddingTop = n7;
            n5 = n8;
        }
        this.mLineHeight = n5;
        int n10;
        if (View$MeasureSpec.getMode(n) == 0) {
            n10 = paddingTop + n5;
        }
        else {
            n10 = n3;
            if (View$MeasureSpec.getMode(n) == Integer.MIN_VALUE) {
                paddingTop += n5;
                if (paddingTop < (n10 = n3)) {
                    n10 = paddingTop;
                }
            }
        }
        this.setMeasuredDimension(n2, n10);
    }
    
    public static class LayoutParams extends ViewGroup$LayoutParams
    {
        public final int mHorizontalSpacing;
        public final int mVerticalSpacing;
        
        public LayoutParams(final int mHorizontalSpacing, final int mVerticalSpacing) {
            super(0, 0);
            this.mHorizontalSpacing = mHorizontalSpacing;
            this.mVerticalSpacing = mVerticalSpacing;
        }
        
        public LayoutParams(final int mHorizontalSpacing, final int mVerticalSpacing, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mHorizontalSpacing = mHorizontalSpacing;
            this.mVerticalSpacing = mVerticalSpacing;
        }
    }
}
