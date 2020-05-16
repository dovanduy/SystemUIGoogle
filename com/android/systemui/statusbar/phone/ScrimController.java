// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.DejankUtils;
import com.android.internal.colorextraction.ColorExtractor;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.graphics.Color;
import android.os.Trace;
import com.android.systemui.statusbar.notification.stack.ViewState;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.view.View;
import com.android.internal.graphics.ColorUtils;
import android.util.MathUtils;
import android.app.AlarmManager$OnAlarmListener;
import java.util.Objects;
import android.view.animation.DecelerateInterpolator;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import android.app.AlarmManager;
import com.android.systemui.R$id;
import android.util.Log;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.util.AlarmTimeout;
import java.util.function.Consumer;
import com.android.internal.util.function.TriConsumer;
import com.android.systemui.statusbar.ScrimView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.view.animation.Interpolator;
import android.os.Handler;
import com.android.systemui.dock.DockManager;
import com.android.internal.colorextraction.ColorExtractor$GradientColors;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import android.animation.Animator$AnimatorListener;
import com.android.systemui.Dumpable;
import com.android.internal.colorextraction.ColorExtractor$OnColorsChangedListener;
import android.view.ViewTreeObserver$OnPreDrawListener;

public class ScrimController implements ViewTreeObserver$OnPreDrawListener, ColorExtractor$OnColorsChangedListener, Dumpable
{
    private static final boolean DEBUG;
    private static final int TAG_END_ALPHA;
    static final int TAG_KEY_ANIM;
    private static final int TAG_START_ALPHA;
    private boolean mAnimateChange;
    private long mAnimationDelay;
    private long mAnimationDuration;
    private Animator$AnimatorListener mAnimatorListener;
    private float mBehindAlpha;
    private int mBehindTint;
    private boolean mBlankScreen;
    private Runnable mBlankingTransitionRunnable;
    private float mBubbleAlpha;
    private int mBubbleTint;
    private Callback mCallback;
    private final SysuiColorExtractor mColorExtractor;
    private ColorExtractor$GradientColors mColors;
    private boolean mDarkenWhileDragging;
    private final float mDefaultScrimAlpha;
    private final DockManager mDockManager;
    private final DozeParameters mDozeParameters;
    private boolean mExpansionAffectsAlpha;
    private float mExpansionFraction;
    private final Handler mHandler;
    private float mInFrontAlpha;
    private int mInFrontTint;
    private final Interpolator mInterpolator;
    private boolean mKeyguardOccluded;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardVisibilityCallback mKeyguardVisibilityCallback;
    private boolean mNeedsDrawableColorUpdate;
    private Runnable mPendingFrameCallback;
    private boolean mScreenBlankingCallbackCalled;
    private boolean mScreenOn;
    private ScrimView mScrimBehind;
    private float mScrimBehindAlphaKeyguard;
    private ScrimView mScrimForBubble;
    private ScrimView mScrimInFront;
    private final TriConsumer<ScrimState, Float, ColorExtractor$GradientColors> mScrimStateListener;
    private Consumer<Integer> mScrimVisibleListener;
    private int mScrimsVisibility;
    private ScrimState mState;
    private final AlarmTimeout mTimeTicker;
    private boolean mTracking;
    private boolean mUpdatePending;
    private final WakeLock mWakeLock;
    private boolean mWakeLockHeld;
    private boolean mWallpaperSupportsAmbientMode;
    private boolean mWallpaperVisibilityTimedOut;
    
    static {
        DEBUG = Log.isLoggable("ScrimController", 3);
        TAG_KEY_ANIM = R$id.scrim;
        TAG_START_ALPHA = R$id.scrim_alpha_start;
        TAG_END_ALPHA = R$id.scrim_alpha_end;
    }
    
