// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.FrameLayout$LayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class ScaleFrameLayout extends FrameLayout
{
    private float mChildScale;
    private float mLayoutScaleX;
    private float mLayoutScaleY;
    
    public ScaleFrameLayout(final Context context) {
        this(context, null);
    }
    
    public ScaleFrameLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ScaleFrameLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mLayoutScaleX = 1.0f;
        this.mLayoutScaleY = 1.0f;
        this.mChildScale = 1.0f;
    }
    
    private static int getScaledMeasureSpec(int measureSpec, final float n) {
        if (n != 1.0f) {
            measureSpec = View$MeasureSpec.makeMeasureSpec((int)(View$MeasureSpec.getSize(measureSpec) / n + 0.5f), View$MeasureSpec.getMode(measureSpec));
        }
        return measureSpec;
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        super.addView(view, n, viewGroup$LayoutParams);
        view.setScaleX(this.mChildScale);
        view.setScaleY(this.mChildScale);
    }
    
    protected boolean addViewInLayout(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams, final boolean b) {
        final boolean addViewInLayout = super.addViewInLayout(view, n, viewGroup$LayoutParams, b);
        if (addViewInLayout) {
            view.setScaleX(this.mChildScale);
            view.setScaleY(this.mChildScale);
        }
        return addViewInLayout;
    }
    
    protected void onLayout(final boolean b, int n, int n2, int i, int n3) {
        final int childCount = this.getChildCount();
        final int layoutDirection = this.getLayoutDirection();
        float pivotX;
        if (layoutDirection == 1) {
            pivotX = this.getWidth() - this.getPivotX();
        }
        else {
            pivotX = this.getPivotX();
        }
        int n5;
        if (this.mLayoutScaleX != 1.0f) {
            final int paddingLeft = this.getPaddingLeft();
            final float mLayoutScaleX = this.mLayoutScaleX;
            final int n4 = paddingLeft + (int)(pivotX - pivotX / mLayoutScaleX + 0.5f);
            i = (int)((i - n - pivotX) / mLayoutScaleX + pivotX + 0.5f);
            n5 = this.getPaddingRight();
            n = n4;
        }
        else {
            final int paddingLeft2 = this.getPaddingLeft();
            i -= n;
            n5 = this.getPaddingRight();
            n = paddingLeft2;
        }
        final int n6 = i - n5;
        final float pivotY = this.getPivotY();
        if (this.mLayoutScaleY != 1.0f) {
            i = this.getPaddingTop();
            final float mLayoutScaleY = this.mLayoutScaleY;
            i += (int)(pivotY - pivotY / mLayoutScaleY + 0.5f);
            final int n7 = (int)((n3 - n2 - pivotY) / mLayoutScaleY + pivotY + 0.5f);
            n3 = this.getPaddingBottom();
            n2 = i;
            i = n7;
        }
        else {
            final int paddingTop = this.getPaddingTop();
            i = n3 - n2;
            n3 = this.getPaddingBottom();
            n2 = paddingTop;
        }
        final int n8 = i - n3;
        View child;
        FrameLayout$LayoutParams frameLayout$LayoutParams;
        int measuredWidth;
        int measuredHeight;
        int absoluteGravity;
        int n9;
        int n10;
        int n11 = 0;
        int n12 = 0;
        int n13;
        int n14 = 0;
        for (i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                frameLayout$LayoutParams = (FrameLayout$LayoutParams)child.getLayoutParams();
                measuredWidth = child.getMeasuredWidth();
                measuredHeight = child.getMeasuredHeight();
                if ((n3 = frameLayout$LayoutParams.gravity) == -1) {
                    n3 = 8388659;
                }
                absoluteGravity = Gravity.getAbsoluteGravity(n3, layoutDirection);
                n9 = (n3 & 0x70);
                n3 = (absoluteGravity & 0x7);
                Label_0422: {
                    if (n3 != 1) {
                        if (n3 != 5) {
                            n3 = frameLayout$LayoutParams.leftMargin + n;
                            break Label_0422;
                        }
                        n3 = n6 - measuredWidth;
                        n10 = frameLayout$LayoutParams.rightMargin;
                    }
                    else {
                        n3 = (n6 - n - measuredWidth) / 2 + n + frameLayout$LayoutParams.leftMargin;
                        n10 = frameLayout$LayoutParams.rightMargin;
                    }
                    n3 -= n10;
                }
                Label_0519: {
                    Label_0512: {
                        if (n9 != 16) {
                            if (n9 != 48) {
                                if (n9 == 80) {
                                    n11 = n8 - measuredHeight;
                                    n12 = frameLayout$LayoutParams.bottomMargin;
                                    break Label_0512;
                                }
                                n13 = frameLayout$LayoutParams.topMargin;
                            }
                            else {
                                n13 = frameLayout$LayoutParams.topMargin;
                            }
                            n14 = n13 + n2;
                            break Label_0519;
                        }
                        n11 = (n8 - n2 - measuredHeight) / 2 + n2 + frameLayout$LayoutParams.topMargin;
                        n12 = frameLayout$LayoutParams.bottomMargin;
                    }
                    n14 = n11 - n12;
                }
                child.layout(n3, n14, measuredWidth + n3, measuredHeight + n14);
                child.setPivotX(pivotX - n3);
                child.setPivotY(pivotY - n14);
            }
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        if (this.mLayoutScaleX == 1.0f && this.mLayoutScaleY == 1.0f) {
            super.onMeasure(n, n2);
        }
        else {
            super.onMeasure(getScaledMeasureSpec(n, this.mLayoutScaleX), getScaledMeasureSpec(n2, this.mLayoutScaleY));
            this.setMeasuredDimension((int)(this.getMeasuredWidth() * this.mLayoutScaleX + 0.5f), (int)(this.getMeasuredHeight() * this.mLayoutScaleY + 0.5f));
        }
    }
    
    public void setForeground(final Drawable drawable) {
        throw new UnsupportedOperationException();
    }
}
