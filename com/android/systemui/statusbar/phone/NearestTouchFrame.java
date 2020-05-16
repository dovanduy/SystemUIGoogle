// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.util.Pair;
import java.util.function.ToIntFunction;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import android.widget.FrameLayout;

public class NearestTouchFrame extends FrameLayout
{
    private final ArrayList<View> mClickableChildren;
    private final boolean mIsActive;
    private final int[] mOffset;
    private final int[] mTmpInt;
    private View mTouchingChild;
    
    public NearestTouchFrame(final Context context, final AttributeSet set) {
        this(context, set, context.getResources().getConfiguration());
    }
    
    NearestTouchFrame(final Context context, final AttributeSet set, final Configuration configuration) {
        super(context, set);
        this.mClickableChildren = new ArrayList<View>();
        this.mTmpInt = new int[2];
        this.mOffset = new int[2];
        this.mIsActive = (configuration.smallestScreenWidthDp < 600);
    }
    
    private void addClickableChildren(final ViewGroup viewGroup) {
        for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = viewGroup.getChildAt(i);
            if (child.isClickable()) {
                this.mClickableChildren.add(child);
            }
            else if (child instanceof ViewGroup) {
                this.addClickableChildren((ViewGroup)child);
            }
        }
    }
    
    private int distance(final View view, final MotionEvent motionEvent) {
        view.getLocationInWindow(this.mTmpInt);
        final int[] mTmpInt = this.mTmpInt;
        final int n = mTmpInt[0];
        final int[] mOffset = this.mOffset;
        final int n2 = n - mOffset[0];
        final int n3 = mTmpInt[1] - mOffset[1];
        return Math.max(Math.min(Math.abs(n2 - (int)motionEvent.getX()), Math.abs((int)motionEvent.getX() - (view.getWidth() + n2))), Math.min(Math.abs(n3 - (int)motionEvent.getY()), Math.abs((int)motionEvent.getY() - (view.getHeight() + n3))));
    }
    
    private View findNearestChild(final MotionEvent motionEvent) {
        if (this.mClickableChildren.isEmpty()) {
            return null;
        }
        return this.mClickableChildren.stream().filter((Predicate<? super Object>)_$$Lambda$dFYK0EjGBZUG5FTAJ9pyZPnsifY.INSTANCE).map((Function<? super Object, ?>)new _$$Lambda$NearestTouchFrame$c68uozdLu3LZY_hrzFrFQ_dtMIM(this, motionEvent)).min(Comparator.comparingInt((ToIntFunction<? super Object>)_$$Lambda$NearestTouchFrame$NP6mvtRuXVTLLChUNbbl4JUIMyU.INSTANCE)).map((Function<? super Object, ? extends View>)_$$Lambda$NearestTouchFrame$KtkvB6kuUFBlaLB_chuEtrCrZqA.INSTANCE).orElse(null);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.getLocationInWindow(this.mOffset);
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        this.mClickableChildren.clear();
        this.addClickableChildren((ViewGroup)this);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mIsActive) {
            if (motionEvent.getAction() == 0) {
                this.mTouchingChild = this.findNearestChild(motionEvent);
            }
            final View mTouchingChild = this.mTouchingChild;
            if (mTouchingChild != null) {
                motionEvent.offsetLocation(mTouchingChild.getWidth() / 2 - motionEvent.getX(), this.mTouchingChild.getHeight() / 2 - motionEvent.getY());
                return this.mTouchingChild.getVisibility() == 0 && this.mTouchingChild.dispatchTouchEvent(motionEvent);
            }
        }
        return super.onTouchEvent(motionEvent);
    }
}
