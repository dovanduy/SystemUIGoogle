// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import java.util.Objects;
import android.graphics.PointF;
import android.util.Size;
import android.content.ComponentName;
import java.io.PrintWriter;
import android.os.RemoteException;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.IAccessibilityInteractionConnection;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import android.view.MotionEvent;
import android.view.InputEvent;
import android.view.WindowManager$LayoutParams;
import android.graphics.Point;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.Pair;
import com.android.internal.os.logging.MetricsLoggerWrapper;
import android.widget.FrameLayout;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import android.view.ViewConfiguration;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.shared.system.InputConsumerController;
import android.app.IActivityTaskManager;
import android.view.WindowManager;
import android.view.ViewGroup;
import com.android.systemui.util.DismissCircleView;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.pip.PipBoundsHandler;
import android.view.IPinnedStackController;
import android.view.View;
import com.android.systemui.util.animation.PhysicsAnimator;
import com.android.systemui.util.magnetictarget.MagnetizedObject;
import android.os.Handler;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.internal.annotations.VisibleForTesting;
import android.graphics.Rect;
import android.content.Context;
import android.app.IActivityManager;
import android.view.accessibility.AccessibilityManager;

public class PipTouchHandler
{
    private final AccessibilityManager mAccessibilityManager;
    private final IActivityManager mActivityManager;
    private PipAccessibilityInteractionConnection mConnection;
    private final Context mContext;
    private int mDeferResizeToNormalBoundsUntilRotation;
    private int mDismissAreaHeight;
    private int mDisplayRotation;
    private final boolean mEnableDismissDragToEdge;
    private final boolean mEnableResize;
    private Rect mExpandedBounds;
    @VisibleForTesting
    Rect mExpandedMovementBounds;
    private int mExpandedShortestEdgeSize;
    private final FlingAnimationUtils mFlingAnimationUtils;
    private final FloatingContentCoordinator mFloatingContentCoordinator;
    private PipTouchGesture mGesture;
    private Handler mHandler;
    private int mImeHeight;
    private int mImeOffset;
    private Rect mInsetBounds;
    private boolean mIsImeShowing;
    private boolean mIsShelfShowing;
    private MagnetizedObject.MagneticTarget mMagneticTarget;
    private PhysicsAnimator<View> mMagneticTargetAnimator;
    private MagnetizedObject<Rect> mMagnetizedPip;
    private final PipMenuActivityController mMenuController;
    private int mMenuState;
    private PipMotionHelper mMotionHelper;
    private Rect mMovementBounds;
    private int mMovementBoundsExtraOffsets;
    private boolean mMovementWithinDismiss;
    private Rect mNormalBounds;
    @VisibleForTesting
    Rect mNormalMovementBounds;
    private IPinnedStackController mPinnedStackController;
    private final PipBoundsHandler mPipBoundsHandler;
    private PipResizeGestureHandler mPipResizeGestureHandler;
    @VisibleForTesting
    Rect mResizedBounds;
    private float mSavedSnapFraction;
    private boolean mSendingHoverAccessibilityEvents;
    private int mShelfHeight;
    private boolean mShowPipMenuOnAnimationEnd;
    private Runnable mShowTargetAction;
    private final PipSnapAlgorithm mSnapAlgorithm;
    private final PhysicsAnimator.SpringConfig mTargetSpringConfig;
    private DismissCircleView mTargetView;
    private ViewGroup mTargetViewContainer;
    private final Rect mTmpBounds;
    private final PipTouchState mTouchState;
    private final WindowManager mWindowManager;
    
