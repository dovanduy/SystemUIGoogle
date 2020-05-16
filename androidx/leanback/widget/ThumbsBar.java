// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.view.ViewGroup;
import androidx.leanback.R$dimen;
import android.util.SparseArray;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class ThumbsBar extends LinearLayout
{
    int mHeroThumbHeightInPixel;
    int mHeroThumbWidthInPixel;
    private boolean mIsUserSets;
    int mMeasuredMarginInPixel;
    int mNumOfThumbs;
    int mThumbHeightInPixel;
    int mThumbWidthInPixel;
    
    public ThumbsBar(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ThumbsBar(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mNumOfThumbs = -1;
        new SparseArray();
        this.mIsUserSets = false;
        this.mThumbWidthInPixel = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_thumbs_width);
        this.mThumbHeightInPixel = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_thumbs_height);
        this.mHeroThumbHeightInPixel = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_hero_thumbs_width);
        this.mHeroThumbWidthInPixel = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_hero_thumbs_height);
        this.mMeasuredMarginInPixel = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_thumbs_margin);
    }
    
    private int calculateNumOfThumbs(int n) {
        final int roundUp = roundUp(n - this.mHeroThumbWidthInPixel, this.mThumbWidthInPixel + this.mMeasuredMarginInPixel);
        if (roundUp < 2) {
            n = 2;
        }
        else {
            n = roundUp;
            if ((roundUp & 0x1) != 0x0) {
                n = roundUp + 1;
            }
        }
        return n + 1;
    }
    
    private static int roundUp(final int n, final int n2) {
        return (n + n2 - 1) / n2;
    }
    
    private void setNumberOfThumbsInternal() {
        while (this.getChildCount() > this.mNumOfThumbs) {
            this.removeView(this.getChildAt(this.getChildCount() - 1));
        }
        while (this.getChildCount() < this.mNumOfThumbs) {
            this.addView(this.createThumbView((ViewGroup)this), (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(this.mThumbWidthInPixel, this.mThumbHeightInPixel));
        }
        final int heroIndex = this.getHeroIndex();
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)child.getLayoutParams();
            if (heroIndex == i) {
                layoutParams.width = this.mHeroThumbWidthInPixel;
                layoutParams.height = this.mHeroThumbHeightInPixel;
            }
            else {
                layoutParams.width = this.mThumbWidthInPixel;
                layoutParams.height = this.mThumbHeightInPixel;
            }
            child.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
    }
    
    protected View createThumbView(final ViewGroup viewGroup) {
        return (View)new ImageView(viewGroup.getContext());
    }
    
    public int getHeroIndex() {
        return this.getChildCount() / 2;
    }
    
    protected void onLayout(final boolean b, int n, int n2, int n3, int n4) {
        super.onLayout(b, n, n2, n3, n4);
        final int heroIndex = this.getHeroIndex();
        final View child = this.getChildAt(heroIndex);
        n4 = this.getWidth() / 2 - child.getMeasuredWidth() / 2;
        final int n5 = this.getWidth() / 2 + child.getMeasuredWidth() / 2;
        child.layout(n4, this.getPaddingTop(), n5, this.getPaddingTop() + child.getMeasuredHeight());
        final int n6 = this.getPaddingTop() + child.getMeasuredHeight() / 2;
        n = heroIndex - 1;
        while (true) {
            n2 = heroIndex;
            n3 = n5;
            if (n < 0) {
                break;
            }
            n2 = n4 - this.mMeasuredMarginInPixel;
            final View child2 = this.getChildAt(n);
            child2.layout(n2 - child2.getMeasuredWidth(), n6 - child2.getMeasuredHeight() / 2, n2, child2.getMeasuredHeight() / 2 + n6);
            n4 = n2 - child2.getMeasuredWidth();
            --n;
        }
        while (++n2 < this.mNumOfThumbs) {
            n = n3 + this.mMeasuredMarginInPixel;
            final View child3 = this.getChildAt(n2);
            child3.layout(n, n6 - child3.getMeasuredHeight() / 2, child3.getMeasuredWidth() + n, child3.getMeasuredHeight() / 2 + n6);
            n3 = n + child3.getMeasuredWidth();
        }
    }
    
    protected void onMeasure(int mNumOfThumbs, final int n) {
        super.onMeasure(mNumOfThumbs, n);
        mNumOfThumbs = this.getMeasuredWidth();
        if (!this.mIsUserSets) {
            mNumOfThumbs = this.calculateNumOfThumbs(mNumOfThumbs);
            if (this.mNumOfThumbs != mNumOfThumbs) {
                this.mNumOfThumbs = mNumOfThumbs;
                this.setNumberOfThumbsInternal();
            }
        }
    }
}
