// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles.animation;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import android.content.Context;
import android.content.res.Resources;
import com.android.systemui.R$integer;
import com.android.systemui.R$dimen;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.collect.Sets;
import java.util.Set;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import androidx.dynamicanimation.animation.DynamicAnimation;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.graphics.Path;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import android.view.View;
import com.android.systemui.util.magnetictarget.MagnetizedObject;
import android.graphics.Point;
import android.graphics.PointF;

public class ExpandedAnimationController extends PhysicsAnimationController
{
    private Runnable mAfterCollapse;
    private Runnable mAfterExpand;
    private boolean mAnimatingCollapse;
    private boolean mAnimatingExpand;
    private boolean mBubbleDraggedOutEnough;
    private float mBubblePaddingTop;
    private float mBubbleSizePx;
    private int mBubblesMaxRendered;
    private PointF mCollapsePoint;
    private Point mDisplaySize;
    private int mExpandedViewPadding;
    private MagnetizedObject<View> mMagnetizedBubbleDraggingOut;
    private int mScreenOrientation;
    private float mSpaceBetweenBubbles;
    private boolean mSpringToTouchOnNextMotionEvent;
    private boolean mSpringingBubbleToTouch;
    private float mStackOffsetPx;
    private float mStatusBarHeight;
    
    public ExpandedAnimationController(final Point point, final int mExpandedViewPadding, final int n) {
        this.mAnimatingExpand = false;
        this.mAnimatingCollapse = false;
        this.mSpringingBubbleToTouch = false;
        this.mSpringToTouchOnNextMotionEvent = false;
        this.mBubbleDraggedOutEnough = false;
        this.updateOrientation(n, point);
        this.mExpandedViewPadding = mExpandedViewPadding;
    }
    
    private float getAvailableScreenWidth(final boolean b) {
        final float n = (float)this.mDisplaySize.x;
        final PhysicsAnimationLayout mLayout = super.mLayout;
        WindowInsets rootWindowInsets;
        if (mLayout != null) {
            rootWindowInsets = mLayout.getRootWindowInsets();
        }
        else {
            rootWindowInsets = null;
        }
        float n2 = n;
        if (rootWindowInsets != null) {
            final DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
            int stableInsetRight = 0;
            int safeInsetLeft;
            int safeInsetRight;
            if (displayCutout != null) {
                safeInsetLeft = displayCutout.getSafeInsetLeft();
                safeInsetRight = displayCutout.getSafeInsetRight();
            }
            else {
                safeInsetRight = (safeInsetLeft = 0);
            }
            int stableInsetLeft;
            if (b) {
                stableInsetLeft = rootWindowInsets.getStableInsetLeft();
            }
            else {
                stableInsetLeft = 0;
            }
            if (b) {
                stableInsetRight = rootWindowInsets.getStableInsetRight();
            }
            n2 = n - Math.max(stableInsetLeft, safeInsetLeft) - Math.max(stableInsetRight, safeInsetRight);
        }
        return n2;
    }
    
    private float getRowLeft() {
        final PhysicsAnimationLayout mLayout = super.mLayout;
        if (mLayout == null) {
            return 0.0f;
        }
        return this.getAvailableScreenWidth(false) / 2.0f - (mLayout.getChildCount() * this.mBubbleSizePx + (super.mLayout.getChildCount() - 1) * this.mSpaceBetweenBubbles) / 2.0f;
    }
    
    private void springBubbleTo(final View view, final float n, final float n2) {
        final PhysicsPropertyAnimator animationForChild = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChild(view);
        animationForChild.translationX(n, new Runnable[0]);
        animationForChild.translationY(n2, new Runnable[0]);
        animationForChild.withStiffness(10000.0f);
        animationForChild.start(new Runnable[0]);
    }
    
    private void startOrUpdatePathAnimation(final boolean b) {
        Runnable runnable;
        if (b) {
            runnable = new _$$Lambda$ExpandedAnimationController$gE2Cl95ubR0Pg2NTtDLGoNhSLoM(this);
        }
        else {
            runnable = new _$$Lambda$ExpandedAnimationController$WjMaDVcvCcyW4ns9Ixw4Q7pkHT4(this);
        }
        ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationsForChildrenFromIndex(0, (ChildAnimationConfigurator)new _$$Lambda$ExpandedAnimationController$7Il03mDM0nM9UqZB95uu3PfeMxA(this, b)).startAll(runnable);
    }
    
