// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.window.WindowContainerToken;
import android.window.TaskOrganizer;
import android.view.SurfaceControl$Transaction;
import android.animation.ValueAnimator;
import java.util.function.Predicate;
import android.graphics.Rect;
import android.util.Slog;
import android.app.ActivityTaskManager;
import android.provider.Settings$Global;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.window.WindowOrganizer;
import android.window.WindowContainerTransaction;
import com.android.systemui.wm.DisplayLayout;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.res.Configuration;
import android.content.Context;
import com.android.systemui.TransactionPool;
import com.android.systemui.wm.SystemWindows;
import android.view.SurfaceSession;
import com.android.systemui.wm.DisplayChangeController;
import com.android.systemui.recents.Recents;
import dagger.Lazy;
import java.util.Optional;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.wm.DisplayImeController;
import android.os.Handler;
import java.util.function.Consumer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import com.android.systemui.wm.DisplayController;
import com.android.systemui.SystemUI;

public class Divider extends SystemUI implements DividerCallbacks, OnDisplaysChangedListener
{
    private boolean mAdjustedForIme;
    private DisplayController mDisplayController;
    private final DividerState mDividerState;
    private final ArrayList<WeakReference<Consumer<Boolean>>> mDockedStackExistsListeners;
    private ForcedResizableInfoActivityController mForcedResizableController;
    private Handler mHandler;
    private boolean mHomeStackResizable;
    private DisplayImeController mImeController;
    private final DividerImeController mImePositionProcessor;
    private KeyguardStateController mKeyguardStateController;
    private boolean mMinimized;
    private final Optional<Lazy<Recents>> mRecentsOptionalLazy;
    private SplitDisplayLayout mRotateSplitLayout;
    private DisplayChangeController.OnDisplayChangingListener mRotationController;
    private SplitDisplayLayout mSplitLayout;
    private SplitScreenTaskOrganizer mSplits;
    final SurfaceSession mSurfaceSession;
    private SystemWindows mSystemWindows;
    final TransactionPool mTransactionPool;
    private DividerView mView;
    private boolean mVisible;
    private DividerWindowManager mWindowManager;
    
    public Divider(final Context context, final Optional<Lazy<Recents>> mRecentsOptionalLazy, final DisplayController mDisplayController, final SystemWindows mSystemWindows, final DisplayImeController mImeController, final Handler mHandler, final KeyguardStateController mKeyguardStateController, final TransactionPool mTransactionPool) {
        super(context);
        this.mDividerState = new DividerState();
        this.mVisible = false;
        this.mMinimized = false;
        this.mAdjustedForIme = false;
        this.mHomeStackResizable = false;
        this.mSurfaceSession = new SurfaceSession();
        this.mDockedStackExistsListeners = new ArrayList<WeakReference<Consumer<Boolean>>>();
        this.mSplits = new SplitScreenTaskOrganizer(this);
        this.mRotationController = new _$$Lambda$Divider$0WHTGcDpweqOnqzkpJAQb7brKYs(this);
        this.mImePositionProcessor = new DividerImeController();
        this.mDisplayController = mDisplayController;
        this.mSystemWindows = mSystemWindows;
        this.mImeController = mImeController;
        this.mHandler = mHandler;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mRecentsOptionalLazy = mRecentsOptionalLazy;
        this.mForcedResizableController = new ForcedResizableInfoActivityController(context, this);
        this.mTransactionPool = mTransactionPool;
    }
    
    private void addDivider(final Configuration configuration) {
        final Context displayContext = this.mDisplayController.getDisplayContext(super.mContext.getDisplayId());
        this.mView = (DividerView)LayoutInflater.from(displayContext).inflate(R$layout.docked_stack_divider, (ViewGroup)null);
        final DisplayLayout displayLayout = this.mDisplayController.getDisplayLayout(super.mContext.getDisplayId());
        this.mView.injectDependencies(this.mWindowManager, this.mDividerState, (DividerView.DividerCallbacks)this, this.mSplits, this.mSplitLayout);
        final DividerView mView = this.mView;
        final boolean mVisible = this.mVisible;
        boolean b = false;
        int visibility;
        if (mVisible) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        mView.setVisibility(visibility);
        this.mView.setMinimizedDockStack(this.mMinimized, this.mHomeStackResizable);
        int n = displayContext.getResources().getDimensionPixelSize(17105175);
        if (configuration.orientation == 2) {
            b = true;
        }
        int width;
        if (b) {
            width = n;
        }
        else {
            width = displayLayout.width();
        }
        if (b) {
            n = displayLayout.height();
        }
        this.mWindowManager.add((View)this.mView, width, n, super.mContext.getDisplayId());
    }
    
