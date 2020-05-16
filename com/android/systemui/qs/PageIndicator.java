// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.content.res.ColorStateList;
import android.util.Log;
import com.android.systemui.R$string;
import android.view.View$MeasureSpec;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import com.android.systemui.R$drawable;
import android.widget.ImageView;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import java.util.ArrayList;
import android.view.ViewGroup;

public class PageIndicator extends ViewGroup
{
    private boolean mAnimating;
    private final Runnable mAnimationDone;
    private final int mPageDotWidth;
    private final int mPageIndicatorHeight;
    private final int mPageIndicatorWidth;
    private int mPosition;
    private final ArrayList<Integer> mQueuedPositions;
    
    public PageIndicator(final Context context, final AttributeSet set) {
        super(context, set);
        this.mQueuedPositions = new ArrayList<Integer>();
        this.mPosition = -1;
        this.mAnimationDone = new Runnable() {
            @Override
            public void run() {
                PageIndicator.this.mAnimating = false;
                if (PageIndicator.this.mQueuedPositions.size() != 0) {
                    final PageIndicator this$0 = PageIndicator.this;
                    this$0.setPosition((int)this$0.mQueuedPositions.remove(0));
                }
            }
        };
        this.mPageIndicatorWidth = (int)super.mContext.getResources().getDimension(R$dimen.qs_page_indicator_width);
        this.mPageIndicatorHeight = (int)super.mContext.getResources().getDimension(R$dimen.qs_page_indicator_height);
        this.mPageDotWidth = (int)(this.mPageIndicatorWidth * 0.4f);
    }
    
    private void animate(int max, int n) {
        final int a = max >> 1;
        final int n2 = n >> 1;
        this.setIndex(a);
        final boolean b = (max & 0x1) != 0x0;
        final boolean b2 = b ? (max > n) : (max < n);
        final int min = Math.min(a, n2);
        n = (max = Math.max(a, n2));
        if (n == min) {
            max = n + 1;
        }
        final ImageView imageView = (ImageView)this.getChildAt(min);
        final ImageView imageView2 = (ImageView)this.getChildAt(max);
        if (imageView != null) {
            if (imageView2 != null) {
                imageView2.setTranslationX(imageView.getX() - imageView2.getX());
                this.playAnimation(imageView, this.getTransition(b, b2, false));
                imageView.setAlpha(this.getAlpha(false));
                this.playAnimation(imageView2, this.getTransition(b, b2, true));
                imageView2.setAlpha(this.getAlpha(true));
                this.mAnimating = true;
            }
        }
    }
    
    private float getAlpha(final boolean b) {
        float n;
        if (b) {
            n = 1.0f;
        }
        else {
            n = 0.42f;
        }
        return n;
    }
    
    private int getTransition(final boolean b, final boolean b2, final boolean b3) {
        if (b3) {
            if (b) {
                if (b2) {
                    return R$drawable.major_b_a_animation;
                }
                return R$drawable.major_b_c_animation;
            }
            else {
                if (b2) {
                    return R$drawable.major_a_b_animation;
                }
                return R$drawable.major_c_b_animation;
            }
        }
        else if (b) {
            if (b2) {
                return R$drawable.minor_b_c_animation;
            }
            return R$drawable.minor_b_a_animation;
        }
        else {
            if (b2) {
                return R$drawable.minor_c_b_animation;
            }
            return R$drawable.minor_a_b_animation;
        }
    }
    
    private void playAnimation(final ImageView imageView, final int n) {
        final AnimatedVectorDrawable imageDrawable = (AnimatedVectorDrawable)this.getContext().getDrawable(n);
        imageView.setImageDrawable((Drawable)imageDrawable);
        imageDrawable.forceAnimationOnUI();
        imageDrawable.start();
        this.postDelayed(this.mAnimationDone, 250L);
    }
    
    private void setIndex(final int n) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final ImageView imageView = (ImageView)this.getChildAt(i);
            imageView.setTranslationX(0.0f);
            imageView.setImageResource(R$drawable.major_a_b);
            imageView.setAlpha(this.getAlpha(i == n));
        }
    }
    
    private void setPosition(final int mPosition) {
        if (this.isVisibleToUser() && Math.abs(this.mPosition - mPosition) == 1) {
            this.animate(this.mPosition, mPosition);
        }
        else {
            this.setIndex(mPosition >> 1);
        }
        this.mPosition = mPosition;
    }
    
    protected void onLayout(final boolean b, int i, int childCount, int n, final int n2) {
        childCount = this.getChildCount();
        if (childCount == 0) {
            return;
        }
        for (i = 0; i < childCount; ++i) {
            n = (this.mPageIndicatorWidth - this.mPageDotWidth) * i;
            this.getChildAt(i).layout(n, 0, this.mPageIndicatorWidth + n, this.mPageIndicatorHeight);
        }
    }
    
    protected void onMeasure(int i, int n) {
        final int childCount = this.getChildCount();
        if (childCount == 0) {
            super.onMeasure(i, n);
            return;
        }
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.mPageIndicatorWidth, 1073741824);
        n = View$MeasureSpec.makeMeasureSpec(this.mPageIndicatorHeight, 1073741824);
        for (i = 0; i < childCount; ++i) {
            this.getChildAt(i).measure(measureSpec, n);
        }
        n = this.mPageIndicatorWidth;
        i = this.mPageDotWidth;
        this.setMeasuredDimension((n - i) * (childCount - 1) + i, this.mPageIndicatorHeight);
    }
    
    public void setLocation(final float n) {
        final int n2 = (int)n;
        final Context context = this.getContext();
        final int accessibility_quick_settings_page = R$string.accessibility_quick_settings_page;
        boolean b = false;
        this.setContentDescription((CharSequence)context.getString(accessibility_quick_settings_page, new Object[] { n2 + 1, this.getChildCount() }));
        if (n != n2) {
            b = true;
        }
        final int n3 = n2 << 1 | (b ? 1 : 0);
        int n4 = this.mPosition;
        if (this.mQueuedPositions.size() != 0) {
            final ArrayList<Integer> mQueuedPositions = this.mQueuedPositions;
            n4 = mQueuedPositions.get(mQueuedPositions.size() - 1);
        }
        if (n3 == n4) {
            return;
        }
        if (this.mAnimating) {
            this.mQueuedPositions.add(n3);
            return;
        }
        this.setPosition(n3);
    }
    
    public void setNumPages(final int i) {
        int visibility;
        if (i > 1) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        this.setVisibility(visibility);
        if (this.mAnimating) {
            Log.w("PageIndicator", "setNumPages during animation");
        }
        while (i < this.getChildCount()) {
            this.removeViewAt(this.getChildCount() - 1);
        }
        final TypedArray obtainStyledAttributes = this.getContext().obtainStyledAttributes(new int[] { 16843818 });
        final int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        while (i > this.getChildCount()) {
            final ImageView imageView = new ImageView(super.mContext);
            imageView.setImageResource(R$drawable.minor_a_b);
            imageView.setImageTintList(ColorStateList.valueOf(color));
            this.addView((View)imageView, new ViewGroup$LayoutParams(this.mPageIndicatorWidth, this.mPageIndicatorHeight));
        }
        this.setIndex(this.mPosition >> 1);
    }
}
