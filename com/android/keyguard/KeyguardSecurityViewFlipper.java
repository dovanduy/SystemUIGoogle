// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.ViewHierarchyEncoder;
import android.content.res.TypedArray;
import com.android.systemui.R$styleable;
import android.view.ViewDebug$ExportedProperty;
import android.content.res.ColorStateList;
import com.android.internal.widget.LockPatternUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout$LayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.widget.ViewFlipper;

public class KeyguardSecurityViewFlipper extends ViewFlipper implements KeyguardSecurityView
{
    private Rect mTempRect;
    
    public KeyguardSecurityViewFlipper(final Context context) {
        this(context, null);
    }
    
    public KeyguardSecurityViewFlipper(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTempRect = new Rect();
    }
    
    private int makeChildMeasureSpec(final int a, final int b) {
        final int n = 1073741824;
        int n2;
        int min;
        if (b != -2) {
            n2 = n;
            min = a;
            if (b != -1) {
                min = Math.min(a, b);
                n2 = n;
            }
        }
        else {
            n2 = Integer.MIN_VALUE;
            min = a;
        }
        return View$MeasureSpec.makeMeasureSpec(min, n2);
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        LayoutParams layoutParams;
        if (viewGroup$LayoutParams instanceof LayoutParams) {
            layoutParams = new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        else {
            layoutParams = new LayoutParams(viewGroup$LayoutParams);
        }
        return (ViewGroup$LayoutParams)layoutParams;
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    KeyguardSecurityView getSecurityView() {
        final View child = this.getChildAt(this.getDisplayedChild());
        if (child instanceof KeyguardSecurityView) {
            return (KeyguardSecurityView)child;
        }
        return null;
    }
    
    public CharSequence getTitle() {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            return securityView.getTitle();
        }
        return "";
    }
    
    public boolean needsInput() {
        final KeyguardSecurityView securityView = this.getSecurityView();
        return securityView != null && securityView.needsInput();
    }
    
    protected void onMeasure(int max, int max2) {
        final int mode = View$MeasureSpec.getMode(max);
        final int mode2 = View$MeasureSpec.getMode(max2);
        final int size = View$MeasureSpec.getSize(max);
        final int size2 = View$MeasureSpec.getSize(max2);
        final int childCount = this.getChildCount();
        final int n = 0;
        int n2 = size;
        max = size2;
        int n3;
        for (int i = 0; i < childCount; ++i, n2 = max2, max = n3) {
            final LayoutParams layoutParams = (LayoutParams)this.getChildAt(i).getLayoutParams();
            final int maxWidth = layoutParams.maxWidth;
            max2 = n2;
            if (maxWidth > 0 && maxWidth < (max2 = n2)) {
                max2 = maxWidth;
            }
            final int maxHeight = layoutParams.maxHeight;
            n3 = max;
            if (maxHeight > 0 && maxHeight < (n3 = max)) {
                n3 = maxHeight;
            }
        }
        final int n4 = this.getPaddingLeft() + this.getPaddingRight();
        final int n5 = this.getPaddingTop() + this.getPaddingBottom();
        final int max3 = Math.max(0, n2 - n4);
        final int max4 = Math.max(0, max - n5);
        if (mode == 1073741824) {
            max2 = size;
        }
        else {
            max2 = 0;
        }
        int j;
        if (mode2 == 1073741824) {
            max = size2;
            j = n;
        }
        else {
            max = 0;
            j = n;
        }
        while (j < childCount) {
            final View child = this.getChildAt(j);
            final LayoutParams layoutParams2 = (LayoutParams)child.getLayoutParams();
            child.measure(this.makeChildMeasureSpec(max3, layoutParams2.width), this.makeChildMeasureSpec(max4, layoutParams2.height));
            max2 = Math.max(max2, Math.min(child.getMeasuredWidth(), size - n4));
            max = Math.max(max, Math.min(child.getMeasuredHeight(), size2 - n5));
            ++j;
        }
        this.setMeasuredDimension(max2 + n4, max + n5);
    }
    
    public void onPause() {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.onPause();
        }
    }
    
    public void onResume(final int n) {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.onResume(n);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        this.mTempRect.set(0, 0, 0, 0);
        boolean b;
        for (int i = 0; i < this.getChildCount(); ++i, onTouchEvent = b) {
            final View child = this.getChildAt(i);
            b = onTouchEvent;
            if (child.getVisibility() == 0) {
                this.offsetRectIntoDescendantCoords(child, this.mTempRect);
                final Rect mTempRect = this.mTempRect;
                motionEvent.offsetLocation((float)mTempRect.left, (float)mTempRect.top);
                b = (child.dispatchTouchEvent(motionEvent) || onTouchEvent);
                final Rect mTempRect2 = this.mTempRect;
                motionEvent.offsetLocation((float)(-mTempRect2.left), (float)(-mTempRect2.top));
            }
        }
        return onTouchEvent;
    }
    
    public void reset() {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.reset();
        }
    }
    
    public void setKeyguardCallback(final KeyguardSecurityCallback keyguardCallback) {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.setKeyguardCallback(keyguardCallback);
        }
    }
    
    public void setLockPatternUtils(final LockPatternUtils lockPatternUtils) {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.setLockPatternUtils(lockPatternUtils);
        }
    }
    
    public void showMessage(final CharSequence charSequence, final ColorStateList list) {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.showMessage(charSequence, list);
        }
    }
    
    public void showPromptReason(final int n) {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.showPromptReason(n);
        }
    }
    
    public void startAppearAnimation() {
        final KeyguardSecurityView securityView = this.getSecurityView();
        if (securityView != null) {
            securityView.startAppearAnimation();
        }
    }
    
    public boolean startDisappearAnimation(final Runnable runnable) {
        final KeyguardSecurityView securityView = this.getSecurityView();
        return securityView != null && securityView.startDisappearAnimation(runnable);
    }
    
    public static class LayoutParams extends FrameLayout$LayoutParams
    {
        @ViewDebug$ExportedProperty(category = "layout")
        public int maxHeight;
        @ViewDebug$ExportedProperty(category = "layout")
        public int maxWidth;
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.KeyguardSecurityViewFlipper_Layout, 0, 0);
            this.maxWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.KeyguardSecurityViewFlipper_Layout_layout_maxWidth, 0);
            this.maxHeight = obtainStyledAttributes.getDimensionPixelSize(R$styleable.KeyguardSecurityViewFlipper_Layout_layout_maxHeight, 0);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((FrameLayout$LayoutParams)layoutParams);
            this.maxWidth = layoutParams.maxWidth;
            this.maxHeight = layoutParams.maxHeight;
        }
        
        protected void encodeProperties(final ViewHierarchyEncoder viewHierarchyEncoder) {
            super.encodeProperties(viewHierarchyEncoder);
            viewHierarchyEncoder.addProperty("layout:maxWidth", this.maxWidth);
            viewHierarchyEncoder.addProperty("layout:maxHeight", this.maxHeight);
        }
    }
}
