// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles.animation;

import android.content.res.Resources;
import com.android.systemui.R$dimen;
import androidx.dynamicanimation.animation.SpringAnimation;
import android.content.Context;
import com.google.android.collect.Sets;
import java.util.Set;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FlingAnimation;
import android.util.Log;
import android.graphics.RectF;
import com.android.systemui.util.animation.PhysicsAnimator;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import androidx.dynamicanimation.animation.SpringForce;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import java.util.HashMap;
import android.graphics.PointF;
import com.android.systemui.util.magnetictarget.MagnetizedObject;
import com.android.systemui.util.FloatingContentCoordinator;
import java.util.function.IntSupplier;
import android.graphics.Rect;

public class StackAnimationController extends PhysicsAnimationController
{
    private Rect mAnimatingToBounds;
    private int mBubbleBitmapSize;
    private IntSupplier mBubbleCountSupplier;
    private int mBubbleOffscreen;
    private int mBubblePaddingTop;
    private int mBubbleSize;
    private boolean mFirstBubbleSpringingToTouch;
    private FloatingContentCoordinator mFloatingContentCoordinator;
    private float mImeHeight;
    private boolean mIsMovingFromFlinging;
    private MagnetizedObject<StackAnimationController> mMagnetizedStack;
    private float mPreImeY;
    private PointF mRestingStackPosition;
    private boolean mSpringToTouchOnNextMotionEvent;
    private final FloatingContentCoordinator.FloatingContent mStackFloatingContent;
    private boolean mStackMovedToStartPosition;
    private float mStackOffset;
    private PointF mStackPosition;
    private HashMap<DynamicAnimation.ViewProperty, DynamicAnimation> mStackPositionAnimations;
    private int mStackStartingVerticalOffset;
    private float mStatusBarHeight;
    
    public StackAnimationController(final FloatingContentCoordinator mFloatingContentCoordinator, final IntSupplier mBubbleCountSupplier) {
        this.mStackPosition = new PointF(-1.0f, -1.0f);
        this.mAnimatingToBounds = new Rect();
        this.mStackMovedToStartPosition = false;
        this.mImeHeight = 0.0f;
        this.mPreImeY = -1.4E-45f;
        this.mStackPositionAnimations = new HashMap<DynamicAnimation.ViewProperty, DynamicAnimation>();
        this.mIsMovingFromFlinging = false;
        this.mFirstBubbleSpringingToTouch = false;
        this.mSpringToTouchOnNextMotionEvent = false;
        this.mStackFloatingContent = new FloatingContentCoordinator.FloatingContent() {
            private final Rect mFloatingBoundsOnScreen = new Rect();
            
            @Override
            public Rect getAllowedFloatingBoundsRegion() {
                final Rect floatingBoundsOnScreen = this.getFloatingBoundsOnScreen();
                final Rect rect = new Rect();
                StackAnimationController.this.getAllowableStackPositionRegion().roundOut(rect);
                rect.right += floatingBoundsOnScreen.width();
                rect.bottom += floatingBoundsOnScreen.height();
                return rect;
            }
            
            @Override
            public Rect getFloatingBoundsOnScreen() {
                if (!StackAnimationController.this.mAnimatingToBounds.isEmpty()) {
                    return StackAnimationController.this.mAnimatingToBounds;
                }
                if (StackAnimationController.this.mLayout.getChildCount() > 0) {
                    this.mFloatingBoundsOnScreen.set((int)StackAnimationController.this.mStackPosition.x, (int)StackAnimationController.this.mStackPosition.y, (int)StackAnimationController.this.mStackPosition.x + StackAnimationController.this.mBubbleSize, (int)StackAnimationController.this.mStackPosition.y + StackAnimationController.this.mBubbleSize + StackAnimationController.this.mBubblePaddingTop);
                }
                else {
                    this.mFloatingBoundsOnScreen.setEmpty();
                }
                return this.mFloatingBoundsOnScreen;
            }
            
            @Override
            public void moveToBounds(final Rect rect) {
                StackAnimationController.this.springStack((float)rect.left, (float)rect.top, 200.0f);
            }
        };
        this.mFloatingContentCoordinator = mFloatingContentCoordinator;
        this.mBubbleCountSupplier = mBubbleCountSupplier;
    }
    