    private void updateBubblePositions() {
        if (!this.mAnimatingExpand) {
            if (!this.mAnimatingCollapse) {
                for (int i = 0; i < super.mLayout.getChildCount(); ++i) {
                    final View child = super.mLayout.getChildAt(i);
                    if (child.equals(this.getDraggedOutBubble())) {
                        return;
                    }
                    final PhysicsPropertyAnimator animationForChild = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChild(child);
                    animationForChild.translationX(this.getBubbleLeft(i), new Runnable[0]);
                    animationForChild.start(new Runnable[0]);
                }
            }
        }
    }
    
    public void collapseBackToStack(final PointF mCollapsePoint, final Runnable mAfterCollapse) {
        this.mAnimatingExpand = false;
        this.mAnimatingCollapse = true;
        this.mAfterCollapse = mAfterCollapse;
        this.mCollapsePoint = mCollapsePoint;
        this.startOrUpdatePathAnimation(false);
    }
    
    public void dismissDraggedOutBubble(final View view, final Runnable runnable) {
        if (view == null) {
            return;
        }
        final PhysicsPropertyAnimator animationForChild = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChild(view);
        animationForChild.withStiffness(10000.0f);
        animationForChild.scaleX(1.1f, new Runnable[0]);
        animationForChild.scaleY(1.1f, new Runnable[0]);
        animationForChild.alpha(0.0f, runnable);
        animationForChild.start(new Runnable[0]);
        this.updateBubblePositions();
    }
    
    public void dragBubbleOut(final View view, final float translationX, final float translationY) {
        final boolean mSpringToTouchOnNextMotionEvent = this.mSpringToTouchOnNextMotionEvent;
        final boolean b = true;
        if (mSpringToTouchOnNextMotionEvent) {
            this.springBubbleTo(this.mMagnetizedBubbleDraggingOut.getUnderlyingObject(), translationX, translationY);
            this.mSpringToTouchOnNextMotionEvent = false;
            this.mSpringingBubbleToTouch = true;
        }
        else if (this.mSpringingBubbleToTouch) {
            if (super.mLayout.arePropertiesAnimatingOnView(view, DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y)) {
                this.springBubbleTo(this.mMagnetizedBubbleDraggingOut.getUnderlyingObject(), translationX, translationY);
            }
            else {
                this.mSpringingBubbleToTouch = false;
            }
        }
        if (!this.mSpringingBubbleToTouch && !this.mMagnetizedBubbleDraggingOut.getObjectStuckToTarget()) {
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }
        boolean mBubbleDraggedOutEnough = b;
        if (translationY <= this.getExpandedY() + this.mBubbleSizePx) {
            mBubbleDraggedOutEnough = (translationY < this.getExpandedY() - this.mBubbleSizePx && b);
        }
        if (mBubbleDraggedOutEnough != this.mBubbleDraggedOutEnough) {
            this.updateBubblePositions();
            this.mBubbleDraggedOutEnough = mBubbleDraggedOutEnough;
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("ExpandedAnimationController state:");
        printWriter.print("  isActive:          ");
        printWriter.println(((PhysicsAnimationLayout.PhysicsAnimationController)this).isActiveController());
        printWriter.print("  animatingExpand:   ");
        printWriter.println(this.mAnimatingExpand);
        printWriter.print("  animatingCollapse: ");
        printWriter.println(this.mAnimatingCollapse);
        printWriter.print("  springingBubble:   ");
        printWriter.println(this.mSpringingBubbleToTouch);
    }
    
    public void expandFromStack(final Runnable mAfterExpand) {
        this.mAnimatingCollapse = false;
        this.mAnimatingExpand = true;
        this.mAfterExpand = mAfterExpand;
        this.startOrUpdatePathAnimation(true);
    }
    
    @Override
    Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
        return (Set<DynamicAnimation.ViewProperty>)Sets.newHashSet((Object[])new DynamicAnimation.ViewProperty[] { DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y, DynamicAnimation.SCALE_X, DynamicAnimation.SCALE_Y, DynamicAnimation.ALPHA });
    }
    
