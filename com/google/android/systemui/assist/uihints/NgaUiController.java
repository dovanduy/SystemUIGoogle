// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import com.android.systemui.DejankUtils;
import java.util.function.Consumer;
import android.view.ViewTreeObserver$InternalInsetsInfo;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;
import android.animation.ObjectAnimator;
import android.view.View;
import com.android.systemui.assist.AssistHandleViewController;
import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import com.google.android.systemui.assist.uihints.edgelights.mode.Gone;
import android.view.SurfaceControl;
import com.android.systemui.R$dimen;
import android.graphics.Rect;
import android.graphics.Region$Op;
import android.graphics.Region;
import com.android.internal.logging.MetricsLogger;
import android.metrics.LogMaker;
import android.util.Log;
import android.os.SystemClock;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.animation.OvershootInterpolator;
import android.util.MathUtils;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.function.Function;
import android.view.ViewGroup;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsListener;
import com.android.systemui.R$id;
import android.os.PowerManager;
import android.os.Looper;
import java.util.Locale;
import android.os.Build;
import android.os.PowerManager$WakeLock;
import android.os.Handler;
import com.android.systemui.statusbar.NavigationBarController;
import android.animation.ValueAnimator;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import android.content.Context;
import dagger.Lazy;
import android.view.animation.PathInterpolator;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.view.ViewTreeObserver$OnComputeInternalInsetsListener;
import com.android.systemui.assist.AssistManager;

public class NgaUiController implements UiController, ViewTreeObserver$OnComputeInternalInsetsListener, StateListener
{
    private static final boolean VERBOSE;
    private static final PathInterpolator mProgressInterpolator;
    private final Lazy<AssistManager> mAssistManager;
    private final AssistantPresenceHandler mAssistantPresenceHandler;
    private final AssistantWarmer mAssistantWarmer;
    private final ColorChangeHandler mColorChangeHandler;
    private long mColorMonitoringStart;
    private final Context mContext;
    private final EdgeLightsController mEdgeLightsController;
    private final FlingVelocityWrapper mFlingVelocity;
    private final GlowController mGlowController;
    private boolean mHasDarkBackground;
    private final IconController mIconController;
    private ValueAnimator mInvocationAnimator;
    private boolean mInvocationInProgress;
    private AssistantInvocationLightsView mInvocationLightsView;
    private boolean mIsMonitoringColor;
    private float mLastInvocationProgress;
    private long mLastInvocationStartTime;
    private final LightnessProvider mLightnessProvider;
    private ValueAnimator mNavBarAlphaAnimator;
    private float mNavBarDestinationAlpha;
    private final Lazy<NavigationBarController> mNavigationBarController;
    private Runnable mPendingEdgeLightsModeChange;
    private PromptView mPromptView;
    private final ScrimController mScrimController;
    private boolean mShouldKeepWakeLock;
    private boolean mShowingAssistUi;
    private final TimeoutManager mTimeoutManager;
    private final TouchInsideHandler mTouchInsideHandler;
    private final TranscriptionController mTranscriptionController;
    private final Handler mUiHandler;
    private final OverlayUiHost mUiHost;
    private PowerManager$WakeLock mWakeLock;
    
