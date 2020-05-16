// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.util.TypedValue;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ObjectAnimator;
import android.os.RemoteException;
import android.util.Log;
import android.view.IRotationWatcher;
import android.view.IWindowManager$Stub;
import android.os.ServiceManager;
import java.util.ArrayList;
import android.view.animation.PathInterpolator;
import android.view.IWindowManager;
import android.view.IRotationWatcher$Stub;
import android.view.View;
import java.util.List;
import android.content.Context;
import android.animation.AnimatorSet;
import android.view.animation.Interpolator;

class SquishyViewController implements FeedbackEffect
{
    private static final Interpolator SQUISH_TRANSLATION_MAP;
    private AnimatorSet mAnimatorSet;
    private final Context mContext;
    private float mLastPressure;
    private final List<View> mLeftViews;
    private float mPressure;
    private final List<View> mRightViews;
    private final IRotationWatcher$Stub mRotationWatcher;
    private int mScreenRotation;
    private final float mSquishTranslationMax;
    private final IWindowManager mWindowManager;
    
    static {
        SQUISH_TRANSLATION_MAP = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
    }
    
    public SquishyViewController(final Context mContext) {
        this.mLeftViews = new ArrayList<View>();
        this.mRightViews = new ArrayList<View>();
        this.mRotationWatcher = new IRotationWatcher$Stub() {
            public void onRotationChanged(final int n) {
                SquishyViewController.this.mScreenRotation = n;
            }
        };
        this.mContext = mContext;
        this.mSquishTranslationMax = this.px(8.0f);
        final IWindowManager interface1 = IWindowManager$Stub.asInterface(ServiceManager.getService("window"));
        this.mWindowManager = interface1;
        try {
            this.mScreenRotation = interface1.watchRotation((IRotationWatcher)this.mRotationWatcher, this.mContext.getDisplay().getDisplayId());
        }
        catch (RemoteException ex) {
            Log.e("SquishyViewController", "Couldn't get screen rotation or set watcher", (Throwable)ex);
            this.mScreenRotation = 0;
        }
    }
    
    private AnimatorSet createSpringbackAnimatorSet(final View view) {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.TRANSLATION_X, new float[] { view.getTranslationX(), 0.0f });
        final ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object)view, View.TRANSLATION_Y, new float[] { view.getTranslationY(), 0.0f });
        ofFloat.setDuration(250L);
        ofFloat2.setDuration(250L);
        final float n = Math.max(Math.abs(view.getTranslationX()) / 8.0f, Math.abs(view.getTranslationY()) / 8.0f) * 3.1f;
        ofFloat.setInterpolator((TimeInterpolator)new SpringInterpolator(0.31f, n));
        ofFloat2.setInterpolator((TimeInterpolator)new SpringInterpolator(0.31f, n));
        final AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[] { (Animator)ofFloat, (Animator)ofFloat2 });
        set.setStartDelay(50L);
        return set;
    }
    
    private AnimatorSet createSpringbackAnimatorSets() {
        final AnimatorSet set = new AnimatorSet();
        final int n = 0;
        int n2 = 0;
        int i;
        while (true) {
            i = n;
            if (n2 >= this.mLeftViews.size()) {
                break;
            }
            set.play((Animator)this.createSpringbackAnimatorSet(this.mLeftViews.get(n2)));
            ++n2;
        }
        while (i < this.mRightViews.size()) {
            set.play((Animator)this.createSpringbackAnimatorSet(this.mRightViews.get(i)));
            ++i;
        }
        return set;
    }
    
    private float px(final float n) {
        return TypedValue.applyDimension(1, n, this.mContext.getResources().getDisplayMetrics());
    }
    
    private void setViewTranslation(final View view, final float n) {
        if (!view.isAttachedToWindow()) {
            return;
        }
        float n2 = n;
        if (view.getLayoutDirection() == 1) {
            n2 = n * -1.0f;
        }
        final int mScreenRotation = this.mScreenRotation;
        if (mScreenRotation != 0) {
            if (mScreenRotation == 1) {
                view.setTranslationX(0.0f);
                view.setTranslationY(-n2);
                return;
            }
            if (mScreenRotation != 2) {
                if (mScreenRotation != 3) {
                    return;
                }
                view.setTranslationX(0.0f);
                view.setTranslationY(n2);
                return;
            }
        }
        view.setTranslationX(n2);
        view.setTranslationY(0.0f);
    }
    
    private void translateViews(final float n) {
        final int n2 = 0;
        int n3 = 0;
        int i;
        while (true) {
            i = n2;
            if (n3 >= this.mLeftViews.size()) {
                break;
            }
            this.setViewTranslation(this.mLeftViews.get(n3), n);
            ++n3;
        }
        while (i < this.mRightViews.size()) {
            this.setViewTranslation(this.mRightViews.get(i), -n);
            ++i;
        }
    }
    
    public void addLeftView(final View view) {
        this.mLeftViews.add(view);
    }
    
    public void addRightView(final View view) {
        this.mRightViews.add(view);
    }
    
    public void clearViews() {
        this.translateViews(0.0f);
        this.mLeftViews.clear();
        this.mRightViews.clear();
    }
    
    public boolean isAttachedToWindow() {
        for (int i = 0; i < this.mLeftViews.size(); ++i) {
            if (!this.mLeftViews.get(i).isAttachedToWindow()) {
                return false;
            }
        }
        for (int j = 0; j < this.mRightViews.size(); ++j) {
            if (!this.mRightViews.get(j).isAttachedToWindow()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onProgress(float n, final int n2) {
        n = Math.min(n, 1.0f) / 1.0f;
        if (n != 0.0f) {
            this.mPressure = 1.0f * n + this.mLastPressure * 0.0f;
        }
        else {
            this.mPressure = n;
        }
        final AnimatorSet mAnimatorSet = this.mAnimatorSet;
        if (mAnimatorSet == null || !mAnimatorSet.isRunning()) {
            if (n - this.mLastPressure < -0.1f) {
                (this.mAnimatorSet = this.createSpringbackAnimatorSets()).start();
            }
            else {
                this.translateViews(this.mSquishTranslationMax * SquishyViewController.SQUISH_TRANSLATION_MAP.getInterpolation(this.mPressure));
            }
        }
        this.mLastPressure = this.mPressure;
    }
    
    @Override
    public void onRelease() {
        this.onProgress(0.0f, 0);
    }
    
    @Override
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        this.onProgress(0.0f, 0);
    }
    
    private class SpringInterpolator implements Interpolator
    {
        private float mBounce;
        private float mMass;
        
        SpringInterpolator(final SquishyViewController squishyViewController, final float mMass, final float mBounce) {
            this.mMass = mMass;
            this.mBounce = mBounce;
        }
        
        public float getInterpolation(final float n) {
            return (float)(-(Math.exp(-(n / this.mMass)) * Math.cos(n * this.mBounce)) + 1.0);
        }
    }
}