    private void removeDivider() {
        final DividerView mView = this.mView;
        if (mView != null) {
            mView.onDividerRemoved();
        }
        this.mWindowManager.remove();
    }
    
    private void setHomeMinimized(final boolean mMinimized, final boolean mHomeStackResizable) {
        final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        final boolean mMinimized2 = this.mMinimized;
        boolean b = true;
        int displayId = 0;
        final boolean b2 = mMinimized2 != mMinimized;
        if (b2) {
            this.mMinimized = mMinimized;
        }
        windowContainerTransaction.setFocusable(this.mSplits.mPrimary.token, this.mMinimized ^ true);
        if (this.mHomeStackResizable == mHomeStackResizable) {
            b = false;
        }
        if (b) {
            this.mHomeStackResizable = mHomeStackResizable;
            if (this.inSplitMode()) {
                WindowManagerProxy.applyHomeTasksMinimized(this.mSplitLayout, this.mSplits.mSecondary.token, windowContainerTransaction);
            }
        }
        final DividerView mView = this.mView;
        if (mView != null) {
            if (mView.getDisplay() != null) {
                displayId = this.mView.getDisplay().getDisplayId();
            }
            if (this.mMinimized) {
                this.mImePositionProcessor.pause(displayId);
            }
            if (b2 || b) {
                this.mView.setMinimizedDockStack(mMinimized, this.getAnimDuration(), mHomeStackResizable);
            }
            if (!this.mMinimized) {
                this.mImePositionProcessor.resume(displayId);
            }
        }
        this.updateTouchable();
        WindowOrganizer.applyTransaction(windowContainerTransaction);
    }
    
    private void update(final Configuration configuration) {
        this.removeDivider();
        this.addDivider(configuration);
        if (this.mMinimized) {
            final DividerView mView = this.mView;
            if (mView != null) {
                mView.setMinimizedDockStack(true, this.mHomeStackResizable);
                this.updateTouchable();
            }
        }
    }
    
    private void updateTouchable() {
        this.mWindowManager.setTouchable((this.mHomeStackResizable || !this.mMinimized) && !this.mAdjustedForIme);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("  mVisible=");
        printWriter.println(this.mVisible);
        printWriter.print("  mMinimized=");
        printWriter.println(this.mMinimized);
        printWriter.print("  mAdjustedForIme=");
        printWriter.println(this.mAdjustedForIme);
    }
    
    void ensureMinimizedSplit() {
        this.setHomeMinimized(true, this.mSplits.mSecondary.isResizable());
        if (!this.inSplitMode()) {
            this.updateVisibility(true);
        }
    }
    
    void ensureNormalSplit() {
        this.setHomeMinimized(false, this.mHomeStackResizable);
        if (!this.inSplitMode()) {
            this.updateVisibility(true);
        }
    }
    
    long getAnimDuration() {
        return (long)(Settings$Global.getFloat(super.mContext.getContentResolver(), "transition_animation_scale", super.mContext.getResources().getFloat(17105052)) * 336.0f);
    }
    
    Handler getHandler() {
        return this.mHandler;
    }
    
    public DividerView getView() {
        return this.mView;
    }
    
    @Override
    public void growRecents() {
        this.mRecentsOptionalLazy.ifPresent((Consumer<? super Lazy<Recents>>)_$$Lambda$Divider$khi_jE4xcxq3HoOT3yA0PC_T5IE.INSTANCE);
    }
    
    public boolean inSplitMode() {
        final DividerView mView = this.mView;
        return mView != null && mView.getVisibility() == 0;
    }
    
    public boolean isHomeStackResizable() {
        return this.mHomeStackResizable;
    }
    
    public boolean isMinimized() {
        return this.mMinimized;
    }
    
    public void onAppTransitionFinished() {
        if (this.mView == null) {
            return;
        }
        this.mForcedResizableController.onAppTransitionFinished();
    }
    
