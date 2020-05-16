// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.function.Function;
import java.util.Optional;
import android.app.ActivityManager$RunningTaskInfo;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ObjectAnimator;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.view.IRotationWatcher;
import android.view.WindowManagerGlobal;
import com.android.systemui.R$style;
import android.view.MotionEvent;
import android.view.View;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.view.View$OnHoverListener;
import android.view.View$OnClickListener;
import android.os.RemoteException;
import android.os.Looper;
import com.android.systemui.Dependency;
import android.view.IRotationWatcher$Stub;
import com.android.systemui.statusbar.policy.RotationLockController;
import android.animation.Animator;
import java.util.function.Consumer;
import com.android.internal.logging.MetricsLogger;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;

public class RotationButtonController
{
    private AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    private final Runnable mCancelPendingRotationProposal;
    private final Context mContext;
    private boolean mHoveringRotationSuggestion;
    private boolean mIsNavigationBarShowing;
    private int mLastRotationSuggestion;
    private boolean mListenersRegistered;
    private final Handler mMainThreadHandler;
    private final MetricsLogger mMetricsLogger;
    private boolean mPendingRotationSuggestion;
    private final Runnable mRemoveRotationProposal;
    private Consumer<Integer> mRotWatcherListener;
    private Animator mRotateHideAnimator;
    private final RotationButton mRotationButton;
    private RotationLockController mRotationLockController;
    private final IRotationWatcher$Stub mRotationWatcher;
    private int mStyleRes;
    private TaskStackListenerImpl mTaskStackListener;
    private final ViewRippler mViewRippler;
    
    RotationButtonController(final Context mContext, final int mStyleRes, final RotationButton mRotationButton) {
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mViewRippler = new ViewRippler();
        this.mListenersRegistered = false;
        this.mRemoveRotationProposal = new _$$Lambda$RotationButtonController$9GntNFTDdKoyCtcSVI_eBCW3dMQ(this);
        this.mCancelPendingRotationProposal = new _$$Lambda$RotationButtonController$rLt402gKIdgNcqykKz16VIeLAMM(this);
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mRotationWatcher = new IRotationWatcher$Stub() {
            public void onRotationChanged(final int n) throws RemoteException {
                RotationButtonController.this.mMainThreadHandler.postAtFrontOfQueue((Runnable)new _$$Lambda$RotationButtonController$1$wNXXdlqLeBk1NR5FrlGSJawDu0I(this, n));
            }
        };
        this.mContext = mContext;
        (this.mRotationButton = mRotationButton).setRotationButtonController(this);
        this.mStyleRes = mStyleRes;
        this.mIsNavigationBarShowing = true;
        this.mRotationLockController = Dependency.get(RotationLockController.class);
        this.mAccessibilityManagerWrapper = Dependency.get(AccessibilityManagerWrapper.class);
        this.mTaskStackListener = new TaskStackListenerImpl();
        this.mRotationButton.setOnClickListener((View$OnClickListener)new _$$Lambda$RotationButtonController$nGgIS1iCjy5uWWIfPZ9LUPKtUUc(this));
        this.mRotationButton.setOnHoverListener((View$OnHoverListener)new _$$Lambda$RotationButtonController$ITAepcsPx2pDX6xNt_4OEwYvoRc(this));
    }
    
    private int computeRotationProposalTimeout() {
        final AccessibilityManagerWrapper mAccessibilityManagerWrapper = this.mAccessibilityManagerWrapper;
        int n;
        if (this.mHoveringRotationSuggestion) {
            n = 16000;
        }
        else {
            n = 5000;
        }
        return mAccessibilityManagerWrapper.getRecommendedTimeoutMillis(n, 4);
    }
    
    static boolean hasDisable2RotateSuggestionFlag(final int n) {
        return (n & 0x10) != 0x0;
    }
    
