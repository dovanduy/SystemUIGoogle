// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.widget.RelativeLayout;
import java.util.ArrayList;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class ReverseLinearLayout extends LinearLayout
{
    private boolean mIsAlternativeOrder;
    private boolean mIsLayoutReverse;
    
    public ReverseLinearLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private static void reverseGroup(final ViewGroup viewGroup, final boolean b) {
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            final View child = viewGroup.getChildAt(i);
            reverseParams(child.getLayoutParams(), child, b);
            if (child instanceof ViewGroup) {
                reverseGroup((ViewGroup)child, b);
            }
        }
    }
    
    private static void reverseParams(final ViewGroup$LayoutParams viewGroup$LayoutParams, final View view, final boolean b) {
        if (view instanceof Reversable) {
            ((Reversable)view).reverse(b);
        }
        if (view.getPaddingLeft() == view.getPaddingRight() && view.getPaddingTop() == view.getPaddingBottom()) {
            view.setPadding(view.getPaddingTop(), view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingLeft());
        }
        if (viewGroup$LayoutParams == null) {
            return;
        }
        final int width = viewGroup$LayoutParams.width;
        viewGroup$LayoutParams.width = viewGroup$LayoutParams.height;
        viewGroup$LayoutParams.height = width;
    }
    
    private void updateOrder() {
        final int layoutDirection = this.getLayoutDirection();
        final int n = 0;
        final boolean mIsLayoutReverse = layoutDirection == 1 ^ this.mIsAlternativeOrder;
        if (this.mIsLayoutReverse != mIsLayoutReverse) {
            final int childCount = this.getChildCount();
            final ArrayList list = new ArrayList<View>(childCount);
            for (int i = n; i < childCount; ++i) {
                list.add(this.getChildAt(i));
            }
            this.removeAllViews();
            for (int j = childCount - 1; j >= 0; --j) {
                super.addView((View)list.get(j));
            }
            this.mIsLayoutReverse = mIsLayoutReverse;
        }
    }
    
    public void addView(final View view) {
        reverseParams(view.getLayoutParams(), view, this.mIsLayoutReverse);
        if (this.mIsLayoutReverse) {
            super.addView(view, 0);
        }
        else {
            super.addView(view);
        }
    }
    
    public void addView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        reverseParams(viewGroup$LayoutParams, view, this.mIsLayoutReverse);
        if (this.mIsLayoutReverse) {
            super.addView(view, 0, viewGroup$LayoutParams);
        }
        else {
            super.addView(view, viewGroup$LayoutParams);
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.updateOrder();
    }
    
    public void onRtlPropertiesChanged(final int n) {
        super.onRtlPropertiesChanged(n);
        this.updateOrder();
    }
    
    public void setAlternativeOrder(final boolean mIsAlternativeOrder) {
        this.mIsAlternativeOrder = mIsAlternativeOrder;
        this.updateOrder();
    }
    
    public interface Reversable
    {
        void reverse(final boolean p0);
    }
    
    public static class ReverseRelativeLayout extends RelativeLayout implements Reversable
    {
        private int mDefaultGravity;
        
        public ReverseRelativeLayout(final Context context) {
            super(context);
            this.mDefaultGravity = 0;
        }
        
        public void reverse(final boolean b) {
            this.updateGravity(b);
            reverseGroup((ViewGroup)this, b);
        }
        
        public void setDefaultGravity(final int mDefaultGravity) {
            this.mDefaultGravity = mDefaultGravity;
        }
        
        public void updateGravity(final boolean b) {
            final int mDefaultGravity = this.mDefaultGravity;
            final int n = 80;
            if (mDefaultGravity != 48 && mDefaultGravity != 80) {
                return;
            }
            int mDefaultGravity2;
            final int n2 = mDefaultGravity2 = this.mDefaultGravity;
            if (b) {
                if (n2 == 48) {
                    mDefaultGravity2 = n;
                }
                else {
                    mDefaultGravity2 = 48;
                }
            }
            if (this.getGravity() != mDefaultGravity2) {
                this.setGravity(mDefaultGravity2);
            }
        }
    }
}
