// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.ActivityStarter;
import android.content.res.ColorStateList;
import android.util.Slog;
import android.os.UserManager;
import android.util.MathUtils;
import android.view.ViewParent;
import com.android.systemui.Dependency;
import com.android.keyguard.KeyguardSecurityModel;
import android.view.KeyEvent;
import android.view.WindowInsets;
import com.android.systemui.R$dimen;
import android.view.View;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import java.io.PrintWriter;
import android.util.Log;
import com.android.systemui.DejankUtils;
import com.android.systemui.shared.system.SysUiStatsLog;
import android.view.ViewTreeObserver$OnPreDrawListener;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardHostView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.os.Handler;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import android.content.Context;
import android.view.ViewGroup;
import com.android.keyguard.ViewMediatorCallback;

public class KeyguardBouncer
{
    private int mBouncerPromptReason;
    protected final ViewMediatorCallback mCallback;
    protected final ViewGroup mContainer;
    protected final Context mContext;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    private float mExpansion;
    private final BouncerExpansionCallback mExpansionCallback;
    private final FalsingManager mFalsingManager;
    private final Handler mHandler;
    private boolean mIsAnimatingAway;
    private boolean mIsScrimmed;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    protected KeyguardHostView mKeyguardView;
    protected final LockPatternUtils mLockPatternUtils;
    private final Runnable mRemoveViewRunnable;
    private final Runnable mResetRunnable;
    protected ViewGroup mRoot;
    private final Runnable mShowRunnable;
    private boolean mShowingSoon;
    private int mStatusBarHeight;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    
    public KeyguardBouncer(final Context mContext, final ViewMediatorCallback mCallback, final LockPatternUtils mLockPatternUtils, final ViewGroup mContainer, final DismissCallbackRegistry mDismissCallbackRegistry, final FalsingManager mFalsingManager, final BouncerExpansionCallback mExpansionCallback, final KeyguardStateController mKeyguardStateController, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final KeyguardBypassController mKeyguardBypassController, final Handler mHandler) {
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onStrongAuthStateChanged(final int n) {
                final KeyguardBouncer this$0 = KeyguardBouncer.this;
                this$0.mBouncerPromptReason = this$0.mCallback.getBouncerPromptReason();
            }
        };
        this.mRemoveViewRunnable = new _$$Lambda$iQsniWdIxLGqyYwRi09kQ_Ah02M(this);
        this.mResetRunnable = new _$$Lambda$KeyguardBouncer$Y9Hvfk0n3yPK2FQ39O1Z5j49gj0(this);
        this.mExpansion = 1.0f;
        this.mShowRunnable = new Runnable() {
            @Override
            public void run() {
                KeyguardBouncer.this.mRoot.setVisibility(0);
                final KeyguardBouncer this$0 = KeyguardBouncer.this;
                this$0.showPromptReason(this$0.mBouncerPromptReason);
                final CharSequence consumeCustomMessage = KeyguardBouncer.this.mCallback.consumeCustomMessage();
                if (consumeCustomMessage != null) {
                    KeyguardBouncer.this.mKeyguardView.showErrorMessage(consumeCustomMessage);
                }
                if (KeyguardBouncer.this.mKeyguardView.getHeight() != 0 && KeyguardBouncer.this.mKeyguardView.getHeight() != KeyguardBouncer.this.mStatusBarHeight) {
                    KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
                }
                else {
                    KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
                        public boolean onPreDraw() {
                            KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                            KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
                            return true;
                        }
                    });
                    KeyguardBouncer.this.mKeyguardView.requestLayout();
                }
                KeyguardBouncer.this.mShowingSoon = false;
                if (KeyguardBouncer.this.mExpansion == 0.0f) {
                    KeyguardBouncer.this.mKeyguardView.onResume();
                    KeyguardBouncer.this.mKeyguardView.resetSecurityContainer();
                    final KeyguardBouncer this$2 = KeyguardBouncer.this;
                    this$2.showPromptReason(this$2.mBouncerPromptReason);
                }
                SysUiStatsLog.write(63, 2);
            }
        };
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.mLockPatternUtils = mLockPatternUtils;
        this.mContainer = mContainer;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mFalsingManager = mFalsingManager;
        this.mDismissCallbackRegistry = mDismissCallbackRegistry;
        this.mExpansionCallback = mExpansionCallback;
        this.mHandler = mHandler;
        this.mKeyguardStateController = mKeyguardStateController;
        mKeyguardUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
        this.mKeyguardBypassController = mKeyguardBypassController;
    }
    
    private void cancelShowRunnable() {
        DejankUtils.removeCallbacks(this.mShowRunnable);
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mShowingSoon = false;
    }
    
    private void onFullyHidden() {
        this.cancelShowRunnable();
        final ViewGroup mRoot = this.mRoot;
        if (mRoot != null) {
            mRoot.setVisibility(4);
        }
        this.mFalsingManager.onBouncerHidden();
        DejankUtils.postAfterTraversal(this.mResetRunnable);
    }
    
    private void onFullyShown() {
        this.mFalsingManager.onBouncerShown();
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        if (mKeyguardView == null) {
            Log.wtf("KeyguardBouncer", "onFullyShown when view was null");
        }
        else {
            mKeyguardView.onResume();
            final ViewGroup mRoot = this.mRoot;
            if (mRoot != null) {
                mRoot.announceForAccessibility(this.mKeyguardView.getAccessibilityTitleForCurrentMode());
            }
        }
    }
    
    private void showPrimarySecurityScreen() {
        this.mKeyguardView.showPrimarySecurityScreen();
    }
    
    public void dump(final PrintWriter printWriter) {
        printWriter.println("KeyguardBouncer");
        final StringBuilder sb = new StringBuilder();
        sb.append("  isShowing(): ");
        sb.append(this.isShowing());
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mStatusBarHeight: ");
        sb2.append(this.mStatusBarHeight);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mExpansion: ");
        sb3.append(this.mExpansion);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mKeyguardView; ");
        sb4.append(this.mKeyguardView);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mShowingSoon: ");
        sb5.append(this.mKeyguardView);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mBouncerPromptReason: ");
        sb6.append(this.mBouncerPromptReason);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  mIsAnimatingAway: ");
        sb7.append(this.mIsAnimatingAway);
        printWriter.println(sb7.toString());
    }
    
    protected void ensureView() {
        final boolean hasCallbacks = this.mHandler.hasCallbacks(this.mRemoveViewRunnable);
        if (this.mRoot == null || hasCallbacks) {
            this.inflateView();
        }
    }
    
    public void hide(final boolean b) {
        if (this.isShowing()) {
            SysUiStatsLog.write(63, 1);
            this.mDismissCallbackRegistry.notifyDismissCancelled();
        }
        this.mIsScrimmed = false;
        this.mFalsingManager.onBouncerHidden();
        this.mCallback.onBouncerVisiblityChanged(false);
        this.cancelShowRunnable();
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        if (mKeyguardView != null) {
            mKeyguardView.cancelDismissAction();
            this.mKeyguardView.cleanUp();
        }
        this.mIsAnimatingAway = false;
        final ViewGroup mRoot = this.mRoot;
        if (mRoot != null) {
            mRoot.setVisibility(4);
            if (b) {
                this.mHandler.postDelayed(this.mRemoveViewRunnable, 50L);
            }
        }
    }
    
    public boolean inTransit() {
        if (!this.mShowingSoon) {
            final float mExpansion = this.mExpansion;
            if (mExpansion == 1.0f || mExpansion == 0.0f) {
                return false;
            }
        }
        return true;
    }
    
    protected void inflateView() {
        this.removeView();
        this.mHandler.removeCallbacks(this.mRemoveViewRunnable);
        final ViewGroup mRoot = (ViewGroup)LayoutInflater.from(this.mContext).inflate(R$layout.keyguard_bouncer, (ViewGroup)null);
        this.mRoot = mRoot;
        (this.mKeyguardView = (KeyguardHostView)mRoot.findViewById(R$id.keyguard_host_view)).setLockPatternUtils(this.mLockPatternUtils);
        this.mKeyguardView.setViewMediatorCallback(this.mCallback);
        final ViewGroup mContainer = this.mContainer;
        mContainer.addView((View)this.mRoot, mContainer.getChildCount());
        this.mStatusBarHeight = this.mRoot.getResources().getDimensionPixelOffset(R$dimen.status_bar_height);
        this.mRoot.setVisibility(4);
        final WindowInsets rootWindowInsets = this.mRoot.getRootWindowInsets();
        if (rootWindowInsets != null) {
            this.mRoot.dispatchApplyWindowInsets(rootWindowInsets);
        }
    }
    
    public boolean interceptMediaKey(final KeyEvent keyEvent) {
        this.ensureView();
        return this.mKeyguardView.interceptMediaKey(keyEvent);
    }
    
    public boolean isAnimatingAway() {
        return this.mIsAnimatingAway;
    }
    
    public boolean isFullscreenBouncer() {
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        boolean b = false;
        if (mKeyguardView != null) {
            final KeyguardSecurityModel.SecurityMode currentSecurityMode = mKeyguardView.getCurrentSecurityMode();
            if (currentSecurityMode != KeyguardSecurityModel.SecurityMode.SimPin) {
                b = b;
                if (currentSecurityMode != KeyguardSecurityModel.SecurityMode.SimPuk) {
                    return b;
                }
            }
            b = true;
        }
        return b;
    }
    
    public boolean isScrimmed() {
        return this.mIsScrimmed;
    }
    
    public boolean isSecure() {
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        return mKeyguardView == null || mKeyguardView.getSecurityMode() != KeyguardSecurityModel.SecurityMode.None;
    }
    
    public boolean isShowing() {
        if (!this.mShowingSoon) {
            final ViewGroup mRoot = this.mRoot;
            if (mRoot == null || mRoot.getVisibility() != 0) {
                return false;
            }
        }
        if (this.mExpansion == 0.0f && !this.isAnimatingAway()) {
            return true;
        }
        return false;
    }
    
    public boolean needsFullscreenBouncer() {
        final KeyguardSecurityModel.SecurityMode securityMode = Dependency.get(KeyguardSecurityModel.class).getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
        return securityMode == KeyguardSecurityModel.SecurityMode.SimPin || securityMode == KeyguardSecurityModel.SecurityMode.SimPuk;
    }
    
    public void notifyKeyguardAuthenticated(final boolean b) {
        this.ensureView();
        this.mKeyguardView.finish(b, KeyguardUpdateMonitor.getCurrentUser());
    }
    
    public void onScreenTurnedOff() {
        if (this.mKeyguardView != null) {
            final ViewGroup mRoot = this.mRoot;
            if (mRoot != null && mRoot.getVisibility() == 0) {
                this.mKeyguardView.onPause();
            }
        }
    }
    
    public void prepare() {
        final boolean b = this.mRoot != null;
        this.ensureView();
        if (b) {
            this.showPrimarySecurityScreen();
        }
        this.mBouncerPromptReason = this.mCallback.getBouncerPromptReason();
    }
    
    protected void removeView() {
        final ViewGroup mRoot = this.mRoot;
        if (mRoot != null) {
            final ViewParent parent = mRoot.getParent();
            final ViewGroup mContainer = this.mContainer;
            if (parent == mContainer) {
                mContainer.removeView((View)this.mRoot);
                this.mRoot = null;
            }
        }
    }
    
    public void setExpansion(final float mExpansion) {
        final float mExpansion2 = this.mExpansion;
        this.mExpansion = mExpansion;
        if (this.mKeyguardView != null && !this.mIsAnimatingAway) {
            this.mKeyguardView.setAlpha(MathUtils.constrain(MathUtils.map(0.95f, 1.0f, 1.0f, 0.0f, mExpansion), 0.0f, 1.0f));
            final KeyguardHostView mKeyguardView = this.mKeyguardView;
            mKeyguardView.setTranslationY(mKeyguardView.getHeight() * mExpansion);
        }
        final float n = fcmpl(mExpansion, 0.0f);
        if (n == 0 && mExpansion2 != 0.0f) {
            this.onFullyShown();
            this.mExpansionCallback.onFullyShown();
        }
        else if (mExpansion == 1.0f && mExpansion2 != 1.0f) {
            this.onFullyHidden();
            this.mExpansionCallback.onFullyHidden();
        }
        else if (n != 0 && mExpansion2 == 0.0f) {
            this.mExpansionCallback.onStartingToHide();
        }
    }
    
    public boolean shouldDismissOnMenuPressed() {
        return this.mKeyguardView.shouldEnableMenuKey();
    }
    
    public void show(final boolean b) {
        this.show(b, true);
    }
    
    public void show(final boolean b, final boolean mIsScrimmed) {
        final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (currentUser == 0 && UserManager.isSplitSystemUser()) {
            return;
        }
        this.ensureView();
        if (this.mIsScrimmed = mIsScrimmed) {
            this.setExpansion(0.0f);
        }
        if (b) {
            this.showPrimarySecurityScreen();
        }
        if (this.mRoot.getVisibility() != 0) {
            if (!this.mShowingSoon) {
                final int currentUser2 = KeyguardUpdateMonitor.getCurrentUser();
                final boolean splitSystemUser = UserManager.isSplitSystemUser();
                final int n = 0;
                final boolean b2 = splitSystemUser && currentUser2 == 0;
                int n2 = n;
                if (!b2) {
                    n2 = n;
                    if (currentUser2 == currentUser) {
                        n2 = 1;
                    }
                }
                if (n2 != 0 && this.mKeyguardView.dismiss(currentUser2)) {
                    return;
                }
                if (n2 == 0) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("User can't dismiss keyguard: ");
                    sb.append(currentUser2);
                    sb.append(" != ");
                    sb.append(currentUser);
                    Slog.w("KeyguardBouncer", sb.toString());
                }
                this.mShowingSoon = true;
                DejankUtils.removeCallbacks(this.mResetRunnable);
                if (this.mKeyguardStateController.isFaceAuthEnabled() && !this.needsFullscreenBouncer() && !this.mKeyguardUpdateMonitor.userNeedsStrongAuth() && !this.mKeyguardBypassController.getBypassEnabled()) {
                    this.mHandler.postDelayed(this.mShowRunnable, 1200L);
                }
                else {
                    DejankUtils.postAfterTraversal(this.mShowRunnable);
                }
                this.mCallback.onBouncerVisiblityChanged(true);
                this.mExpansionCallback.onStartingToShow();
            }
        }
    }
    
    public void showMessage(final String s, final ColorStateList list) {
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        if (mKeyguardView != null) {
            mKeyguardView.showMessage(s, list);
        }
        else {
            Log.w("KeyguardBouncer", "Trying to show message on empty bouncer");
        }
    }
    
    public void showPromptReason(final int n) {
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        if (mKeyguardView != null) {
            mKeyguardView.showPromptReason(n);
        }
        else {
            Log.w("KeyguardBouncer", "Trying to show prompt reason on empty bouncer");
        }
    }
    
    public void showWithDismissAction(final ActivityStarter.OnDismissAction onDismissAction, final Runnable runnable) {
        this.ensureView();
        this.mKeyguardView.setOnDismissAction(onDismissAction, runnable);
        this.show(false);
    }
    
    public void startPreHideAnimation(final Runnable runnable) {
        this.mIsAnimatingAway = true;
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        if (mKeyguardView != null) {
            mKeyguardView.startDisappearAnimation(runnable);
        }
        else if (runnable != null) {
            runnable.run();
        }
    }
    
    public boolean willDismissWithAction() {
        final KeyguardHostView mKeyguardView = this.mKeyguardView;
        return mKeyguardView != null && mKeyguardView.hasDismissActions();
    }
    
    public interface BouncerExpansionCallback
    {
        void onFullyHidden();
        
        void onFullyShown();
        
        void onStartingToHide();
        
        void onStartingToShow();
    }
}
