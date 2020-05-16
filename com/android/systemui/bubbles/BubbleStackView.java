// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.graphics.Region;
import android.util.Log;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import com.android.systemui.R$string;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.ViewPropertyAnimator;
import android.graphics.PointF;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.widget.ViewClippingUtil;
import android.content.res.TypedArray;
import com.android.systemui.R$id;
import android.widget.TextView;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.R$layout;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.android.systemui.Prefs;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.view.DisplayCutout;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.WindowInsets;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.view.View$OnTouchListener;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.ColorMatrix;
import android.view.View$OnApplyWindowInsetsListener;
import com.android.systemui.util.DismissCircleView;
import androidx.dynamicanimation.animation.SpringForce;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import java.util.function.IntSupplier;
import android.os.Vibrator;
import android.view.WindowManager;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import android.view.MotionEvent;
import java.util.Collections;
import android.graphics.RectF;
import com.android.systemui.util.FloatingContentCoordinator;
import android.content.Context;
import android.view.Choreographer$FrameCallback;
import android.view.Choreographer;
import android.view.ViewTreeObserver$OnPreDrawListener;
import android.graphics.Rect;
import java.util.List;
import android.view.ViewTreeObserver$OnDrawListener;
import com.android.systemui.model.SysUiState;
import com.android.systemui.bubbles.animation.StackAnimationController;
import android.view.View$OnLayoutChangeListener;
import android.view.LayoutInflater;
import com.android.systemui.util.magnetictarget.MagnetizedObject;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.systemui.bubbles.animation.ExpandedAnimationController;
import android.graphics.Point;
import android.view.ViewGroup;
import android.view.View;
import android.graphics.Paint;
import android.animation.ValueAnimator;
import com.android.internal.widget.ViewClippingUtil$ClippingParameters;
import com.android.systemui.util.RelativeTouchListener;
import com.android.systemui.bubbles.animation.PhysicsAnimationLayout;
import android.view.View$OnClickListener;
import androidx.dynamicanimation.animation.DynamicAnimation;
import com.android.systemui.util.animation.PhysicsAnimator;
import com.android.internal.annotations.VisibleForTesting;
import android.widget.FrameLayout;

public class BubbleStackView extends FrameLayout
{
    private static final SurfaceSynchronizer DEFAULT_SURFACE_SYNCHRONIZER;
    @VisibleForTesting
    static final int FLYOUT_HIDE_AFTER = 5000;
    private static final PhysicsAnimator.SpringConfig FLYOUT_IME_ANIMATION_SPRING_CONFIG;
    private Runnable mAfterFlyoutHidden;
    private final DynamicAnimation.OnAnimationEndListener mAfterFlyoutTransitionSpring;
    private Runnable mAnimateInFlyout;
    private boolean mAnimatingEducationAway;
    private boolean mAnimatingManageEducationAway;
    private View$OnClickListener mBubbleClickListener;
    private PhysicsAnimationLayout mBubbleContainer;
    private final BubbleData mBubbleData;
    private int mBubbleElevation;
    private BubbleOverflow mBubbleOverflow;
    private int mBubblePaddingTop;
    private int mBubbleSize;
    private Bubble mBubbleToExpandAfterFlyoutCollapse;
    private RelativeTouchListener mBubbleTouchListener;
    private int mBubbleTouchPadding;
    private ViewClippingUtil$ClippingParameters mClippingParameters;
    private final ValueAnimator mDesaturateAndDarkenAnimator;
    private final Paint mDesaturateAndDarkenPaint;
    private View mDesaturateAndDarkenTargetView;
    private PhysicsAnimator<View> mDismissTargetAnimator;
    private ViewGroup mDismissTargetContainer;
    private PhysicsAnimator.SpringConfig mDismissTargetSpring;
    private Point mDisplaySize;
    private BubbleController.BubbleExpandListener mExpandListener;
    private int mExpandedAnimateXDistance;
    private int mExpandedAnimateYDistance;
    private ExpandedAnimationController mExpandedAnimationController;
    private BubbleViewProvider mExpandedBubble;
    private FrameLayout mExpandedViewContainer;
    private int mExpandedViewPadding;
    private final SpringAnimation mExpandedViewXAnim;
    private final SpringAnimation mExpandedViewYAnim;
    private BubbleFlyoutView mFlyout;
    private View$OnClickListener mFlyoutClickListener;
    private final FloatPropertyCompat mFlyoutCollapseProperty;
    private float mFlyoutDragDeltaX;
    private RelativeTouchListener mFlyoutTouchListener;
    private final SpringAnimation mFlyoutTransitionSpring;
    private Runnable mHideFlyout;
    private int mImeOffset;
    private final MagnetizedObject.MagnetListener mIndividualBubbleMagnetListener;
    private LayoutInflater mInflater;
    private boolean mIsExpanded;
    private boolean mIsExpansionAnimating;
    private boolean mIsGestureInProgress;
    private MagnetizedObject.MagneticTarget mMagneticTarget;
    private MagnetizedObject<?> mMagnetizedObject;
    private BubbleManageEducationView mManageEducationView;
    private int mMaxBubbles;
    private int mOrientation;
    private View$OnLayoutChangeListener mOrientationChangedListener;
    private int mPointerHeight;
    private boolean mShouldShowManageEducation;
    private boolean mShouldShowUserEducation;
    private boolean mShowingDismiss;
    private StackAnimationController mStackAnimationController;
    private final MagnetizedObject.MagnetListener mStackMagnetListener;
    private boolean mStackOnLeftOrWillBe;
    private int mStatusBarHeight;
    private final SurfaceSynchronizer mSurfaceSynchronizer;
    private SysUiState mSysUiState;
    private ViewTreeObserver$OnDrawListener mSystemGestureExcludeUpdater;
    private final List<Rect> mSystemGestureExclusionRects;
    private View mUserEducationView;
    private float mVerticalPosPercentBeforeRotation;
    private boolean mViewUpdatedRequested;
    private ViewTreeObserver$OnPreDrawListener mViewUpdater;
    private boolean mWasOnLeftBeforeRotation;
    
    static {
        FLYOUT_IME_ANIMATION_SPRING_CONFIG = new PhysicsAnimator.SpringConfig(200.0f, 0.9f);
        DEFAULT_SURFACE_SYNCHRONIZER = (SurfaceSynchronizer)new SurfaceSynchronizer() {
            @Override
            public void syncSurfaceAndRun(final Runnable runnable) {
                Choreographer.getInstance().postFrameCallback((Choreographer$FrameCallback)new Choreographer$FrameCallback(this) {
                    private int mFrameWait = 2;
                    
                    public void doFrame(final long n) {
                        final int mFrameWait = this.mFrameWait - 1;
                        this.mFrameWait = mFrameWait;
                        if (mFrameWait > 0) {
                            Choreographer.getInstance().postFrameCallback((Choreographer$FrameCallback)this);
                        }
                        else {
                            runnable.run();
                        }
                    }
                });
            }
        };
    }
    
