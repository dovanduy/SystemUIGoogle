// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.animation;

import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Paint;
import android.animation.TimeInterpolator;
import android.animation.ObjectAnimator;
import android.view.RenderNodeAnimator;
import com.android.settingslib.R$dimen;
import android.view.animation.AnimationUtils;
import android.content.Context;
import android.view.animation.Interpolator;
import android.view.View;

public class AppearAnimationUtils implements AppearAnimationCreator<View>
{
    protected boolean mAppearing;
    protected final float mDelayScale;
    private final long mDuration;
    private final Interpolator mInterpolator;
    private final AppearAnimationProperties mProperties;
    protected RowTranslationScaler mRowTranslationScaler;
    private final float mStartTranslation;
    
    public AppearAnimationUtils(final Context context) {
        this(context, 220L, 1.0f, 1.0f, AnimationUtils.loadInterpolator(context, 17563662));
    }
    
    public AppearAnimationUtils(final Context context, final long mDuration, final float n, final float mDelayScale, final Interpolator mInterpolator) {
        this.mProperties = new AppearAnimationProperties();
        this.mInterpolator = mInterpolator;
        this.mStartTranslation = context.getResources().getDimensionPixelOffset(R$dimen.appear_y_translation_start) * n;
        this.mDelayScale = mDelayScale;
        this.mDuration = mDuration;
        this.mAppearing = true;
    }
    
    private <T> AppearAnimationProperties getDelays(final T[] array) {
        final AppearAnimationProperties mProperties = this.mProperties;
        mProperties.maxDelayColIndex = -1;
        mProperties.maxDelayRowIndex = -1;
        mProperties.delays = new long[array.length][];
        long n = -1L;
        long n2;
        for (int i = 0; i < array.length; ++i, n = n2) {
            this.mProperties.delays[i] = new long[1];
            final long calculateDelay = this.calculateDelay(i, 0);
            final AppearAnimationProperties mProperties2 = this.mProperties;
            mProperties2.delays[i][0] = calculateDelay;
            n2 = n;
            if (array[i] != null) {
                n2 = n;
                if (calculateDelay > n) {
                    mProperties2.maxDelayColIndex = 0;
                    mProperties2.maxDelayRowIndex = i;
                    n2 = calculateDelay;
                }
            }
        }
        return this.mProperties;
    }
    
    private <T> AppearAnimationProperties getDelays(final T[][] array) {
        final AppearAnimationProperties mProperties = this.mProperties;
        mProperties.maxDelayColIndex = -1;
        mProperties.maxDelayRowIndex = -1;
        mProperties.delays = new long[array.length][];
        long n = -1L;
        for (int i = 0; i < array.length; ++i) {
            final T[] array2 = array[i];
            this.mProperties.delays[i] = new long[array2.length];
            long n2;
            for (int j = 0; j < array2.length; ++j, n = n2) {
                final long calculateDelay = this.calculateDelay(i, j);
                final AppearAnimationProperties mProperties2 = this.mProperties;
                mProperties2.delays[i][j] = calculateDelay;
                n2 = n;
                if (array[i][j] != null) {
                    n2 = n;
                    if (calculateDelay > n) {
                        mProperties2.maxDelayColIndex = j;
                        mProperties2.maxDelayRowIndex = i;
                        n2 = calculateDelay;
                    }
                }
            }
        }
        return this.mProperties;
    }
    
    private <T> void startAnimations(final AppearAnimationProperties appearAnimationProperties, final T[] array, final Runnable runnable, final AppearAnimationCreator<T> appearAnimationCreator) {
        if (appearAnimationProperties.maxDelayRowIndex != -1 && appearAnimationProperties.maxDelayColIndex != -1) {
            int n = 0;
            while (true) {
                final long[][] delays = appearAnimationProperties.delays;
                if (n >= delays.length) {
                    break;
                }
                final long n2 = delays[n][0];
                Runnable runnable2;
                if (appearAnimationProperties.maxDelayRowIndex == n && appearAnimationProperties.maxDelayColIndex == 0) {
                    runnable2 = runnable;
                }
                else {
                    runnable2 = null;
                }
                final RowTranslationScaler mRowTranslationScaler = this.mRowTranslationScaler;
                float rowTranslationScale;
                if (mRowTranslationScaler != null) {
                    rowTranslationScale = mRowTranslationScaler.getRowTranslationScale(n, appearAnimationProperties.delays.length);
                }
                else {
                    rowTranslationScale = 1.0f;
                }
                float n3 = rowTranslationScale * this.mStartTranslation;
                final T t = array[n];
                final long mDuration = this.mDuration;
                if (!this.mAppearing) {
                    n3 = -n3;
                }
                appearAnimationCreator.createAnimation(t, n2, mDuration, n3, this.mAppearing, this.mInterpolator, runnable2);
                ++n;
            }
            return;
        }
        runnable.run();
    }
    