    private void incrementNumAcceptedRotationSuggestionsIfNeeded() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        final int int1 = Settings$Secure.getInt(contentResolver, "num_rotation_suggestions_accepted", 0);
        if (int1 < 3) {
            Settings$Secure.putInt(contentResolver, "num_rotation_suggestions_accepted", int1 + 1);
        }
    }
    
    private boolean isRotateSuggestionIntroduced() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getInt(contentResolver, "num_rotation_suggestions_accepted", 0) >= 3) {
            b = true;
        }
        return b;
    }
    
    private boolean isRotationAnimationCCW(final int n, final int n2) {
        return (n != 0 || n2 != 1) && ((n == 0 && n2 == 2) || (n == 0 && n2 == 3) || (n == 1 && n2 == 0) || ((n != 1 || n2 != 2) && ((n == 1 && n2 == 3) || (n == 2 && n2 == 0) || (n == 2 && n2 == 1) || ((n != 2 || n2 != 3) && (n != 3 || n2 != 0) && ((n == 3 && n2 == 1) || (n == 3 && n2 == 2))))));
    }
    
    private void onRotateSuggestionClick(final View view) {
        this.mMetricsLogger.action(1287);
        this.incrementNumAcceptedRotationSuggestionsIfNeeded();
        this.setRotationLockedAtAngle(this.mLastRotationSuggestion);
    }
    
    private boolean onRotateSuggestionHover(final View view, final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        this.mHoveringRotationSuggestion = (actionMasked == 9 || actionMasked == 7);
        this.rescheduleRotationTimeout(true);
        return false;
    }
    
    private void onRotationSuggestionsDisabled() {
        this.setRotateSuggestionButtonState(false, true);
        this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
    }
    
    private void rescheduleRotationTimeout(final boolean b) {
        if (b) {
            final Animator mRotateHideAnimator = this.mRotateHideAnimator;
            if (mRotateHideAnimator != null && mRotateHideAnimator.isRunning()) {
                return;
            }
            if (!this.mRotationButton.isVisible()) {
                return;
            }
        }
        this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
        this.mMainThreadHandler.postDelayed(this.mRemoveRotationProposal, (long)this.computeRotationProposalTimeout());
    }
    
    private boolean shouldOverrideUserLockPrefs(final int n) {
        return n == 0;
    }
    
    private void showAndLogRotationSuggestion() {
        this.setRotateSuggestionButtonState(true);
        this.rescheduleRotationTimeout(false);
        this.mMetricsLogger.visible(1288);
    }
    
    void addRotationCallback(final Consumer<Integer> mRotWatcherListener) {
        this.mRotWatcherListener = mRotWatcherListener;
    }
    
    RotationButton getRotationButton() {
        return this.mRotationButton;
    }
    
    int getStyleRes() {
        return this.mStyleRes;
    }
    
    public boolean isRotationLocked() {
        return this.mRotationLockController.isRotationLocked();
    }
    
    void onDisable2FlagChanged(final int n) {
        if (hasDisable2RotateSuggestionFlag(n)) {
            this.onRotationSuggestionsDisabled();
        }
    }
    
    void onNavigationBarWindowVisibilityChange(final boolean mIsNavigationBarShowing) {
        if (this.mIsNavigationBarShowing != mIsNavigationBarShowing && (this.mIsNavigationBarShowing = mIsNavigationBarShowing) && this.mPendingRotationSuggestion) {
            this.showAndLogRotationSuggestion();
        }
    }
    
    void onRotationProposal(int n, final int n2, final boolean b) {
        if (!this.mRotationButton.acceptRotationProposal()) {
            return;
        }
        if (!b) {
            this.setRotateSuggestionButtonState(false);
            return;
        }
        if (n == n2) {
            this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
            this.setRotateSuggestionButtonState(false);
            return;
        }
        this.mLastRotationSuggestion = n;
        final boolean rotationAnimationCCW = this.isRotationAnimationCCW(n2, n);
        if (n2 != 0 && n2 != 2) {
            if (rotationAnimationCCW) {
                n = R$style.RotateButtonCCWStart0;
            }
            else {
                n = R$style.RotateButtonCWStart0;
            }
        }
        else if (rotationAnimationCCW) {
            n = R$style.RotateButtonCCWStart90;
        }
        else {
            n = R$style.RotateButtonCWStart90;
        }
        this.mStyleRes = n;
        this.mRotationButton.updateIcon();
        if (this.mIsNavigationBarShowing) {
            this.showAndLogRotationSuggestion();
        }
        else {
            this.mPendingRotationSuggestion = true;
            this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
            this.mMainThreadHandler.postDelayed(this.mCancelPendingRotationProposal, 20000L);
        }
    }
    
    void registerListeners() {
        if (this.mListenersRegistered) {
            return;
        }
        this.mListenersRegistered = true;
        try {
            WindowManagerGlobal.getWindowManagerService().watchRotation((IRotationWatcher)this.mRotationWatcher, this.mContext.getDisplay().getDisplayId());
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        }
        catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    
    void setDarkIntensity(final float darkIntensity) {
        this.mRotationButton.setDarkIntensity(darkIntensity);
    }
    
    void setRotateSuggestionButtonState(final boolean b) {
        this.setRotateSuggestionButtonState(b, false);
    }
    
    void setRotateSuggestionButtonState(final boolean b, final boolean b2) {
        if (!b && !this.mRotationButton.isVisible()) {
            return;
        }
        final View currentView = this.mRotationButton.getCurrentView();
        if (currentView == null) {
            return;
        }
        final KeyButtonDrawable imageDrawable = this.mRotationButton.getImageDrawable();
        if (imageDrawable == null) {
            return;
        }
        this.mPendingRotationSuggestion = false;
        this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
        if (b) {
            final Animator mRotateHideAnimator = this.mRotateHideAnimator;
            if (mRotateHideAnimator != null && mRotateHideAnimator.isRunning()) {
                this.mRotateHideAnimator.cancel();
            }
            this.mRotateHideAnimator = null;
            currentView.setAlpha(1.0f);
            if (imageDrawable.canAnimate()) {
                imageDrawable.resetAnimation();
                imageDrawable.startAnimation();
            }
            if (!this.isRotateSuggestionIntroduced()) {
                this.mViewRippler.start(currentView);
            }
            this.mRotationButton.show();
        }
        else {
            this.mViewRippler.stop();
            if (b2) {
                final Animator mRotateHideAnimator2 = this.mRotateHideAnimator;
                if (mRotateHideAnimator2 != null && mRotateHideAnimator2.isRunning()) {
                    this.mRotateHideAnimator.pause();
                }
                this.mRotationButton.hide();
                return;
            }
            final Animator mRotateHideAnimator3 = this.mRotateHideAnimator;
            if (mRotateHideAnimator3 != null && mRotateHideAnimator3.isRunning()) {
                return;
            }
            final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)currentView, "alpha", new float[] { 0.0f });
            ofFloat.setDuration(100L);
            ofFloat.setInterpolator((TimeInterpolator)Interpolators.LINEAR);
            ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    RotationButtonController.this.mRotationButton.hide();
                }
            });
            ((ObjectAnimator)(this.mRotateHideAnimator = (Animator)ofFloat)).start();
        }
    }
    
    void setRotationLockedAtAngle(final int n) {
        this.mRotationLockController.setRotationLockedAtAngle(true, n);
    }
    
    void unregisterListeners() {
        if (!this.mListenersRegistered) {
            return;
        }
        this.mListenersRegistered = false;
        try {
            WindowManagerGlobal.getWindowManagerService().removeRotationWatcher((IRotationWatcher)this.mRotationWatcher);
            ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
        }
        catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    
    private class TaskStackListenerImpl extends TaskStackChangeListener
    {
        @Override
        public void onActivityRequestedOrientationChanged(final int n, final int n2) {
            Optional.ofNullable(ActivityManagerWrapper.getInstance()).map((Function<? super ActivityManagerWrapper, ?>)_$$Lambda$Zm3Yj0EQnVWvu_ZksQ_OsrTwJ3k.INSTANCE).ifPresent(new _$$Lambda$RotationButtonController$TaskStackListenerImpl$zCjhcFpUTQGdzdQEgIMUjTrjPZU(this, n));
        }
        
        @Override
        public void onTaskMovedToFront(final int n) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }
        
        @Override
        public void onTaskRemoved(final int n) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }
        
        @Override
        public void onTaskStackChanged() {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }
    }
    
    private class ViewRippler
    {
        private final Runnable mRipple;
        private View mRoot;
        
        private ViewRippler(final RotationButtonController rotationButtonController) {
            this.mRipple = new Runnable() {
                @Override
                public void run() {
                    if (!ViewRippler.this.mRoot.isAttachedToWindow()) {
                        return;
                    }
                    ViewRippler.this.mRoot.setPressed(true);
                    ViewRippler.this.mRoot.setPressed(false);
                }
            };
        }
        
        public void start(final View mRoot) {
            this.stop();
            (this.mRoot = mRoot).postOnAnimationDelayed(this.mRipple, 50L);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 2000L);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 4000L);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 6000L);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 8000L);
        }
        
        public void stop() {
            final View mRoot = this.mRoot;
            if (mRoot != null) {
                mRoot.removeCallbacks(this.mRipple);
            }
        }
    }
}
