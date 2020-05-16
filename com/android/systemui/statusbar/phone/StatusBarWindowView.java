// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Insets;
import android.view.WindowInsets$Type;
import android.view.WindowInsets;
import com.android.systemui.ScreenDecorations;
import android.graphics.Rect;
import android.graphics.Point;
import android.util.Pair;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.View;
import android.widget.FrameLayout$LayoutParams;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class StatusBarWindowView extends FrameLayout
{
    private int mLeftInset;
    private int mRightInset;
    private int mTopInset;
    
    public StatusBarWindowView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mLeftInset = 0;
        this.mRightInset = 0;
        this.mTopInset = 0;
    }
    
    private void applyMargins() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getLayoutParams() instanceof FrameLayout$LayoutParams) {
                final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)child.getLayoutParams();
                if (frameLayout$LayoutParams.rightMargin != this.mRightInset || frameLayout$LayoutParams.leftMargin != this.mLeftInset || frameLayout$LayoutParams.topMargin != this.mTopInset) {
                    frameLayout$LayoutParams.rightMargin = this.mRightInset;
                    frameLayout$LayoutParams.leftMargin = this.mLeftInset;
                    frameLayout$LayoutParams.topMargin = this.mTopInset;
                    child.requestLayout();
                }
            }
        }
    }
    
    public static Pair<Integer, Integer> cornerCutoutMargins(final DisplayCutout displayCutout, final Display display) {
        return statusBarCornerCutoutMargins(displayCutout, display, 0, 0);
    }
    
    public static Pair<Integer, Integer> paddingNeededForCutoutAndRoundedCorner(final DisplayCutout displayCutout, final Pair<Integer, Integer> pair, final int n) {
        if (displayCutout == null) {
            return (Pair<Integer, Integer>)new Pair((Object)n, (Object)n);
        }
        final int safeInsetLeft = displayCutout.getSafeInsetLeft();
        final int safeInsetRight = displayCutout.getSafeInsetRight();
        int max = safeInsetLeft;
        int max2 = safeInsetRight;
        if (pair != null) {
            max = Math.max(safeInsetLeft, (int)pair.first);
            max2 = Math.max(safeInsetRight, (int)pair.second);
        }
        return (Pair<Integer, Integer>)new Pair((Object)Math.max(max, n), (Object)Math.max(max2, n));
    }
    
    public static Pair<Integer, Integer> statusBarCornerCutoutMargins(final DisplayCutout displayCutout, final Display display, final int n, final int n2) {
        if (displayCutout == null) {
            return null;
        }
        final Point point = new Point();
        display.getRealSize(point);
        final Rect rect = new Rect();
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n == 3) {
                        return null;
                    }
                }
                else {
                    ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 5, rect);
                }
            }
            else {
                ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 3, rect);
            }
        }
        else {
            ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 48, rect);
        }
        if (n2 >= 0 && rect.top > n2) {
            return null;
        }
        if (rect.left <= 0) {
            return (Pair<Integer, Integer>)new Pair((Object)rect.right, (Object)0);
        }
        if (rect.right >= point.x) {
            return (Pair<Integer, Integer>)new Pair((Object)0, (Object)(point.x - rect.left));
        }
        return null;
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        final Insets insetsIgnoringVisibility = windowInsets.getInsetsIgnoringVisibility(WindowInsets$Type.systemBars());
        this.mLeftInset = insetsIgnoringVisibility.left;
        this.mRightInset = insetsIgnoringVisibility.right;
        this.mTopInset = 0;
        final DisplayCutout displayCutout = this.getRootWindowInsets().getDisplayCutout();
        if (displayCutout != null) {
            this.mTopInset = displayCutout.getWaterfallInsets().top;
        }
        this.applyMargins();
        return windowInsets;
    }
}