    public ScrimController(final LightBarController obj, final DozeParameters mDozeParameters, final AlarmManager alarmManager, final KeyguardStateController mKeyguardStateController, final DelayedWakeLock.Builder builder, final Handler mHandler, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final SysuiColorExtractor mColorExtractor, final DockManager mDockManager, final BlurUtils blurUtils) {
        this.mState = ScrimState.UNINITIALIZED;
        this.mScrimBehindAlphaKeyguard = 0.2f;
        this.mExpansionFraction = 1.0f;
        this.mExpansionAffectsAlpha = true;
        this.mAnimationDuration = -1L;
        this.mInterpolator = (Interpolator)new DecelerateInterpolator();
        this.mInFrontAlpha = -1.0f;
        this.mBehindAlpha = -1.0f;
        this.mBubbleAlpha = -1.0f;
        Objects.requireNonNull(obj);
        this.mScrimStateListener = (TriConsumer<ScrimState, Float, ColorExtractor$GradientColors>)new _$$Lambda$v3pYAGeeZEy0j9LKp92o1adNfrk(obj);
        float mDefaultScrimAlpha;
        if (blurUtils.supportsBlursOnWindows()) {
            mDefaultScrimAlpha = 0.54f;
        }
        else {
            mDefaultScrimAlpha = 0.75f;
        }
        this.mDefaultScrimAlpha = mDefaultScrimAlpha;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mDarkenWhileDragging = (mKeyguardStateController.canDismissLockScreen() ^ true);
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mKeyguardVisibilityCallback = new KeyguardVisibilityCallback();
        this.mHandler = mHandler;
        this.mTimeTicker = new AlarmTimeout(alarmManager, (AlarmManager$OnAlarmListener)new _$$Lambda$ZxOK9HbkOUnaEI0FKoidLb2saOY(this), "hide_aod_wallpaper", this.mHandler);
        builder.setHandler(this.mHandler);
        builder.setTag("Scrims");
        this.mWakeLock = builder.build();
        this.mDozeParameters = mDozeParameters;
        this.mDockManager = mDockManager;
        mKeyguardStateController.addCallback((KeyguardStateController.Callback)new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardFadingAwayChanged() {
                ScrimController.this.setKeyguardFadingAway(mKeyguardStateController.isKeyguardFadingAway(), mKeyguardStateController.getKeyguardFadingAwayDuration());
            }
        });
        (this.mColorExtractor = mColorExtractor).addOnColorsChangedListener((ColorExtractor$OnColorsChangedListener)this);
        this.mColors = this.mColorExtractor.getNeutralColors();
        this.mNeedsDrawableColorUpdate = true;
    }
    
    private void applyExpansionToAlpha() {
        if (!this.mExpansionAffectsAlpha) {
            return;
        }
        final ScrimState mState = this.mState;
        if (mState != ScrimState.UNLOCKED && mState != ScrimState.BUBBLE_EXPANDED) {
            if (mState == ScrimState.KEYGUARD || mState == ScrimState.PULSING) {
                final float interpolatedFraction = this.getInterpolatedFraction();
                final float behindAlpha = this.mState.getBehindAlpha();
                if (this.mDarkenWhileDragging) {
                    this.mBehindAlpha = MathUtils.lerp(this.mDefaultScrimAlpha, behindAlpha, interpolatedFraction);
                    this.mInFrontAlpha = this.mState.getFrontAlpha();
                }
                else {
                    this.mBehindAlpha = MathUtils.lerp(0.0f, behindAlpha, interpolatedFraction);
                    this.mInFrontAlpha = this.mState.getFrontAlpha();
                }
                this.mBehindTint = ColorUtils.blendARGB(ScrimState.BOUNCER.getBehindTint(), this.mState.getBehindTint(), interpolatedFraction);
            }
        }
        else {
            this.mBehindAlpha = (float)Math.pow(this.getInterpolatedFraction(), 0.800000011920929) * this.mDefaultScrimAlpha;
            this.mInFrontAlpha = 0.0f;
        }
        if (!Float.isNaN(this.mBehindAlpha) && !Float.isNaN(this.mInFrontAlpha)) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Scrim opacity is NaN for state: ");
        sb.append(this.mState);
        sb.append(", front: ");
        sb.append(this.mInFrontAlpha);
        sb.append(", back: ");
        sb.append(this.mBehindAlpha);
        throw new IllegalStateException(sb.toString());
    }
    
    private void blankDisplay() {
        this.updateScrimColor(this.mScrimInFront, 1.0f, -16777216);
        this.doOnTheNextFrame(this.mPendingFrameCallback = new _$$Lambda$ScrimController$ag_08GXJhpSWypcA8_hrLE9y1Zo(this));
    }
    
    private void cancelAnimator(final ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
    
    private void dispatchScrimState(final float f) {
        this.mScrimStateListener.accept((Object)this.mState, (Object)f, (Object)this.mScrimInFront.getColors());
    }
    
    private void dispatchScrimsVisible() {
        int n;
        if (this.mScrimInFront.getViewAlpha() != 1.0f && this.mScrimBehind.getViewAlpha() != 1.0f) {
            if (this.mScrimInFront.getViewAlpha() == 0.0f && this.mScrimBehind.getViewAlpha() == 0.0f) {
                n = 0;
            }
            else {
                n = 1;
            }
        }
        else {
            n = 2;
        }
        if (this.mScrimsVisibility != n) {
            this.mScrimsVisibility = n;
            this.mScrimVisibleListener.accept(n);
        }
    }
    
    private float getCurrentScrimAlpha(final View view) {
        if (view == this.mScrimInFront) {
            return this.mInFrontAlpha;
        }
        if (view == this.mScrimBehind) {
            return this.mBehindAlpha;
        }
        if (view == this.mScrimForBubble) {
            return this.mBubbleAlpha;
        }
        throw new IllegalArgumentException("Unknown scrim view");
    }
    
    private int getCurrentScrimTint(final View view) {
        if (view == this.mScrimInFront) {
            return this.mInFrontTint;
        }
        if (view == this.mScrimBehind) {
            return this.mBehindTint;
        }
        if (view == this.mScrimForBubble) {
            return this.mBubbleTint;
        }
        throw new IllegalArgumentException("Unknown scrim view");
    }
    
    private float getInterpolatedFraction() {
        final float n = this.mExpansionFraction * 1.2f - 0.2f;
        if (n <= 0.0f) {
            return 0.0f;
        }
        return (float)(1.0 - (1.0 - Math.cos(Math.pow(1.0f - n, 2.0) * 3.141590118408203)) * 0.5);
    }
    
    private String getScrimName(final ScrimView scrimView) {
        if (scrimView == this.mScrimInFront) {
            return "front_scrim";
        }
        if (scrimView == this.mScrimBehind) {
            return "back_scrim";
        }
        if (scrimView == this.mScrimForBubble) {
            return "bubble_scrim";
        }
        return "unknown_scrim";
    }
    
    private void holdWakeLock() {
        if (!this.mWakeLockHeld) {
            final WakeLock mWakeLock = this.mWakeLock;
            if (mWakeLock != null) {
                this.mWakeLockHeld = true;
                mWakeLock.acquire("ScrimController");
            }
            else {
                Log.w("ScrimController", "Cannot hold wake lock, it has not been set yet");
            }
        }
    }
    
    private boolean isAnimating(final View view) {
        return view.getTag(ScrimController.TAG_KEY_ANIM) != null;
    }
    
    private void onFinished() {
        this.onFinished(this.mCallback);
    }
    
    private void onFinished(final Callback callback) {
        if (this.mPendingFrameCallback != null) {
            return;
        }
        if (!this.isAnimating(this.mScrimBehind) && !this.isAnimating(this.mScrimInFront) && !this.isAnimating(this.mScrimForBubble)) {
            if (this.mWakeLockHeld) {
                this.mWakeLock.release("ScrimController");
                this.mWakeLockHeld = false;
            }
            if (callback != null) {
                callback.onFinished();
                if (callback == this.mCallback) {
                    this.mCallback = null;
                }
            }
            if (this.mState == ScrimState.UNLOCKED) {
                this.mInFrontTint = 0;
                this.mBehindTint = 0;
                this.mBubbleTint = 0;
                this.updateScrimColor(this.mScrimInFront, this.mInFrontAlpha, 0);
                this.updateScrimColor(this.mScrimBehind, this.mBehindAlpha, this.mBehindTint);
                this.updateScrimColor(this.mScrimForBubble, this.mBubbleAlpha, this.mBubbleTint);
            }
            return;
        }
        if (callback != null && callback != this.mCallback) {
            callback.onFinished();
        }
    }
    
    private void setKeyguardFadingAway(final boolean b, final long n) {
        final ScrimState[] values = ScrimState.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            values[i].setKeyguardFadingAway(b, n);
        }
    }
    
    private void setOrAdaptCurrentAnimation(final View view) {
        final float currentScrimAlpha = this.getCurrentScrimAlpha(view);
        if (this.isAnimating(view)) {
            final ValueAnimator valueAnimator = (ValueAnimator)view.getTag(ScrimController.TAG_KEY_ANIM);
            view.setTag(ScrimController.TAG_START_ALPHA, (Object)((float)view.getTag(ScrimController.TAG_START_ALPHA) + (currentScrimAlpha - (float)view.getTag(ScrimController.TAG_END_ALPHA))));
            view.setTag(ScrimController.TAG_END_ALPHA, (Object)currentScrimAlpha);
            valueAnimator.setCurrentPlayTime(valueAnimator.getCurrentPlayTime());
        }
        else {
            this.updateScrimColor(view, currentScrimAlpha, this.getCurrentScrimTint(view));
        }
    }
    
    private void setScrimAlpha(final ScrimView scrimView, final float n) {
        boolean clickable = false;
        if (n == 0.0f) {
            scrimView.setClickable(false);
        }
        else {
            if (this.mState != ScrimState.AOD) {
                clickable = true;
            }
            scrimView.setClickable(clickable);
        }
        this.updateScrim(scrimView, n);
    }
    
    private boolean shouldFadeAwayWallpaper() {
        return this.mWallpaperSupportsAmbientMode && (this.mState == ScrimState.AOD && (this.mDozeParameters.getAlwaysOn() || this.mDockManager.isDocked()));
    }
    
    private boolean shouldUpdateFrontScrimAlpha() {
        return (this.mState == ScrimState.AOD && (this.mDozeParameters.getAlwaysOn() || this.mDockManager.isDocked())) || this.mState == ScrimState.PULSING;
    }
    
    private void startScrimAnimation(final View view, final float f) {
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        final Animator$AnimatorListener mAnimatorListener = this.mAnimatorListener;
        if (mAnimatorListener != null) {
            ofFloat.addListener(mAnimatorListener);
        }
        int tint;
        if (view instanceof ScrimView) {
            tint = ((ScrimView)view).getTint();
        }
        else {
            tint = 0;
        }
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ScrimController$pQ1ZzyQHHAbZJylpLDQQk40ggTo(this, view, tint));
        ofFloat.setInterpolator((TimeInterpolator)this.mInterpolator);
        ofFloat.setStartDelay(this.mAnimationDelay);
        ofFloat.setDuration(this.mAnimationDuration);
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private Callback lastCallback = ScrimController.this.mCallback;
            
            public void onAnimationEnd(final Animator animator) {
                view.setTag(ScrimController.TAG_KEY_ANIM, (Object)null);
                ScrimController.this.onFinished(this.lastCallback);
                ScrimController.this.dispatchScrimsVisible();
            }
        });
        view.setTag(ScrimController.TAG_START_ALPHA, (Object)f);
        view.setTag(ScrimController.TAG_END_ALPHA, (Object)this.getCurrentScrimAlpha(view));
        view.setTag(ScrimController.TAG_KEY_ANIM, (Object)ofFloat);
        ofFloat.start();
    }
    
    private void updateScrim(final ScrimView scrimView, final float n) {
        final float viewAlpha = scrimView.getViewAlpha();
        final ValueAnimator valueAnimator = ViewState.getChildTag(scrimView, ScrimController.TAG_KEY_ANIM);
        if (valueAnimator != null) {
            this.cancelAnimator(valueAnimator);
        }
        if (this.mPendingFrameCallback != null) {
            return;
        }
        if (this.mBlankScreen) {
            this.blankDisplay();
            return;
        }
        final boolean mScreenBlankingCallbackCalled = this.mScreenBlankingCallbackCalled;
        boolean b = true;
        if (!mScreenBlankingCallbackCalled) {
            final Callback mCallback = this.mCallback;
            if (mCallback != null) {
                mCallback.onDisplayBlanked();
                this.mScreenBlankingCallbackCalled = true;
            }
        }
        if (scrimView == this.mScrimBehind) {
            this.dispatchScrimState(n);
        }
        final boolean b2 = n != viewAlpha;
        if (scrimView.getTint() == this.getCurrentScrimTint(scrimView)) {
            b = false;
        }
        if (b2 || b) {
            if (this.mAnimateChange) {
                this.startScrimAnimation(scrimView, viewAlpha);
            }
            else {
                this.updateScrimColor(scrimView, n, this.getCurrentScrimTint(scrimView));
            }
        }
    }
    
    private void updateScrimColor(final View view, float max, final int tint) {
        max = Math.max(0.0f, Math.min(1.0f, max));
        if (view instanceof ScrimView) {
            final ScrimView scrimView = (ScrimView)view;
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getScrimName(scrimView));
            sb.append("_alpha");
            Trace.traceCounter(4096L, sb.toString(), (int)(255.0f * max));
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(this.getScrimName(scrimView));
            sb2.append("_tint");
            Trace.traceCounter(4096L, sb2.toString(), Color.alpha(tint));
            scrimView.setTint(tint);
            scrimView.setViewAlpha(max);
        }
        else {
            view.setAlpha(max);
        }
        this.dispatchScrimsVisible();
    }
    
    public void attachViews(final ScrimView mScrimBehind, final ScrimView mScrimInFront, final ScrimView mScrimForBubble) {
        this.mScrimBehind = mScrimBehind;
        this.mScrimInFront = mScrimInFront;
        this.mScrimForBubble = mScrimForBubble;
        final ScrimState[] values = ScrimState.values();
        for (int i = 0; i < values.length; ++i) {
            values[i].init(this.mScrimInFront, this.mScrimBehind, this.mScrimForBubble, this.mDozeParameters, this.mDockManager);
            values[i].setScrimBehindAlphaKeyguard(this.mScrimBehindAlphaKeyguard);
            values[i].setDefaultScrimAlpha(this.mDefaultScrimAlpha);
        }
        this.mScrimBehind.setDefaultFocusHighlightEnabled(false);
        this.mScrimInFront.setDefaultFocusHighlightEnabled(false);
        this.mScrimForBubble.setDefaultFocusHighlightEnabled(false);
        this.updateScrims();
        this.mKeyguardUpdateMonitor.registerCallback(this.mKeyguardVisibilityCallback);
    }
    
    @VisibleForTesting
    protected void doOnTheNextFrame(final Runnable runnable) {
        this.mScrimBehind.postOnAnimationDelayed(runnable, 32L);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println(" ScrimController: ");
        printWriter.print("  state: ");
        printWriter.println(this.mState);
        printWriter.print("  frontScrim:");
        printWriter.print(" viewAlpha=");
        printWriter.print(this.mScrimInFront.getViewAlpha());
        printWriter.print(" alpha=");
        printWriter.print(this.mInFrontAlpha);
        printWriter.print(" tint=0x");
        printWriter.println(Integer.toHexString(this.mScrimInFront.getTint()));
        printWriter.print("  backScrim:");
        printWriter.print(" viewAlpha=");
        printWriter.print(this.mScrimBehind.getViewAlpha());
        printWriter.print(" alpha=");
        printWriter.print(this.mBehindAlpha);
        printWriter.print(" tint=0x");
        printWriter.println(Integer.toHexString(this.mScrimBehind.getTint()));
        printWriter.print("  bubbleScrim:");
        printWriter.print(" viewAlpha=");
        printWriter.print(this.mScrimForBubble.getViewAlpha());
        printWriter.print(" alpha=");
        printWriter.print(this.mBubbleAlpha);
        printWriter.print(" tint=0x");
        printWriter.println(Integer.toHexString(this.mScrimForBubble.getTint()));
        printWriter.print("  mTracking=");
        printWriter.println(this.mTracking);
        printWriter.print("  mDefaultScrimAlpha=");
        printWriter.println(this.mDefaultScrimAlpha);
        printWriter.print("  mExpansionFraction=");
        printWriter.println(this.mExpansionFraction);
    }
    
    public ScrimState getState() {
        return this.mState;
    }
    
    public void onColorsChanged(final ColorExtractor colorExtractor, final int n) {
        this.mColors = this.mColorExtractor.getNeutralColors();
        this.mNeedsDrawableColorUpdate = true;
        this.scheduleUpdate();
    }
    
    public void onExpandingFinished() {
        this.mTracking = false;
    }
    
    @VisibleForTesting
    protected void onHideWallpaperTimeout() {
        final ScrimState mState = this.mState;
        if (mState != ScrimState.AOD && mState != ScrimState.PULSING) {
            return;
        }
        this.holdWakeLock();
        this.mWallpaperVisibilityTimedOut = true;
        this.mAnimateChange = true;
        this.mAnimationDuration = this.mDozeParameters.getWallpaperFadeOutDuration();
        this.scheduleUpdate();
    }
    
    public boolean onPreDraw() {
        this.mScrimBehind.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
        this.mUpdatePending = false;
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onStart();
        }
        this.updateScrims();
        return true;
    }
    
    public void onScreenTurnedOff() {
        this.mScreenOn = false;
    }
    
    public void onScreenTurnedOn() {
        this.mScreenOn = true;
        if (this.mHandler.hasCallbacks(this.mBlankingTransitionRunnable)) {
            if (ScrimController.DEBUG) {
                Log.d("ScrimController", "Shorter blanking because screen turned on. All good.");
            }
            this.mHandler.removeCallbacks(this.mBlankingTransitionRunnable);
            this.mBlankingTransitionRunnable.run();
        }
    }
    
    public void onTrackingStarted() {
        this.mTracking = true;
        this.mDarkenWhileDragging = (true ^ this.mKeyguardStateController.canDismissLockScreen());
    }
    
    protected void scheduleUpdate() {
        if (!this.mUpdatePending) {
            final ScrimView mScrimBehind = this.mScrimBehind;
            if (mScrimBehind != null) {
                mScrimBehind.invalidate();
                this.mScrimBehind.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                this.mUpdatePending = true;
            }
        }
    }
    
    @VisibleForTesting
    void setAnimatorListener(final Animator$AnimatorListener mAnimatorListener) {
        this.mAnimatorListener = mAnimatorListener;
    }
    
    public void setAodFrontScrimAlpha(final float aodFrontScrimAlpha) {
        if (this.mInFrontAlpha != aodFrontScrimAlpha && this.shouldUpdateFrontScrimAlpha()) {
            this.mInFrontAlpha = aodFrontScrimAlpha;
            this.updateScrims();
        }
        ScrimState.AOD.setAodFrontScrimAlpha(aodFrontScrimAlpha);
        ScrimState.PULSING.setAodFrontScrimAlpha(aodFrontScrimAlpha);
    }
    
    public void setCurrentUser(final int n) {
    }
    
    public void setExpansionAffectsAlpha(final boolean mExpansionAffectsAlpha) {
        this.mExpansionAffectsAlpha = mExpansionAffectsAlpha;
    }
    
    public void setHasBackdrop(final boolean hasBackdrop) {
        final ScrimState[] values = ScrimState.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            values[i].setHasBackdrop(hasBackdrop);
        }
        final ScrimState mState = this.mState;
        if (mState == ScrimState.AOD || mState == ScrimState.PULSING) {
            final float behindAlpha = this.mState.getBehindAlpha();
            if (Float.isNaN(behindAlpha)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Scrim opacity is NaN for state: ");
                sb.append(this.mState);
                sb.append(", back: ");
                sb.append(this.mBehindAlpha);
                throw new IllegalStateException(sb.toString());
            }
            if (this.mBehindAlpha != behindAlpha) {
                this.mBehindAlpha = behindAlpha;
                this.updateScrims();
            }
        }
    }
    
    public void setKeyguardOccluded(final boolean mKeyguardOccluded) {
        this.mKeyguardOccluded = mKeyguardOccluded;
        this.updateScrims();
    }
    
    public void setLaunchingAffordanceWithPreview(final boolean launchingAffordanceWithPreview) {
        final ScrimState[] values = ScrimState.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            values[i].setLaunchingAffordanceWithPreview(launchingAffordanceWithPreview);
        }
    }
    
    public void setPanelExpansion(final float n) {
        if (!Float.isNaN(n)) {
            if (this.mExpansionFraction != n) {
                this.mExpansionFraction = n;
                final ScrimState mState = this.mState;
                if (mState == ScrimState.UNLOCKED || mState == ScrimState.KEYGUARD || mState == ScrimState.PULSING || mState == ScrimState.BUBBLE_EXPANDED) {
                    if (this.mExpansionAffectsAlpha) {
                        this.applyExpansionToAlpha();
                        if (this.mUpdatePending) {
                            return;
                        }
                        this.setOrAdaptCurrentAnimation(this.mScrimBehind);
                        this.setOrAdaptCurrentAnimation(this.mScrimInFront);
                        this.setOrAdaptCurrentAnimation(this.mScrimForBubble);
                        this.dispatchScrimState(this.mScrimBehind.getViewAlpha());
                        if (this.mWallpaperVisibilityTimedOut) {
                            this.mWallpaperVisibilityTimedOut = false;
                            DejankUtils.postAfterTraversal(new _$$Lambda$ScrimController$3j_fFZLqB7tqf9vzdgmHNd2VqbY(this));
                        }
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException("Fraction should not be NaN");
    }
    
    public void setScrimBehindChangeRunnable(final Runnable changeRunnable) {
        this.mScrimBehind.setChangeRunnable(changeRunnable);
    }
    
    protected void setScrimBehindValues(final float n) {
        this.mScrimBehindAlphaKeyguard = n;
        final ScrimState[] values = ScrimState.values();
        for (int i = 0; i < values.length; ++i) {
            values[i].setScrimBehindAlphaKeyguard(n);
        }
        this.scheduleUpdate();
    }
    
    void setScrimVisibleListener(final Consumer<Integer> mScrimVisibleListener) {
        this.mScrimVisibleListener = mScrimVisibleListener;
    }
    
    public void setWakeLockScreenSensorActive(final boolean wakeLockScreenSensorActive) {
        final ScrimState[] values = ScrimState.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            values[i].setWakeLockScreenSensorActive(wakeLockScreenSensorActive);
        }
        final ScrimState mState = this.mState;
        if (mState == ScrimState.PULSING) {
            final float behindAlpha = mState.getBehindAlpha();
            if (this.mBehindAlpha != behindAlpha) {
                this.mBehindAlpha = behindAlpha;
                if (Float.isNaN(behindAlpha)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Scrim opacity is NaN for state: ");
                    sb.append(this.mState);
                    sb.append(", back: ");
                    sb.append(this.mBehindAlpha);
                    throw new IllegalStateException(sb.toString());
                }
                this.updateScrims();
            }
        }
    }
    
    public void setWallpaperSupportsAmbientMode(final boolean b) {
        this.mWallpaperSupportsAmbientMode = b;
        final ScrimState[] values = ScrimState.values();
        for (int i = 0; i < values.length; ++i) {
            values[i].setWallpaperSupportsAmbientMode(b);
        }
    }
    
    public void transitionTo(final ScrimState scrimState) {
        this.transitionTo(scrimState, null);
    }
    
    public void transitionTo(final ScrimState obj, final Callback mCallback) {
        if (obj == this.mState) {
            if (mCallback != null && this.mCallback != mCallback) {
                mCallback.onFinished();
            }
            return;
        }
        if (ScrimController.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("State changed to: ");
            sb.append(obj);
            Log.d("ScrimController", sb.toString());
        }
        if (obj == ScrimState.UNINITIALIZED) {
            throw new IllegalArgumentException("Cannot change to UNINITIALIZED.");
        }
        final ScrimState mState = this.mState;
        this.mState = obj;
        Trace.traceCounter(4096L, "scrim_state", obj.ordinal());
        final Callback mCallback2 = this.mCallback;
        if (mCallback2 != null) {
            mCallback2.onCancelled();
        }
        this.mCallback = mCallback;
        obj.prepare(mState);
        this.mScreenBlankingCallbackCalled = false;
        this.mAnimationDelay = 0L;
        this.mBlankScreen = obj.getBlanksScreen();
        this.mAnimateChange = obj.getAnimateChange();
        this.mAnimationDuration = obj.getAnimationDuration();
        this.mInFrontTint = obj.getFrontTint();
        this.mBehindTint = obj.getBehindTint();
        this.mBubbleTint = obj.getBubbleTint();
        this.mInFrontAlpha = obj.getFrontAlpha();
        this.mBehindAlpha = obj.getBehindAlpha();
        this.mBubbleAlpha = obj.getBubbleAlpha();
        if (!Float.isNaN(this.mBehindAlpha) && !Float.isNaN(this.mInFrontAlpha)) {
            this.applyExpansionToAlpha();
            final ScrimView mScrimInFront = this.mScrimInFront;
            final boolean lowPowerState = obj.isLowPowerState();
            boolean mNeedsDrawableColorUpdate = true;
            mScrimInFront.setFocusable(lowPowerState ^ true);
            this.mScrimBehind.setFocusable(obj.isLowPowerState() ^ true);
            final Runnable mPendingFrameCallback = this.mPendingFrameCallback;
            if (mPendingFrameCallback != null) {
                this.mScrimBehind.removeCallbacks(mPendingFrameCallback);
                this.mPendingFrameCallback = null;
            }
            if (this.mHandler.hasCallbacks(this.mBlankingTransitionRunnable)) {
                this.mHandler.removeCallbacks(this.mBlankingTransitionRunnable);
                this.mBlankingTransitionRunnable = null;
            }
            if (obj == ScrimState.BRIGHTNESS_MIRROR) {
                mNeedsDrawableColorUpdate = false;
            }
            this.mNeedsDrawableColorUpdate = mNeedsDrawableColorUpdate;
            if (this.mState.isLowPowerState()) {
                this.holdWakeLock();
            }
            this.mWallpaperVisibilityTimedOut = false;
            if (this.shouldFadeAwayWallpaper()) {
                DejankUtils.postAfterTraversal(new _$$Lambda$ScrimController$YQJRwwTLFgaOweq9aHvS8f9csz8(this));
            }
            else {
                final AlarmTimeout mTimeTicker = this.mTimeTicker;
                Objects.requireNonNull(mTimeTicker);
                DejankUtils.postAfterTraversal(new _$$Lambda$0ZxUFLvlsGlm9ET2o7nSDW8wc5w(mTimeTicker));
            }
            if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition() && this.mState == ScrimState.UNLOCKED) {
                this.mScrimInFront.postOnAnimationDelayed((Runnable)new _$$Lambda$5DY8P9cXHTvbVZZOVB_VSCJUZk0(this), 16L);
                this.mAnimationDelay = 100L;
            }
            else if ((!this.mDozeParameters.getAlwaysOn() && mState == ScrimState.AOD) || (this.mState == ScrimState.AOD && !this.mDozeParameters.getDisplayNeedsBlanking())) {
                this.onPreDraw();
            }
            else {
                this.scheduleUpdate();
            }
            this.dispatchScrimState(this.mScrimBehind.getViewAlpha());
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Scrim opacity is NaN for state: ");
        sb2.append(obj);
        sb2.append(", front: ");
        sb2.append(this.mInFrontAlpha);
        sb2.append(", back: ");
        sb2.append(this.mBehindAlpha);
        throw new IllegalStateException(sb2.toString());
    }
    
    protected void updateScrims() {
        final boolean mNeedsDrawableColorUpdate = this.mNeedsDrawableColorUpdate;
        boolean b = true;
        if (mNeedsDrawableColorUpdate) {
            this.mNeedsDrawableColorUpdate = false;
            final boolean b2 = this.mScrimInFront.getViewAlpha() != 0.0f && !this.mBlankScreen;
            final boolean b3 = this.mScrimBehind.getViewAlpha() != 0.0f && !this.mBlankScreen;
            final boolean b4 = this.mScrimForBubble.getViewAlpha() != 0.0f && !this.mBlankScreen;
            this.mScrimInFront.setColors(this.mColors, b2);
            this.mScrimBehind.setColors(this.mColors, b3);
            this.mScrimForBubble.setColors(this.mColors, b4);
            int n;
            if (this.mColors.supportsDarkText()) {
                n = -16777216;
            }
            else {
                n = -1;
            }
            ColorUtils.calculateMinimumBackgroundAlpha(n, this.mColors.getMainColor(), 4.5f);
            this.dispatchScrimState(this.mScrimBehind.getViewAlpha());
        }
        final ScrimState mState = this.mState;
        final boolean b5 = (mState == ScrimState.AOD || mState == ScrimState.PULSING) && this.mWallpaperVisibilityTimedOut;
        final ScrimState mState2 = this.mState;
        if ((mState2 != ScrimState.PULSING && mState2 != ScrimState.AOD) || !this.mKeyguardOccluded) {
            b = false;
        }
        if (b5 || b) {
            this.mBehindAlpha = 1.0f;
        }
        this.setScrimAlpha(this.mScrimInFront, this.mInFrontAlpha);
        this.setScrimAlpha(this.mScrimBehind, this.mBehindAlpha);
        this.setScrimAlpha(this.mScrimForBubble, this.mBubbleAlpha);
        this.onFinished();
        this.dispatchScrimsVisible();
    }
    
    public interface Callback
    {
        default void onCancelled() {
        }
        
        default void onDisplayBlanked() {
        }
        
        default void onFinished() {
        }
        
        default void onStart() {
        }
    }
    
    private class KeyguardVisibilityCallback extends KeyguardUpdateMonitorCallback
    {
        @Override
        public void onKeyguardVisibilityChanged(final boolean b) {
            ScrimController.this.mNeedsDrawableColorUpdate = true;
            ScrimController.this.scheduleUpdate();
        }
    }
}