    static {
        VERBOSE = (Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng"));
        mProgressInterpolator = new PathInterpolator(0.83f, 0.0f, 0.84f, 1.0f);
    }
    
    public NgaUiController(final Context mContext, final TimeoutManager mTimeoutManager, final AssistantPresenceHandler mAssistantPresenceHandler, final TouchInsideHandler mTouchInsideHandler, final ColorChangeHandler mColorChangeHandler, final OverlayUiHost mUiHost, final EdgeLightsController mEdgeLightsController, final GlowController mGlowController, final ScrimController mScrimController, final TranscriptionController mTranscriptionController, final IconController mIconController, final LightnessProvider mLightnessProvider, final StatusBarStateController statusBarStateController, final Lazy<AssistManager> mAssistManager, final Lazy<NavigationBarController> mNavigationBarController, final FlingVelocityWrapper mFlingVelocity, final AssistantWarmer mAssistantWarmer) {
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mHasDarkBackground = false;
        this.mIsMonitoringColor = false;
        this.mInvocationInProgress = false;
        this.mShowingAssistUi = false;
        this.mShouldKeepWakeLock = false;
        this.mLastInvocationStartTime = 0L;
        this.mLastInvocationProgress = 0.0f;
        this.mNavBarDestinationAlpha = -1.0f;
        this.mColorMonitoringStart = 0L;
        this.mContext = mContext;
        (this.mColorChangeHandler = mColorChangeHandler).onColorChange(false);
        this.mTimeoutManager = mTimeoutManager;
        this.mAssistantPresenceHandler = mAssistantPresenceHandler;
        this.mTouchInsideHandler = mTouchInsideHandler;
        this.mUiHost = mUiHost;
        this.mEdgeLightsController = mEdgeLightsController;
        this.mGlowController = mGlowController;
        this.mScrimController = mScrimController;
        this.mTranscriptionController = mTranscriptionController;
        this.mIconController = mIconController;
        this.mLightnessProvider = mLightnessProvider;
        this.mAssistManager = mAssistManager;
        this.mNavigationBarController = mNavigationBarController;
        this.mFlingVelocity = mFlingVelocity;
        this.mAssistantWarmer = mAssistantWarmer;
        mLightnessProvider.setListener(new _$$Lambda$NgaUiController$F0C5sXLepbJ7B_YbmVggBejRhWY(this));
        this.mAssistantPresenceHandler.registerSysUiIsNgaUiChangeListener((AssistantPresenceHandler.SysUiIsNgaUiChangeListener)new _$$Lambda$NgaUiController$50Z2w_yTV5fzer2h0l95J0m7H9s(this));
        this.mTouchInsideHandler.setFallback(new _$$Lambda$NgaUiController$oR12pNJdVFKpuKDyss8_ez7xrG8(this));
        this.mEdgeLightsController.setModeChangeThrottler((EdgeLightsController.ModeChangeThrottler)new _$$Lambda$NgaUiController$6hU4RPs2LIWw4X6froGuypvPmmw(this));
        this.mWakeLock = ((PowerManager)mContext.getSystemService("power")).newWakeLock(805306378, "Assist (NGA)");
        final _$$Lambda$NgaUiController$4GhIoaWGm6twYJc1tT2_hhB1Tms $$Lambda$NgaUiController$4GhIoaWGm6twYJc1tT2_hhB1Tms = new _$$Lambda$NgaUiController$4GhIoaWGm6twYJc1tT2_hhB1Tms(this);
        this.mGlowController.setVisibilityListener($$Lambda$NgaUiController$4GhIoaWGm6twYJc1tT2_hhB1Tms);
        this.mScrimController.setVisibilityListener($$Lambda$NgaUiController$4GhIoaWGm6twYJc1tT2_hhB1Tms);
        final ViewGroup parent = this.mUiHost.getParent();
        (this.mInvocationLightsView = (AssistantInvocationLightsView)parent.findViewById(R$id.invocation_lights)).setGoogleAssistant(true);
        this.mEdgeLightsController.addListener(this.mGlowController);
        this.mEdgeLightsController.addListener(this.mScrimController);
        this.mTranscriptionController.setListener((TranscriptionController.TranscriptionSpaceListener)this.mScrimController);
        this.mPromptView = (PromptView)parent.findViewById(R$id.prompt);
        this.dispatchHasDarkBackground();
        statusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.refresh();
        this.mTimeoutManager.setTimeoutCallback((TimeoutManager.TimeoutCallback)new _$$Lambda$s7K6qg6WmJuq9_QppnTmnejq7Z4(new _$$Lambda$NgaUiController$kQXu6RTwp9gQJxFsGvF5miPQJlU(this)));
    }
    
    private float approximateInverse(final Float key, final Function<Float, Float> function) {
        final ArrayList<Comparable<? super Float>> list = new ArrayList<Comparable<? super Float>>((int)200.0f);
        for (float f = 0.0f; f < 1.0f; f += 0.005f) {
            list.add(function.apply(f));
        }
        final int binarySearch = Collections.binarySearch(list, key);
        int n;
        if ((n = binarySearch) < 0) {
            n = (binarySearch + 1) * -1;
        }
        return n * 0.005f;
    }
    
    private void closeNgaUi() {
        this.mAssistManager.get().hideAssist();
        this.hide();
    }
    
    private void completeInvocation(final int n) {
        if (!this.mAssistantPresenceHandler.isSysUiNgaUi()) {
            this.setProgress(n, 0.0f);
            this.resetInvocationProgress();
            return;
        }
        this.mTouchInsideHandler.maybeSetGuarded();
        this.mTimeoutManager.resetTimeout();
        this.mPromptView.disable();
        final ValueAnimator mInvocationAnimator = this.mInvocationAnimator;
        if (mInvocationAnimator != null && mInvocationAnimator.isStarted()) {
            this.mInvocationAnimator.cancel();
        }
        final float velocity = this.mFlingVelocity.getVelocity();
        float constrain = 3.0f;
        if (velocity != 0.0f) {
            constrain = MathUtils.constrain(-velocity / 1.45f, 3.0f, 12.0f);
        }
        final OvershootInterpolator overshootInterpolator = new OvershootInterpolator(constrain);
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.approximateInverse(this.getAnimationProgress(n, this.mLastInvocationProgress), new _$$Lambda$NgaUiController$xx1_d2xL8Zm7hcnRQB0juPcutug(overshootInterpolator)), 1.0f });
        ofFloat.setDuration(600L);
        ofFloat.setStartDelay(1L);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$NgaUiController$x2Vvqaffql7Fsq5L3bVSvLMHTVY(this, n, overshootInterpolator));
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled = false;
            
            public void onAnimationCancel(final Animator animator) {
                super.onAnimationCancel(animator);
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                super.onAnimationEnd(animator);
                if (!this.mCancelled) {
                    if (NgaUiController.this.mPendingEdgeLightsModeChange == null) {
                        NgaUiController.this.mEdgeLightsController.setFullListening();
                    }
                    else {
                        NgaUiController.this.mPendingEdgeLightsModeChange.run();
                        NgaUiController.this.mPendingEdgeLightsModeChange = null;
                    }
                }
                NgaUiController.this.mUiHandler.post((Runnable)new _$$Lambda$NgaUiController$1$qsAEeSJReadrxEvwrYjXI7UybWQ(this));
            }
        });
        (this.mInvocationAnimator = ofFloat).start();
    }
    
    private void dispatchHasDarkBackground() {
        this.mTranscriptionController.setHasDarkBackground(this.mHasDarkBackground);
        this.mIconController.setHasDarkBackground(this.mHasDarkBackground);
        this.mPromptView.setHasDarkBackground(this.mHasDarkBackground);
    }
    
    private float getAnimationProgress(final int n, final float n2) {
        if (n == 2) {
            return n2 * 0.95f;
        }
        return NgaUiController.mProgressInterpolator.getInterpolation(n2 * 0.8f);
    }
    
    private void logInvocationProgressMetrics(final int i, final float n, final boolean b) {
        if (n == 1.0f && NgaUiController.VERBOSE) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invocation complete: type=");
            sb.append(i);
            Log.v("NgaUiController", sb.toString());
        }
        if (!b && n > 0.0f) {
            if (NgaUiController.VERBOSE) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Invocation started: type=");
                sb2.append(i);
                Log.v("NgaUiController", sb2.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(4).setSubtype(this.mAssistManager.get().toLoggingSubType(i)));
        }
        final ValueAnimator mInvocationAnimator = this.mInvocationAnimator;
        if ((mInvocationAnimator == null || !mInvocationAnimator.isRunning()) && b && n == 0.0f) {
            if (NgaUiController.VERBOSE) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Invocation cancelled: type=");
                sb3.append(i);
                Log.v("NgaUiController", sb3.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(1));
        }
    }
    
    private void refresh() {
        this.updateShowingAssistUi();
        this.updateShowingNavBar();
    }
    
    private void resetInvocationProgress() {
        this.mInvocationInProgress = false;
        this.mInvocationLightsView.hide();
        this.mLastInvocationProgress = 0.0f;
        this.mScrimController.setInvocationProgress(0.0f);
        this.refresh();
    }
    
    private void setColorMonitoringState(final boolean mIsMonitoringColor) {
        if (this.mIsMonitoringColor == mIsMonitoringColor) {
            return;
        }
        if (mIsMonitoringColor && this.mScrimController.isVisible() && this.mScrimController.getSurfaceControllerHandle() == null) {
            return;
        }
        if (this.mIsMonitoringColor = mIsMonitoringColor) {
            final int n = DisplayUtils.getRotatedHeight(this.mContext) - (int)this.mContext.getResources().getDimension(R$dimen.transcription_space_bottom_margin) - DisplayUtils.convertSpToPx(20.0f, this.mContext);
            final Rect rect = new Rect(0, n - DisplayUtils.convertDpToPx(160.0f, this.mContext), DisplayUtils.getRotatedWidth(this.mContext), n);
            this.mColorMonitoringStart = SystemClock.elapsedRealtime();
            this.mLightnessProvider.enableColorMonitoring(true, rect, this.mScrimController.getSurfaceControllerHandle());
        }
        else {
            this.mLightnessProvider.enableColorMonitoring(false, null, null);
            this.mIconController.setHasAccurateLuma(false);
            this.mScrimController.onLightnessInvalidated();
            this.mTranscriptionController.setHasAccurateBackground(false);
        }
    }
    
    private void setHasDarkBackground(final boolean mHasDarkBackground) {
        final boolean mHasDarkBackground2 = this.mHasDarkBackground;
        String s = "dark";
        if (mHasDarkBackground2 == mHasDarkBackground) {
            if (NgaUiController.VERBOSE) {
                final StringBuilder sb = new StringBuilder();
                sb.append("not switching; already ");
                if (!mHasDarkBackground) {
                    s = "light";
                }
                sb.append(s);
                Log.v("NgaUiController", sb.toString());
            }
            return;
        }
        this.mHasDarkBackground = mHasDarkBackground;
        this.mColorChangeHandler.onColorChange(mHasDarkBackground);
        if (NgaUiController.VERBOSE) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("switching to ");
            if (!this.mHasDarkBackground) {
                s = "light";
            }
            sb2.append(s);
            Log.v("NgaUiController", sb2.toString());
        }
        this.dispatchHasDarkBackground();
    }
    
    private void setProgress(final int n, final float n2) {
        this.mInvocationLightsView.onInvocationProgress(n2);
        this.mGlowController.setInvocationProgress(n2);
        this.mScrimController.setInvocationProgress(n2);
        this.mPromptView.onInvocationProgress(n, n2);
        this.refresh();
    }
    
    private void updateShowingAssistUi() {
        final boolean b = this.mEdgeLightsController.getMode() instanceof Gone;
        boolean assistHintBlocked = false;
        final boolean mShouldKeepWakeLock = !b || this.mGlowController.isVisible() || this.mScrimController.isVisible() || this.mInvocationInProgress;
        if (mShouldKeepWakeLock || this.mIconController.isVisible() || this.mIconController.isRequested()) {
            assistHintBlocked = true;
        }
        this.setColorMonitoringState(assistHintBlocked);
        if (this.mShowingAssistUi != assistHintBlocked) {
            this.mShowingAssistUi = assistHintBlocked;
            final AssistHandleViewController assistHandlerViewController = this.mNavigationBarController.get().getAssistHandlerViewController();
            if (assistHandlerViewController != null) {
                assistHandlerViewController.setAssistHintBlocked(assistHintBlocked);
            }
            this.mUiHost.setAssistState(assistHintBlocked, this.mEdgeLightsController.getMode() instanceof FullListening);
            if (assistHintBlocked) {
                this.mUiHost.getParent().getViewTreeObserver().addOnComputeInternalInsetsListener((ViewTreeObserver$OnComputeInternalInsetsListener)this);
            }
            else {
                this.mUiHost.getParent().getViewTreeObserver().removeOnComputeInternalInsetsListener((ViewTreeObserver$OnComputeInternalInsetsListener)this);
                final ValueAnimator mInvocationAnimator = this.mInvocationAnimator;
                if (mInvocationAnimator != null && mInvocationAnimator.isStarted()) {
                    this.mInvocationAnimator.cancel();
                }
            }
        }
        if (this.mShouldKeepWakeLock != mShouldKeepWakeLock) {
            this.mShouldKeepWakeLock = mShouldKeepWakeLock;
            if (mShouldKeepWakeLock) {
                this.mWakeLock.acquire();
            }
            else {
                this.mWakeLock.release();
            }
        }
    }
    
    private void updateShowingNavBar() {
        final EdgeLightsView.Mode mode = this.mEdgeLightsController.getMode();
        final boolean b = !this.mInvocationInProgress && mode instanceof Gone;
        float mNavBarDestinationAlpha;
        if (b) {
            mNavBarDestinationAlpha = 1.0f;
        }
        else {
            mNavBarDestinationAlpha = 0.0f;
        }
        final NavigationBarView defaultNavigationBarView = this.mNavigationBarController.get().getDefaultNavigationBarView();
        if (defaultNavigationBarView == null) {
            return;
        }
        final float alpha = ((View)defaultNavigationBarView).getAlpha();
        if (mNavBarDestinationAlpha != alpha) {
            if (mNavBarDestinationAlpha != this.mNavBarDestinationAlpha) {
                this.mNavBarDestinationAlpha = mNavBarDestinationAlpha;
                final ValueAnimator mNavBarAlphaAnimator = this.mNavBarAlphaAnimator;
                if (mNavBarAlphaAnimator != null) {
                    mNavBarAlphaAnimator.cancel();
                }
                final ObjectAnimator setDuration = ObjectAnimator.ofFloat((Object)defaultNavigationBarView, View.ALPHA, new float[] { alpha, mNavBarDestinationAlpha }).setDuration((long)Math.abs((mNavBarDestinationAlpha - alpha) * 80.0f));
                this.mNavBarAlphaAnimator = (ValueAnimator)setDuration;
                if (b) {
                    ((ValueAnimator)setDuration).setStartDelay(80L);
                }
                this.mNavBarAlphaAnimator.start();
            }
        }
    }
    
    @Override
    public void hide() {
        final ValueAnimator mInvocationAnimator = this.mInvocationAnimator;
        if (mInvocationAnimator != null && mInvocationAnimator.isStarted()) {
            this.mInvocationAnimator.cancel();
        }
        this.mInvocationInProgress = false;
        this.mTranscriptionController.onClear(false);
        this.mEdgeLightsController.setGone();
        this.mPendingEdgeLightsModeChange = null;
        this.mPromptView.disable();
        this.mIconController.onHideKeyboard();
        this.mIconController.onHideZerostate();
        this.refresh();
    }
    
    public void onComputeInternalInsets(final ViewTreeObserver$InternalInsetsInfo viewTreeObserver$InternalInsetsInfo) {
        viewTreeObserver$InternalInsetsInfo.setTouchableInsets(3);
        final Region region = new Region();
        this.mIconController.getTouchActionRegion().ifPresent(new _$$Lambda$NgaUiController$SO6XSFFvv75AycszdACBqyLElQU(region));
        final Region region2 = new Region();
        final EdgeLightsView.Mode mode = this.mEdgeLightsController.getMode();
        if (!(mode instanceof FullListening) || !((FullListening)mode).isFakeForHalfListening()) {
            this.mGlowController.getTouchInsideRegion().ifPresent(new _$$Lambda$NgaUiController$10fisFPqI6sEzP945Gm7ad6C_u4(region2));
        }
        this.mScrimController.getTouchInsideRegion().ifPresent(new _$$Lambda$NgaUiController$rxEPNlCPG7rh9kp4i01WHpjV4rA(region2));
        final _$$Lambda$NgaUiController$LqQG_DaL0eMcmw4B_N5N3flgBSY $$Lambda$NgaUiController$LqQG_DaL0eMcmw4B_N5N3flgBSY = new _$$Lambda$NgaUiController$LqQG_DaL0eMcmw4B_N5N3flgBSY(region2);
        this.mTranscriptionController.getTouchInsideRegion().ifPresent($$Lambda$NgaUiController$LqQG_DaL0eMcmw4B_N5N3flgBSY);
        this.mTranscriptionController.getTouchActionRegion().ifPresent($$Lambda$NgaUiController$LqQG_DaL0eMcmw4B_N5N3flgBSY);
        region.op(region2, Region$Op.UNION);
        viewTreeObserver$InternalInsetsInfo.touchableRegion.set(region);
    }
    
    public void onDozingChanged(final boolean isDozing) {
        if (Looper.myLooper() != this.mUiHandler.getLooper()) {
            this.mUiHandler.post((Runnable)new _$$Lambda$NgaUiController$gCwgsAI9ZcWA1G2b_7QWhkiCbkI(this, isDozing));
            return;
        }
        this.mScrimController.setIsDozing(isDozing);
        if (isDozing && this.mShowingAssistUi) {
            DejankUtils.whitelistIpcs(new _$$Lambda$NgaUiController$oR12pNJdVFKpuKDyss8_ez7xrG8(this));
        }
    }
    
    @Override
    public void onGestureCompletion(final float velocity) {
        if (this.mEdgeLightsController.getMode().preventsInvocations()) {
            if (NgaUiController.VERBOSE) {
                final StringBuilder sb = new StringBuilder();
                sb.append("ignoring invocation; mode is ");
                sb.append(this.mEdgeLightsController.getMode().getClass().getSimpleName());
                Log.v("NgaUiController", sb.toString());
            }
            return;
        }
        this.mFlingVelocity.setVelocity(velocity);
        this.completeInvocation(1);
        this.logInvocationProgressMetrics(1, 1.0f, this.mInvocationInProgress);
    }
    
    @Override
    public void onInvocationProgress(final int n, final float mLastInvocationProgress) {
        final ValueAnimator mInvocationAnimator = this.mInvocationAnimator;
        if (mInvocationAnimator != null && mInvocationAnimator.isStarted()) {
            Log.w("NgaUiController", "Already animating; ignoring invocation progress");
            return;
        }
        if (this.mEdgeLightsController.getMode().preventsInvocations()) {
            if (NgaUiController.VERBOSE) {
                final StringBuilder sb = new StringBuilder();
                sb.append("ignoring invocation; mode is ");
                sb.append(this.mEdgeLightsController.getMode().getClass().getSimpleName());
                Log.v("NgaUiController", sb.toString());
            }
            return;
        }
        final boolean mInvocationInProgress = this.mInvocationInProgress;
        final float n2 = fcmpg(mLastInvocationProgress, 1.0f);
        if (n2 < 0) {
            this.mLastInvocationProgress = mLastInvocationProgress;
            if (!mInvocationInProgress && mLastInvocationProgress > 0.0f) {
                this.mLastInvocationStartTime = SystemClock.uptimeMillis();
            }
            if (!(this.mInvocationInProgress = (mLastInvocationProgress > 0.0f && n2 < 0))) {
                this.mPromptView.disable();
            }
            else if (mLastInvocationProgress < 0.9f && SystemClock.uptimeMillis() - this.mLastInvocationStartTime > 200L) {
                this.mPromptView.enable();
            }
            this.setProgress(n, this.getAnimationProgress(n, mLastInvocationProgress));
        }
        else {
            final ValueAnimator mInvocationAnimator2 = this.mInvocationAnimator;
            if (mInvocationAnimator2 == null || !mInvocationAnimator2.isStarted()) {
                this.mFlingVelocity.setVelocity(0.0f);
                this.completeInvocation(n);
            }
        }
        this.mAssistantWarmer.onInvocationProgress(mLastInvocationProgress);
        this.logInvocationProgressMetrics(n, mLastInvocationProgress, mInvocationInProgress);
    }
    
    void onUiMessageReceived() {
        this.refresh();
    }
}
