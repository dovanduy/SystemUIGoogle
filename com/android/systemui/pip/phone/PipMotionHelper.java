// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.util.magnetictarget.MagnetizedObject;
import java.io.PrintWriter;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.systemui.util.animation.FloatProperties;
import android.os.RemoteException;
import android.util.Log;
import android.util.ArrayMap;
import java.util.Objects;
import com.android.systemui.statusbar.FlingAnimationUtils;
import java.util.function.Consumer;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.pip.PipTaskOrganizer;
import android.content.Context;
import com.android.systemui.util.animation.PhysicsAnimator;
import android.graphics.Rect;
import android.app.IActivityTaskManager;
import com.android.systemui.util.FloatingContentCoordinator;

public class PipMotionHelper implements Callback, FloatingContent
{
    private final IActivityTaskManager mActivityTaskManager;
    private final Rect mAnimatedBounds;
    private PhysicsAnimator<Rect> mAnimatedBoundsPhysicsAnimator;
    private final Rect mAnimatingToBounds;
    private final Rect mBounds;
    private final PhysicsAnimator.SpringConfig mConflictResolutionSpringConfig;
    private final Context mContext;
    private PhysicsAnimator.FlingConfig mFlingConfigX;
    private PhysicsAnimator.FlingConfig mFlingConfigY;
    private final Rect mFloatingAllowedArea;
    private FloatingContentCoordinator mFloatingContentCoordinator;
    private PipMenuActivityController mMenuController;
    private final Rect mMovementBounds;
    private final PipTaskOrganizer mPipTaskOrganizer;
    final PhysicsAnimator.UpdateListener<Rect> mResizePipUpdateListener;
    private PipSnapAlgorithm mSnapAlgorithm;
    private final PhysicsAnimator.SpringConfig mSpringConfig;
    private boolean mSpringingToTouch;
    private final Rect mStableInsets;
    private final Consumer<Rect> mUpdateBoundsCallback;
    
    public PipMotionHelper(final Context mContext, final IActivityTaskManager mActivityTaskManager, final PipTaskOrganizer mPipTaskOrganizer, final PipMenuActivityController mMenuController, final PipSnapAlgorithm mSnapAlgorithm, final FlingAnimationUtils flingAnimationUtils, final FloatingContentCoordinator mFloatingContentCoordinator) {
        this.mStableInsets = new Rect();
        this.mBounds = new Rect();
        this.mMovementBounds = new Rect();
        this.mFloatingAllowedArea = new Rect();
        this.mAnimatedBounds = new Rect();
        this.mAnimatingToBounds = new Rect();
        this.mAnimatedBoundsPhysicsAnimator = PhysicsAnimator.getInstance(this.mAnimatedBounds);
        this.mResizePipUpdateListener = (PhysicsAnimator.UpdateListener<Rect>)new _$$Lambda$PipMotionHelper$LRAHsRaN1jXnWC7ljDFO0mBwcYY(this);
        this.mSpringConfig = new PhysicsAnimator.SpringConfig(1500.0f, 0.75f);
        this.mConflictResolutionSpringConfig = new PhysicsAnimator.SpringConfig(200.0f, 0.75f);
        final Rect mBounds = this.mBounds;
        Objects.requireNonNull(mBounds);
        this.mUpdateBoundsCallback = (Consumer<Rect>)new _$$Lambda$9ryw0tgRGCMDitW4U_PfPc0I9v4(mBounds);
        this.mSpringingToTouch = false;
        this.mContext = mContext;
        this.mActivityTaskManager = mActivityTaskManager;
        this.mPipTaskOrganizer = mPipTaskOrganizer;
        this.mMenuController = mMenuController;
        this.mSnapAlgorithm = mSnapAlgorithm;
        this.mFloatingContentCoordinator = mFloatingContentCoordinator;
        this.onConfigurationChanged();
    }
    
    private void cancelAnimations() {
        this.mAnimatedBoundsPhysicsAnimator.cancel();
        this.mAnimatingToBounds.setEmpty();
        this.mSpringingToTouch = false;
    }
    
    private void rebuildFlingConfigs() {
        final Rect mMovementBounds = this.mMovementBounds;
        this.mFlingConfigX = new PhysicsAnimator.FlingConfig(2.0f, (float)mMovementBounds.left, (float)mMovementBounds.right);
        final Rect mMovementBounds2 = this.mMovementBounds;
        this.mFlingConfigY = new PhysicsAnimator.FlingConfig(2.0f, (float)mMovementBounds2.top, (float)mMovementBounds2.bottom);
    }
    
    private void resizeAndAnimatePipUnchecked(final Rect animatingToBounds, final int n) {
        if (!animatingToBounds.equals((Object)this.mBounds)) {
            this.mPipTaskOrganizer.scheduleAnimateResizePip(animatingToBounds, n, this.mUpdateBoundsCallback);
            this.setAnimatingToBounds(animatingToBounds);
        }
    }
    
    private void resizePipUnchecked(final Rect rect) {
        if (!rect.equals((Object)this.mBounds)) {
            this.mPipTaskOrganizer.scheduleResizePip(rect, this.mUpdateBoundsCallback);
        }
    }
    
