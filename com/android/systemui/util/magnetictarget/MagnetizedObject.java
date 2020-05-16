// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.magnetictarget;

import android.graphics.PointF;
import java.util.Iterator;
import android.view.View;
import android.annotation.SuppressLint;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import android.view.MotionEvent;
import kotlin.TypeCastException;
import android.database.ContentObserver;
import android.provider.Settings$System;
import android.os.Handler;
import kotlin.jvm.internal.Intrinsics;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import android.os.Vibrator;
import android.view.VelocityTracker;
import android.content.Context;
import java.util.ArrayList;
import com.android.systemui.util.animation.PhysicsAnimator;

public abstract class MagnetizedObject<T>
{
    private final PhysicsAnimator<T> animator;
    private final ArrayList<MagneticTarget> associatedTargets;
    private final Context context;
    private boolean flingToTargetEnabled;
    private float flingToTargetMinVelocity;
    private float flingToTargetWidthPercent;
    private float flingUnstuckFromTargetMinVelocity;
    private PhysicsAnimator.SpringConfig flungIntoTargetSpringConfig;
    private boolean hapticsEnabled;
    public MagnetListener magnetListener;
    private final int[] objectLocationOnScreen;
    private PhysicsAnimator.EndListener<T> physicsAnimatorEndListener;
    private PhysicsAnimator.UpdateListener<T> physicsAnimatorUpdateListener;
    private PhysicsAnimator.SpringConfig springConfig;
    private float stickToTargetMaxVelocity;
    private boolean systemHapticsEnabled;
    private MagneticTarget targetObjectIsStuckTo;
    private final T underlyingObject;
    private final VelocityTracker velocityTracker;
    private final Vibrator vibrator;
    private final FloatPropertyCompat<? super T> xProperty;
    private final FloatPropertyCompat<? super T> yProperty;
    
    public MagnetizedObject(final Context context, final T underlyingObject, final FloatPropertyCompat<? super T> xProperty, final FloatPropertyCompat<? super T> yProperty) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(underlyingObject, "underlyingObject");
        Intrinsics.checkParameterIsNotNull(xProperty, "xProperty");
        Intrinsics.checkParameterIsNotNull(yProperty, "yProperty");
        this.context = context;
        this.underlyingObject = underlyingObject;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
        this.animator = PhysicsAnimator.Companion.getInstance(underlyingObject);
        this.objectLocationOnScreen = new int[2];
        this.associatedTargets = new ArrayList<MagneticTarget>();
        final VelocityTracker obtain = VelocityTracker.obtain();
        Intrinsics.checkExpressionValueIsNotNull(obtain, "VelocityTracker.obtain()");
        this.velocityTracker = obtain;
        final Object systemService = this.context.getSystemService("vibrator");
        if (systemService != null) {
            this.vibrator = (Vibrator)systemService;
            this.flingToTargetEnabled = true;
            this.flingToTargetWidthPercent = 3.0f;
            this.flingToTargetMinVelocity = 4000.0f;
            this.flingUnstuckFromTargetMinVelocity = 1000.0f;
            this.stickToTargetMaxVelocity = 2000.0f;
            this.hapticsEnabled = true;
            final PhysicsAnimator.SpringConfig springConfig = new PhysicsAnimator.SpringConfig(1500.0f, 1.0f);
            this.springConfig = springConfig;
            this.flungIntoTargetSpringConfig = springConfig;
            final MagnetizedObject$hapticSettingObserver.MagnetizedObject$hapticSettingObserver$1 magnetizedObject$hapticSettingObserver$1 = new MagnetizedObject$hapticSettingObserver.MagnetizedObject$hapticSettingObserver$1(this, Handler.getMain());
            this.context.getContentResolver().registerContentObserver(Settings$System.getUriFor("haptic_feedback_enabled"), true, (ContentObserver)magnetizedObject$hapticSettingObserver$1);
            magnetizedObject$hapticSettingObserver$1.onChange(false);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.os.Vibrator");
    }
    
