// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.widget.LinearLayout$LayoutParams;
import android.view.View$MeasureSpec;
import androidx.appcompat.R$id;
import android.content.res.TypedArray;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.appcompat.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class ButtonBarLayout extends LinearLayout
{
    private boolean mAllowStacking;
    private int mLastWidthSize;
    private int mMinimumHeight;
    
    public ButtonBarLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mLastWidthSize = -1;
        this.mMinimumHeight = 0;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ButtonBarLayout);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.ButtonBarLayout, set, obtainStyledAttributes, 0, 0);
        this.mAllowStacking = obtainStyledAttributes.getBoolean(R$styleable.ButtonBarLayout_allowStacking, true);
        obtainStyledAttributes.recycle();
    }
    
    private int getNextVisibleChildIndex(int i) {
        while (i < this.getChildCount()) {
            if (this.getChildAt(i).getVisibility() == 0) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    private boolean isStacked() {
        final int orientation = this.getOrientation();
        boolean b = true;
        if (orientation != 1) {
            b = false;
        }
        return b;
    }
    
    private void setStacked(final boolean orientation) {
        this.setOrientation((int)(orientation ? 1 : 0));
        int gravity;
        if ((orientation ? 1 : 0) != 0) {
            gravity = 5;
        }
        else {
            gravity = 80;
        }
        this.setGravity(gravity);
        final View viewById = this.findViewById(R$id.spacer);
        if (viewById != null) {
            int visibility;
            if ((orientation ? 1 : 0) != 0) {
                visibility = 8;
            }
            else {
                visibility = 4;
            }
            viewById.setVisibility(visibility);
        }
        for (int i = this.getChildCount() - 2; i >= 0; --i) {
            this.bringChildToFront(this.getChildAt(i));
        }
    }
    
    public int getMinimumHeight() {
        return Math.max(this.mMinimumHeight, super.getMinimumHeight());
    }
    
    protected void onMeasure(int minimumHeight, int n) {
        final int size = View$MeasureSpec.getSize(minimumHeight);
        final boolean mAllowStacking = this.mAllowStacking;
        final int n2 = 0;
        if (mAllowStacking) {
            if (size > this.mLastWidthSize && this.isStacked()) {
                this.setStacked(false);
            }
            this.mLastWidthSize = size;
        }
        int measureSpec;
        boolean b;
        if (!this.isStacked() && View$MeasureSpec.getMode(minimumHeight) == 1073741824) {
            measureSpec = View$MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            b = true;
        }
        else {
            measureSpec = minimumHeight;
            b = false;
        }
        super.onMeasure(measureSpec, n);
        boolean b2 = b;
        if (this.mAllowStacking) {
            b2 = b;
            if (!this.isStacked()) {
                final boolean b3 = (this.getMeasuredWidthAndState() & 0xFF000000) == 0x1000000;
                b2 = b;
                if (b3) {
                    this.setStacked(true);
                    b2 = true;
                }
            }
        }
        if (b2) {
            super.onMeasure(minimumHeight, n);
        }
        final int nextVisibleChildIndex = this.getNextVisibleChildIndex(0);
        minimumHeight = n2;
        if (nextVisibleChildIndex >= 0) {
            final View child = this.getChildAt(nextVisibleChildIndex);
            final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)child.getLayoutParams();
            n = this.getPaddingTop() + child.getMeasuredHeight() + linearLayout$LayoutParams.topMargin + linearLayout$LayoutParams.bottomMargin + 0;
            if (this.isStacked()) {
                final int nextVisibleChildIndex2 = this.getNextVisibleChildIndex(nextVisibleChildIndex + 1);
                minimumHeight = n;
                if (nextVisibleChildIndex2 >= 0) {
                    minimumHeight = n + (this.getChildAt(nextVisibleChildIndex2).getPaddingTop() + (int)(this.getResources().getDisplayMetrics().density * 16.0f));
                }
            }
            else {
                minimumHeight = n + this.getPaddingBottom();
            }
        }
        if (ViewCompat.getMinimumHeight((View)this) != minimumHeight) {
            this.setMinimumHeight(minimumHeight);
        }
    }
}
