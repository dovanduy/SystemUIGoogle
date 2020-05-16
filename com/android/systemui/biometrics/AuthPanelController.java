// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.util.Log;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.Outline;
import android.animation.ValueAnimator;
import com.android.systemui.R$dimen;
import android.view.View;
import android.content.Context;
import android.view.ViewOutlineProvider;

public class AuthPanelController extends ViewOutlineProvider
{
    private int mContainerHeight;
    private int mContainerWidth;
    private int mContentHeight;
    private int mContentWidth;
    private final Context mContext;
    private float mCornerRadius;
    private int mMargin;
    private final View mPanelView;
    private boolean mUseFullScreen;
    
    AuthPanelController(final Context mContext, final View mPanelView) {
        this.mContext = mContext;
        this.mPanelView = mPanelView;
        this.mCornerRadius = mContext.getResources().getDimension(R$dimen.biometric_dialog_corner_size);
        this.mMargin = (int)mContext.getResources().getDimension(R$dimen.biometric_dialog_border_padding);
        this.mPanelView.setOutlineProvider((ViewOutlineProvider)this);
        this.mPanelView.setClipToOutline(true);
    }
    
    int getContainerHeight() {
        return this.mContainerHeight;
    }
    
    int getContainerWidth() {
        return this.mContainerWidth;
    }
    
    public void getOutline(final View view, final Outline outline) {
        final int mContainerWidth = this.mContainerWidth;
        final int n = (mContainerWidth - this.mContentWidth) / 2;
        final int mContentHeight = this.mContentHeight;
        final int mContainerHeight = this.mContainerHeight;
        int mMargin;
        if (mContentHeight < mContainerHeight) {
            mMargin = mContainerHeight - mContentHeight - this.mMargin;
        }
        else {
            mMargin = this.mMargin;
        }
        outline.setRoundRect(n, mMargin, mContainerWidth - n, this.mContainerHeight - this.mMargin + 1, this.mCornerRadius);
    }
    
    public void setContainerDimensions(final int mContainerWidth, final int mContainerHeight) {
        this.mContainerWidth = mContainerWidth;
        this.mContainerHeight = mContainerHeight;
    }
    
    public void setUseFullScreen(final boolean mUseFullScreen) {
        this.mUseFullScreen = mUseFullScreen;
    }
    
    public void updateForContentDimensions(final int mContentWidth, final int mContentHeight, final int n) {
        if (this.mContainerWidth != 0 && this.mContainerHeight != 0) {
            int mMargin;
            if (this.mUseFullScreen) {
                mMargin = 0;
            }
            else {
                mMargin = (int)this.mContext.getResources().getDimension(R$dimen.biometric_dialog_border_padding);
            }
            float dimension;
            if (this.mUseFullScreen) {
                dimension = 0.0f;
            }
            else {
                dimension = this.mContext.getResources().getDimension(R$dimen.biometric_dialog_corner_size);
            }
            if (n > 0) {
                final ValueAnimator ofInt = ValueAnimator.ofInt(new int[] { this.mMargin, mMargin });
                ofInt.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthPanelController$FfAW_fJIxdruLyni5niGyYZPKQI(this));
                final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mCornerRadius, dimension });
                ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthPanelController$InH1YHCYbFS1oQ8661noD2sY0tQ(this));
                final ValueAnimator ofInt2 = ValueAnimator.ofInt(new int[] { this.mContentHeight, mContentHeight });
                ofInt2.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthPanelController$gEQd5p8htInmfU5UNk3JBrR4jEs(this));
                final ValueAnimator ofInt3 = ValueAnimator.ofInt(new int[] { this.mContentWidth, mContentWidth });
                ofInt3.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthPanelController$T_ye3d_LoD4zTMypSnctnhLSMzU(this));
                final AnimatorSet set = new AnimatorSet();
                set.setDuration((long)n);
                set.setInterpolator((TimeInterpolator)new AccelerateDecelerateInterpolator());
                set.playTogether(new Animator[] { (Animator)ofFloat, (Animator)ofInt2, (Animator)ofInt3, (Animator)ofInt });
                set.start();
            }
            else {
                this.mMargin = mMargin;
                this.mCornerRadius = dimension;
                this.mContentWidth = mContentWidth;
                this.mContentHeight = mContentHeight;
                this.mPanelView.invalidateOutline();
            }
            return;
        }
        Log.w("BiometricPrompt/AuthPanelController", "Not done measuring yet");
    }
}