    private void setAnimatingToBounds(final Rect rect) {
        this.mAnimatingToBounds.set(rect);
        this.mFloatingContentCoordinator.onContentMoved((FloatingContentCoordinator.FloatingContent)this);
    }
    
    private void startBoundsAnimator(final float n, final float n2) {
        if (!this.mSpringingToTouch) {
            this.cancelAnimations();
        }
        final Rect mAnimatingToBounds = this.mAnimatingToBounds;
        final int n3 = (int)n;
        final int n4 = (int)n2;
        mAnimatingToBounds.set(n3, n4, this.mBounds.width() + n3, this.mBounds.height() + n4);
        this.setAnimatingToBounds(this.mAnimatingToBounds);
        final PhysicsAnimator<Rect> mAnimatedBoundsPhysicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        mAnimatedBoundsPhysicsAnimator.withEndActions(new _$$Lambda$PipMotionHelper$2f_HfjcUJAkurZCYkvwlotOnmOw(this));
        mAnimatedBoundsPhysicsAnimator.addUpdateListener(this.mResizePipUpdateListener);
        mAnimatedBoundsPhysicsAnimator.start();
    }
    
    void animateDismiss(final float n, final float n2, final Runnable runnable) {
        this.mAnimatedBounds.set(this.mBounds);
        final PhysicsAnimator<Rect> mAnimatedBoundsPhysicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        final FloatPropertyCompat<Rect> rect_Y = FloatProperties.RECT_Y;
        final Rect mBounds = this.mBounds;
        mAnimatedBoundsPhysicsAnimator.spring(rect_Y, (float)(mBounds.bottom + mBounds.height()), n2, this.mSpringConfig);
        mAnimatedBoundsPhysicsAnimator.withEndActions(new _$$Lambda$kQFaBNknFROC8D1C4ywIb9w3JTU(this));
        if (runnable != null) {
            this.mAnimatedBoundsPhysicsAnimator.addUpdateListener(new _$$Lambda$PipMotionHelper$Ctg5c8GF13v0ibxgl1HaEu_BZow(runnable));
        }
        final Rect mBounds2 = this.mBounds;
        this.startBoundsAnimator((float)mBounds2.left, (float)(mBounds2.bottom + mBounds2.height()));
    }
    
    void animateToBounds(final Rect rect, final PhysicsAnimator.SpringConfig springConfig) {
        this.mAnimatedBounds.set(this.mBounds);
        final PhysicsAnimator<Rect> mAnimatedBoundsPhysicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        mAnimatedBoundsPhysicsAnimator.spring(FloatProperties.RECT_X, (float)rect.left, springConfig);
        mAnimatedBoundsPhysicsAnimator.spring(FloatProperties.RECT_Y, (float)rect.top, springConfig);
        this.startBoundsAnimator((float)rect.left, (float)rect.top);
    }
    
    void animateToClosestSnapTarget() {
        final Rect rect = new Rect();
        this.mSnapAlgorithm.snapRectToClosestEdge(this.mBounds, this.mMovementBounds, rect);
        this.animateToBounds(rect, this.mSpringConfig);
    }
    
    float animateToExpandedState(final Rect rect, final Rect rect2, final Rect rect3) {
        final float snapFraction = this.mSnapAlgorithm.getSnapFraction(new Rect(this.mBounds), rect2);
        this.mSnapAlgorithm.applySnapFraction(rect, rect3, snapFraction);
        this.resizeAndAnimatePipUnchecked(rect, 250);
        return snapFraction;
    }
    
    void animateToOffset(final Rect rect, final int n) {
        this.cancelAnimations();
        this.mPipTaskOrganizer.scheduleOffsetPip(rect, n, 300, this.mUpdateBoundsCallback);
    }
    
    void animateToUnexpandedState(final Rect rect, final float n, final Rect rect2, final Rect rect3, final boolean b) {
        float snapFraction = n;
        if (n < 0.0f) {
            snapFraction = this.mSnapAlgorithm.getSnapFraction(new Rect(this.mBounds), rect3);
        }
        this.mSnapAlgorithm.applySnapFraction(rect, rect2, snapFraction);
        if (b) {
            this.movePip(rect);
        }
        else {
            this.resizeAndAnimatePipUnchecked(rect, 250);
        }
    }
    