    @Override
    public void onDisplayAdded(final int n) {
        if (n != 0) {
            return;
        }
        this.mSplitLayout = new SplitDisplayLayout(this.mDisplayController.getDisplayContext(n), this.mDisplayController.getDisplayLayout(n), this.mSplits);
        this.mImeController.addPositionProcessor((DisplayImeController.ImePositionProcessor)this.mImePositionProcessor);
        this.mDisplayController.addDisplayChangingController(this.mRotationController);
        if (!ActivityTaskManager.supportsSplitScreenMultiWindow(super.mContext)) {
            this.removeDivider();
            return;
        }
        try {
            this.mSplits.init(this.mSurfaceSession);
            final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            this.mSplitLayout.resizeSplits(this.mSplitLayout.getSnapAlgorithm().getMiddleTarget().position, windowContainerTransaction);
            WindowOrganizer.applyTransaction(windowContainerTransaction);
            this.update(this.mDisplayController.getDisplayContext(n).getResources().getConfiguration());
        }
        catch (Exception ex) {
            Slog.e("Divider", "Failed to register docked stack listener", (Throwable)ex);
            this.removeDivider();
        }
    }
    
    @Override
    public void onDisplayConfigurationChanged(int position, final Configuration configuration) {
        if (position == 0) {
            if (this.mSplits.isSplitScreenSupported()) {
                final SplitDisplayLayout mSplitLayout = new SplitDisplayLayout(this.mDisplayController.getDisplayContext(position), this.mDisplayController.getDisplayLayout(position), this.mSplits);
                this.mSplitLayout = mSplitLayout;
                if (this.mRotateSplitLayout == null) {
                    position = mSplitLayout.getSnapAlgorithm().getMiddleTarget().position;
                    final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
                    this.mSplitLayout.resizeSplits(position, windowContainerTransaction);
                    WindowOrganizer.applyTransaction(windowContainerTransaction);
                }
                else if (mSplitLayout.mDisplayLayout.rotation() == this.mRotateSplitLayout.mDisplayLayout.rotation()) {
                    this.mSplitLayout.mPrimary = new Rect(this.mRotateSplitLayout.mPrimary);
                    this.mSplitLayout.mSecondary = new Rect(this.mRotateSplitLayout.mSecondary);
                    this.mRotateSplitLayout = null;
                }
                this.update(configuration);
            }
        }
    }
    
    public void onDockedFirstAnimationFrame() {
        final DividerView mView = this.mView;
        if (mView != null) {
            mView.onDockedFirstAnimationFrame();
        }
    }
    
    public void onDockedTopTask() {
        final DividerView mView = this.mView;
        if (mView != null) {
            mView.onDockedTopTask();
        }
    }
    
    @Override
    public void onDraggingEnd() {
        this.mForcedResizableController.onDraggingEnd();
    }
    
    @Override
    public void onDraggingStart() {
        this.mForcedResizableController.onDraggingStart();
    }
    
    public void onRecentsDrawn() {
        final DividerView mView = this.mView;
        if (mView != null) {
            mView.onRecentsDrawn();
        }
    }
    
    public void onUndockingTask() {
        final DividerView mView = this.mView;
        if (mView != null) {
            mView.onUndockingTask();
        }
    }
    
    public void registerInSplitScreenListener(final Consumer<Boolean> referent) {
        referent.accept(this.inSplitMode());
        synchronized (this.mDockedStackExistsListeners) {
            this.mDockedStackExistsListeners.add(new WeakReference<Consumer<Boolean>>(referent));
        }
    }
    
    void setAdjustedForIme(final boolean mAdjustedForIme) {
        if (this.mAdjustedForIme == mAdjustedForIme) {
            return;
        }
        this.mAdjustedForIme = mAdjustedForIme;
        this.updateTouchable();
    }
    
    public void setMinimized(final boolean b) {
        this.mHandler.post((Runnable)new _$$Lambda$Divider$GLpBX22_HsHK0Y2WrbtOJyYYvDU(this, b));
    }
    
