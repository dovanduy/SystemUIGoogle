// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.graphics.drawable.Drawable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.R$id;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;

public class AlertDialogLayout extends LinearLayoutCompat
{
    public AlertDialogLayout(final Context context) {
        super(context);
    }
    
    public AlertDialogLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void forceUniformWidth(final int n, final int n2) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 1073741824);
        for (int i = 0; i < n; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.width == -1) {
                    final int height = layoutParams.height;
                    layoutParams.height = child.getMeasuredHeight();
                    this.measureChildWithMargins(child, measureSpec, 0, n2, 0);
                    layoutParams.height = height;
                }
            }
        }
    }
    
    private static int resolveMinimumHeight(final View view) {
        final int minimumHeight = ViewCompat.getMinimumHeight(view);
        if (minimumHeight > 0) {
            return minimumHeight;
        }
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            if (viewGroup.getChildCount() == 1) {
                return resolveMinimumHeight(viewGroup.getChildAt(0));
            }
        }
        return 0;
    }
    
    private void setChildFrame(final View view, final int n, final int n2, final int n3, final int n4) {
        view.layout(n, n2, n3 + n, n4 + n2);
    }
    
    private boolean tryOnMeasure(final int n, final int n2) {
        final int childCount = this.getChildCount();
        View view = null;
        View view3;
        View view2 = view3 = null;
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final int id = child.getId();
                if (id == R$id.topPanel) {
                    view = child;
                }
                else if (id == R$id.buttonPanel) {
                    view2 = child;
                }
                else {
                    if (id != R$id.contentPanel && id != R$id.customPanel) {
                        return false;
                    }
                    if (view3 != null) {
                        return false;
                    }
                    view3 = child;
                }
            }
        }
        final int mode = View$MeasureSpec.getMode(n2);
        final int size = View$MeasureSpec.getSize(n2);
        final int mode2 = View$MeasureSpec.getMode(n);
        int n3 = this.getPaddingTop() + this.getPaddingBottom();
        int n4;
        if (view != null) {
            view.measure(n, 0);
            n3 += view.getMeasuredHeight();
            n4 = View.combineMeasuredStates(0, view.getMeasuredState());
        }
        else {
            n4 = 0;
        }
        int resolveMinimumHeight;
        int b;
        if (view2 != null) {
            view2.measure(n, 0);
            resolveMinimumHeight = resolveMinimumHeight(view2);
            b = view2.getMeasuredHeight() - resolveMinimumHeight;
            n3 += resolveMinimumHeight;
            n4 = View.combineMeasuredStates(n4, view2.getMeasuredState());
        }
        else {
            resolveMinimumHeight = (b = 0);
        }
        int measuredHeight;
        if (view3 != null) {
            int measureSpec;
            if (mode == 0) {
                measureSpec = 0;
            }
            else {
                measureSpec = View$MeasureSpec.makeMeasureSpec(Math.max(0, size - n3), mode);
            }
            view3.measure(n, measureSpec);
            measuredHeight = view3.getMeasuredHeight();
            n3 += measuredHeight;
            n4 = View.combineMeasuredStates(n4, view3.getMeasuredState());
        }
        else {
            measuredHeight = 0;
        }
        final int a = size - n3;
        int n5 = n4;
        int n6 = a;
        int n7 = n3;
        if (view2 != null) {
            final int min = Math.min(a, b);
            int n8 = a;
            int n9 = resolveMinimumHeight;
            if (min > 0) {
                n8 = a - min;
                n9 = resolveMinimumHeight + min;
            }
            view2.measure(n, View$MeasureSpec.makeMeasureSpec(n9, 1073741824));
            n7 = n3 - resolveMinimumHeight + view2.getMeasuredHeight();
            final int combineMeasuredStates = View.combineMeasuredStates(n4, view2.getMeasuredState());
            n6 = n8;
            n5 = combineMeasuredStates;
        }
        int combineMeasuredStates2 = n5;
        int n10 = n7;
        if (view3 != null) {
            combineMeasuredStates2 = n5;
            n10 = n7;
            if (n6 > 0) {
                view3.measure(n, View$MeasureSpec.makeMeasureSpec(measuredHeight + n6, mode));
                n10 = n7 - measuredHeight + view3.getMeasuredHeight();
                combineMeasuredStates2 = View.combineMeasuredStates(n5, view3.getMeasuredState());
            }
        }
        int j = 0;
        int a2 = 0;
        while (j < childCount) {
            final View child2 = this.getChildAt(j);
            int max = a2;
            if (child2.getVisibility() != 8) {
                max = Math.max(a2, child2.getMeasuredWidth());
            }
            ++j;
            a2 = max;
        }
        this.setMeasuredDimension(View.resolveSizeAndState(a2 + (this.getPaddingLeft() + this.getPaddingRight()), n, combineMeasuredStates2), View.resolveSizeAndState(n10, n2, 0));
        if (mode2 != 1073741824) {
            this.forceUniformWidth(childCount, n2);
        }
        return true;
    }
    
    @Override
    protected void onLayout(final boolean b, int paddingTop, int intrinsicHeight, int i, int gravity) {
        final int paddingLeft = this.getPaddingLeft();
        final int n = i - paddingTop;
        final int paddingRight = this.getPaddingRight();
        final int paddingRight2 = this.getPaddingRight();
        i = this.getMeasuredHeight();
        final int childCount = this.getChildCount();
        final int gravity2 = this.getGravity();
        paddingTop = (gravity2 & 0x70);
        if (paddingTop != 16) {
            if (paddingTop != 80) {
                paddingTop = this.getPaddingTop();
            }
            else {
                paddingTop = this.getPaddingTop() + gravity - intrinsicHeight - i;
            }
        }
        else {
            paddingTop = this.getPaddingTop() + (gravity - intrinsicHeight - i) / 2;
        }
        final Drawable dividerDrawable = this.getDividerDrawable();
        if (dividerDrawable == null) {
            intrinsicHeight = 0;
        }
        else {
            intrinsicHeight = dividerDrawable.getIntrinsicHeight();
        }
        View child;
        int measuredWidth;
        int measuredHeight;
        LayoutParams layoutParams;
        int n2;
        int n3;
        for (i = 0; i < childCount; ++i, paddingTop = gravity) {
            child = this.getChildAt(i);
            gravity = paddingTop;
            if (child != null) {
                gravity = paddingTop;
                if (child.getVisibility() != 8) {
                    measuredWidth = child.getMeasuredWidth();
                    measuredHeight = child.getMeasuredHeight();
                    layoutParams = (LayoutParams)child.getLayoutParams();
                    if ((gravity = layoutParams.gravity) < 0) {
                        gravity = (gravity2 & 0x800007);
                    }
                    gravity = (GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection((View)this)) & 0x7);
                    Label_0304: {
                        if (gravity != 1) {
                            if (gravity != 5) {
                                gravity = layoutParams.leftMargin + paddingLeft;
                                break Label_0304;
                            }
                            gravity = n - paddingRight - measuredWidth;
                            n2 = layoutParams.rightMargin;
                        }
                        else {
                            gravity = (n - paddingLeft - paddingRight2 - measuredWidth) / 2 + paddingLeft + layoutParams.leftMargin;
                            n2 = layoutParams.rightMargin;
                        }
                        gravity -= n2;
                    }
                    n3 = paddingTop;
                    if (this.hasDividerBeforeChildAt(i)) {
                        n3 = paddingTop + intrinsicHeight;
                    }
                    paddingTop = n3 + layoutParams.topMargin;
                    this.setChildFrame(child, gravity, paddingTop, measuredWidth, measuredHeight);
                    gravity = paddingTop + (measuredHeight + layoutParams.bottomMargin);
                }
            }
        }
    }
    
    @Override
    protected void onMeasure(final int n, final int n2) {
        if (!this.tryOnMeasure(n, n2)) {
            super.onMeasure(n, n2);
        }
    }
}