    @Override
    public void dismissPip() {
        this.cancelAnimations();
        this.mMenuController.hideMenuWithoutResize();
        this.mPipTaskOrganizer.getUpdateHandler().post((Runnable)new _$$Lambda$PipMotionHelper$G8A90SJYu6GlqvuyWBP9SX39xS8(this));
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("PipMotionHelper");
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string);
        sb3.append("mBounds=");
        sb3.append(this.mBounds);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(string);
        sb4.append("mStableInsets=");
        sb4.append(this.mStableInsets);
        printWriter.println(sb4.toString());
    }
    
    void expandPip() {
        this.expandPip(false);
    }
    
    void expandPip(final boolean b) {
        this.cancelAnimations();
        this.mMenuController.hideMenuWithoutResize();
        this.mPipTaskOrganizer.getUpdateHandler().post((Runnable)new _$$Lambda$PipMotionHelper$1ujOdiFRFp2G6LoSLOJQgMePIVI(this, b));
    }
    
    void flingToSnapTarget(final float n, final float n2, final Runnable runnable, final Runnable runnable2) {
        this.mAnimatedBounds.set(this.mBounds);
        final PhysicsAnimator<Rect> mAnimatedBoundsPhysicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        mAnimatedBoundsPhysicsAnimator.flingThenSpring(FloatProperties.RECT_X, n, this.mFlingConfigX, this.mSpringConfig, true);
        mAnimatedBoundsPhysicsAnimator.flingThenSpring(FloatProperties.RECT_Y, n2, this.mFlingConfigY, this.mSpringConfig);
        mAnimatedBoundsPhysicsAnimator.withEndActions(runnable2);
        if (runnable != null) {
            this.mAnimatedBoundsPhysicsAnimator.addUpdateListener(new _$$Lambda$PipMotionHelper$7Yrc0Hc7TaZDEL4H0b0nv8p6rmo(runnable));
        }
        int n3;
        if (n < 0.0f) {
            n3 = this.mMovementBounds.left;
        }
        else {
            n3 = this.mMovementBounds.right;
        }
        this.startBoundsAnimator((float)n3, PhysicsAnimator.estimateFlingEndValue((float)this.mBounds.top, n2, this.mFlingConfigY));
    }
    
    @Override
    public Rect getAllowedFloatingBoundsRegion() {
        return this.mFloatingAllowedArea;
    }
    
    Rect getBounds() {
        return this.mBounds;
    }
    
    @Override
    public Rect getFloatingBoundsOnScreen() {
        Rect rect;
        if (!this.mAnimatingToBounds.isEmpty()) {
            rect = this.mAnimatingToBounds;
        }
        else {
            rect = this.mBounds;
        }
        return rect;
    }
    
    MagnetizedObject<Rect> getMagnetizedPip() {
        return new MagnetizedObject<Rect>(this, this.mContext, this.mAnimatedBounds, FloatProperties.RECT_X, FloatProperties.RECT_Y) {
            @Override
            public float getHeight(final Rect rect) {
                return (float)rect.height();
            }
            
            @Override
            public void getLocationOnScreen(final Rect rect, final int[] array) {
                array[0] = rect.left;
                array[1] = rect.top;
            }
            
            @Override
            public float getWidth(final Rect rect) {
                return (float)rect.width();
            }
        };
    }
    
    void movePip(final Rect rect) {
        this.movePip(rect, false);
    }
    
    void movePip(final Rect rect, final boolean b) {
        if (!b) {
            this.mFloatingContentCoordinator.onContentMoved((FloatingContentCoordinator.FloatingContent)this);
        }
        if (!this.mSpringingToTouch) {
            this.cancelAnimations();
            this.resizePipUnchecked(rect);
            this.mBounds.set(rect);
        }
        else {
            final PhysicsAnimator<Rect> mAnimatedBoundsPhysicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
            mAnimatedBoundsPhysicsAnimator.spring(FloatProperties.RECT_X, (float)rect.left, this.mSpringConfig);
            mAnimatedBoundsPhysicsAnimator.spring(FloatProperties.RECT_Y, (float)rect.top, this.mSpringConfig);
            mAnimatedBoundsPhysicsAnimator.withEndActions(new _$$Lambda$PipMotionHelper$babxwrrlTra5BuX5eMh_eFY151s(this));
            this.startBoundsAnimator((float)rect.left, (float)rect.top);
        }
    }
    
    @Override
    public void moveToBounds(final Rect rect) {
        this.animateToBounds(rect, this.mConflictResolutionSpringConfig);
    }
    
    void onConfigurationChanged() {
        this.mSnapAlgorithm.onConfigurationChanged();
        WindowManagerWrapper.getInstance().getStableInsets(this.mStableInsets);
    }
    
    void prepareForAnimation() {
        this.mAnimatedBounds.set(this.mBounds);
    }
    
    void setCurrentMovementBounds(Rect rect) {
        this.mMovementBounds.set(rect);
        this.rebuildFlingConfigs();
        this.mFloatingAllowedArea.set(this.mMovementBounds);
        rect = this.mFloatingAllowedArea;
        rect.right += this.mBounds.width();
        rect = this.mFloatingAllowedArea;
        rect.bottom += this.mBounds.height();
    }
    
    void setSpringingToTouch(final boolean mSpringingToTouch) {
        if (mSpringingToTouch) {
            this.mAnimatedBounds.set(this.mBounds);
        }
        this.mSpringingToTouch = mSpringingToTouch;
    }
    
    void synchronizePinnedStackBounds() {
        this.cancelAnimations();
        this.mBounds.set(this.mPipTaskOrganizer.getLastReportedBounds());
    }
    
    void synchronizePinnedStackBoundsForTouchGesture() {
        if (this.mAnimatingToBounds.isEmpty()) {
            this.synchronizePinnedStackBounds();
        }
        else {
            this.mBounds.set(this.mAnimatedBounds);
        }
    }
}