    private <T> void startAnimations(final AppearAnimationProperties appearAnimationProperties, final T[][] array, final Runnable runnable, final AppearAnimationCreator<T> appearAnimationCreator) {
        if (appearAnimationProperties.maxDelayRowIndex != -1 && appearAnimationProperties.maxDelayColIndex != -1) {
            int n = 0;
            while (true) {
                final long[][] delays = appearAnimationProperties.delays;
                if (n >= delays.length) {
                    break;
                }
                final long[] array2 = delays[n];
                final RowTranslationScaler mRowTranslationScaler = this.mRowTranslationScaler;
                float rowTranslationScale;
                if (mRowTranslationScaler != null) {
                    rowTranslationScale = mRowTranslationScaler.getRowTranslationScale(n, delays.length);
                }
                else {
                    rowTranslationScale = 1.0f;
                }
                final float n2 = rowTranslationScale * this.mStartTranslation;
                for (int i = 0; i < array2.length; ++i) {
                    final long n3 = array2[i];
                    Runnable runnable2;
                    if (appearAnimationProperties.maxDelayRowIndex == n && appearAnimationProperties.maxDelayColIndex == i) {
                        runnable2 = runnable;
                    }
                    else {
                        runnable2 = null;
                    }
                    final T t = array[n][i];
                    final long mDuration = this.mDuration;
                    float n4;
                    if (this.mAppearing) {
                        n4 = n2;
                    }
                    else {
                        n4 = -n2;
                    }
                    appearAnimationCreator.createAnimation(t, n3, mDuration, n4, this.mAppearing, this.mInterpolator, runnable2);
                }
                ++n;
            }
            return;
        }
        runnable.run();
    }
    
    public static void startTranslationYAnimation(final View target, final long startDelay, final long duration, final float n, final Interpolator interpolator) {
        Object ofFloat;
        if (target.isHardwareAccelerated()) {
            final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(1, n);
            renderNodeAnimator.setTarget(target);
            ofFloat = renderNodeAnimator;
        }
        else {
            ofFloat = ObjectAnimator.ofFloat((Object)target, View.TRANSLATION_Y, new float[] { target.getTranslationY(), n });
        }
        ((Animator)ofFloat).setInterpolator((TimeInterpolator)interpolator);
        ((Animator)ofFloat).setDuration(duration);
        ((Animator)ofFloat).setStartDelay(startDelay);
        ((Animator)ofFloat).start();
    }
    
    protected long calculateDelay(final int n, final int n2) {
        return (long)((n * 40 + n2 * (Math.pow(n, 0.4) + 0.4) * 20.0) * this.mDelayScale);
    }
    
    @Override
    public void createAnimation(final View target, final long startDelay, final long duration, float n, final boolean b, final Interpolator interpolator, final Runnable runnable) {
        if (target != null) {
            final float n2 = 1.0f;
            float alpha;
            if (b) {
                alpha = 0.0f;
            }
            else {
                alpha = 1.0f;
            }
            target.setAlpha(alpha);
            float translationY;
            if (b) {
                translationY = n;
            }
            else {
                translationY = 0.0f;
            }
            target.setTranslationY(translationY);
            float n3;
            if (b) {
                n3 = n2;
            }
            else {
                n3 = 0.0f;
            }
            Object ofFloat;
            if (target.isHardwareAccelerated()) {
                ofFloat = new RenderNodeAnimator(11, n3);
                ((RenderNodeAnimator)ofFloat).setTarget(target);
            }
            else {
                ofFloat = ObjectAnimator.ofFloat((Object)target, View.ALPHA, new float[] { target.getAlpha(), n3 });
            }
            ((Animator)ofFloat).setInterpolator((TimeInterpolator)interpolator);
            ((Animator)ofFloat).setDuration(duration);
            ((Animator)ofFloat).setStartDelay(startDelay);
            if (target.hasOverlappingRendering()) {
                target.setLayerType(2, (Paint)null);
                ((Animator)ofFloat).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                    public void onAnimationEnd(final Animator animator) {
                        target.setLayerType(0, (Paint)null);
                    }
                });
            }
            if (runnable != null) {
                ((Animator)ofFloat).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                    public void onAnimationEnd(final Animator animator) {
                        runnable.run();
                    }
                });
            }
            ((Animator)ofFloat).start();
            if (b) {
                n = 0.0f;
            }
            startTranslationYAnimation(target, startDelay, duration, n, interpolator);
        }
    }
    
    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }
    
    public float getStartTranslation() {
        return this.mStartTranslation;
    }
    
    public void startAnimation(final View[] array, final Runnable runnable) {
        this.startAnimation(array, runnable, this);
    }
    
    public <T> void startAnimation(final T[] array, final Runnable runnable, final AppearAnimationCreator<T> appearAnimationCreator) {
        this.startAnimations(this.getDelays(array), array, runnable, appearAnimationCreator);
    }
    
    public void startAnimation2d(final View[][] array, final Runnable runnable) {
        this.startAnimation2d(array, runnable, this);
    }
    
    public <T> void startAnimation2d(final T[][] array, final Runnable runnable, final AppearAnimationCreator<T> appearAnimationCreator) {
        this.startAnimations(this.getDelays(array), array, runnable, appearAnimationCreator);
    }
    
    public class AppearAnimationProperties
    {
        public long[][] delays;
        public int maxDelayColIndex;
        public int maxDelayRowIndex;
        
        public AppearAnimationProperties(final AppearAnimationUtils appearAnimationUtils) {
        }
    }
    
    public interface RowTranslationScaler
    {
        float getRowTranslationScale(final int p0, final int p1);
    }
}