    @Override
    public void start() {
        this.mWindowManager = new DividerWindowManager(this.mSystemWindows);
        this.mDisplayController.addDisplayWindowListener((DisplayController.OnDisplaysChangedListener)this);
        this.mKeyguardStateController.addCallback((KeyguardStateController.Callback)new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardFadingAwayChanged() {
            }
            
            @Override
            public void onKeyguardShowingChanged() {
                if (Divider.this.inSplitMode()) {
                    if (Divider.this.mView != null) {
                        Divider.this.mView.setHidden(Divider.this.mKeyguardStateController.isShowing());
                    }
                }
            }
            
            @Override
            public void onUnlockedChanged() {
            }
        });
    }
    
    void startEnterSplit() {
        this.mHomeStackResizable = WindowManagerProxy.applyEnterSplit(this.mSplits, this.mSplitLayout);
    }
    
    void updateVisibility(final boolean mVisible) {
        if (this.mVisible != mVisible) {
            this.mVisible = mVisible;
            final DividerView mView = this.mView;
            int visibility;
            if (mVisible) {
                visibility = 0;
            }
            else {
                visibility = 4;
            }
            mView.setVisibility(visibility);
            if (mVisible) {
                this.mView.enterSplitMode(this.mHomeStackResizable);
                this.mView.setMinimizedDockStack(this.mMinimized, this.mHomeStackResizable);
            }
            else {
                this.mView.exitSplitMode();
                this.mView.setMinimizedDockStack(false, this.mHomeStackResizable);
            }
            synchronized (this.mDockedStackExistsListeners) {
                this.mDockedStackExistsListeners.removeIf(new _$$Lambda$Divider$tBeiPAxiGmIEGdiyhUHw_hirJpI(mVisible));
            }
        }
    }
    
    private class DividerImeController implements ImePositionProcessor
    {
        private boolean mAdjusted;
        private ValueAnimator mAnimation;
        private int mHiddenTop;
        private boolean mImeWasShown;
        private int mLastAdjustTop;
        private float mLastPrimaryDim;
        private float mLastSecondaryDim;
        private boolean mPaused;
        private boolean mPausedTargetAdjusted;
        private boolean mSecondaryHasFocus;
        private int mShownTop;
        private boolean mTargetAdjusted;
        private float mTargetPrimaryDim;
        private float mTargetSecondaryDim;
        private boolean mTargetShown;
        
        private DividerImeController() {
            this.mHiddenTop = 0;
            this.mShownTop = 0;
            this.mTargetAdjusted = false;
            this.mTargetShown = false;
            this.mTargetPrimaryDim = 0.0f;
            this.mTargetSecondaryDim = 0.0f;
            this.mSecondaryHasFocus = false;
            this.mLastPrimaryDim = 0.0f;
            this.mLastSecondaryDim = 0.0f;
            this.mLastAdjustTop = -1;
            this.mImeWasShown = false;
            this.mAdjusted = false;
            this.mAnimation = null;
            this.mPaused = true;
            this.mPausedTargetAdjusted = false;
        }
        
        private boolean getSecondaryHasFocus(final int n) {
            final WindowContainerToken imeTarget = TaskOrganizer.getImeTarget(n);
            return imeTarget != null && imeTarget.asBinder() == Divider.this.mSplits.mSecondary.token.asBinder();
        }
        
        private void onEnd(final boolean b, final SurfaceControl$Transaction surfaceControl$Transaction) {
            if (!b) {
                this.onProgress(1.0f, surfaceControl$Transaction);
                final boolean mTargetAdjusted = this.mTargetAdjusted;
                this.mAdjusted = mTargetAdjusted;
                this.mImeWasShown = this.mTargetShown;
                int mLastAdjustTop;
                if (mTargetAdjusted) {
                    mLastAdjustTop = this.mShownTop;
                }
                else {
                    mLastAdjustTop = this.mHiddenTop;
                }
                this.mLastAdjustTop = mLastAdjustTop;
                this.mLastPrimaryDim = this.mTargetPrimaryDim;
                this.mLastSecondaryDim = this.mTargetSecondaryDim;
            }
        }
        
        private void onProgress(final float n, final SurfaceControl$Transaction surfaceControl$Transaction) {
            final boolean mTargetAdjusted = this.mTargetAdjusted;
            if (mTargetAdjusted != this.mAdjusted && !this.mPaused) {
                float n2;
                if (mTargetAdjusted) {
                    n2 = n;
                }
                else {
                    n2 = 1.0f - n;
                }
                this.mLastAdjustTop = (int)(this.mShownTop * n2 + (1.0f - n2) * this.mHiddenTop);
                Divider.this.mSplitLayout.updateAdjustedBounds(this.mLastAdjustTop, this.mHiddenTop, this.mShownTop);
                Divider.this.mView.resizeSplitSurfaces(surfaceControl$Transaction, Divider.this.mSplitLayout.mAdjustedPrimary, Divider.this.mSplitLayout.mAdjustedSecondary);
            }
            final float n3 = 1.0f - n;
            Divider.this.mView.setResizeDimLayer(surfaceControl$Transaction, true, this.mLastPrimaryDim * n3 + this.mTargetPrimaryDim * n);
            Divider.this.mView.setResizeDimLayer(surfaceControl$Transaction, false, this.mLastSecondaryDim * n3 + n * this.mTargetSecondaryDim);
        }
        
        private void startAsyncAnimation() {
            final ValueAnimator mAnimation = this.mAnimation;
            if (mAnimation != null) {
                mAnimation.cancel();
            }
            (this.mAnimation = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).setDuration(275L);
            final boolean mTargetAdjusted = this.mTargetAdjusted;
            if (mTargetAdjusted != this.mAdjusted) {
                final float n = (float)this.mLastAdjustTop;
                final int mHiddenTop = this.mHiddenTop;
                float currentFraction = (n - mHiddenTop) / (this.mShownTop - mHiddenTop);
                if (!mTargetAdjusted) {
                    currentFraction = 1.0f - currentFraction;
                }
                this.mAnimation.setCurrentFraction(currentFraction);
            }
            this.mAnimation.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$Divider$DividerImeController$_93oTXqVNNBj24IpV3fxpo7bU4w(this));
            this.mAnimation.setInterpolator((TimeInterpolator)DisplayImeController.INTERPOLATOR);
            this.mAnimation.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                private boolean mCancel = false;
                
                public void onAnimationCancel(final Animator animator) {
                    this.mCancel = true;
                }
                
                public void onAnimationEnd(final Animator animator) {
                    final SurfaceControl$Transaction acquire = Divider.this.mTransactionPool.acquire();
                    DividerImeController.this.onEnd(this.mCancel, acquire);
                    acquire.apply();
                    Divider.this.mTransactionPool.release(acquire);
                    DividerImeController.this.mAnimation = null;
                }
            });
            this.mAnimation.start();
        }
        
        private void updateDimTargets() {
            final boolean b = Divider.this.mView.isHidden() ^ true;
            final boolean mSecondaryHasFocus = this.mSecondaryHasFocus;
            final float n = 0.3f;
            float mTargetPrimaryDim;
            if (mSecondaryHasFocus && this.mTargetShown && b) {
                mTargetPrimaryDim = 0.3f;
            }
            else {
                mTargetPrimaryDim = 0.0f;
            }
            this.mTargetPrimaryDim = mTargetPrimaryDim;
            float mTargetSecondaryDim;
            if (!this.mSecondaryHasFocus && this.mTargetShown && b) {
                mTargetSecondaryDim = n;
            }
            else {
                mTargetSecondaryDim = 0.0f;
            }
            this.mTargetSecondaryDim = mTargetSecondaryDim;
        }
        
        private void updateImeAdjustState() {
            final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            final boolean mTargetAdjusted = this.mTargetAdjusted;
            final boolean b = false;
            if (mTargetAdjusted) {
                final SplitDisplayLayout access$200 = Divider.this.mSplitLayout;
                final int mShownTop = this.mShownTop;
                access$200.updateAdjustedBounds(mShownTop, this.mHiddenTop, mShownTop);
                windowContainerTransaction.setBounds(Divider.this.mSplits.mSecondary.token, Divider.this.mSplitLayout.mAdjustedSecondary);
                final Rect rect = new Rect(Divider.this.mSplits.mSecondary.configuration.windowConfiguration.getAppBounds());
                rect.offset(0, Divider.this.mSplitLayout.mAdjustedSecondary.top - Divider.this.mSplitLayout.mSecondary.top);
                windowContainerTransaction.setAppBounds(Divider.this.mSplits.mSecondary.token, rect);
                windowContainerTransaction.setScreenSizeDp(Divider.this.mSplits.mSecondary.token, Divider.this.mSplits.mSecondary.configuration.screenWidthDp, Divider.this.mSplits.mSecondary.configuration.screenHeightDp);
            }
            else {
                windowContainerTransaction.setBounds(Divider.this.mSplits.mSecondary.token, Divider.this.mSplitLayout.mSecondary);
                windowContainerTransaction.setAppBounds(Divider.this.mSplits.mSecondary.token, (Rect)null);
                windowContainerTransaction.setScreenSizeDp(Divider.this.mSplits.mSecondary.token, 0, 0);
            }
            WindowOrganizer.applyTransaction(windowContainerTransaction);
            if (!this.mPaused) {
                final DividerView access$201 = Divider.this.mView;
                final boolean mTargetShown = this.mTargetShown;
                long n;
                if (mTargetShown) {
                    n = 275L;
                }
                else {
                    n = 340L;
                }
                access$201.setAdjustedForIme(mTargetShown, n);
            }
            final Divider this$0 = Divider.this;
            boolean adjustedForIme = b;
            if (this.mTargetShown) {
                adjustedForIme = b;
                if (!this.mPaused) {
                    adjustedForIme = true;
                }
            }
            this$0.setAdjustedForIme(adjustedForIme);
        }
        
        @Override
        public void onImeEndPositioning(final int n, final boolean b, final SurfaceControl$Transaction surfaceControl$Transaction) {
            if (this.mAnimation == null && Divider.this.inSplitMode()) {
                if (!this.mPaused) {
                    this.onEnd(b, surfaceControl$Transaction);
                }
            }
        }
        
        @Override
        public void onImePositionChanged(int mHiddenTop, final int n, final SurfaceControl$Transaction surfaceControl$Transaction) {
            if (this.mAnimation == null && Divider.this.inSplitMode()) {
                if (!this.mPaused) {
                    final float n2 = (float)n;
                    mHiddenTop = this.mHiddenTop;
                    float n3 = (n2 - mHiddenTop) / (this.mShownTop - mHiddenTop);
                    if (!this.mTargetShown) {
                        n3 = 1.0f - n3;
                    }
                    this.onProgress(n3, surfaceControl$Transaction);
                }
            }
        }
        
        @Override
        public void onImeStartPositioning(int mLastAdjustTop, int n, final int mShownTop, final boolean mTargetShown, final SurfaceControl$Transaction surfaceControl$Transaction) {
            if (!Divider.this.inSplitMode()) {
                return;
            }
            final boolean hidden = Divider.this.mView.isHidden();
            boolean b = true;
            final boolean b2 = hidden ^ true;
            final boolean secondaryHasFocus = this.getSecondaryHasFocus(mLastAdjustTop);
            this.mSecondaryHasFocus = secondaryHasFocus;
            if (!b2 || !mTargetShown || !secondaryHasFocus || Divider.this.mSplitLayout.mDisplayLayout.isLandscape()) {
                b = false;
            }
            this.mHiddenTop = n;
            this.mShownTop = mShownTop;
            this.mTargetShown = mTargetShown;
            mLastAdjustTop = this.mLastAdjustTop;
            if (mLastAdjustTop < 0) {
                if (!mTargetShown) {
                    n = mShownTop;
                }
                this.mLastAdjustTop = n;
            }
            else if (this.mTargetAdjusted != b && b == this.mAdjusted) {
                if (mTargetShown) {
                    n = mShownTop;
                }
                if (mLastAdjustTop != n) {
                    this.mAdjusted = this.mTargetAdjusted;
                }
            }
            if (this.mPaused) {
                this.mPausedTargetAdjusted = b;
                return;
            }
            this.mTargetAdjusted = b;
            this.updateDimTargets();
            if (this.mAnimation != null || (this.mImeWasShown && mTargetShown && this.mTargetAdjusted != this.mAdjusted)) {
                this.startAsyncAnimation();
            }
            if (b2) {
                this.updateImeAdjustState();
            }
        }
        
        public void pause(final int n) {
            Divider.this.mHandler.post((Runnable)new _$$Lambda$Divider$DividerImeController$6L0YPEcAbgIW0zbmYR8YZHTFot8(this));
        }
        
        public void resume(final int n) {
            Divider.this.mHandler.post((Runnable)new _$$Lambda$Divider$DividerImeController$sCwXcQ2P0r6yAMPL_xlyyKOzOQE(this));
        }
    }
}