    @SuppressLint({ "ClickableViewAccessibility" })
    public BubbleStackView(final Context context, final BubbleData mBubbleData, SurfaceSynchronizer default_SURFACE_SYNCHRONIZER, final FloatingContentCoordinator floatingContentCoordinator, final SysUiState mSysUiState) {
        super(context);
        this.mDesaturateAndDarkenPaint = new Paint();
        this.mHideFlyout = new _$$Lambda$BubbleStackView$jXS10HgKCVgyvjX1UcSgdO2D_ug(this);
        this.mBubbleToExpandAfterFlyoutCollapse = null;
        this.mWasOnLeftBeforeRotation = false;
        this.mVerticalPosPercentBeforeRotation = -1.0f;
        this.mStackOnLeftOrWillBe = true;
        this.mIsGestureInProgress = false;
        this.mViewUpdatedRequested = false;
        this.mIsExpansionAnimating = false;
        this.mShowingDismiss = false;
        new RectF();
        this.mSystemGestureExclusionRects = Collections.singletonList(new Rect());
        this.mViewUpdater = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                BubbleStackView.this.getViewTreeObserver().removeOnPreDrawListener(BubbleStackView.this.mViewUpdater);
                BubbleStackView.this.updateExpandedView();
                BubbleStackView.this.mViewUpdatedRequested = false;
                return true;
            }
        };
        this.mSystemGestureExcludeUpdater = (ViewTreeObserver$OnDrawListener)new _$$Lambda$BubbleStackView$c_OiserdP7VIvU56hCAARnBncEE(this);
        this.mClippingParameters = (ViewClippingUtil$ClippingParameters)new ViewClippingUtil$ClippingParameters() {
            public boolean isClippingEnablingAllowed(final View view) {
                return BubbleStackView.this.mIsExpanded ^ true;
            }
            
            public boolean shouldFinish(final View view) {
                return false;
            }
        };
        this.mFlyoutCollapseProperty = new FloatPropertyCompat("FlyoutCollapseSpring") {
            @Override
            public float getValue(final Object o) {
                return BubbleStackView.this.mFlyoutDragDeltaX;
            }
            
            @Override
            public void setValue(final Object o, final float flyoutStateForDragLength) {
                BubbleStackView.this.setFlyoutStateForDragLength(flyoutStateForDragLength);
            }
        };
        this.mFlyoutTransitionSpring = new SpringAnimation((K)this, this.mFlyoutCollapseProperty);
        this.mFlyoutDragDeltaX = 0.0f;
        this.mAfterFlyoutTransitionSpring = new _$$Lambda$BubbleStackView$qNTN7f0ovKQkRVyENDOFd8Z5ydA(this);
        this.mIndividualBubbleMagnetListener = new MagnetizedObject.MagnetListener() {
            @Override
            public void onReleasedInTarget(final MagneticTarget magneticTarget) {
                BubbleStackView.this.mExpandedAnimationController.dismissDraggedOutBubble(BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble(), new _$$Lambda$BubbleStackView$5$FX1v7HkoJImZTGVe6EDqbDOD6ok(BubbleStackView.this));
                BubbleStackView.this.hideDismissTarget();
            }
            
            @Override
            public void onStuckToTarget(final MagneticTarget magneticTarget) {
                final BubbleStackView this$0 = BubbleStackView.this;
                this$0.animateDesaturateAndDarken(this$0.mExpandedAnimationController.getDraggedOutBubble(), true);
            }
            
            @Override
            public void onUnstuckFromTarget(final MagneticTarget magneticTarget, final float n, final float n2, final boolean b) {
                final BubbleStackView this$0 = BubbleStackView.this;
                this$0.animateDesaturateAndDarken(this$0.mExpandedAnimationController.getDraggedOutBubble(), false);
                if (b) {
                    BubbleStackView.this.mExpandedAnimationController.snapBubbleBack(BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble(), n, n2);
                    BubbleStackView.this.hideDismissTarget();
                }
                else {
                    BubbleStackView.this.mExpandedAnimationController.onUnstuckFromTarget();
                }
            }
        };
        this.mStackMagnetListener = new MagnetizedObject.MagnetListener() {
            @Override
            public void onReleasedInTarget(final MagneticTarget magneticTarget) {
                BubbleStackView.this.mStackAnimationController.implodeStack(new _$$Lambda$BubbleStackView$6$6thZa1dZE3Fr_3nThZBI53E1CHg(this));
                BubbleStackView.this.hideDismissTarget();
            }
            
            @Override
            public void onStuckToTarget(final MagneticTarget magneticTarget) {
                final BubbleStackView this$0 = BubbleStackView.this;
                this$0.animateDesaturateAndDarken((View)this$0.mBubbleContainer, true);
            }
            
            @Override
            public void onUnstuckFromTarget(final MagneticTarget magneticTarget, final float n, final float n2, final boolean b) {
                final BubbleStackView this$0 = BubbleStackView.this;
                this$0.animateDesaturateAndDarken((View)this$0.mBubbleContainer, false);
                if (b) {
                    BubbleStackView.this.mStackAnimationController.flingStackThenSpringToEdge(BubbleStackView.this.mStackAnimationController.getStackPosition().x, n, n2);
                    BubbleStackView.this.hideDismissTarget();
                }
                else {
                    BubbleStackView.this.mStackAnimationController.onUnstuckFromTarget();
                }
            }
        };
        this.mBubbleClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final Bubble bubbleWithView = BubbleStackView.this.mBubbleData.getBubbleWithView(view);
                if (bubbleWithView == null) {
                    return;
                }
                final boolean equals = bubbleWithView.getKey().equals(BubbleStackView.this.mExpandedBubble.getKey());
                if (BubbleStackView.this.isExpanded() && !equals) {
                    if (bubbleWithView != BubbleStackView.this.mBubbleData.getSelectedBubble()) {
                        BubbleStackView.this.mBubbleData.setSelectedBubble(bubbleWithView);
                    }
                    else {
                        BubbleStackView.this.setSelectedBubble(bubbleWithView);
                    }
                }
                else if (!BubbleStackView.this.maybeShowStackUserEducation()) {
                    BubbleStackView.this.mBubbleData.setExpanded(BubbleStackView.this.mBubbleData.isExpanded() ^ true);
                }
            }
        };
        this.mBubbleTouchListener = new RelativeTouchListener() {
            @Override
            public boolean onDown(final View view, final MotionEvent motionEvent) {
                if (BubbleStackView.this.mIsExpansionAnimating) {
                    return true;
                }
                if (BubbleStackView.this.mBubbleData.isExpanded()) {
                    BubbleStackView.this.maybeShowManageEducation(false);
                    BubbleStackView.this.mExpandedAnimationController.prepareForBubbleDrag(view, BubbleStackView.this.mMagneticTarget, BubbleStackView.this.mIndividualBubbleMagnetListener);
                    final BubbleStackView this$0 = BubbleStackView.this;
                    this$0.mMagnetizedObject = this$0.mExpandedAnimationController.getMagnetizedBubbleDraggingOut();
                }
                else {
                    BubbleStackView.this.mStackAnimationController.cancelStackPositionAnimations();
                    BubbleStackView.this.mBubbleContainer.setActiveController((PhysicsAnimationLayout.PhysicsAnimationController)BubbleStackView.this.mStackAnimationController);
                    BubbleStackView.this.hideFlyoutImmediate();
                    final BubbleStackView this$2 = BubbleStackView.this;
                    this$2.mMagnetizedObject = this$2.mStackAnimationController.getMagnetizedStack(BubbleStackView.this.mMagneticTarget);
                    BubbleStackView.this.mMagnetizedObject.setMagnetListener(BubbleStackView.this.mStackMagnetListener);
                }
                BubbleStackView.this.passEventToMagnetizedObject(motionEvent);
                return true;
            }
            
            @Override
            public void onMove(final View view, final MotionEvent motionEvent, final float n, final float n2, final float n3, final float n4) {
                if (BubbleStackView.this.mIsExpansionAnimating) {
                    return;
                }
                BubbleStackView.this.springInDismissTargetMaybe();
                if (!BubbleStackView.this.passEventToMagnetizedObject(motionEvent)) {
                    if (BubbleStackView.this.mBubbleData.isExpanded()) {
                        BubbleStackView.this.mExpandedAnimationController.dragBubbleOut(view, n + n3, n2 + n4);
                    }
                    else {
                        BubbleStackView.this.hideStackUserEducation(false);
                        BubbleStackView.this.mStackAnimationController.moveStackFromTouch(n + n3, n2 + n4);
                    }
                }
            }
            
            @Override
            public void onUp(final View view, final MotionEvent motionEvent, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
                if (BubbleStackView.this.mIsExpansionAnimating) {
                    return;
                }
                if (!BubbleStackView.this.passEventToMagnetizedObject(motionEvent)) {
                    if (BubbleStackView.this.mBubbleData.isExpanded()) {
                        BubbleStackView.this.mExpandedAnimationController.snapBubbleBack(view, n5, n6);
                    }
                    else {
                        final BubbleStackView this$0 = BubbleStackView.this;
                        this$0.mStackOnLeftOrWillBe = (this$0.mStackAnimationController.flingStackThenSpringToEdge(n + n3, n5, n6) <= 0.0f);
                        BubbleStackView.this.updateBubbleZOrdersAndDotPosition(true);
                        BubbleStackView.this.logBubbleEvent(null, 7);
                    }
                    BubbleStackView.this.hideDismissTarget();
                }
            }
        };
        this.mFlyoutClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                if (BubbleStackView.this.maybeShowStackUserEducation()) {
                    BubbleStackView.this.mBubbleToExpandAfterFlyoutCollapse = null;
                }
                else {
                    final BubbleStackView this$0 = BubbleStackView.this;
                    this$0.mBubbleToExpandAfterFlyoutCollapse = this$0.mBubbleData.getSelectedBubble();
                }
                BubbleStackView.this.mFlyout.removeCallbacks(BubbleStackView.this.mHideFlyout);
                BubbleStackView.this.mHideFlyout.run();
            }
        };
        this.mFlyoutTouchListener = new RelativeTouchListener() {
            @Override
            public boolean onDown(final View view, final MotionEvent motionEvent) {
                BubbleStackView.this.mFlyout.removeCallbacks(BubbleStackView.this.mHideFlyout);
                return true;
            }
            
            @Override
            public void onMove(final View view, final MotionEvent motionEvent, final float n, final float n2, final float flyoutStateForDragLength, final float n3) {
                BubbleStackView.this.setFlyoutStateForDragLength(flyoutStateForDragLength);
            }
            
            @Override
            public void onUp(final View view, final MotionEvent motionEvent, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
                final boolean stackOnLeftSide = BubbleStackView.this.mStackAnimationController.isStackOnLeftSide();
                final boolean b = true;
                final boolean b2 = stackOnLeftSide ? (n5 < -2000.0f) : (n5 > 2000.0f);
                final boolean b3 = stackOnLeftSide ? (n3 < -BubbleStackView.this.mFlyout.getWidth() * 0.25f) : (n3 > BubbleStackView.this.mFlyout.getWidth() * 0.25f);
                final boolean b4 = stackOnLeftSide ? (n5 > 0.0f) : (n5 < 0.0f);
                boolean b5 = b;
                if (!b2) {
                    b5 = (b3 && !b4 && b);
                }
                BubbleStackView.this.mFlyout.removeCallbacks(BubbleStackView.this.mHideFlyout);
                BubbleStackView.this.animateFlyoutCollapsed(b5, n5);
                BubbleStackView.this.maybeShowStackUserEducation();
            }
        };
        this.mDismissTargetSpring = new PhysicsAnimator.SpringConfig(200.0f, 0.75f);
        this.mOrientation = 0;
        this.mBubbleData = mBubbleData;
        this.mInflater = LayoutInflater.from(context);
        this.mSysUiState = mSysUiState;
        final Resources resources = this.getResources();
        this.mMaxBubbles = resources.getInteger(R$integer.bubbles_max_rendered);
        this.mBubbleSize = resources.getDimensionPixelSize(R$dimen.individual_bubble_size);
        this.mBubbleElevation = resources.getDimensionPixelSize(R$dimen.bubble_elevation);
        this.mBubblePaddingTop = resources.getDimensionPixelSize(R$dimen.bubble_padding_top);
        this.mBubbleTouchPadding = resources.getDimensionPixelSize(R$dimen.bubble_touch_padding);
        this.mExpandedAnimateXDistance = resources.getDimensionPixelSize(R$dimen.bubble_expanded_animate_x_distance);
        this.mExpandedAnimateYDistance = resources.getDimensionPixelSize(R$dimen.bubble_expanded_animate_y_distance);
        this.mPointerHeight = resources.getDimensionPixelSize(R$dimen.bubble_pointer_height);
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105471);
        this.mImeOffset = resources.getDimensionPixelSize(R$dimen.pip_ime_offset);
        this.mDisplaySize = new Point();
        ((WindowManager)context.getSystemService("window")).getDefaultDisplay().getRealSize(this.mDisplaySize);
        final Vibrator vibrator = (Vibrator)context.getSystemService("vibrator");
        this.mExpandedViewPadding = resources.getDimensionPixelSize(R$dimen.bubble_expanded_view_padding);
        final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.bubble_elevation);
        this.mStackAnimationController = new StackAnimationController(floatingContentCoordinator, new _$$Lambda$3l4urKvsZCQadEpiHWxUBGPGyvY(this));
        this.mExpandedAnimationController = new ExpandedAnimationController(this.mDisplaySize, this.mExpandedViewPadding, resources.getConfiguration().orientation);
        if (default_SURFACE_SYNCHRONIZER == null) {
            default_SURFACE_SYNCHRONIZER = BubbleStackView.DEFAULT_SURFACE_SYNCHRONIZER;
        }
        this.mSurfaceSynchronizer = default_SURFACE_SYNCHRONIZER;
        this.setUpUserEducation();
        (this.mBubbleContainer = new PhysicsAnimationLayout(context)).setActiveController((PhysicsAnimationLayout.PhysicsAnimationController)this.mStackAnimationController);
        final PhysicsAnimationLayout mBubbleContainer = this.mBubbleContainer;
        final float n = (float)dimensionPixelSize;
        mBubbleContainer.setElevation(n);
        this.mBubbleContainer.setClipChildren(false);
        this.addView((View)this.mBubbleContainer, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        (this.mExpandedViewContainer = new FrameLayout(context)).setElevation(n);
        final FrameLayout mExpandedViewContainer = this.mExpandedViewContainer;
        final int mExpandedViewPadding = this.mExpandedViewPadding;
        mExpandedViewContainer.setPadding(mExpandedViewPadding, mExpandedViewPadding, mExpandedViewPadding, mExpandedViewPadding);
        this.mExpandedViewContainer.setClipChildren(false);
        this.addView((View)this.mExpandedViewContainer);
        this.setUpFlyout();
        final SpringAnimation mFlyoutTransitionSpring = this.mFlyoutTransitionSpring;
        final SpringForce spring = new SpringForce();
        spring.setStiffness(200.0f);
        spring.setDampingRatio(0.75f);
        mFlyoutTransitionSpring.setSpring(spring);
        this.mFlyoutTransitionSpring.addEndListener(this.mAfterFlyoutTransitionSpring);
        final int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.dismiss_circle_size);
        final DismissCircleView dismissCircleView = new DismissCircleView(context);
        final FrameLayout$LayoutParams layoutParams = new FrameLayout$LayoutParams(dimensionPixelSize2, dimensionPixelSize2);
        layoutParams.gravity = 17;
        ((View)dismissCircleView).setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        this.mDismissTargetAnimator = PhysicsAnimator.getInstance((View)dismissCircleView);
        (this.mDismissTargetContainer = (ViewGroup)new FrameLayout(context)).setLayoutParams((ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, this.getResources().getDimensionPixelSize(R$dimen.floating_dismiss_gradient_height), 80));
        this.mDismissTargetContainer.setClipChildren(false);
        this.mDismissTargetContainer.addView((View)dismissCircleView);
        this.mDismissTargetContainer.setVisibility(4);
        this.addView((View)this.mDismissTargetContainer);
        ((View)dismissCircleView).setTranslationY((float)this.getResources().getDimensionPixelSize(R$dimen.floating_dismiss_gradient_height));
        this.mMagneticTarget = new MagnetizedObject.MagneticTarget((View)dismissCircleView, this.mBubbleSize * 2);
        final SpringAnimation mExpandedViewXAnim = new SpringAnimation((K)this.mExpandedViewContainer, (FloatPropertyCompat<K>)DynamicAnimation.TRANSLATION_X);
        this.mExpandedViewXAnim = mExpandedViewXAnim;
        final SpringForce spring2 = new SpringForce();
        spring2.setStiffness(200.0f);
        spring2.setDampingRatio(0.75f);
        mExpandedViewXAnim.setSpring(spring2);
        final SpringAnimation mExpandedViewYAnim = new SpringAnimation((K)this.mExpandedViewContainer, (FloatPropertyCompat<K>)DynamicAnimation.TRANSLATION_Y);
        this.mExpandedViewYAnim = mExpandedViewYAnim;
        final SpringForce spring3 = new SpringForce();
        spring3.setStiffness(200.0f);
        spring3.setDampingRatio(0.75f);
        mExpandedViewYAnim.setSpring(spring3);
        this.mExpandedViewYAnim.addEndListener((DynamicAnimation.OnAnimationEndListener)new _$$Lambda$BubbleStackView$pASZEuVtfFyo_FF2s4CpK8srlzg(this));
        this.setClipChildren(false);
        this.setFocusable(true);
        this.mBubbleContainer.bringToFront();
        this.setUpOverflow();
        this.setOnApplyWindowInsetsListener((View$OnApplyWindowInsetsListener)new _$$Lambda$BubbleStackView$JEhiIzPncR72OLevX_9noDIsyDo(this));
        this.mOrientationChangedListener = (View$OnLayoutChangeListener)new _$$Lambda$BubbleStackView$zB8p0_cj_tonbCXvIH4kDoBtabk(this);
        this.getViewTreeObserver().addOnDrawListener(this.mSystemGestureExcludeUpdater);
        (this.mDesaturateAndDarkenAnimator = ValueAnimator.ofFloat(new float[] { 1.0f, 0.0f })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$BubbleStackView$nTtH9EoKZ3I47Rp_Pl0BGULUUeI(this, new ColorMatrix(), new ColorMatrix()));
        this.setOnTouchListener((View$OnTouchListener)new _$$Lambda$BubbleStackView$DgIHzfVnE2ZObZ8qcZwxCeDQAK0(this));
    }
    
    private void afterExpandedViewAnimation() {
        this.updateExpandedView();
        this.mIsExpansionAnimating = false;
        this.requestUpdate();
    }
    
    private void animateCollapse() {
        this.mIsExpanded = false;
        final BubbleViewProvider mExpandedBubble = this.mExpandedBubble;
        this.beforeExpandedViewAnimation();
        this.maybeShowManageEducation(false);
        this.updateOverflowBtnVisibility(false);
        this.mBubbleContainer.cancelAllAnimations();
        this.mExpandedAnimationController.collapseBackToStack(this.mStackAnimationController.getStackPositionAlongNearestHorizontalEdge(), new _$$Lambda$BubbleStackView$NH7DZgjpopqQcy_7kuRW2G2lqco(this, mExpandedBubble));
        this.mExpandedViewXAnim.animateToFinalPosition(this.getCollapsedX());
        this.mExpandedViewYAnim.animateToFinalPosition(this.getCollapsedY());
        this.mExpandedViewContainer.animate().setDuration(100L).alpha(0.0f);
    }
    
    private void animateDesaturateAndDarken(final View mDesaturateAndDarkenTargetView, final boolean b) {
        this.mDesaturateAndDarkenTargetView = mDesaturateAndDarkenTargetView;
        if (b) {
            mDesaturateAndDarkenTargetView.setLayerType(2, this.mDesaturateAndDarkenPaint);
            this.mDesaturateAndDarkenAnimator.removeAllListeners();
            this.mDesaturateAndDarkenAnimator.start();
        }
        else {
            this.mDesaturateAndDarkenAnimator.removeAllListeners();
            this.mDesaturateAndDarkenAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    super.onAnimationEnd(animator);
                    BubbleStackView.this.resetDesaturationAndDarken();
                }
            });
            this.mDesaturateAndDarkenAnimator.reverse();
        }
    }
    
    private void animateExpansion() {
        this.hideStackUserEducation(this.mIsExpanded = true);
        this.beforeExpandedViewAnimation();
        this.mBubbleContainer.setActiveController((PhysicsAnimationLayout.PhysicsAnimationController)this.mExpandedAnimationController);
        this.updateOverflowBtnVisibility(false);
        this.mExpandedAnimationController.expandFromStack(new _$$Lambda$BubbleStackView$x3ffD7PdKF5RECIYunqPLZIMVLI(this));
        this.mExpandedViewContainer.setTranslationX(this.getCollapsedX());
        this.mExpandedViewContainer.setTranslationY(this.getCollapsedY());
        this.mExpandedViewContainer.setAlpha(0.0f);
        this.mExpandedViewXAnim.animateToFinalPosition(0.0f);
        this.mExpandedViewYAnim.animateToFinalPosition(this.getExpandedViewY());
        this.mExpandedViewContainer.animate().setDuration(100L).alpha(1.0f);
    }
    
    private void animateFlyoutCollapsed(final boolean b, float startVelocity) {
        final boolean stackOnLeftSide = this.mStackAnimationController.isStackOnLeftSide();
        final SpringForce spring = this.mFlyoutTransitionSpring.getSpring();
        float stiffness;
        if (this.mBubbleToExpandAfterFlyoutCollapse != null) {
            stiffness = 1500.0f;
        }
        else {
            stiffness = 200.0f;
        }
        spring.setStiffness(stiffness);
        final SpringAnimation mFlyoutTransitionSpring = this.mFlyoutTransitionSpring;
        mFlyoutTransitionSpring.setStartValue(this.mFlyoutDragDeltaX);
        final SpringAnimation springAnimation = mFlyoutTransitionSpring;
        springAnimation.setStartVelocity(startVelocity);
        final SpringAnimation springAnimation2 = springAnimation;
        if (b) {
            int width = this.mFlyout.getWidth();
            if (stackOnLeftSide) {
                width = -width;
            }
            startVelocity = (float)width;
        }
        else {
            startVelocity = 0.0f;
        }
        springAnimation2.animateToFinalPosition(startVelocity);
    }
    
    private void beforeExpandedViewAnimation() {
        this.hideFlyoutImmediate();
        this.updateExpandedBubble();
        this.updateExpandedView();
        this.mIsExpansionAnimating = true;
    }
    
    private void clearFlyoutOnHide() {
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        final Runnable mAfterFlyoutHidden = this.mAfterFlyoutHidden;
        if (mAfterFlyoutHidden == null) {
            return;
        }
        mAfterFlyoutHidden.run();
        this.mAfterFlyoutHidden = null;
    }
    
    private void dismissMagnetizedObject() {
        if (this.mIsExpanded) {
            final Bubble bubbleWithView = this.mBubbleData.getBubbleWithView((View)this.mMagnetizedObject.getUnderlyingObject());
            if (this.mBubbleData.hasBubbleWithKey(bubbleWithView.getKey())) {
                this.mBubbleData.notificationEntryRemoved(bubbleWithView.getEntry(), 1);
            }
        }
        else {
            this.mBubbleData.dismissAll(1);
        }
    }
    
    private float getCollapsedX() {
        int mExpandedAnimateXDistance;
        if (this.mStackAnimationController.getStackPosition().x < this.getWidth() / 2) {
            mExpandedAnimateXDistance = -this.mExpandedAnimateXDistance;
        }
        else {
            mExpandedAnimateXDistance = this.mExpandedAnimateXDistance;
        }
        return (float)mExpandedAnimateXDistance;
    }
    
    private float getCollapsedY() {
        return Math.min(this.mStackAnimationController.getStackPosition().y, (float)this.mExpandedAnimateYDistance);
    }
    
    private int getStatusBarHeight() {
        final WindowInsets rootWindowInsets = this.getRootWindowInsets();
        int safeInsetTop = 0;
        if (rootWindowInsets != null) {
            final WindowInsets rootWindowInsets2 = this.getRootWindowInsets();
            final int mStatusBarHeight = this.mStatusBarHeight;
            if (rootWindowInsets2.getDisplayCutout() != null) {
                safeInsetTop = rootWindowInsets2.getDisplayCutout().getSafeInsetTop();
            }
            return Math.max(mStatusBarHeight, safeInsetTop);
        }
        return 0;
    }
    
    private void hideDismissTarget() {
        if (!this.mShowingDismiss) {
            return;
        }
        this.mShowingDismiss = false;
        final PhysicsAnimator<View> mDismissTargetAnimator = this.mDismissTargetAnimator;
        mDismissTargetAnimator.spring(DynamicAnimation.TRANSLATION_Y, (float)this.mDismissTargetContainer.getHeight(), this.mDismissTargetSpring);
        mDismissTargetAnimator.withEndActions(new _$$Lambda$BubbleStackView$newjpfUFAw4aLGVO6pItsW2zylg(this));
        mDismissTargetAnimator.start();
    }
    
    private void hideFlyoutImmediate() {
        this.clearFlyoutOnHide();
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        this.mFlyout.hideFlyout();
    }
    
    private void logBubbleEvent(final BubbleViewProvider bubbleViewProvider, final int n) {
        if (bubbleViewProvider == null) {
            return;
        }
        bubbleViewProvider.logUIEvent(this.getBubbleCount(), n, this.getNormalizedXPosition(), this.getNormalizedYPosition(), this.getBubbleIndex(bubbleViewProvider));
    }
    
    private boolean maybeShowStackUserEducation() {
        final boolean mShouldShowUserEducation = this.mShouldShowUserEducation;
        boolean b = false;
        if (mShouldShowUserEducation) {
            b = b;
            if (this.mUserEducationView.getVisibility() != 0) {
                this.mUserEducationView.setAlpha(0.0f);
                this.mUserEducationView.setVisibility(0);
                this.mUserEducationView.post((Runnable)new _$$Lambda$BubbleStackView$L0DeK_79IYzUixFIaNFN8mJ6vEY(this));
                final Context context = this.getContext();
                b = true;
                Prefs.putBoolean(context, "HasSeenBubblesOnboarding", true);
            }
        }
        return b;
    }
    
    private void notifyExpansionChanged(final BubbleViewProvider bubbleViewProvider, final boolean b) {
        final BubbleController.BubbleExpandListener mExpandListener = this.mExpandListener;
        if (mExpandListener != null && bubbleViewProvider != null) {
            mExpandListener.onBubbleExpandChanged(b, bubbleViewProvider.getKey());
        }
    }
    
    private boolean passEventToMagnetizedObject(final MotionEvent motionEvent) {
        final MagnetizedObject<?> mMagnetizedObject = this.mMagnetizedObject;
        return mMagnetizedObject != null && mMagnetizedObject.maybeConsumeMotionEvent(motionEvent);
    }
    
    private void requestUpdate() {
        if (!this.mViewUpdatedRequested) {
            if (!this.mIsExpansionAnimating) {
                this.mViewUpdatedRequested = true;
                this.getViewTreeObserver().addOnPreDrawListener(this.mViewUpdater);
                this.invalidate();
            }
        }
    }
    
    private void resetDesaturationAndDarken() {
        this.mDesaturateAndDarkenAnimator.removeAllListeners();
        this.mDesaturateAndDarkenAnimator.cancel();
        this.mDesaturateAndDarkenTargetView.setLayerType(0, (Paint)null);
    }
    
    @SuppressLint({ "ClickableViewAccessibility" })
    private void setUpFlyout() {
        final BubbleFlyoutView mFlyout = this.mFlyout;
        if (mFlyout != null) {
            this.removeView((View)mFlyout);
        }
        (this.mFlyout = new BubbleFlyoutView(this.getContext())).setVisibility(8);
        this.mFlyout.animate().setDuration(100L).setInterpolator((TimeInterpolator)new AccelerateDecelerateInterpolator());
        this.mFlyout.setOnClickListener(this.mFlyoutClickListener);
        this.mFlyout.setOnTouchListener((View$OnTouchListener)this.mFlyoutTouchListener);
        this.addView((View)this.mFlyout, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2));
    }
    
    private void setUpOverflow() {
        if (!BubbleExperimentConfig.allowBubbleOverflow(super.mContext)) {
            return;
        }
        int childCount = 0;
        final BubbleOverflow mBubbleOverflow = this.mBubbleOverflow;
        if (mBubbleOverflow == null) {
            (this.mBubbleOverflow = new BubbleOverflow(this.getContext())).setUpOverflow((ViewGroup)this.mBubbleContainer, this);
        }
        else {
            this.mBubbleContainer.removeView((View)mBubbleOverflow.getBtn());
            this.mBubbleOverflow.updateIcon(super.mContext, (ViewGroup)this);
            childCount = this.mBubbleContainer.getChildCount();
        }
        this.mBubbleContainer.addView((View)this.mBubbleOverflow.getBtn(), childCount, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2));
        this.mBubbleOverflow.getBtn().setOnClickListener((View$OnClickListener)new _$$Lambda$BubbleStackView$5BzDDKSraOgvq4XmyITlhveyYzQ(this));
    }
    
    private void setUpUserEducation() {
        final View mUserEducationView = this.mUserEducationView;
        if (mUserEducationView != null) {
            this.removeView(mUserEducationView);
        }
        final boolean shouldShowBubblesEducation = this.shouldShowBubblesEducation();
        this.mShouldShowUserEducation = shouldShowBubblesEducation;
        if (shouldShowBubblesEducation) {
            (this.mUserEducationView = this.mInflater.inflate(R$layout.bubble_stack_user_education, (ViewGroup)this, false)).setVisibility(8);
            final TypedArray obtainStyledAttributes = super.mContext.obtainStyledAttributes(new int[] { 16843829, 16842809 });
            final int color = obtainStyledAttributes.getColor(0, -16777216);
            final int color2 = obtainStyledAttributes.getColor(1, -1);
            obtainStyledAttributes.recycle();
            final int ensureTextContrast = ContrastColorUtil.ensureTextContrast(color2, color, true);
            final TextView textView = (TextView)this.mUserEducationView.findViewById(R$id.user_education_title);
            final TextView textView2 = (TextView)this.mUserEducationView.findViewById(R$id.user_education_description);
            textView.setTextColor(ensureTextContrast);
            textView2.setTextColor(ensureTextContrast);
            this.addView(this.mUserEducationView);
        }
        final BubbleManageEducationView mManageEducationView = this.mManageEducationView;
        if (mManageEducationView != null) {
            this.removeView((View)mManageEducationView);
        }
        final boolean shouldShowManageEducation = this.shouldShowManageEducation();
        this.mShouldShowManageEducation = shouldShowManageEducation;
        if (shouldShowManageEducation) {
            (this.mManageEducationView = (BubbleManageEducationView)this.mInflater.inflate(R$layout.bubbles_manage_button_education, (ViewGroup)this, false)).setVisibility(8);
            this.mManageEducationView.setElevation((float)this.mBubbleElevation);
            this.addView((View)this.mManageEducationView);
        }
    }
    
    private boolean shouldShowBubblesEducation() {
        final boolean forceShowUserEducation = BubbleDebugConfig.forceShowUserEducation(this.getContext());
        boolean b = false;
        if (forceShowUserEducation || !Prefs.getBoolean(this.getContext(), "HasSeenBubblesOnboarding", false)) {
            b = true;
        }
        return b;
    }
    
    private boolean shouldShowManageEducation() {
        final boolean forceShowUserEducation = BubbleDebugConfig.forceShowUserEducation(this.getContext());
        boolean b = false;
        if (forceShowUserEducation || !Prefs.getBoolean(this.getContext(), "HasSeenBubblesManageOnboarding", false)) {
            b = true;
        }
        return b;
    }
    
    private void springInDismissTargetMaybe() {
        if (this.mShowingDismiss) {
            return;
        }
        this.mShowingDismiss = true;
        this.mDismissTargetContainer.bringToFront();
        this.mDismissTargetContainer.setZ(32766.0f);
        this.mDismissTargetContainer.setVisibility(0);
        this.mDismissTargetAnimator.cancel();
        final PhysicsAnimator<View> mDismissTargetAnimator = this.mDismissTargetAnimator;
        mDismissTargetAnimator.spring(DynamicAnimation.TRANSLATION_Y, 0.0f, this.mDismissTargetSpring);
        mDismissTargetAnimator.start();
    }
    
    private void updateBubbleZOrdersAndDotPosition(final boolean b) {
        for (int bubbleCount = this.getBubbleCount(), i = 0; i < bubbleCount; ++i) {
            final BadgedImageView badgedImageView = (BadgedImageView)this.mBubbleContainer.getChildAt(i);
            badgedImageView.setZ((float)(this.mMaxBubbles * this.mBubbleElevation - i));
            final boolean dotPositionOnLeft = badgedImageView.getDotPositionOnLeft();
            final boolean mStackOnLeftOrWillBe = this.mStackOnLeftOrWillBe;
            if (dotPositionOnLeft == mStackOnLeftOrWillBe) {
                badgedImageView.setDotPositionOnLeft(mStackOnLeftOrWillBe ^ true, b);
            }
            if (!this.mIsExpanded && i > 0) {
                badgedImageView.addDotSuppressionFlag(BadgedImageView.SuppressionFlag.BEHIND_STACK);
            }
            else {
                badgedImageView.removeDotSuppressionFlag(BadgedImageView.SuppressionFlag.BEHIND_STACK);
            }
        }
    }
    
    private void updateExpandedBubble() {
        this.mExpandedViewContainer.removeAllViews();
        if (this.mIsExpanded) {
            final BubbleViewProvider mExpandedBubble = this.mExpandedBubble;
            if (mExpandedBubble != null) {
                final BubbleExpandedView expandedView = mExpandedBubble.getExpandedView();
                this.mExpandedViewContainer.addView((View)expandedView);
                expandedView.populateExpandedView();
                this.mExpandedViewContainer.setVisibility(0);
                this.mExpandedViewContainer.setAlpha(1.0f);
            }
        }
    }
    
    private void updateExpandedView() {
        final FrameLayout mExpandedViewContainer = this.mExpandedViewContainer;
        int visibility;
        if (this.mIsExpanded) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mExpandedViewContainer.setVisibility(visibility);
        if (this.mIsExpanded) {
            final float expandedViewY = this.getExpandedViewY();
            if (!this.mExpandedViewYAnim.isRunning()) {
                this.mExpandedViewContainer.setTranslationY(expandedViewY);
                final BubbleViewProvider mExpandedBubble = this.mExpandedBubble;
                if (mExpandedBubble != null) {
                    mExpandedBubble.getExpandedView().updateView();
                }
            }
            else {
                this.mExpandedViewYAnim.animateToFinalPosition(expandedViewY);
            }
        }
        this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
        this.updateBubbleZOrdersAndDotPosition(false);
    }
    
    private void updateOverflowBtnVisibility(final boolean b) {
        if (!BubbleExperimentConfig.allowBubbleOverflow(super.mContext)) {
            return;
        }
        if (this.mIsExpanded) {
            this.mBubbleOverflow.setBtnVisible(0);
            if (b) {
                this.mExpandedAnimationController.expandFromStack(new _$$Lambda$BubbleStackView$AAqZu1adQiTWTO7_uDzMErt7Omw(this));
            }
        }
        else {
            this.mBubbleOverflow.setBtnVisible(8);
        }
    }
    
    private void updatePointerPosition() {
        final BubbleViewProvider mExpandedBubble = this.mExpandedBubble;
        if (mExpandedBubble == null) {
            return;
        }
        final int bubbleIndex = this.getBubbleIndex(mExpandedBubble);
        if (bubbleIndex == -1) {
            return;
        }
        this.mExpandedBubble.getExpandedView().setPointerPosition(this.mExpandedAnimationController.getBubbleLeft(bubbleIndex) + this.mBubbleSize / 2.0f - this.mExpandedViewContainer.getPaddingLeft());
    }
    
    private void updateSystemGestureExcludeRects() {
        final Rect rect = this.mSystemGestureExclusionRects.get(0);
        if (this.getBubbleCount() > 0) {
            final View child = this.mBubbleContainer.getChildAt(0);
            rect.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            rect.offset((int)(child.getTranslationX() + 0.5f), (int)(child.getTranslationY() + 0.5f));
            this.mBubbleContainer.setSystemGestureExclusionRects((List)this.mSystemGestureExclusionRects);
        }
        else {
            rect.setEmpty();
            this.mBubbleContainer.setSystemGestureExclusionRects((List)Collections.emptyList());
        }
    }
    
    @SuppressLint({ "ClickableViewAccessibility" })
    void addBubble(final Bubble bubble) {
        if (this.getBubbleCount() == 0 && this.mShouldShowUserEducation) {
            final StackAnimationController mStackAnimationController = this.mStackAnimationController;
            mStackAnimationController.setStackPosition(mStackAnimationController.getDefaultStartPosition());
        }
        if (this.getBubbleCount() == 0) {
            this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
        }
        if (bubble.getIconView() == null) {
            return;
        }
        bubble.getIconView().setDotPositionOnLeft(this.mStackOnLeftOrWillBe ^ true, false);
        bubble.getIconView().setOnClickListener(this.mBubbleClickListener);
        bubble.getIconView().setOnTouchListener((View$OnTouchListener)this.mBubbleTouchListener);
        this.mBubbleContainer.addView((View)bubble.getIconView(), 0, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2));
        ViewClippingUtil.setClippingDeactivated((View)bubble.getIconView(), true, this.mClippingParameters);
        this.animateInFlyoutForBubble(bubble);
        this.requestUpdate();
        this.logBubbleEvent(bubble, 1);
    }
    
    @VisibleForTesting
    void animateInFlyoutForBubble(final Bubble bubble) {
        final Bubble.FlyoutMessage flyoutMessage = bubble.getFlyoutMessage();
        final BadgedImageView iconView = bubble.getIconView();
        if (flyoutMessage != null && flyoutMessage.message != null && bubble.showFlyout()) {
            final View mUserEducationView = this.mUserEducationView;
            if ((mUserEducationView == null || mUserEducationView.getVisibility() != 0) && !this.isExpanded() && !this.mIsExpansionAnimating && !this.mIsGestureInProgress && this.mBubbleToExpandAfterFlyoutCollapse == null) {
                if (iconView != null) {
                    this.mFlyoutDragDeltaX = 0.0f;
                    this.clearFlyoutOnHide();
                    this.mAfterFlyoutHidden = new _$$Lambda$BubbleStackView$AnQlgdjpMrCO8BzuhlyuWZ_oXAk(this, iconView);
                    this.mFlyout.setVisibility(4);
                    iconView.addDotSuppressionFlag(BadgedImageView.SuppressionFlag.FLYOUT_VISIBLE);
                    this.post((Runnable)new _$$Lambda$BubbleStackView$JWniT8bstFjCci6XlFjzagyaRA8(this, bubble, flyoutMessage));
                    this.mFlyout.removeCallbacks(this.mHideFlyout);
                    this.mFlyout.postDelayed(this.mHideFlyout, 5000L);
                    this.logBubbleEvent(bubble, 16);
                    return;
                }
            }
        }
        if (iconView != null) {
            iconView.removeDotSuppressionFlag(BadgedImageView.SuppressionFlag.FLYOUT_VISIBLE);
        }
    }
    
    @Deprecated
    void collapseStack(final Runnable runnable) {
        this.mBubbleData.setExpanded(false);
        runnable.run();
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        boolean b2;
        final boolean b = b2 = super.dispatchTouchEvent(motionEvent);
        if (!b) {
            b2 = b;
            if (!this.mIsExpanded) {
                b2 = b;
                if (this.mIsGestureInProgress) {
                    b2 = this.mBubbleTouchListener.onTouch((View)this, motionEvent);
                }
            }
        }
        final int action = motionEvent.getAction();
        boolean mIsGestureInProgress = true;
        if (action == 1 || motionEvent.getAction() == 3) {
            mIsGestureInProgress = false;
        }
        this.mIsGestureInProgress = mIsGestureInProgress;
        return b2;
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("Stack view state:");
        printWriter.print("  gestureInProgress:    ");
        printWriter.println(this.mIsGestureInProgress);
        printWriter.print("  showingDismiss:       ");
        printWriter.println(this.mShowingDismiss);
        printWriter.print("  isExpansionAnimating: ");
        printWriter.println(this.mIsExpansionAnimating);
        this.mStackAnimationController.dump(fileDescriptor, printWriter, array);
        this.mExpandedAnimationController.dump(fileDescriptor, printWriter, array);
    }
    
    public void getBoundsOnScreen(final Rect rect) {
        final View mUserEducationView = this.mUserEducationView;
        if (mUserEducationView != null && mUserEducationView.getVisibility() == 0) {
            rect.set(0, 0, this.getWidth(), this.getHeight());
            return;
        }
        if (!this.mIsExpanded) {
            if (this.getBubbleCount() > 0) {
                this.mBubbleContainer.getChildAt(0).getBoundsOnScreen(rect);
            }
            final int top = rect.top;
            final int mBubbleTouchPadding = this.mBubbleTouchPadding;
            rect.top = top - mBubbleTouchPadding;
            rect.left -= mBubbleTouchPadding;
            rect.right += mBubbleTouchPadding;
            rect.bottom += mBubbleTouchPadding;
        }
        else {
            this.mBubbleContainer.getBoundsOnScreen(rect);
        }
        if (this.mFlyout.getVisibility() == 0) {
            final Rect rect2 = new Rect();
            this.mFlyout.getBoundsOnScreen(rect2);
            rect.union(rect2);
        }
    }
    
    public void getBoundsOnScreen(final Rect rect, final boolean b) {
        this.getBoundsOnScreen(rect);
    }
    
    public int getBubbleCount() {
        if (BubbleExperimentConfig.allowBubbleOverflow(super.mContext)) {
            return this.mBubbleContainer.getChildCount() - 1;
        }
        return this.mBubbleContainer.getChildCount();
    }
    
    int getBubbleIndex(final BubbleViewProvider bubbleViewProvider) {
        if (bubbleViewProvider == null) {
            return 0;
        }
        return this.mBubbleContainer.indexOfChild(bubbleViewProvider.getIconView());
    }
    
    BubbleViewProvider getExpandedBubble() {
        return this.mExpandedBubble;
    }
    
    float getExpandedViewY() {
        return (float)(this.getStatusBarHeight() + this.mBubbleSize + this.mBubblePaddingTop + this.mPointerHeight);
    }
    
    public float getNormalizedXPosition() {
        final BigDecimal bigDecimal = new BigDecimal(this.getStackPosition().x / this.mDisplaySize.x);
        final RoundingMode ceiling = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }
    
    public float getNormalizedYPosition() {
        final BigDecimal bigDecimal = new BigDecimal(this.getStackPosition().y / this.mDisplaySize.y);
        final RoundingMode ceiling = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }
    
    public PointF getStackPosition() {
        return this.mStackAnimationController.getStackPosition();
    }
    
    void hideStackUserEducation(final boolean b) {
        if (this.mShouldShowUserEducation && this.mUserEducationView.getVisibility() == 0 && !this.mAnimatingEducationAway) {
            this.mAnimatingEducationAway = true;
            final ViewPropertyAnimator alpha = this.mUserEducationView.animate().alpha(0.0f);
            long duration;
            if (b) {
                duration = 40L;
            }
            else {
                duration = 200L;
            }
            alpha.setDuration(duration).withEndAction((Runnable)new _$$Lambda$BubbleStackView$nNAvddcBIDq1Y55IbYeEwWpZS3s(this));
        }
    }
    
    public boolean isExpanded() {
        return this.mIsExpanded;
    }
    
    public boolean isExpansionAnimating() {
        return this.mIsExpansionAnimating;
    }
    
    void maybeShowManageEducation(final boolean b) {
        final BubbleManageEducationView mManageEducationView = this.mManageEducationView;
        if (mManageEducationView == null) {
            return;
        }
        if (b && this.mShouldShowManageEducation && mManageEducationView.getVisibility() != 0 && this.mIsExpanded) {
            this.mManageEducationView.setAlpha(0.0f);
            this.mManageEducationView.setVisibility(0);
            this.mManageEducationView.post((Runnable)new _$$Lambda$BubbleStackView$3HGDYH6yrpt7QgOCyCm4Di0ZjZM(this));
            Prefs.putBoolean(this.getContext(), "HasSeenBubblesManageOnboarding", true);
        }
        else if (!b && this.mManageEducationView.getVisibility() == 0 && !this.mAnimatingManageEducationAway) {
            final ViewPropertyAnimator alpha = this.mManageEducationView.animate().alpha(0.0f);
            long duration;
            if (this.mIsExpansionAnimating) {
                duration = 40L;
            }
            else {
                duration = 200L;
            }
            alpha.setDuration(duration).withEndAction((Runnable)new _$$Lambda$BubbleStackView$JxBykQqjc_JimSKpBu7GlD_Hnds(this));
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.getViewTreeObserver().removeOnPreDrawListener(this.mViewUpdater);
    }
    
    public void onImeVisibilityChanged(final boolean b, int imeHeight) {
        final StackAnimationController mStackAnimationController = this.mStackAnimationController;
        if (b) {
            imeHeight += this.mImeOffset;
        }
        else {
            imeHeight = 0;
        }
        mStackAnimationController.setImeHeight(imeHeight);
        if (!this.mIsExpanded && this.getBubbleCount() > 0) {
            final float animateForImeVisibility = this.mStackAnimationController.animateForImeVisibility(b);
            final float y = this.mStackAnimationController.getStackPosition().y;
            if (this.mFlyout.getVisibility() == 0) {
                final PhysicsAnimator<BubbleFlyoutView> instance = PhysicsAnimator.getInstance(this.mFlyout);
                instance.spring((FloatPropertyCompat<? super BubbleFlyoutView>)DynamicAnimation.TRANSLATION_Y, this.mFlyout.getTranslationY() + (animateForImeVisibility - y), BubbleStackView.FLYOUT_IME_ANIMATION_SPRING_CONFIG);
                instance.start();
            }
        }
    }
    
    public void onInitializeAccessibilityNodeInfoInternal(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_top_left, (CharSequence)this.getContext().getResources().getString(R$string.bubble_accessibility_action_move_top_left)));
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_top_right, (CharSequence)this.getContext().getResources().getString(R$string.bubble_accessibility_action_move_top_right)));
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_bottom_left, (CharSequence)this.getContext().getResources().getString(R$string.bubble_accessibility_action_move_bottom_left)));
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_bottom_right, (CharSequence)this.getContext().getResources().getString(R$string.bubble_accessibility_action_move_bottom_right)));
        accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_DISMISS);
        if (this.mIsExpanded) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_COLLAPSE);
        }
        else {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_EXPAND);
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent);
    }
    
    public void onOrientationChanged(final int mOrientation) {
        this.mOrientation = mOrientation;
        ((WindowManager)this.getContext().getSystemService("window")).getDefaultDisplay().getRealSize(this.mDisplaySize);
        final Resources resources = this.getContext().getResources();
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105471);
        this.mBubblePaddingTop = resources.getDimensionPixelSize(R$dimen.bubble_padding_top);
        final RectF allowableStackPositionRegion = this.mStackAnimationController.getAllowableStackPositionRegion();
        this.mWasOnLeftBeforeRotation = this.mStackAnimationController.isStackOnLeftSide();
        final float y = this.mStackAnimationController.getStackPosition().y;
        final float top = allowableStackPositionRegion.top;
        this.mVerticalPosPercentBeforeRotation = (y - top) / (allowableStackPositionRegion.bottom - top);
        this.addOnLayoutChangeListener(this.mOrientationChangedListener);
        this.hideFlyoutImmediate();
    }
    
    public void onThemeChanged() {
        this.setUpFlyout();
        this.setUpOverflow();
        this.setUpUserEducation();
    }
    
    public boolean performAccessibilityActionInternal(final int n, final Bundle bundle) {
        if (super.performAccessibilityActionInternal(n, bundle)) {
            return true;
        }
        final RectF allowableStackPositionRegion = this.mStackAnimationController.getAllowableStackPositionRegion();
        if (n == 1048576) {
            this.mBubbleData.dismissAll(6);
            return true;
        }
        if (n == 524288) {
            this.mBubbleData.setExpanded(false);
            return true;
        }
        if (n == 262144) {
            this.mBubbleData.setExpanded(true);
            return true;
        }
        if (n == R$id.action_move_top_left) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.left, allowableStackPositionRegion.top);
            return true;
        }
        if (n == R$id.action_move_top_right) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.right, allowableStackPositionRegion.top);
            return true;
        }
        if (n == R$id.action_move_bottom_left) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.left, allowableStackPositionRegion.bottom);
            return true;
        }
        if (n == R$id.action_move_bottom_right) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.right, allowableStackPositionRegion.bottom);
            return true;
        }
        return false;
    }
    
    boolean performBackPressIfNeeded() {
        if (this.isExpanded()) {
            final BubbleViewProvider mExpandedBubble = this.mExpandedBubble;
            if (mExpandedBubble != null) {
                return mExpandedBubble.getExpandedView().performBackPressIfNeeded();
            }
        }
        return false;
    }
    
    void removeBubble(final Bubble obj) {
        for (int i = 0; i < this.getBubbleCount(); ++i) {
            final View child = this.mBubbleContainer.getChildAt(i);
            if (child instanceof BadgedImageView && ((BadgedImageView)child).getKey().equals(obj.getKey())) {
                this.mBubbleContainer.removeViewAt(i);
                obj.cleanupViews();
                this.logBubbleEvent(obj, 5);
                return;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("was asked to remove Bubble, but didn't find the view! ");
        sb.append(obj);
        Log.d("Bubbles", sb.toString());
    }
    
    public void setExpandListener(final BubbleController.BubbleExpandListener mExpandListener) {
        this.mExpandListener = mExpandListener;
    }
    
    public void setExpanded(final boolean b) {
        if (b == this.mIsExpanded) {
            return;
        }
        final SysUiState mSysUiState = this.mSysUiState;
        mSysUiState.setFlag(16384, b);
        mSysUiState.commitUpdate(super.mContext.getDisplayId());
        if (this.mIsExpanded) {
            this.animateCollapse();
            this.logBubbleEvent(this.mExpandedBubble, 4);
        }
        else {
            this.animateExpansion();
            this.logBubbleEvent(this.mExpandedBubble, 3);
            this.logBubbleEvent(this.mExpandedBubble, 15);
        }
        this.notifyExpansionChanged(this.mExpandedBubble, this.mIsExpanded);
    }
    
    void setFlyoutStateForDragLength(float mFlyoutDragDeltaX) {
        if (this.mFlyout.getWidth() <= 0) {
            return;
        }
        final boolean stackOnLeftSide = this.mStackAnimationController.isStackOnLeftSide();
        this.mFlyoutDragDeltaX = mFlyoutDragDeltaX;
        float n = mFlyoutDragDeltaX;
        if (stackOnLeftSide) {
            n = -mFlyoutDragDeltaX;
        }
        final float b = n / this.mFlyout.getWidth();
        final BubbleFlyoutView mFlyout = this.mFlyout;
        mFlyoutDragDeltaX = 0.0f;
        mFlyout.setCollapsePercent(Math.min(1.0f, Math.max(0.0f, b)));
        final float n2 = fcmpg(b, 0.0f);
        if (n2 < 0 || b > 1.0f) {
            final float n3 = fcmpl(b, 1.0f);
            final boolean b2 = false;
            final int n4 = 1;
            final boolean b3 = n3 > 0;
            int n5 = 0;
            Label_0136: {
                if (!stackOnLeftSide || n3 <= 0) {
                    n5 = (b2 ? 1 : 0);
                    if (stackOnLeftSide) {
                        break Label_0136;
                    }
                    n5 = (b2 ? 1 : 0);
                    if (n2 >= 0) {
                        break Label_0136;
                    }
                }
                n5 = 1;
            }
            if (b3) {
                mFlyoutDragDeltaX = b - 1.0f;
            }
            else {
                mFlyoutDragDeltaX = b * -1.0f;
            }
            int n6;
            if (n5 != 0) {
                n6 = -1;
            }
            else {
                n6 = 1;
            }
            final float n7 = (float)n6;
            final float n8 = (float)this.mFlyout.getWidth();
            int n9 = n4;
            if (b3) {
                n9 = 2;
            }
            mFlyoutDragDeltaX = mFlyoutDragDeltaX * n7 * (n8 / (8.0f / n9));
        }
        final BubbleFlyoutView mFlyout2 = this.mFlyout;
        mFlyout2.setTranslationX(mFlyout2.getRestingTranslationX() + mFlyoutDragDeltaX);
    }
    
    public void setSelectedBubble(final BubbleViewProvider bubbleViewProvider) {
        final BubbleViewProvider mExpandedBubble = this.mExpandedBubble;
        if (mExpandedBubble != null && mExpandedBubble.equals(bubbleViewProvider)) {
            return;
        }
        final BubbleViewProvider mExpandedBubble2 = this.mExpandedBubble;
        this.mExpandedBubble = bubbleViewProvider;
        if (this.mIsExpanded) {
            this.mExpandedViewContainer.setAlpha(0.0f);
            this.mSurfaceSynchronizer.syncSurfaceAndRun(new _$$Lambda$BubbleStackView$Q6OzuEINn8c6GCtpuMgtnBaXtjs(this, mExpandedBubble2, bubbleViewProvider));
        }
    }
    
    void showExpandedViewContents(final int n) {
        final BubbleViewProvider mExpandedBubble = this.mExpandedBubble;
        if (mExpandedBubble != null && mExpandedBubble.getExpandedView() != null && this.mExpandedBubble.getExpandedView().getVirtualDisplayId() == n) {
            this.mExpandedBubble.setContentVisibility(true);
        }
    }
    
    public void subtractObscuredTouchableRegion(final Region region, final View view) {
    }
    
    void updateBubble(final Bubble bubble) {
        this.animateInFlyoutForBubble(bubble);
        this.requestUpdate();
        this.logBubbleEvent(bubble, 2);
    }
    
    public void updateBubbleOrder(final List<Bubble> list) {
        for (int i = 0; i < list.size(); ++i) {
            this.mBubbleContainer.reorderView((View)list.get(i).getIconView(), i);
        }
        this.updateBubbleZOrdersAndDotPosition(false);
        this.updatePointerPosition();
    }
    
    public void updateContentDescription() {
        if (this.mBubbleData.getBubbles().isEmpty()) {
            return;
        }
        final Bubble bubble = this.mBubbleData.getBubbles().get(0);
        final String appName = bubble.getAppName();
        final CharSequence charSequence = bubble.getEntry().getSbn().getNotification().extras.getCharSequence("android.title");
        String s = this.getResources().getString(R$string.notification_bubble_title);
        if (charSequence != null) {
            s = charSequence.toString();
        }
        final int i = this.mBubbleContainer.getChildCount() - 1;
        final String string = this.getResources().getString(R$string.bubble_content_description_single, new Object[] { s, appName });
        final String string2 = this.getResources().getString(R$string.bubble_content_description_stack, new Object[] { s, appName, i });
        if (!this.mIsExpanded) {
            if (i > 0) {
                this.mBubbleContainer.setContentDescription((CharSequence)string2);
            }
            else {
                this.mBubbleContainer.setContentDescription((CharSequence)string);
            }
        }
    }
    
    interface SurfaceSynchronizer
    {
        void syncSurfaceAndRun(final Runnable p0);
    }
}