    @SuppressLint({ "InflateParams" })
    public PipTouchHandler(final Context mContext, final IActivityManager mActivityManager, final IActivityTaskManager activityTaskManager, final PipMenuActivityController mMenuController, final InputConsumerController inputConsumerController, final PipBoundsHandler mPipBoundsHandler, final PipTaskOrganizer pipTaskOrganizer, final FloatingContentCoordinator mFloatingContentCoordinator, final DeviceConfigProxy deviceConfigProxy, final PipSnapAlgorithm mSnapAlgorithm) {
        this.mShowPipMenuOnAnimationEnd = false;
        this.mTargetSpringConfig = new PhysicsAnimator.SpringConfig(1500.0f, 1.0f);
        this.mMovementBounds = new Rect();
        this.mResizedBounds = new Rect();
        this.mInsetBounds = new Rect();
        this.mNormalBounds = new Rect();
        this.mNormalMovementBounds = new Rect();
        this.mExpandedBounds = new Rect();
        this.mExpandedMovementBounds = new Rect();
        this.mDeferResizeToNormalBoundsUntilRotation = -1;
        this.mShowTargetAction = new _$$Lambda$PipTouchHandler$bnz9PC9JAAj_rxnZq96LLBoKnqw(this);
        this.mHandler = new Handler();
        this.mMenuState = 0;
        this.mSavedSnapFraction = -1.0f;
        this.mTmpBounds = new Rect();
        this.mContext = mContext;
        this.mActivityManager = mActivityManager;
        this.mAccessibilityManager = (AccessibilityManager)mContext.getSystemService((Class)AccessibilityManager.class);
        this.mWindowManager = (WindowManager)this.mContext.getSystemService("window");
        (this.mMenuController = mMenuController).addListener((PipMenuActivityController.Listener)new PipMenuListener());
        this.mSnapAlgorithm = mSnapAlgorithm;
        this.mFlingAnimationUtils = new FlingAnimationUtils(mContext.getResources().getDisplayMetrics(), 2.5f);
        this.mGesture = new DefaultPipTouchGesture();
        final PipMotionHelper mMotionHelper = new PipMotionHelper(this.mContext, activityTaskManager, pipTaskOrganizer, this.mMenuController, this.mSnapAlgorithm, this.mFlingAnimationUtils, mFloatingContentCoordinator);
        this.mMotionHelper = mMotionHelper;
        this.mPipResizeGestureHandler = new PipResizeGestureHandler(mContext, mPipBoundsHandler, this, mMotionHelper, deviceConfigProxy, pipTaskOrganizer);
        this.mTouchState = new PipTouchState(ViewConfiguration.get(mContext), this.mHandler, new _$$Lambda$PipTouchHandler$Uq5M9Md512Sfgd22VAeFpot25E0(this));
        final Resources resources = mContext.getResources();
        this.mExpandedShortestEdgeSize = resources.getDimensionPixelSize(R$dimen.pip_expanded_shortest_edge_size);
        this.mImeOffset = resources.getDimensionPixelSize(R$dimen.pip_ime_offset);
        this.mDismissAreaHeight = resources.getDimensionPixelSize(R$dimen.floating_dismiss_gradient_height);
        this.mEnableDismissDragToEdge = resources.getBoolean(R$bool.config_pipEnableDismissDragToEdge);
        this.mEnableResize = resources.getBoolean(R$bool.config_pipEnableResizeForMenu);
        inputConsumerController.setInputListener((InputConsumerController.InputListener)new _$$Lambda$PipTouchHandler$A78OVgVs8H_2SG6WUxzMSclOdX0(this));
        inputConsumerController.setRegistrationListener((InputConsumerController.RegistrationListener)new _$$Lambda$PipTouchHandler$NVpciZTELe_GnxXPZeY5rYMmqJQ(this));
        this.mPipBoundsHandler = mPipBoundsHandler;
        this.mFloatingContentCoordinator = mFloatingContentCoordinator;
        this.mConnection = new PipAccessibilityInteractionConnection(this.mMotionHelper, (PipAccessibilityInteractionConnection.AccessibilityCallbacks)new _$$Lambda$PipTouchHandler$1nY3kLe318Fm3UtIAbDmSK80h7w(this), this.mHandler);
        final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.dismiss_circle_size);
        this.mTargetView = new DismissCircleView(mContext);
        final FrameLayout$LayoutParams layoutParams = new FrameLayout$LayoutParams(dimensionPixelSize, dimensionPixelSize);
        layoutParams.gravity = 17;
        this.mTargetView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        (this.mTargetViewContainer = (ViewGroup)new FrameLayout(mContext)).setClipChildren(false);
        this.mTargetViewContainer.addView((View)this.mTargetView);
        final MagnetizedObject<Rect> magnetizedPip = this.mMotionHelper.getMagnetizedPip();
        this.mMagnetizedPip = magnetizedPip;
        this.mMagneticTarget = magnetizedPip.addTarget((View)this.mTargetView, 0);
        this.mMagnetizedPip.setPhysicsAnimatorUpdateListener(this.mMotionHelper.mResizePipUpdateListener);
        this.mMagnetizedPip.setMagnetListener((MagnetizedObject.MagnetListener)new MagnetizedObject.MagnetListener() {
            @Override
            public void onReleasedInTarget(final MagneticTarget magneticTarget) {
                PipTouchHandler.this.mHandler.post((Runnable)new _$$Lambda$PipTouchHandler$1$zJ5cwW9_qQ4umng_tgHurxl3qHI(this));
                MetricsLoggerWrapper.logPictureInPictureDismissByDrag(PipTouchHandler.this.mContext, (Pair)PipUtils.getTopPipActivity(PipTouchHandler.this.mContext, PipTouchHandler.this.mActivityManager));
            }
            
            @Override
            public void onStuckToTarget(final MagneticTarget magneticTarget) {
                PipTouchHandler.this.mMotionHelper.prepareForAnimation();
                PipTouchHandler.this.showDismissTargetMaybe();
            }
            
            @Override
            public void onUnstuckFromTarget(final MagneticTarget magneticTarget, final float n, final float n2, final boolean b) {
                if (b) {
                    PipTouchHandler.this.mMotionHelper.flingToSnapTarget(n, n2, null, null);
                    PipTouchHandler.this.hideDismissTarget();
                }
                else {
                    PipTouchHandler.this.mMotionHelper.setSpringingToTouch(true);
                }
            }
        });
        this.mMagneticTargetAnimator = PhysicsAnimator.getInstance((View)this.mTargetView);
    }
    
    private void cleanUpDismissTarget() {
        this.mHandler.removeCallbacks(this.mShowTargetAction);
        if (this.mTargetViewContainer.isAttachedToWindow()) {
            this.mWindowManager.removeView((View)this.mTargetViewContainer);
        }
    }
    
    private void createDismissTargetMaybe() {
        if (!this.mTargetViewContainer.isAttachedToWindow()) {
            this.mHandler.removeCallbacks(this.mShowTargetAction);
            this.mMagneticTargetAnimator.cancel();
            final Point point = new Point();
            this.mWindowManager.getDefaultDisplay().getRealSize(point);
            final int mDismissAreaHeight = this.mDismissAreaHeight;
            final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-1, mDismissAreaHeight, 0, point.y - mDismissAreaHeight, 2024, 280, -3);
            windowManager$LayoutParams.setTitle((CharSequence)"pip-dismiss-overlay");
            windowManager$LayoutParams.privateFlags |= 0x10;
            windowManager$LayoutParams.setFitInsetsTypes(0);
            this.mTargetViewContainer.setVisibility(4);
            this.mWindowManager.addView((View)this.mTargetViewContainer, (ViewGroup$LayoutParams)windowManager$LayoutParams);
        }
    }
    
    private boolean handleTouchEvent(final InputEvent inputEvent) {
        if (!(inputEvent instanceof MotionEvent)) {
            return true;
        }
        if (this.mPinnedStackController == null) {
            return true;
        }
        final MotionEvent motionEvent = (MotionEvent)inputEvent;
        if (this.mMagnetizedPip.maybeConsumeMotionEvent(motionEvent)) {
            if (motionEvent.getAction() == 0) {
                this.mTouchState.onTouchEvent(motionEvent);
            }
            this.mTouchState.addMovementToVelocityTracker(motionEvent);
            return true;
        }
        this.mTouchState.onTouchEvent(motionEvent);
        final int mMenuState = this.mMenuState;
        final boolean b = false;
        final boolean b2 = mMenuState != 0;
        final int action = motionEvent.getAction();
        int n = 0;
        Label_0345: {
            if (action != 0) {
                if (action != 1) {
                    if (action != 2) {
                        if (action != 3) {
                            if (action != 7) {
                                if (action != 9) {
                                    if (action != 10) {
                                        n = (b2 ? 1 : 0);
                                        break Label_0345;
                                    }
                                    this.mMenuController.hideMenu();
                                    if ((n = (b2 ? 1 : 0)) != 0) {
                                        break Label_0345;
                                    }
                                    n = (b2 ? 1 : 0);
                                    if (this.mSendingHoverAccessibilityEvents) {
                                        this.sendAccessibilityHoverEvent(256);
                                        this.mSendingHoverAccessibilityEvents = false;
                                        n = (b2 ? 1 : 0);
                                    }
                                    break Label_0345;
                                }
                                else {
                                    this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, false, false);
                                }
                            }
                            if ((n = (b2 ? 1 : 0)) != 0) {
                                break Label_0345;
                            }
                            n = (b2 ? 1 : 0);
                            if (!this.mSendingHoverAccessibilityEvents) {
                                this.sendAccessibilityHoverEvent(128);
                                this.mSendingHoverAccessibilityEvents = true;
                                n = (b2 ? 1 : 0);
                            }
                            break Label_0345;
                        }
                    }
                    else {
                        if (this.mGesture.onMove(this.mTouchState)) {
                            n = (b2 ? 1 : 0);
                            break Label_0345;
                        }
                        n = ((this.mTouchState.isDragging() ^ true) ? 1 : 0);
                        break Label_0345;
                    }
                }
                else {
                    this.updateMovementBounds();
                    if (this.mGesture.onUp(this.mTouchState)) {
                        n = (b2 ? 1 : 0);
                        break Label_0345;
                    }
                }
                int n2 = b ? 1 : 0;
                if (!this.mTouchState.startedDragging()) {
                    n2 = (b ? 1 : 0);
                    if (!this.mTouchState.isDragging()) {
                        n2 = 1;
                    }
                }
                this.mTouchState.reset();
                n = n2;
            }
            else {
                this.mMotionHelper.synchronizePinnedStackBoundsForTouchGesture();
                this.mGesture.onDown(this.mTouchState);
                n = (b2 ? 1 : 0);
            }
        }
        if (n != 0) {
            final MotionEvent obtain = MotionEvent.obtain(motionEvent);
            if (this.mTouchState.startedDragging()) {
                obtain.setAction(3);
                this.mMenuController.pokeMenu();
            }
            this.mMenuController.handlePointerEvent(obtain);
        }
        return true;
    }
    
    private void hideDismissTarget() {
        this.mHandler.removeCallbacks(this.mShowTargetAction);
        final PhysicsAnimator<View> mMagneticTargetAnimator = this.mMagneticTargetAnimator;
        mMagneticTargetAnimator.spring(DynamicAnimation.TRANSLATION_Y, (float)this.mTargetViewContainer.getHeight(), this.mTargetSpringConfig);
        mMagneticTargetAnimator.withEndActions(new _$$Lambda$PipTouchHandler$yL_GUvHePkpkC6nKdxGopgiyu_g(this));
        mMagneticTargetAnimator.start();
    }
    
    private void onAccessibilityShowMenu() {
        this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, true, this.willResizeMenu());
    }
    
    private void onRegistrationChanged(final boolean b) {
        final AccessibilityManager mAccessibilityManager = this.mAccessibilityManager;
        Object mConnection;
        if (b) {
            mConnection = this.mConnection;
        }
        else {
            mConnection = null;
        }
        mAccessibilityManager.setPictureInPictureActionReplacingConnection((IAccessibilityInteractionConnection)mConnection);
        if (!b && this.mTouchState.isUserInteracting()) {
            this.cleanUpDismissTarget();
        }
    }
    
    private void sendAccessibilityHoverEvent(final int n) {
        if (!this.mAccessibilityManager.isEnabled()) {
            return;
        }
        final AccessibilityEvent obtain = AccessibilityEvent.obtain(n);
        obtain.setImportantForAccessibility(true);
        obtain.setSourceNodeId(AccessibilityNodeInfo.ROOT_NODE_ID);
        obtain.setWindowId(-3);
        this.mAccessibilityManager.sendAccessibilityEvent(obtain);
    }
    
    private void setMenuState(final int mMenuState, final boolean b) {
        if (this.mMenuState == mMenuState && !b) {
            return;
        }
        final boolean b2 = false;
        if (mMenuState == 2 && this.mMenuState != 2) {
            if (b) {
                this.mResizedBounds.set(this.mMotionHelper.getBounds());
                this.mSavedSnapFraction = this.mMotionHelper.animateToExpandedState(new Rect(this.mExpandedBounds), this.mMovementBounds, this.mExpandedMovementBounds);
            }
        }
        else if (mMenuState == 0 && this.mMenuState == 2) {
            if (b) {
                if (this.mDeferResizeToNormalBoundsUntilRotation == -1) {
                    try {
                        final int displayRotation = this.mPinnedStackController.getDisplayRotation();
                        if (this.mDisplayRotation != displayRotation) {
                            this.mDeferResizeToNormalBoundsUntilRotation = displayRotation;
                        }
                    }
                    catch (RemoteException ex) {
                        Log.e("PipTouchHandler", "Could not get display rotation from controller");
                    }
                }
                if (this.mDeferResizeToNormalBoundsUntilRotation == -1) {
                    final Rect rect = new Rect(this.mResizedBounds);
                    final Rect rect2 = new Rect();
                    final PipSnapAlgorithm mSnapAlgorithm = this.mSnapAlgorithm;
                    final Rect mInsetBounds = this.mInsetBounds;
                    int mImeHeight;
                    if (this.mIsImeShowing) {
                        mImeHeight = this.mImeHeight;
                    }
                    else {
                        mImeHeight = 0;
                    }
                    mSnapAlgorithm.getMovementBounds(rect, mInsetBounds, rect2, mImeHeight);
                    this.mMotionHelper.animateToUnexpandedState(rect, this.mSavedSnapFraction, rect2, this.mMovementBounds, false);
                    this.mSavedSnapFraction = -1.0f;
                }
            }
            else {
                this.mSavedSnapFraction = -1.0f;
            }
        }
        this.mMenuState = mMenuState;
        this.updateMovementBounds();
        this.onRegistrationChanged(mMenuState == 0);
        if (mMenuState != 1) {
            final Context mContext = this.mContext;
            boolean b3 = b2;
            if (mMenuState == 2) {
                b3 = true;
            }
            MetricsLoggerWrapper.logPictureInPictureMenuVisible(mContext, b3);
        }
    }
    
    private void showDismissTargetMaybe() {
        this.createDismissTargetMaybe();
        if (this.mTargetViewContainer.getVisibility() != 0) {
            this.mTargetView.setTranslationY((float)this.mTargetViewContainer.getHeight());
            this.mTargetViewContainer.setVisibility(0);
            this.mMagneticTarget.setMagneticFieldRadiusPx(this.mMotionHelper.getBounds().width());
            this.mMagneticTargetAnimator.cancel();
            final PhysicsAnimator<View> mMagneticTargetAnimator = this.mMagneticTargetAnimator;
            mMagneticTargetAnimator.spring(DynamicAnimation.TRANSLATION_Y, 0.0f, this.mTargetSpringConfig);
            mMagneticTargetAnimator.start();
        }
    }
    
    private void updateDismissFraction() {
        if (this.mMenuController != null && !this.mIsImeShowing) {
            final Rect bounds = this.mMotionHelper.getBounds();
            final float n = (float)this.mInsetBounds.bottom;
            final int bottom = bounds.bottom;
            float min;
            if (bottom > n) {
                min = Math.min((bottom - n) / bounds.height(), 1.0f);
            }
            else {
                min = 0.0f;
            }
            if (Float.compare(min, 0.0f) != 0 || this.mMenuController.isMenuActivityVisible()) {
                this.mMenuController.setDismissFraction(min);
            }
        }
    }
    
    private void updateMovementBounds() {
        final PipSnapAlgorithm mSnapAlgorithm = this.mSnapAlgorithm;
        final Rect bounds = this.mMotionHelper.getBounds();
        final Rect mInsetBounds = this.mInsetBounds;
        final Rect mMovementBounds = this.mMovementBounds;
        final boolean mIsImeShowing = this.mIsImeShowing;
        final int n = 0;
        int mImeHeight;
        if (mIsImeShowing) {
            mImeHeight = this.mImeHeight;
        }
        else {
            mImeHeight = 0;
        }
        mSnapAlgorithm.getMovementBounds(bounds, mInsetBounds, mMovementBounds, mImeHeight);
        this.mMotionHelper.setCurrentMovementBounds(this.mMovementBounds);
        final boolean b = this.mMenuState == 2;
        final PipBoundsHandler mPipBoundsHandler = this.mPipBoundsHandler;
        int mExpandedShortestEdgeSize = n;
        if (b) {
            mExpandedShortestEdgeSize = n;
            if (this.willResizeMenu()) {
                mExpandedShortestEdgeSize = this.mExpandedShortestEdgeSize;
            }
        }
        mPipBoundsHandler.setMinEdgeSize(mExpandedShortestEdgeSize);
    }
    
    private boolean willResizeMenu() {
        final boolean mEnableResize = this.mEnableResize;
        boolean b = false;
        if (!mEnableResize) {
            return false;
        }
        if (this.mExpandedBounds.width() != this.mNormalBounds.width() || this.mExpandedBounds.height() != this.mNormalBounds.height()) {
            b = true;
        }
        return b;
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("PipTouchHandler");
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string);
        sb3.append("mMovementBounds=");
        sb3.append(this.mMovementBounds);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(string);
        sb4.append("mNormalBounds=");
        sb4.append(this.mNormalBounds);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(string);
        sb5.append("mNormalMovementBounds=");
        sb5.append(this.mNormalMovementBounds);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append(string);
        sb6.append("mExpandedBounds=");
        sb6.append(this.mExpandedBounds);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append(string);
        sb7.append("mExpandedMovementBounds=");
        sb7.append(this.mExpandedMovementBounds);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append(string);
        sb8.append("mMenuState=");
        sb8.append(this.mMenuState);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append(string);
        sb9.append("mIsImeShowing=");
        sb9.append(this.mIsImeShowing);
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append(string);
        sb10.append("mImeHeight=");
        sb10.append(this.mImeHeight);
        printWriter.println(sb10.toString());
        final StringBuilder sb11 = new StringBuilder();
        sb11.append(string);
        sb11.append("mIsShelfShowing=");
        sb11.append(this.mIsShelfShowing);
        printWriter.println(sb11.toString());
        final StringBuilder sb12 = new StringBuilder();
        sb12.append(string);
        sb12.append("mShelfHeight=");
        sb12.append(this.mShelfHeight);
        printWriter.println(sb12.toString());
        final StringBuilder sb13 = new StringBuilder();
        sb13.append(string);
        sb13.append("mSavedSnapFraction=");
        sb13.append(this.mSavedSnapFraction);
        printWriter.println(sb13.toString());
        final StringBuilder sb14 = new StringBuilder();
        sb14.append(string);
        sb14.append("mEnableDragToEdgeDismiss=");
        sb14.append(this.mEnableDismissDragToEdge);
        printWriter.println(sb14.toString());
        this.mSnapAlgorithm.dump(printWriter, string);
        this.mTouchState.dump(printWriter, string);
        this.mMotionHelper.dump(printWriter, string);
    }
    
    public PipMotionHelper getMotionHelper() {
        return this.mMotionHelper;
    }
    
    public Rect getNormalBounds() {
        return this.mNormalBounds;
    }
    
    @VisibleForTesting
    PipResizeGestureHandler getPipResizeGestureHandler() {
        return this.mPipResizeGestureHandler;
    }
    
    public void onActivityPinned() {
        this.createDismissTargetMaybe();
        this.mShowPipMenuOnAnimationEnd = true;
        this.mPipResizeGestureHandler.onActivityPinned();
        this.mFloatingContentCoordinator.onContentAdded((FloatingContentCoordinator.FloatingContent)this.mMotionHelper);
    }
    
    public void onActivityUnpinned(final ComponentName componentName) {
        if (componentName == null) {
            this.cleanUpDismissTarget();
            this.mFloatingContentCoordinator.onContentRemoved((FloatingContentCoordinator.FloatingContent)this.mMotionHelper);
        }
        this.mResizedBounds.setEmpty();
        this.mPipResizeGestureHandler.onActivityUnpinned();
    }
    
    public void onConfigurationChanged() {
        this.mMotionHelper.onConfigurationChanged();
        this.mMotionHelper.synchronizePinnedStackBounds();
        this.cleanUpDismissTarget();
        this.createDismissTargetMaybe();
    }
    
    public void onImeVisibilityChanged(final boolean mIsImeShowing, final int mImeHeight) {
        this.mIsImeShowing = mIsImeShowing;
        this.mImeHeight = mImeHeight;
    }
    
    public void onMovementBoundsChanged(final Rect rect, final Rect mNormalBounds, final Rect rect2, final boolean b, final boolean b2, final int mDisplayRotation) {
        final boolean mIsImeShowing = this.mIsImeShowing;
        final int n = 0;
        int mImeHeight;
        if (mIsImeShowing) {
            mImeHeight = this.mImeHeight;
        }
        else {
            mImeHeight = 0;
        }
        final boolean b3 = this.mDisplayRotation != mDisplayRotation;
        if (b3) {
            this.mTouchState.reset();
        }
        this.mNormalBounds = mNormalBounds;
        final Rect mNormalMovementBounds = new Rect();
        this.mSnapAlgorithm.getMovementBounds(this.mNormalBounds, rect, mNormalMovementBounds, mImeHeight);
        final float n2 = mNormalBounds.width() / (float)mNormalBounds.height();
        final Point point = new Point();
        this.mContext.getDisplay().getRealSize(point);
        final Size sizeForAspectRatio = this.mSnapAlgorithm.getSizeForAspectRatio(n2, (float)this.mExpandedShortestEdgeSize, point.x, point.y);
        this.mExpandedBounds.set(0, 0, sizeForAspectRatio.getWidth(), sizeForAspectRatio.getHeight());
        final Rect mExpandedMovementBounds = new Rect();
        this.mSnapAlgorithm.getMovementBounds(this.mExpandedBounds, rect, mExpandedMovementBounds, mImeHeight);
        this.mPipResizeGestureHandler.updateMinSize(this.mNormalBounds.width(), this.mNormalBounds.height());
        this.mPipResizeGestureHandler.updateMaxSize(this.mExpandedBounds.width(), this.mExpandedBounds.height());
        int mImeOffset;
        if (this.mIsImeShowing) {
            mImeOffset = this.mImeOffset;
        }
        else {
            mImeOffset = 0;
        }
        int mShelfHeight = n;
        if (!this.mIsImeShowing) {
            mShelfHeight = n;
            if (this.mIsShelfShowing) {
                mShelfHeight = this.mShelfHeight;
            }
        }
        final int max = Math.max(mImeOffset, mShelfHeight);
        if (b || b2 || b3) {
            if (!this.mTouchState.isUserInteracting()) {
                final float n3 = this.mContext.getResources().getDisplayMetrics().density * 1.0f;
                Rect rect3;
                if (this.mMenuState == 2 && this.willResizeMenu()) {
                    rect3 = new Rect(mExpandedMovementBounds);
                }
                else {
                    rect3 = new Rect(mNormalMovementBounds);
                }
                final int n4 = this.mMovementBounds.bottom - this.mMovementBoundsExtraOffsets;
                int bottom = rect3.bottom;
                if (bottom >= rect3.top) {
                    bottom -= max;
                }
                final float n5 = (float)Math.min(n4, bottom);
                final int top = rect2.top;
                if (n5 - n3 <= top && top <= Math.max(n4, bottom) + n3) {
                    this.mMotionHelper.animateToOffset(rect2, bottom - rect2.top);
                }
            }
        }
        this.mNormalMovementBounds = mNormalMovementBounds;
        this.mExpandedMovementBounds = mExpandedMovementBounds;
        this.mDisplayRotation = mDisplayRotation;
        this.mInsetBounds.set(rect);
        this.updateMovementBounds();
        this.mMovementBoundsExtraOffsets = max;
        if (this.mDeferResizeToNormalBoundsUntilRotation == mDisplayRotation) {
            this.mMotionHelper.animateToUnexpandedState(mNormalBounds, this.mSavedSnapFraction, this.mNormalMovementBounds, this.mMovementBounds, true);
            this.mSavedSnapFraction = -1.0f;
            this.mDeferResizeToNormalBoundsUntilRotation = -1;
        }
    }
    
    public void onPinnedStackAnimationEnded() {
        this.mMotionHelper.synchronizePinnedStackBounds();
        this.updateMovementBounds();
        this.mResizedBounds.set(this.mMotionHelper.getBounds());
        if (this.mShowPipMenuOnAnimationEnd) {
            this.mMenuController.showMenu(1, this.mMotionHelper.getBounds(), this.mMovementBounds, true, false);
            this.mShowPipMenuOnAnimationEnd = false;
        }
    }
    
    public void onShelfVisibilityChanged(final boolean mIsShelfShowing, final int mShelfHeight) {
        this.mIsShelfShowing = mIsShelfShowing;
        this.mShelfHeight = mShelfHeight;
    }
    
    void setPinnedStackController(final IPinnedStackController mPinnedStackController) {
        this.mPinnedStackController = mPinnedStackController;
    }
    
    @VisibleForTesting
    void setPipMotionHelper(final PipMotionHelper mMotionHelper) {
        this.mMotionHelper = mMotionHelper;
    }
    
    @VisibleForTesting
    void setPipResizeGestureHandler(final PipResizeGestureHandler mPipResizeGestureHandler) {
        this.mPipResizeGestureHandler = mPipResizeGestureHandler;
    }
    
    public void setTouchEnabled(final boolean allowTouches) {
        this.mTouchState.setAllowTouches(allowTouches);
    }
    
    public void showPictureInPictureMenu() {
        if (!this.mTouchState.isUserInteracting()) {
            this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, false, this.willResizeMenu());
        }
    }
    
    private class DefaultPipTouchGesture extends PipTouchGesture
    {
        private final PointF mDelta;
        private final Point mStartPosition;
        
        private DefaultPipTouchGesture() {
            this.mStartPosition = new Point();
            this.mDelta = new PointF();
        }
        
        @Override
        public void onDown(final PipTouchState pipTouchState) {
            if (!pipTouchState.isUserInteracting()) {
                return;
            }
            final Rect bounds = PipTouchHandler.this.mMotionHelper.getBounds();
            this.mDelta.set(0.0f, 0.0f);
            this.mStartPosition.set(bounds.left, bounds.top);
            PipTouchHandler.this.mMovementWithinDismiss = (pipTouchState.getDownTouchPosition().y >= PipTouchHandler.this.mMovementBounds.bottom);
            PipTouchHandler.this.mMotionHelper.setSpringingToTouch(false);
            if (PipTouchHandler.this.mMenuState != 0) {
                PipTouchHandler.this.mMenuController.pokeMenu();
            }
        }
        
        @Override
        public boolean onMove(final PipTouchState pipTouchState) {
            final boolean userInteracting = pipTouchState.isUserInteracting();
            boolean b = false;
            if (!userInteracting) {
                return false;
            }
            if (pipTouchState.startedDragging()) {
                PipTouchHandler.this.mSavedSnapFraction = -1.0f;
                if (PipTouchHandler.this.mEnableDismissDragToEdge && PipTouchHandler.this.mTargetViewContainer.getVisibility() != 0) {
                    PipTouchHandler.this.mHandler.removeCallbacks(PipTouchHandler.this.mShowTargetAction);
                    PipTouchHandler.this.showDismissTargetMaybe();
                }
            }
            if (pipTouchState.isDragging()) {
                final PointF lastTouchDelta = pipTouchState.getLastTouchDelta();
                final Point mStartPosition = this.mStartPosition;
                final float n = (float)mStartPosition.x;
                final PointF mDelta = this.mDelta;
                final float x = mDelta.x;
                final float n2 = n + x;
                final float n3 = (float)mStartPosition.y;
                final float y = mDelta.y;
                final float n4 = n3 + y;
                final float n5 = lastTouchDelta.x + n2;
                final float n6 = lastTouchDelta.y + n4;
                mDelta.x = x + (n5 - n2);
                mDelta.y = y + (n6 - n4);
                PipTouchHandler.this.mTmpBounds.set(PipTouchHandler.this.mMotionHelper.getBounds());
                PipTouchHandler.this.mTmpBounds.offsetTo((int)n5, (int)n6);
                PipTouchHandler.this.mMotionHelper.movePip(PipTouchHandler.this.mTmpBounds, true);
                final PointF lastTouchPosition = pipTouchState.getLastTouchPosition();
                if (PipTouchHandler.this.mMovementWithinDismiss) {
                    final PipTouchHandler this$0 = PipTouchHandler.this;
                    if (lastTouchPosition.y >= this$0.mMovementBounds.bottom) {
                        b = true;
                    }
                    this$0.mMovementWithinDismiss = b;
                }
                return true;
            }
            return false;
        }
        
        @Override
        public boolean onUp(final PipTouchState pipTouchState) {
            if (PipTouchHandler.this.mEnableDismissDragToEdge) {
                PipTouchHandler.this.hideDismissTarget();
            }
            if (!pipTouchState.isUserInteracting()) {
                return false;
            }
            final PointF velocity = pipTouchState.getVelocity();
            final boolean b = PointF.length(velocity.x, velocity.y) > PipTouchHandler.this.mFlingAnimationUtils.getMinVelocityPxPerSecond();
            if (pipTouchState.isDragging()) {
                Runnable runnable = null;
                if (PipTouchHandler.this.mMenuState != 0) {
                    PipTouchHandler.this.mMenuController.showMenu(PipTouchHandler.this.mMenuState, PipTouchHandler.this.mMotionHelper.getBounds(), PipTouchHandler.this.mMovementBounds, true, PipTouchHandler.this.willResizeMenu());
                }
                else {
                    final PipMenuActivityController access$600 = PipTouchHandler.this.mMenuController;
                    Objects.requireNonNull(access$600);
                    runnable = new _$$Lambda$QWy_27z4N9eSKLQk7iOWRu3Ei38(access$600);
                }
                if (b) {
                    PipTouchHandler.this.mMotionHelper.flingToSnapTarget(velocity.x, velocity.y, new _$$Lambda$PipTouchHandler$DefaultPipTouchGesture$K8tFYcJKtB3Bkuu5piDq_0_1YhA(PipTouchHandler.this), runnable);
                }
                else {
                    PipTouchHandler.this.mMotionHelper.animateToClosestSnapTarget();
                }
            }
            else if (PipTouchHandler.this.mTouchState.isDoubleTap()) {
                PipTouchHandler.this.setTouchEnabled(false);
                PipTouchHandler.this.mMotionHelper.expandPip();
            }
            else if (PipTouchHandler.this.mMenuState != 2) {
                if (!PipTouchHandler.this.mTouchState.isWaitingForDoubleTap()) {
                    PipTouchHandler.this.mMenuController.showMenu(2, PipTouchHandler.this.mMotionHelper.getBounds(), PipTouchHandler.this.mMovementBounds, true, PipTouchHandler.this.willResizeMenu());
                }
                else {
                    PipTouchHandler.this.mTouchState.scheduleDoubleTapTimeoutCallback();
                }
            }
            return true;
        }
    }
    
    private class PipMenuListener implements Listener
    {
        @Override
        public void onPipDismiss() {
            final Pair<ComponentName, Integer> topPipActivity = PipUtils.getTopPipActivity(PipTouchHandler.this.mContext, PipTouchHandler.this.mActivityManager);
            if (topPipActivity.first != null) {
                MetricsLoggerWrapper.logPictureInPictureDismissByTap(PipTouchHandler.this.mContext, (Pair)topPipActivity);
            }
            PipTouchHandler.this.mMotionHelper.dismissPip();
        }
        
        @Override
        public void onPipExpand() {
            PipTouchHandler.this.mMotionHelper.expandPip();
        }
        
        @Override
        public void onPipMenuStateChanged(final int n, final boolean b) {
            PipTouchHandler.this.setMenuState(n, b);
        }
        
        @Override
        public void onPipShowMenu() {
            PipTouchHandler.this.mMenuController.showMenu(2, PipTouchHandler.this.mMotionHelper.getBounds(), PipTouchHandler.this.mMovementBounds, true, PipTouchHandler.this.willResizeMenu());
        }
    }
}