    public float getBubbleLeft(final int n) {
        return this.getRowLeft() + n * (this.mBubbleSizePx + this.mSpaceBetweenBubbles);
    }
    
    public View getDraggedOutBubble() {
        final MagnetizedObject<View> mMagnetizedBubbleDraggingOut = this.mMagnetizedBubbleDraggingOut;
        View view;
        if (mMagnetizedBubbleDraggingOut == null) {
            view = null;
        }
        else {
            view = mMagnetizedBubbleDraggingOut.getUnderlyingObject();
        }
        return view;
    }
    
    public float getExpandedY() {
        final PhysicsAnimationLayout mLayout = super.mLayout;
        float b = 0.0f;
        if (mLayout != null && mLayout.getRootWindowInsets() != null) {
            final WindowInsets rootWindowInsets = super.mLayout.getRootWindowInsets();
            final float mBubblePaddingTop = this.mBubblePaddingTop;
            final float mStatusBarHeight = this.mStatusBarHeight;
            if (rootWindowInsets.getDisplayCutout() != null) {
                b = (float)rootWindowInsets.getDisplayCutout().getSafeInsetTop();
            }
            return mBubblePaddingTop + Math.max(mStatusBarHeight, b);
        }
        return 0.0f;
    }
    
    public MagnetizedObject<View> getMagnetizedBubbleDraggingOut() {
        return this.mMagnetizedBubbleDraggingOut;
    }
    
    @Override
    int getNextAnimationInChain(final DynamicAnimation.ViewProperty viewProperty, final int n) {
        return -1;
    }
    
    @Override
    float getOffsetForChainedPropertyAnimation(final DynamicAnimation.ViewProperty viewProperty) {
        return 0.0f;
    }
    
    @Override
    SpringForce getSpringForce(final DynamicAnimation.ViewProperty viewProperty, final View view) {
        final SpringForce springForce = new SpringForce();
        springForce.setDampingRatio(0.75f);
        springForce.setStiffness(200.0f);
        return springForce;
    }
    
    public float getWidthForDisplayingBubbles() {
        final float availableScreenWidth = this.getAvailableScreenWidth(true);
        if (this.mScreenOrientation == 2) {
            return Math.max((float)this.mDisplaySize.y, availableScreenWidth * 0.66f);
        }
        return availableScreenWidth;
    }
    
    @Override
    void onActiveControllerForLayout(final PhysicsAnimationLayout physicsAnimationLayout) {
        final Resources resources = physicsAnimationLayout.getResources();
        this.mStackOffsetPx = (float)resources.getDimensionPixelSize(R$dimen.bubble_stack_offset);
        this.mBubblePaddingTop = (float)resources.getDimensionPixelSize(R$dimen.bubble_padding_top);
        this.mBubbleSizePx = (float)resources.getDimensionPixelSize(R$dimen.individual_bubble_size);
        this.mStatusBarHeight = (float)resources.getDimensionPixelSize(17105471);
        this.mBubblesMaxRendered = resources.getInteger(R$integer.bubbles_max_rendered);
        final float widthForDisplayingBubbles = this.getWidthForDisplayingBubbles();
        final float n = (float)(this.mExpandedViewPadding * 2);
        final int mBubblesMaxRendered = this.mBubblesMaxRendered;
        this.mSpaceBetweenBubbles = (widthForDisplayingBubbles - n - (mBubblesMaxRendered + 1) * this.mBubbleSizePx) / mBubblesMaxRendered;
        super.mLayout.setVisibility(0);
        ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationsForChildrenFromIndex(0, (ChildAnimationConfigurator)_$$Lambda$ExpandedAnimationController$MQDrBXWQvl1BITN7BEHGEeBiDc0.INSTANCE).startAll(new Runnable[0]);
    }
    
    @Override
    void onChildAdded(final View view, final int n) {
        if (this.mAnimatingExpand) {
            this.startOrUpdatePathAnimation(true);
        }
        else if (this.mAnimatingCollapse) {
            this.startOrUpdatePathAnimation(false);
        }
        else {
            view.setTranslationX(this.getBubbleLeft(n));
            final PhysicsPropertyAnimator animationForChild = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChild(view);
            animationForChild.translationY(this.getExpandedY() - this.mBubbleSizePx * 4.0f, this.getExpandedY(), new Runnable[0]);
            animationForChild.start(new Runnable[0]);
            this.updateBubblePositions();
        }
    }
    
