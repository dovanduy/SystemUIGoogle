// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.notification;

import android.view.ViewGroup;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class ZenRadioLayout extends LinearLayout
{
    public ZenRadioLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private View findFirstClickable(View firstClickable) {
        if (firstClickable.isClickable()) {
            return firstClickable;
        }
        if (firstClickable instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)firstClickable;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                firstClickable = this.findFirstClickable(viewGroup.getChildAt(i));
                if (firstClickable != null) {
                    return firstClickable;
                }
            }
        }
        return null;
    }
    
    private View findLastClickable(final View view) {
        if (view.isClickable()) {
            return view;
        }
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View lastClickable = this.findLastClickable(viewGroup.getChildAt(i));
                if (lastClickable != null) {
                    return lastClickable;
                }
            }
        }
        return null;
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        int i = 0;
        final ViewGroup viewGroup = (ViewGroup)this.getChildAt(0);
        final ViewGroup viewGroup2 = (ViewGroup)this.getChildAt(1);
        final int childCount = viewGroup.getChildCount();
        if (childCount == viewGroup2.getChildCount()) {
            View lastClickable = null;
            boolean b = false;
            while (i < childCount) {
                final View child = viewGroup.getChildAt(i);
                final View child2 = viewGroup2.getChildAt(i);
                if (lastClickable != null) {
                    child.setAccessibilityTraversalAfter(lastClickable.getId());
                }
                final View firstClickable = this.findFirstClickable(child2);
                if (firstClickable != null) {
                    firstClickable.setAccessibilityTraversalAfter(child.getId());
                }
                lastClickable = this.findLastClickable(child2);
                if (child.getLayoutParams().height != child2.getMeasuredHeight()) {
                    child.getLayoutParams().height = child2.getMeasuredHeight();
                    b = true;
                }
                ++i;
            }
            if (b) {
                super.onMeasure(n, n2);
            }
            return;
        }
        throw new IllegalStateException("Expected matching children");
    }
}
