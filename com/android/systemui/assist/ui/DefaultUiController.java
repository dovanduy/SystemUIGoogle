// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import com.android.internal.logging.MetricsLogger;
import android.metrics.LogMaker;
import android.util.Log;
import com.android.systemui.assist.AssistHandleViewController;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NavigationBarController;
import android.view.ViewGroup$LayoutParams;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.Context;
import java.util.Locale;
import android.os.Build;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.view.animation.PathInterpolator;
import android.view.WindowManager$LayoutParams;
import android.animation.ValueAnimator;
import com.android.systemui.assist.AssistManager;

public class DefaultUiController implements UiController
{
    private static final boolean VERBOSE;
    private boolean mAttached;
    private ValueAnimator mInvocationAnimator;
    private boolean mInvocationInProgress;
    protected InvocationLightsView mInvocationLightsView;
    private float mLastInvocationProgress;
    private final WindowManager$LayoutParams mLayoutParams;
    private final PathInterpolator mProgressInterpolator;
    protected final FrameLayout mRoot;
    private final WindowManager mWindowManager;
    
    static {
        VERBOSE = (Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng"));
    }
    
    public DefaultUiController(final Context context) {
        this.mProgressInterpolator = new PathInterpolator(0.83f, 0.0f, 0.84f, 1.0f);
        this.mAttached = false;
        this.mInvocationInProgress = false;
        this.mLastInvocationProgress = 0.0f;
        this.mInvocationAnimator = new ValueAnimator();
        this.mRoot = new FrameLayout(context);
        this.mWindowManager = (WindowManager)context.getSystemService("window");
        final WindowManager$LayoutParams mLayoutParams = new WindowManager$LayoutParams(-1, -2, 0, 0, 2024, 808, -3);
        this.mLayoutParams = mLayoutParams;
        mLayoutParams.privateFlags = 64;
        mLayoutParams.gravity = 80;
        mLayoutParams.setFitInsetsTypes(0);
        this.mLayoutParams.setTitle((CharSequence)"Assist");
        final InvocationLightsView mInvocationLightsView = (InvocationLightsView)LayoutInflater.from(context).inflate(R$layout.invocation_lights, (ViewGroup)this.mRoot, false);
        this.mInvocationLightsView = mInvocationLightsView;
        this.mRoot.addView((View)mInvocationLightsView);
    }
    
    private void animateInvocationCompletion(final int n, final float n2) {
        (this.mInvocationAnimator = ValueAnimator.ofFloat(new float[] { this.mLastInvocationProgress, 1.0f })).setStartDelay(1L);
        this.mInvocationAnimator.setDuration(200L);
        this.mInvocationAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$DefaultUiController$DsyFMixn8vpgo7pkqARg9d_ZEVw(this, n));
        this.mInvocationAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                super.onAnimationEnd(animator);
                DefaultUiController.this.mInvocationInProgress = false;
                DefaultUiController.this.mLastInvocationProgress = 0.0f;
                DefaultUiController.this.hide();
            }
        });
        this.mInvocationAnimator.start();
    }
    
    private void attach() {
        if (!this.mAttached) {
            this.mWindowManager.addView((View)this.mRoot, (ViewGroup$LayoutParams)this.mLayoutParams);
            this.mAttached = true;
        }
    }
    
    private void detach() {
        if (this.mAttached) {
            this.mWindowManager.removeViewImmediate((View)this.mRoot);
            this.mAttached = false;
        }
    }
    
    private void setProgressInternal(final int n, final float n2) {
        this.mInvocationLightsView.onInvocationProgress(this.mProgressInterpolator.getInterpolation(n2));
    }
    
    private void updateAssistHandleVisibility() {
        final NavigationBarController navigationBarController = Dependency.get(NavigationBarController.class);
        AssistHandleViewController assistHandlerViewController;
        if (navigationBarController == null) {
            assistHandlerViewController = null;
        }
        else {
            assistHandlerViewController = navigationBarController.getAssistHandlerViewController();
        }
        if (assistHandlerViewController != null) {
            assistHandlerViewController.setAssistHintBlocked(this.mInvocationInProgress);
        }
    }
    
    @Override
    public void hide() {
        this.detach();
        if (this.mInvocationAnimator.isRunning()) {
            this.mInvocationAnimator.cancel();
        }
        this.mInvocationLightsView.hide();
        this.mInvocationInProgress = false;
        this.updateAssistHandleVisibility();
    }
    
    protected void logInvocationProgressMetrics(final int i, final float n, final boolean b) {
        if (n == 1.0f && DefaultUiController.VERBOSE) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invocation complete: type=");
            sb.append(i);
            Log.v("DefaultUiController", sb.toString());
        }
        if (!b && n > 0.0f) {
            if (DefaultUiController.VERBOSE) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Invocation started: type=");
                sb2.append(i);
                Log.v("DefaultUiController", sb2.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(4).setSubtype(Dependency.get(AssistManager.class).toLoggingSubType(i)));
        }
        final ValueAnimator mInvocationAnimator = this.mInvocationAnimator;
        if ((mInvocationAnimator == null || !mInvocationAnimator.isRunning()) && b && n == 0.0f) {
            if (DefaultUiController.VERBOSE) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Invocation cancelled: type=");
                sb3.append(i);
                Log.v("DefaultUiController", sb3.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(1));
        }
    }
    
    @Override
    public void onGestureCompletion(final float n) {
        this.animateInvocationCompletion(1, n);
        this.logInvocationProgressMetrics(1, 1.0f, this.mInvocationInProgress);
    }
    
    @Override
    public void onInvocationProgress(final int n, final float mLastInvocationProgress) {
        final boolean mInvocationInProgress = this.mInvocationInProgress;
        if (mLastInvocationProgress == 1.0f) {
            this.animateInvocationCompletion(n, 0.0f);
        }
        else if (mLastInvocationProgress == 0.0f) {
            this.hide();
        }
        else {
            if (!mInvocationInProgress) {
                this.attach();
                this.mInvocationInProgress = true;
                this.updateAssistHandleVisibility();
            }
            this.setProgressInternal(n, mLastInvocationProgress);
        }
        this.logInvocationProgressMetrics(n, this.mLastInvocationProgress = mLastInvocationProgress, mInvocationInProgress);
    }
}