    private void animateInBubble(final View view, int n) {
        if (!((PhysicsAnimationLayout.PhysicsAnimationController)this).isActiveController()) {
            return;
        }
        final float offsetForChainedPropertyAnimation = this.getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_X);
        view.setTranslationX(this.mStackPosition.x + n * offsetForChainedPropertyAnimation);
        view.setTranslationY(this.mStackPosition.y);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        if (++n < super.mLayout.getChildCount()) {
            final PhysicsPropertyAnimator animationForChildAtIndex = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChildAtIndex(n);
            animationForChildAtIndex.translationX(this.mStackPosition.x + offsetForChainedPropertyAnimation * n, new Runnable[0]);
            animationForChildAtIndex.withStiffness(200.0f);
            animationForChildAtIndex.start(new Runnable[0]);
        }
        final PhysicsPropertyAnimator animationForChild = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChild(view);
        animationForChild.scaleX(1.0f, new Runnable[0]);
        animationForChild.scaleY(1.0f, new Runnable[0]);
        animationForChild.withStiffness(1000.0f);
        long n2;
        if (super.mLayout.getChildCount() > 1) {
            n2 = 25L;
        }
        else {
            n2 = 0L;
        }
        animationForChild.withStartDelay(n2);
        animationForChild.start(new Runnable[0]);
    }
    
    private void cancelStackPositionAnimation(final DynamicAnimation.ViewProperty viewProperty) {
        if (this.mStackPositionAnimations.containsKey(viewProperty)) {
            this.mStackPositionAnimations.get(viewProperty).cancel();
        }
    }
    
    private int getBubbleCount() {
        return this.mBubbleCountSupplier.getAsInt();
    }
    
    private boolean isStackPositionSet() {
        return this.mStackMovedToStartPosition;
    }
    
    private boolean isStackStuckToTarget() {
        final MagnetizedObject<StackAnimationController> mMagnetizedStack = this.mMagnetizedStack;
        return mMagnetizedStack != null && mMagnetizedStack.getObjectStuckToTarget();
    }
    
    private void moveFirstBubbleWithStackFollowing(final DynamicAnimation.ViewProperty viewProperty, final float n) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X)) {
            this.mStackPosition.x = n;
        }
        else if (viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            this.mStackPosition.y = n;
        }
        if (super.mLayout.getChildCount() > 0) {
            viewProperty.setValue(super.mLayout.getChildAt(0), n);
            if (super.mLayout.getChildCount() > 1) {
                final PhysicsPropertyAnimator animationForChildAtIndex = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChildAtIndex(1);
                animationForChildAtIndex.property(viewProperty, n + this.getOffsetForChainedPropertyAnimation(viewProperty), new Runnable[0]);
                animationForChildAtIndex.start(new Runnable[0]);
            }
        }
    }
    
    private void moveStackToStartPosition() {
        super.mLayout.setVisibility(4);
        super.mLayout.post((Runnable)new _$$Lambda$StackAnimationController$jfnsYuE3h8YMEP49TtOumHHuV4Q(this));
    }
    
    private void notifyFloatingCoordinatorStackAnimatingTo(final float n, final float n2) {
        final Rect floatingBoundsOnScreen = this.mStackFloatingContent.getFloatingBoundsOnScreen();
        floatingBoundsOnScreen.offsetTo((int)n, (int)n2);
        this.mAnimatingToBounds = floatingBoundsOnScreen;
        this.mFloatingContentCoordinator.onContentMoved(this.mStackFloatingContent);
    }
    
    public float animateForImeVisibility(final boolean b) {
        float n = this.getAllowableStackPositionRegion().bottom;
        Label_0068: {
            if (b) {
                final float y = this.mStackPosition.y;
                if (y > n && this.mPreImeY == -1.4E-45f) {
                    this.mPreImeY = y;
                    break Label_0068;
                }
            }
            else {
                n = this.mPreImeY;
                if (n != -1.4E-45f) {
                    this.mPreImeY = -1.4E-45f;
                    break Label_0068;
                }
            }
            n = -1.4E-45f;
        }
        final float n2 = fcmpl(n, -1.4E-45f);
        if (n2 != 0) {
            final DynamicAnimation.ViewProperty translation_Y = DynamicAnimation.TRANSLATION_Y;
            final SpringForce springForce = this.getSpringForce(translation_Y, null);
            springForce.setStiffness(200.0f);
            this.springFirstBubbleWithStackFollowing(translation_Y, springForce, 0.0f, n, new Runnable[0]);
            this.notifyFloatingCoordinatorStackAnimatingTo(this.mStackPosition.x, n);
        }
        if (n2 == 0) {
            n = this.mStackPosition.y;
        }
        return n;
    }
    
    public void cancelStackPositionAnimations() {
        this.cancelStackPositionAnimation(DynamicAnimation.TRANSLATION_X);
        this.cancelStackPositionAnimation(DynamicAnimation.TRANSLATION_Y);
        ((PhysicsAnimationLayout.PhysicsAnimationController)this).removeEndActionForProperty(DynamicAnimation.TRANSLATION_X);
        ((PhysicsAnimationLayout.PhysicsAnimationController)this).removeEndActionForProperty(DynamicAnimation.TRANSLATION_Y);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("StackAnimationController state:");
        printWriter.print("  isActive:             ");
        printWriter.println(((PhysicsAnimationLayout.PhysicsAnimationController)this).isActiveController());
        printWriter.print("  restingStackPos:      ");
        final PointF mRestingStackPosition = this.mRestingStackPosition;
        String string;
        if (mRestingStackPosition != null) {
            string = mRestingStackPosition.toString();
        }
        else {
            string = "null";
        }
        printWriter.println(string);
        printWriter.print("  currentStackPos:      ");
        printWriter.println(this.mStackPosition.toString());
        printWriter.print("  isMovingFromFlinging: ");
        printWriter.println(this.mIsMovingFromFlinging);
        printWriter.print("  withinDismiss:        ");
        printWriter.println(this.isStackStuckToTarget());
        printWriter.print("  firstBubbleSpringing: ");
        printWriter.println(this.mFirstBubbleSpringingToTouch);
    }
    
    public float flingStackThenSpringToEdge(float n, final float n2, final float n3) {
        final boolean b = (n - this.mBubbleBitmapSize / 2 < super.mLayout.getWidth() / 2) ? (n2 < 750.0f) : (n2 < -750.0f);
        final RectF allowableStackPositionRegion = this.getAllowableStackPositionRegion();
        float f;
        if (b) {
            f = allowableStackPositionRegion.left;
        }
        else {
            f = allowableStackPositionRegion.right;
        }
        final PhysicsAnimationLayout mLayout = super.mLayout;
        if (mLayout != null) {
            if (mLayout.getChildCount() != 0) {
                n = (f - n) * 9.24f;
                this.notifyFloatingCoordinatorStackAnimatingTo(f, PhysicsAnimator.estimateFlingEndValue(this.mStackPosition.y, n3, new PhysicsAnimator.FlingConfig(2.2f, allowableStackPositionRegion.top, allowableStackPositionRegion.bottom)));
                if (b) {
                    n = Math.min(n, n2);
                }
                else {
                    n = Math.max(n, n2);
                }
                final DynamicAnimation.ViewProperty translation_X = DynamicAnimation.TRANSLATION_X;
                final SpringForce springForce = new SpringForce();
                springForce.setStiffness(750.0f);
                springForce.setDampingRatio(0.85f);
                this.flingThenSpringFirstBubbleWithStackFollowing(translation_X, n, 2.2f, springForce, f);
                final DynamicAnimation.ViewProperty translation_Y = DynamicAnimation.TRANSLATION_Y;
                final SpringForce springForce2 = new SpringForce();
                springForce2.setStiffness(750.0f);
                springForce2.setDampingRatio(0.85f);
                this.flingThenSpringFirstBubbleWithStackFollowing(translation_Y, n3, 2.2f, springForce2, null);
                this.mFirstBubbleSpringingToTouch = false;
                this.mIsMovingFromFlinging = true;
            }
        }
        return f;
    }
    
    protected void flingThenSpringFirstBubbleWithStackFollowing(final DynamicAnimation.ViewProperty key, final float startVelocity, final float friction, final SpringForce springForce, final Float n) {
        Log.d("Bubbs.StackCtrl", String.format("Flinging %s.", PhysicsAnimationLayout.getReadablePropertyName(key)));
        final StackPositionProperty stackPositionProperty = new StackPositionProperty(key);
        final float value = stackPositionProperty.getValue(this);
        final RectF allowableStackPositionRegion = this.getAllowableStackPositionRegion();
        float b;
        if (key.equals(DynamicAnimation.TRANSLATION_X)) {
            b = allowableStackPositionRegion.left;
        }
        else {
            b = allowableStackPositionRegion.top;
        }
        float b2;
        if (key.equals(DynamicAnimation.TRANSLATION_X)) {
            b2 = allowableStackPositionRegion.right;
        }
        else {
            b2 = allowableStackPositionRegion.bottom;
        }
        final FlingAnimation value2 = new FlingAnimation((K)this, (FloatPropertyCompat<K>)stackPositionProperty);
        value2.setFriction(friction);
        value2.setStartVelocity(startVelocity);
        value2.setMinValue(Math.min(value, b));
        value2.setMaxValue(Math.max(value, b2));
        value2.addEndListener((DynamicAnimation.OnAnimationEndListener)new _$$Lambda$StackAnimationController$bZgezj9fblRl_isenTD4ApewvoU(this, key, springForce, n, b, b2));
        this.cancelStackPositionAnimation(key);
        this.mStackPositionAnimations.put(key, value2);
        value2.start();
    }
    
    public RectF getAllowableStackPositionRegion() {
        final WindowInsets rootWindowInsets = super.mLayout.getRootWindowInsets();
        final RectF rectF = new RectF();
        if (rootWindowInsets != null) {
            final int n = -this.mBubbleOffscreen;
            final int systemWindowInsetLeft = rootWindowInsets.getSystemWindowInsetLeft();
            final DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
            final int n2 = 0;
            int safeInsetLeft;
            if (displayCutout != null) {
                safeInsetLeft = rootWindowInsets.getDisplayCutout().getSafeInsetLeft();
            }
            else {
                safeInsetLeft = 0;
            }
            rectF.left = (float)(n + Math.max(systemWindowInsetLeft, safeInsetLeft));
            final int width = super.mLayout.getWidth();
            final int mBubbleSize = this.mBubbleSize;
            final int mBubbleOffscreen = this.mBubbleOffscreen;
            final int systemWindowInsetRight = rootWindowInsets.getSystemWindowInsetRight();
            int safeInsetRight;
            if (rootWindowInsets.getDisplayCutout() != null) {
                safeInsetRight = rootWindowInsets.getDisplayCutout().getSafeInsetRight();
            }
            else {
                safeInsetRight = 0;
            }
            rectF.right = (float)(width - mBubbleSize + mBubbleOffscreen - Math.max(systemWindowInsetRight, safeInsetRight));
            final float n3 = (float)this.mBubblePaddingTop;
            final float mStatusBarHeight = this.mStatusBarHeight;
            final DisplayCutout displayCutout2 = rootWindowInsets.getDisplayCutout();
            final float n4 = 0.0f;
            float b;
            if (displayCutout2 != null) {
                b = (float)rootWindowInsets.getDisplayCutout().getSafeInsetTop();
            }
            else {
                b = 0.0f;
            }
            rectF.top = n3 + Math.max(mStatusBarHeight, b);
            final int height = super.mLayout.getHeight();
            final int mBubbleSize2 = this.mBubbleSize;
            final int mBubblePaddingTop = this.mBubblePaddingTop;
            final float n5 = (float)(height - mBubbleSize2 - mBubblePaddingTop);
            final float mImeHeight = this.mImeHeight;
            float n6 = n4;
            if (mImeHeight != -1.4E-45f) {
                n6 = mImeHeight + mBubblePaddingTop;
            }
            final int stableInsetBottom = rootWindowInsets.getStableInsetBottom();
            int safeInsetBottom = n2;
            if (rootWindowInsets.getDisplayCutout() != null) {
                safeInsetBottom = rootWindowInsets.getDisplayCutout().getSafeInsetBottom();
            }
            rectF.bottom = n5 - n6 - Math.max(stableInsetBottom, safeInsetBottom);
        }
        return rectF;
    }
    
    @Override
    Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
        return (Set<DynamicAnimation.ViewProperty>)Sets.newHashSet((Object[])new DynamicAnimation.ViewProperty[] { DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y, DynamicAnimation.ALPHA, DynamicAnimation.SCALE_X, DynamicAnimation.SCALE_Y });
    }
    
    public PointF getDefaultStartPosition() {
        return new PointF(this.getAllowableStackPositionRegion().left, this.getAllowableStackPositionRegion().top + this.mStackStartingVerticalOffset);
    }
    
    public MagnetizedObject<StackAnimationController> getMagnetizedStack(final MagnetizedObject.MagneticTarget magneticTarget) {
        if (this.mMagnetizedStack == null) {
            (this.mMagnetizedStack = new MagnetizedObject<StackAnimationController>(super.mLayout.getContext(), this, new StackPositionProperty(DynamicAnimation.TRANSLATION_X), new StackPositionProperty(DynamicAnimation.TRANSLATION_Y)) {
                @Override
                public float getHeight(final StackAnimationController stackAnimationController) {
                    return (float)StackAnimationController.this.mBubbleSize;
                }
                
                @Override
                public void getLocationOnScreen(final StackAnimationController stackAnimationController, final int[] array) {
                    array[0] = (int)StackAnimationController.this.mStackPosition.x;
                    array[1] = (int)StackAnimationController.this.mStackPosition.y;
                }
                
                @Override
                public float getWidth(final StackAnimationController stackAnimationController) {
                    return (float)StackAnimationController.this.mBubbleSize;
                }
            }).addTarget(magneticTarget);
            this.mMagnetizedStack.setHapticsEnabled(true);
            this.mMagnetizedStack.setFlingToTargetMinVelocity(4000.0f);
        }
        return this.mMagnetizedStack;
    }
    
    @Override
    int getNextAnimationInChain(final DynamicAnimation.ViewProperty viewProperty, final int n) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X) || viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            return n + 1;
        }
        if (this.isStackStuckToTarget()) {
            return n + 1;
        }
        return -1;
    }
    
    @Override
    float getOffsetForChainedPropertyAnimation(final DynamicAnimation.ViewProperty viewProperty) {
        if (!viewProperty.equals(DynamicAnimation.TRANSLATION_X)) {
            return 0.0f;
        }
        if (this.isStackStuckToTarget()) {
            return 0.0f;
        }
        float mStackOffset;
        if (super.mLayout.isFirstChildXLeftOfCenter(this.mStackPosition.x)) {
            mStackOffset = -this.mStackOffset;
        }
        else {
            mStackOffset = this.mStackOffset;
        }
        return mStackOffset;
    }
    
    @Override
    SpringForce getSpringForce(final DynamicAnimation.ViewProperty viewProperty, final View view) {
        final SpringForce springForce = new SpringForce();
        springForce.setDampingRatio(0.9f);
        float stiffness;
        if (this.mIsMovingFromFlinging) {
            stiffness = 20000.0f;
        }
        else {
            stiffness = 12000.0f;
        }
        springForce.setStiffness(stiffness);
        return springForce;
    }
    
    public PointF getStackPosition() {
        return this.mStackPosition;
    }
    
    public PointF getStackPositionAlongNearestHorizontalEdge() {
        final PointF stackPosition = this.getStackPosition();
        final boolean firstChildXLeftOfCenter = super.mLayout.isFirstChildXLeftOfCenter(stackPosition.x);
        final RectF allowableStackPositionRegion = this.getAllowableStackPositionRegion();
        float x;
        if (firstChildXLeftOfCenter) {
            x = allowableStackPositionRegion.left;
        }
        else {
            x = allowableStackPositionRegion.right;
        }
        stackPosition.x = x;
        return stackPosition;
    }
    
    public void implodeStack(final Runnable runnable) {
        final PhysicsPropertyAnimator animationForChildAtIndex = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChildAtIndex(0);
        animationForChildAtIndex.scaleX(0.5f, new Runnable[0]);
        animationForChildAtIndex.scaleY(0.5f, new Runnable[0]);
        animationForChildAtIndex.alpha(0.0f, new Runnable[0]);
        animationForChildAtIndex.withDampingRatio(1.0f);
        animationForChildAtIndex.withStiffness(10000.0f);
        animationForChildAtIndex.start(runnable);
    }
    
    public boolean isStackOnLeftSide() {
        final PhysicsAnimationLayout mLayout = super.mLayout;
        boolean b2;
        final boolean b = b2 = true;
        if (mLayout != null) {
            if (!this.isStackPositionSet()) {
                b2 = b;
            }
            else {
                b2 = (this.mStackPosition.x + this.mBubbleBitmapSize / 2 < super.mLayout.getWidth() / 2 && b);
            }
        }
        return b2;
    }
    
    public void moveFirstBubbleWithStackFollowing(final float n, final float n2) {
        this.mAnimatingToBounds.setEmpty();
        this.mPreImeY = -1.4E-45f;
        this.moveFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, n);
        this.moveFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, n2);
        this.mIsMovingFromFlinging = false;
    }
    
    public void moveStackFromTouch(final float n, final float n2) {
        if (this.mSpringToTouchOnNextMotionEvent) {
            this.springStack(n, n2, 12000.0f);
            this.mSpringToTouchOnNextMotionEvent = false;
            this.mFirstBubbleSpringingToTouch = true;
        }
        else if (this.mFirstBubbleSpringingToTouch) {
            final SpringAnimation springAnimation = this.mStackPositionAnimations.get(DynamicAnimation.TRANSLATION_X);
            final SpringAnimation springAnimation2 = this.mStackPositionAnimations.get(DynamicAnimation.TRANSLATION_Y);
            if (!springAnimation.isRunning() && !springAnimation2.isRunning()) {
                this.mFirstBubbleSpringingToTouch = false;
            }
            else {
                springAnimation.animateToFinalPosition(n);
                springAnimation2.animateToFinalPosition(n2);
            }
        }
        if (!this.mFirstBubbleSpringingToTouch && !this.isStackStuckToTarget()) {
            this.moveFirstBubbleWithStackFollowing(n, n2);
        }
    }
    
    public void moveStackToSimilarPositionAfterRotation(final boolean b, final float n) {
        final RectF allowableStackPositionRegion = this.getAllowableStackPositionRegion();
        final float bottom = allowableStackPositionRegion.bottom;
        final float top = allowableStackPositionRegion.top;
        float n2;
        if (b) {
            n2 = allowableStackPositionRegion.left;
        }
        else {
            n2 = allowableStackPositionRegion.right;
        }
        this.setStackPosition(new PointF(n2, (bottom - top) * n + allowableStackPositionRegion.top));
    }
    
    @Override
    void onActiveControllerForLayout(final PhysicsAnimationLayout physicsAnimationLayout) {
        final Resources resources = physicsAnimationLayout.getResources();
        this.mStackOffset = (float)resources.getDimensionPixelSize(R$dimen.bubble_stack_offset);
        this.mBubbleSize = resources.getDimensionPixelSize(R$dimen.individual_bubble_size);
        this.mBubbleBitmapSize = resources.getDimensionPixelSize(R$dimen.bubble_bitmap_size);
        this.mBubblePaddingTop = resources.getDimensionPixelSize(R$dimen.bubble_padding_top);
        this.mBubbleOffscreen = resources.getDimensionPixelSize(R$dimen.bubble_stack_offscreen);
        this.mStackStartingVerticalOffset = resources.getDimensionPixelSize(R$dimen.bubble_stack_starting_offset_y);
        this.mStatusBarHeight = (float)resources.getDimensionPixelSize(17105471);
    }
    
    @Override
    void onChildAdded(final View view, final int n) {
        if (this.isStackStuckToTarget()) {
            return;
        }
        if (this.getBubbleCount() == 1) {
            this.moveStackToStartPosition();
        }
        else if (this.isStackPositionSet() && super.mLayout.indexOfChild(view) == 0) {
            this.animateInBubble(view, n);
        }
    }
    
    @Override
    void onChildRemoved(final View view, final int n, final Runnable runnable) {
        final float offsetForChainedPropertyAnimation = this.getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_X);
        final PhysicsPropertyAnimator animationForChild = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChild(view);
        animationForChild.alpha(0.0f, runnable);
        animationForChild.scaleX(1.15f, new Runnable[0]);
        animationForChild.scaleY(1.15f, new Runnable[0]);
        animationForChild.translationX(this.mStackPosition.x - -offsetForChainedPropertyAnimation * 4.0f, new Runnable[0]);
        animationForChild.start(new Runnable[0]);
        if (this.getBubbleCount() > 0) {
            final PhysicsPropertyAnimator animationForChildAtIndex = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChildAtIndex(0);
            animationForChildAtIndex.translationX(this.mStackPosition.x, new Runnable[0]);
            animationForChildAtIndex.start(new Runnable[0]);
        }
        else {
            PointF stackPosition;
            if ((stackPosition = this.mRestingStackPosition) == null) {
                stackPosition = this.getDefaultStartPosition();
            }
            this.setStackPosition(stackPosition);
            this.mFloatingContentCoordinator.onContentRemoved(this.mStackFloatingContent);
        }
    }
    
    @Override
    void onChildReordered(final View view, final int n, final int n2) {
        if (this.isStackPositionSet()) {
            this.setStackPosition(this.mStackPosition);
        }
    }
    
    public void onUnstuckFromTarget() {
        this.mSpringToTouchOnNextMotionEvent = true;
    }
    
    public void setImeHeight(final int n) {
        this.mImeHeight = (float)n;
    }
    
    public void setStackPosition(final PointF pointF) {
        final float x = pointF.x;
        int i = 0;
        Log.d("Bubbs.StackCtrl", String.format("Setting position to (%f, %f).", x, pointF.y));
        this.mStackPosition.set(pointF.x, pointF.y);
        if (this.mRestingStackPosition == null) {
            this.mRestingStackPosition = new PointF();
        }
        this.mRestingStackPosition.set(this.mStackPosition);
        if (((PhysicsAnimationLayout.PhysicsAnimationController)this).isActiveController()) {
            super.mLayout.cancelAllAnimationsOfProperties(DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y);
            this.cancelStackPositionAnimations();
            final float offsetForChainedPropertyAnimation = this.getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_X);
            final float offsetForChainedPropertyAnimation2 = this.getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_Y);
            while (i < super.mLayout.getChildCount()) {
                final View child = super.mLayout.getChildAt(i);
                final float x2 = pointF.x;
                final float n = (float)i;
                child.setTranslationX(x2 + n * offsetForChainedPropertyAnimation);
                super.mLayout.getChildAt(i).setTranslationY(pointF.y + n * offsetForChainedPropertyAnimation2);
                ++i;
            }
        }
    }
    
    protected void springFirstBubbleWithStackFollowing(final DynamicAnimation.ViewProperty key, final SpringForce spring, final float startVelocity, final float f, final Runnable... array) {
        if (super.mLayout.getChildCount() == 0) {
            return;
        }
        Log.d("Bubbs.StackCtrl", String.format("Springing %s to final position %f.", PhysicsAnimationLayout.getReadablePropertyName(key), f));
        final SpringAnimation springAnimation = new SpringAnimation((K)this, (FloatPropertyCompat<K>)new StackPositionProperty(key));
        springAnimation.setSpring(spring);
        springAnimation.addEndListener((DynamicAnimation.OnAnimationEndListener)new _$$Lambda$StackAnimationController$dqZ4e0qj3qPo8gvBLRbZzxV8wpE(this, array));
        final SpringAnimation springAnimation2 = springAnimation;
        springAnimation2.setStartVelocity(startVelocity);
        final SpringAnimation value = springAnimation2;
        this.cancelStackPositionAnimation(key);
        this.mStackPositionAnimations.put(key, value);
        value.animateToFinalPosition(f);
    }
    
    public void springStack(final float n, final float n2, final float n3) {
        this.notifyFloatingCoordinatorStackAnimatingTo(n, n2);
        final DynamicAnimation.ViewProperty translation_X = DynamicAnimation.TRANSLATION_X;
        final SpringForce springForce = new SpringForce();
        springForce.setStiffness(n3);
        springForce.setDampingRatio(0.85f);
        this.springFirstBubbleWithStackFollowing(translation_X, springForce, 0.0f, n, new Runnable[0]);
        final DynamicAnimation.ViewProperty translation_Y = DynamicAnimation.TRANSLATION_Y;
        final SpringForce springForce2 = new SpringForce();
        springForce2.setStiffness(n3);
        springForce2.setDampingRatio(0.85f);
        this.springFirstBubbleWithStackFollowing(translation_Y, springForce2, 0.0f, n2, new Runnable[0]);
    }
    
    public void springStackAfterFling(final float n, final float n2) {
        this.springStack(n, n2, 750.0f);
    }
    
    public void updateOrientation(final int n) {
        final PhysicsAnimationLayout mLayout = super.mLayout;
        if (mLayout != null) {
            final Resources resources = mLayout.getContext().getResources();
            this.mBubblePaddingTop = resources.getDimensionPixelSize(R$dimen.bubble_padding_top);
            this.mStatusBarHeight = (float)resources.getDimensionPixelSize(17105471);
        }
    }
    
    private class StackPositionProperty extends FloatPropertyCompat<StackAnimationController>
    {
        private final DynamicAnimation.ViewProperty mProperty;
        
        private StackPositionProperty(final DynamicAnimation.ViewProperty mProperty) {
            super(mProperty.toString());
            this.mProperty = mProperty;
        }
        
        @Override
        public float getValue(final StackAnimationController stackAnimationController) {
            float value;
            if (StackAnimationController.this.mLayout.getChildCount() > 0) {
                value = this.mProperty.getValue(StackAnimationController.this.mLayout.getChildAt(0));
            }
            else {
                value = 0.0f;
            }
            return value;
        }
        
        @Override
        public void setValue(final StackAnimationController stackAnimationController, final float n) {
            StackAnimationController.this.moveFirstBubbleWithStackFollowing(this.mProperty, n);
        }
    }
}
