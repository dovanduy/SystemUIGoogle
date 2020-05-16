// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import java.util.Iterator;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.view.animation.OvershootInterpolator;
import android.util.Log;
import com.android.systemui.R$dimen;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Bundle;
import java.util.List;
import android.animation.ValueAnimator;
import android.widget.LinearLayout;

public class ChipsContainer extends LinearLayout implements TranscriptionSpaceView
{
    private final int CHIP_MARGIN;
    private final int START_DELTA;
    private ValueAnimator mAnimator;
    private int mAvailableWidth;
    private List<ChipView> mChipViews;
    private List<Bundle> mChips;
    private boolean mDarkBackground;
    
    public ChipsContainer(final Context context) {
        this(context, null);
    }
    
    public ChipsContainer(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ChipsContainer(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ChipsContainer(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mChips = new ArrayList<Bundle>();
        this.mChipViews = new ArrayList<ChipView>();
        this.mAnimator = new ValueAnimator();
        this.CHIP_MARGIN = (int)this.getResources().getDimension(R$dimen.assist_chip_horizontal_margin);
        this.START_DELTA = (int)this.getResources().getDimension(R$dimen.assist_greeting_start_delta);
    }
    
    private void bounceAnimate(final float n) {
        if (this.mAnimator.isRunning()) {
            Log.w("ChipsContainer", "Already animating in chips view; ignoring");
            return;
        }
        (this.mAnimator = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).setInterpolator((TimeInterpolator)new OvershootInterpolator(Math.min(10.0f, n / 1.2f + 3.0f)));
        this.mAnimator.setDuration(400L);
        this.mAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ChipsContainer$1CJNZhibxoZr6ANR2w1NLADw_Hc(this));
        this.setVisibility(0);
        this.mAnimator.start();
    }
    
    private void setChipsInternal() {
        int mAvailableWidth = this.mAvailableWidth;
        final Iterator<Bundle> iterator = this.mChips.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            final Bundle chip = iterator.next();
            ChipView chipView;
            if (i < this.mChipViews.size()) {
                chipView = this.mChipViews.get(i);
            }
            else {
                chipView = (ChipView)LayoutInflater.from(this.getContext()).inflate(R$layout.assist_chip, (ViewGroup)this, false);
                this.mChipViews.add(chipView);
            }
            if (chipView.setChip(chip)) {
                chipView.setHasDarkBackground(this.mDarkBackground);
                chipView.measure(0, 0);
                final int n = chipView.getMeasuredWidth() + this.CHIP_MARGIN * 2;
                if (n >= mAvailableWidth) {
                    continue;
                }
                if (chipView.getParent() == null) {
                    chipView.setVisibility(0);
                    this.addView((View)chipView);
                }
                mAvailableWidth -= n;
                ++i;
            }
        }
        if (i < this.mChipViews.size()) {
            while (i < this.mChipViews.size()) {
                this.mChipViews.get(i).setVisibility(8);
                ++i;
            }
        }
        this.requestLayout();
    }
    
    private void zoomAnimate() {
        if (this.mAnimator.isRunning()) {
            Log.w("ChipsContainer", "Already animating in chips view; ignoring");
            return;
        }
        final AnimatorSet set = new AnimatorSet();
        set.play((Animator)ObjectAnimator.ofFloat((Object)this, View.SCALE_X, new float[] { 0.8f, 1.0f })).with((Animator)ObjectAnimator.ofFloat((Object)this, View.SCALE_Y, new float[] { 0.8f, 1.0f })).with((Animator)ObjectAnimator.ofFloat((Object)this, View.ALPHA, new float[] { 0.0f, 1.0f }));
        set.setDuration(200L);
        this.setVisibility(0);
        set.start();
    }
    
    public ListenableFuture<Void> hide(final boolean b) {
        if (this.mAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        this.removeAllViews();
        this.setVisibility(8);
        this.setTranslationY(0.0f);
        return Futures.immediateFuture((Void)null);
    }
    
    public void onFontSizeChanged() {
        final float dimension = super.mContext.getResources().getDimension(R$dimen.assist_chip_text_size);
        final Iterator<ChipView> iterator = this.mChipViews.iterator();
        while (iterator.hasNext()) {
            iterator.next().updateTextSize(dimension);
        }
        this.requestLayout();
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int rotatedWidth = DisplayUtils.getRotatedWidth(this.getContext());
        if (rotatedWidth != this.mAvailableWidth) {
            this.mAvailableWidth = rotatedWidth;
            this.setChipsInternal();
        }
        super.onMeasure(n, n2);
    }
    
    void setChips(final List<Bundle> mChips) {
        this.mChips = mChips;
        this.setChipsInternal();
        this.setVisibility(0);
    }
    
    void setChipsAnimatedBounce(final List<Bundle> mChips, final float n) {
        this.mChips = mChips;
        this.setChipsInternal();
        this.bounceAnimate(n);
    }
    
    void setChipsAnimatedZoom(final List<Bundle> mChips) {
        this.mChips = mChips;
        this.setChipsInternal();
        this.zoomAnimate();
    }
    
    public void setHasDarkBackground(final boolean b) {
        if (this.mDarkBackground != b) {
            this.mDarkBackground = b;
            for (int i = 0; i < this.getChildCount(); ++i) {
                ((ChipView)this.getChildAt(i)).setHasDarkBackground(b);
            }
        }
    }
}