    @Override
    void onChildRemoved(final View view, final int n, final Runnable runnable) {
        final PhysicsPropertyAnimator animationForChild = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChild(view);
        if (view.equals(this.getDraggedOutBubble())) {
            this.mMagnetizedBubbleDraggingOut = null;
            runnable.run();
        }
        else {
            animationForChild.alpha(0.0f, runnable);
            animationForChild.withStiffness(10000.0f);
            animationForChild.withDampingRatio(1.0f);
            animationForChild.scaleX(1.1f, new Runnable[0]);
            animationForChild.scaleY(1.1f, new Runnable[0]);
            animationForChild.start(new Runnable[0]);
        }
        this.updateBubblePositions();
    }
    
    @Override
    void onChildReordered(final View view, final int n, final int n2) {
        this.updateBubblePositions();
        if (this.mAnimatingCollapse) {
            this.startOrUpdatePathAnimation(false);
        }
    }
    
    public void onUnstuckFromTarget() {
        this.mSpringToTouchOnNextMotionEvent = true;
    }
    
    public void prepareForBubbleDrag(final View view, final MagnetizedObject.MagneticTarget magneticTarget, final MagnetizedObject.MagnetListener magnetListener) {
        super.mLayout.cancelAnimationsOnView(view);
        view.setTranslationZ(32767.0f);
        (this.mMagnetizedBubbleDraggingOut = new MagnetizedObject<View>(super.mLayout.getContext(), view, DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y) {
            @Override
            public float getHeight(final View view) {
                return ExpandedAnimationController.this.mBubbleSizePx;
            }
            
            @Override
            public void getLocationOnScreen(final View view, final int[] array) {
                array[0] = (int)view.getTranslationX();
                array[1] = (int)view.getTranslationY();
            }
            
            @Override
            public float getWidth(final View view) {
                return ExpandedAnimationController.this.mBubbleSizePx;
            }
        }).addTarget(magneticTarget);
        this.mMagnetizedBubbleDraggingOut.setMagnetListener(magnetListener);
        this.mMagnetizedBubbleDraggingOut.setHapticsEnabled(true);
        this.mMagnetizedBubbleDraggingOut.setFlingToTargetMinVelocity(6000.0f);
    }
    
    public void snapBubbleBack(final View view, final float n, final float n2) {
        final int indexOfChild = super.mLayout.indexOfChild(view);
        final PhysicsPropertyAnimator animationForChildAtIndex = ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationForChildAtIndex(indexOfChild);
        animationForChildAtIndex.position(this.getBubbleLeft(indexOfChild), this.getExpandedY(), new Runnable[0]);
        animationForChildAtIndex.withPositionStartVelocities(n, n2);
        animationForChildAtIndex.start(new _$$Lambda$ExpandedAnimationController$n3D_KDDz_uA6Zea2rmmE2_UxikI(view));
        this.mMagnetizedBubbleDraggingOut = null;
        this.updateBubblePositions();
    }
    
    public void updateOrientation(final int mScreenOrientation, final Point mDisplaySize) {
        this.mScreenOrientation = mScreenOrientation;
        this.mDisplaySize = mDisplaySize;
        final PhysicsAnimationLayout mLayout = super.mLayout;
        if (mLayout != null) {
            final Resources resources = mLayout.getContext().getResources();
            this.mBubblePaddingTop = (float)resources.getDimensionPixelSize(R$dimen.bubble_padding_top);
            this.mStatusBarHeight = (float)resources.getDimensionPixelSize(17105471);
        }
    }
    
    public void updateYPosition(final Runnable runnable) {
        if (super.mLayout == null) {
            return;
        }
        ((PhysicsAnimationLayout.PhysicsAnimationController)this).animationsForChildrenFromIndex(0, (ChildAnimationConfigurator)new _$$Lambda$ExpandedAnimationController$8QomesE6Zam2GSy9tW1fTh6Elo8(this)).startAll(runnable);
    }
}
