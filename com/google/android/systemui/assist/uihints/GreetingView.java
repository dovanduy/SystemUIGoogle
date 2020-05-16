// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.text.TextPaint;
import android.animation.Animator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.text.style.CharacterStyle;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import android.view.ViewGroup$LayoutParams;
import java.util.Iterator;
import android.view.animation.OvershootInterpolator;
import android.util.Log;
import android.graphics.Color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import java.util.ArrayList;
import android.text.SpannableStringBuilder;
import android.animation.AnimatorSet;
import android.widget.TextView;

public class GreetingView extends TextView implements TranscriptionSpaceView
{
    private final int START_DELTA;
    private final int TEXT_COLOR_DARK;
    private final int TEXT_COLOR_LIGHT;
    private AnimatorSet mAnimatorSet;
    private final SpannableStringBuilder mGreetingBuilder;
    private float mMaxAlpha;
    private final ArrayList<StaggeredSpan> mSpans;
    
    public GreetingView(final Context context) {
        this(context, null);
    }
    
    public GreetingView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public GreetingView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public GreetingView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mGreetingBuilder = new SpannableStringBuilder();
        this.mSpans = new ArrayList<StaggeredSpan>();
        this.mAnimatorSet = new AnimatorSet();
        this.TEXT_COLOR_DARK = this.getResources().getColor(R$color.transcription_text_dark);
        this.TEXT_COLOR_LIGHT = this.getResources().getColor(R$color.transcription_text_light);
        this.START_DELTA = (int)this.getResources().getDimension(R$dimen.assist_greeting_start_delta);
        this.mMaxAlpha = (float)Color.alpha(this.getCurrentTextColor());
    }
    
    private void animateIn(float min) {
        if (this.mAnimatorSet.isRunning()) {
            Log.w("GreetingView", "Already animating in greeting view; ignoring");
            return;
        }
        this.mAnimatorSet = new AnimatorSet();
        min = Math.min(10.0f, min / 1.2f + 3.0f);
        final OvershootInterpolator overshootInterpolator = new OvershootInterpolator(min);
        long n = 0L;
        final Iterator<StaggeredSpan> iterator = this.mSpans.iterator();
        while (iterator.hasNext()) {
            iterator.next().initAnimator(n, overshootInterpolator, this.mAnimatorSet);
            n += 8L;
        }
        this.setLayoutParams(min, overshootInterpolator);
        this.mAnimatorSet.start();
    }
    
    private void setLayoutParams(float n, final OvershootInterpolator overshootInterpolator) {
        final float n2 = (float)DisplayUtils.convertSpToPx(this.getResources().getDimension(R$dimen.transcription_text_size), super.mContext);
        final float n3 = (2.0f * n + 6.0f) / (n * 6.0f + 6.0f);
        n = (float)this.START_DELTA;
        final float interpolation = overshootInterpolator.getInterpolation(n3);
        final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = (int)(n * interpolation + n2);
        }
        this.setVisibility(0);
        this.requestLayout();
    }
    
    private void setUpTextSpans(final String s) {
        final String[] split = s.split("\\s+");
        this.mGreetingBuilder.clear();
        this.mSpans.clear();
        this.mGreetingBuilder.append((CharSequence)s);
        final int length = split.length;
        int i = 0;
        int fromIndex = 0;
        while (i < length) {
            final String str = split[i];
            final StaggeredSpan e = new StaggeredSpan();
            final int index = s.indexOf(str, fromIndex);
            fromIndex = str.length() + index;
            this.mGreetingBuilder.setSpan((Object)e, index, fromIndex, 33);
            this.mSpans.add(e);
            ++i;
        }
    }
    
    public ListenableFuture<Void> hide(final boolean b) {
        if (this.mAnimatorSet.isRunning()) {
            this.mAnimatorSet.cancel();
        }
        this.setVisibility(8);
        return Futures.immediateFuture((Void)null);
    }
    
    public void onFontSizeChanged() {
        this.setTextSize(0, super.mContext.getResources().getDimension(R$dimen.transcription_text_size));
    }
    
    void setGreeting(final String text) {
        this.setPadding(0, 0, 0, 0);
        this.setText((CharSequence)text);
        this.setVisibility(0);
    }
    
    void setGreetingAnimated(final String upTextSpans, final float a) {
        this.setPadding(0, 0, 0, -this.START_DELTA);
        this.setUpTextSpans(upTextSpans);
        this.setText((CharSequence)this.mGreetingBuilder);
        this.animateIn(Math.abs(a));
    }
    
    public void setHasDarkBackground(final boolean b) {
        int textColor;
        if (b) {
            textColor = this.TEXT_COLOR_DARK;
        }
        else {
            textColor = this.TEXT_COLOR_LIGHT;
        }
        this.setTextColor(textColor);
        this.mMaxAlpha = (float)Color.alpha(this.getCurrentTextColor());
    }
    
    private class StaggeredSpan extends CharacterStyle
    {
        private int mAlpha;
        private int mShift;
        
        private StaggeredSpan() {
            this.mShift = 0;
            this.mAlpha = 0;
        }
        
        void initAnimator(final long n, final OvershootInterpolator interpolator, final AnimatorSet set) {
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
            ofFloat.setInterpolator((TimeInterpolator)interpolator);
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GreetingView$StaggeredSpan$22yc2yUxbFF2pvbaLdFyZnZnGH4(this));
            ofFloat.setDuration(400L);
            ofFloat.setStartDelay(n);
            set.play((Animator)ofFloat);
            final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
            ofFloat2.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GreetingView$StaggeredSpan$EPImxE3HXNOzfVNqeE9slHPh5Ys(this));
            ofFloat2.setDuration(100L);
            ofFloat2.setStartDelay(n);
            set.play((Animator)ofFloat2);
        }
        
        public void updateDrawState(final TextPaint textPaint) {
            textPaint.baselineShift -= this.mShift;
            textPaint.setAlpha(this.mAlpha);
            GreetingView.this.invalidate();
        }
    }
}