    private final void addMovement(final MotionEvent motionEvent) {
        final float n = motionEvent.getRawX() - motionEvent.getX();
        final float n2 = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(n, n2);
        this.velocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-n, -n2);
    }
    
    private final void animateStuckToTarget(final MagneticTarget magneticTarget, final float n, final float n2, final boolean b, final Function0<Unit> function0) {
        magneticTarget.updateLocationOnScreen();
        this.getLocationOnScreen(this.underlyingObject, this.objectLocationOnScreen);
        final float x = magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().x;
        final float n3 = this.getWidth(this.underlyingObject) / 2.0f;
        final float n4 = (float)this.objectLocationOnScreen[0];
        final float y = magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().y;
        final float n5 = this.getHeight(this.underlyingObject) / 2.0f;
        final float n6 = (float)this.objectLocationOnScreen[1];
        PhysicsAnimator.SpringConfig springConfig;
        if (b) {
            springConfig = this.flungIntoTargetSpringConfig;
        }
        else {
            springConfig = this.springConfig;
        }
        this.cancelAnimations$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
        final PhysicsAnimator<T> animator = this.animator;
        final FloatPropertyCompat<? super T> xProperty = this.xProperty;
        animator.spring(xProperty, xProperty.getValue(this.underlyingObject) + (x - n3 - n4), n, springConfig);
        final FloatPropertyCompat<? super T> yProperty = this.yProperty;
        animator.spring(yProperty, yProperty.getValue(this.underlyingObject) + (y - n5 - n6), n2, springConfig);
        final PhysicsAnimator.UpdateListener<T> physicsAnimatorUpdateListener = this.physicsAnimatorUpdateListener;
        if (physicsAnimatorUpdateListener != null) {
            final PhysicsAnimator<T> animator2 = this.animator;
            if (physicsAnimatorUpdateListener == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            animator2.addUpdateListener(physicsAnimatorUpdateListener);
        }
        final PhysicsAnimator.EndListener<T> physicsAnimatorEndListener = this.physicsAnimatorEndListener;
        if (physicsAnimatorEndListener != null) {
            final PhysicsAnimator<T> animator3 = this.animator;
            if (physicsAnimatorEndListener == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            animator3.addEndListener(physicsAnimatorEndListener);
        }
        if (function0 != null) {
            this.animator.withEndActions(function0);
        }
        this.animator.start();
    }
    
    static /* synthetic */ void animateStuckToTarget$default(final MagnetizedObject magnetizedObject, final MagneticTarget magneticTarget, final float n, final float n2, final boolean b, Function0 function0, final int n3, final Object o) {
        if (o == null) {
            if ((n3 & 0x10) != 0x0) {
                function0 = null;
            }
            magnetizedObject.animateStuckToTarget(magneticTarget, n, n2, b, function0);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: animateStuckToTarget");
    }
    
    private final boolean isForcefulFlingTowardsTarget(final MagneticTarget magneticTarget, float x, float n, float flingToTargetWidthPercent, final float n2) {
        final boolean flingToTargetEnabled = this.flingToTargetEnabled;
        final boolean b = false;
        if (!flingToTargetEnabled) {
            return false;
        }
        if (!((n < magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().y) ? (n2 > this.flingToTargetMinVelocity) : (n2 < this.flingToTargetMinVelocity))) {
            return false;
        }
        float n3 = x;
        if (flingToTargetWidthPercent != 0.0f) {
            flingToTargetWidthPercent = n2 / flingToTargetWidthPercent;
            n3 = (magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().y - (n - x * flingToTargetWidthPercent)) / flingToTargetWidthPercent;
        }
        n = (float)magneticTarget.getTargetView$frameworks__base__packages__SystemUI__android_common__SystemUI_core().getWidth();
        flingToTargetWidthPercent = this.flingToTargetWidthPercent;
        x = magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().x;
        n = n * flingToTargetWidthPercent / 2;
        boolean b2 = b;
        if (n3 > x - n) {
            b2 = b;
            if (n3 < magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().x + n) {
                b2 = true;
            }
        }
        return b2;
    }
    
    @SuppressLint({ "MissingPermission" })
    private final void vibrateIfEnabled(final int n) {
        if (this.hapticsEnabled && this.systemHapticsEnabled) {
            this.vibrator.vibrate((long)n);
        }
    }
    
    public final MagneticTarget addTarget(final View view, final int n) {
        Intrinsics.checkParameterIsNotNull(view, "target");
        final MagneticTarget magneticTarget = new MagneticTarget(view, n);
        this.addTarget(magneticTarget);
        return magneticTarget;
    }
    
    public final void addTarget(final MagneticTarget e) {
        Intrinsics.checkParameterIsNotNull(e, "target");
        this.associatedTargets.add(e);
        e.updateLocationOnScreen();
    }
    
    public final void cancelAnimations$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
        this.animator.cancel(this.xProperty, this.yProperty);
    }
    
    public final Context getContext() {
        return this.context;
    }
    
    public abstract float getHeight(final T p0);
    
    public abstract void getLocationOnScreen(final T p0, final int[] p1);
    
    public final MagnetListener getMagnetListener() {
        final MagnetListener magnetListener = this.magnetListener;
        if (magnetListener != null) {
            return magnetListener;
        }
        Intrinsics.throwUninitializedPropertyAccessException("magnetListener");
        throw null;
    }
    
    public final boolean getObjectStuckToTarget() {
        return this.targetObjectIsStuckTo != null;
    }
    
    public final T getUnderlyingObject() {
        return this.underlyingObject;
    }
    
    public abstract float getWidth(final T p0);
    
    public final boolean maybeConsumeMotionEvent(final MotionEvent motionEvent) {
        Intrinsics.checkParameterIsNotNull(motionEvent, "ev");
        if (this.associatedTargets.size() == 0) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            this.updateTargetViewLocations$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
            this.velocityTracker.clear();
            this.targetObjectIsStuckTo = null;
        }
        this.addMovement(motionEvent);
        while (true) {
            for (final MagneticTarget next : this.associatedTargets) {
                final MagneticTarget magneticTarget = next;
                if ((float)Math.hypot(motionEvent.getRawX() - magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().x, motionEvent.getRawY() - magneticTarget.getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core().y) < magneticTarget.getMagneticFieldRadiusPx()) {
                    final MagneticTarget targetObjectIsStuckTo = next;
                    final boolean b = !this.getObjectStuckToTarget() && targetObjectIsStuckTo != null;
                    final boolean b2 = this.getObjectStuckToTarget() && targetObjectIsStuckTo != null && (Intrinsics.areEqual(this.targetObjectIsStuckTo, targetObjectIsStuckTo) ^ true);
                    if (!b && !b2) {
                        if (targetObjectIsStuckTo == null && this.getObjectStuckToTarget()) {
                            this.velocityTracker.computeCurrentVelocity(1000);
                            this.cancelAnimations$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
                            final MagnetListener magnetListener = this.magnetListener;
                            if (magnetListener == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("magnetListener");
                                throw null;
                            }
                            final MagneticTarget targetObjectIsStuckTo2 = this.targetObjectIsStuckTo;
                            if (targetObjectIsStuckTo2 == null) {
                                Intrinsics.throwNpe();
                                throw null;
                            }
                            magnetListener.onUnstuckFromTarget(targetObjectIsStuckTo2, this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity(), false);
                            this.targetObjectIsStuckTo = null;
                            this.vibrateIfEnabled(2);
                        }
                    }
                    else {
                        this.velocityTracker.computeCurrentVelocity(1000);
                        final float xVelocity = this.velocityTracker.getXVelocity();
                        final float yVelocity = this.velocityTracker.getYVelocity();
                        if (b && (float)Math.hypot(xVelocity, yVelocity) > this.stickToTargetMaxVelocity) {
                            return false;
                        }
                        this.targetObjectIsStuckTo = targetObjectIsStuckTo;
                        this.cancelAnimations$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
                        final MagnetListener magnetListener2 = this.magnetListener;
                        if (magnetListener2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("magnetListener");
                            throw null;
                        }
                        if (targetObjectIsStuckTo == null) {
                            Intrinsics.throwNpe();
                            throw null;
                        }
                        magnetListener2.onStuckToTarget(targetObjectIsStuckTo);
                        animateStuckToTarget$default(this, targetObjectIsStuckTo, xVelocity, yVelocity, false, null, 16, null);
                        this.vibrateIfEnabled(5);
                    }
                    if (motionEvent.getAction() != 1) {
                        return this.getObjectStuckToTarget();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    final float xVelocity2 = this.velocityTracker.getXVelocity();
                    final float yVelocity2 = this.velocityTracker.getYVelocity();
                    this.cancelAnimations$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
                    if (this.getObjectStuckToTarget()) {
                        if ((float)Math.hypot(xVelocity2, yVelocity2) > this.flingUnstuckFromTargetMinVelocity) {
                            final MagnetListener magnetListener3 = this.magnetListener;
                            if (magnetListener3 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("magnetListener");
                                throw null;
                            }
                            final MagneticTarget targetObjectIsStuckTo3 = this.targetObjectIsStuckTo;
                            if (targetObjectIsStuckTo3 == null) {
                                Intrinsics.throwNpe();
                                throw null;
                            }
                            magnetListener3.onUnstuckFromTarget(targetObjectIsStuckTo3, xVelocity2, yVelocity2, true);
                        }
                        else {
                            final MagnetListener magnetListener4 = this.magnetListener;
                            if (magnetListener4 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("magnetListener");
                                throw null;
                            }
                            final MagneticTarget targetObjectIsStuckTo4 = this.targetObjectIsStuckTo;
                            if (targetObjectIsStuckTo4 == null) {
                                Intrinsics.throwNpe();
                                throw null;
                            }
                            magnetListener4.onReleasedInTarget(targetObjectIsStuckTo4);
                            this.vibrateIfEnabled(5);
                        }
                        this.targetObjectIsStuckTo = null;
                        return true;
                    }
                    while (true) {
                        for (final MagneticTarget next2 : this.associatedTargets) {
                            if (this.isForcefulFlingTowardsTarget(next2, motionEvent.getRawX(), motionEvent.getRawY(), xVelocity2, yVelocity2)) {
                                final MagneticTarget magneticTarget2 = next2;
                                final MagneticTarget targetObjectIsStuckTo5 = magneticTarget2;
                                if (targetObjectIsStuckTo5 == null) {
                                    return false;
                                }
                                final MagnetListener magnetListener5 = this.magnetListener;
                                if (magnetListener5 != null) {
                                    magnetListener5.onStuckToTarget(targetObjectIsStuckTo5);
                                    this.animateStuckToTarget(this.targetObjectIsStuckTo = targetObjectIsStuckTo5, xVelocity2, yVelocity2, true, (Function0<Unit>)new MagnetizedObject$maybeConsumeMotionEvent.MagnetizedObject$maybeConsumeMotionEvent$1(this, targetObjectIsStuckTo5));
                                    return true;
                                }
                                Intrinsics.throwUninitializedPropertyAccessException("magnetListener");
                                throw null;
                            }
                        }
                        final MagneticTarget magneticTarget2 = null;
                        continue;
                    }
                }
            }
            MagneticTarget next = null;
            continue;
        }
    }
    
    public final void setFlingToTargetMinVelocity(final float flingToTargetMinVelocity) {
        this.flingToTargetMinVelocity = flingToTargetMinVelocity;
    }
    
    public final void setHapticsEnabled(final boolean hapticsEnabled) {
        this.hapticsEnabled = hapticsEnabled;
    }
    
    public final void setMagnetListener(final MagnetListener magnetListener) {
        Intrinsics.checkParameterIsNotNull(magnetListener, "<set-?>");
        this.magnetListener = magnetListener;
    }
    
    public final void setPhysicsAnimatorUpdateListener(final PhysicsAnimator.UpdateListener<T> physicsAnimatorUpdateListener) {
        this.physicsAnimatorUpdateListener = physicsAnimatorUpdateListener;
    }
    
    public final void updateTargetViewLocations$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
        final Iterator<MagneticTarget> iterator = this.associatedTargets.iterator();
        while (iterator.hasNext()) {
            iterator.next().updateLocationOnScreen();
        }
    }
    
    public interface MagnetListener
    {
        void onReleasedInTarget(final MagneticTarget p0);
        
        void onStuckToTarget(final MagneticTarget p0);
        
        void onUnstuckFromTarget(final MagneticTarget p0, final float p1, final float p2, final boolean p3);
    }
    
    public static final class MagneticTarget
    {
        private final PointF centerOnScreen;
        private int magneticFieldRadiusPx;
        private final View targetView;
        private final int[] tempLoc;
        
        public MagneticTarget(final View targetView, final int magneticFieldRadiusPx) {
            Intrinsics.checkParameterIsNotNull(targetView, "targetView");
            this.targetView = targetView;
            this.magneticFieldRadiusPx = magneticFieldRadiusPx;
            this.centerOnScreen = new PointF();
            this.tempLoc = new int[2];
        }
        
        public final PointF getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.centerOnScreen;
        }
        
        public final int getMagneticFieldRadiusPx() {
            return this.magneticFieldRadiusPx;
        }
        
        public final View getTargetView$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.targetView;
        }
        
        public final void setMagneticFieldRadiusPx(final int magneticFieldRadiusPx) {
            this.magneticFieldRadiusPx = magneticFieldRadiusPx;
        }
        
        public final void updateLocationOnScreen() {
            this.targetView.post((Runnable)new MagnetizedObject$MagneticTarget$updateLocationOnScreen.MagnetizedObject$MagneticTarget$updateLocationOnScreen$1(this));
        }
    }
}
