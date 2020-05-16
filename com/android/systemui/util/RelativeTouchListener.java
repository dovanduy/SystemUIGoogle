// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.view.ViewConfiguration;
import kotlin.jvm.internal.Intrinsics;
import android.view.View;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.graphics.PointF;
import android.os.Handler;
import android.view.View$OnTouchListener;

public abstract class RelativeTouchListener implements View$OnTouchListener
{
    private final Handler handler;
    private boolean movedEnough;
    private boolean performedLongClick;
    private final PointF touchDown;
    private int touchSlop;
    private final VelocityTracker velocityTracker;
    private final PointF viewPositionOnTouchDown;
    
    public RelativeTouchListener() {
        this.touchDown = new PointF();
        this.viewPositionOnTouchDown = new PointF();
        this.velocityTracker = VelocityTracker.obtain();
        this.touchSlop = -1;
        this.handler = new Handler();
    }
    
    private final void addMovement(final MotionEvent motionEvent) {
        final float n = motionEvent.getRawX() - motionEvent.getX();
        final float n2 = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(n, n2);
        this.velocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-n, -n2);
    }
    
    public abstract boolean onDown(final View p0, final MotionEvent p1);
    
    public abstract void onMove(final View p0, final MotionEvent p1, final float p2, final float p3, final float p4, final float p5);
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        Intrinsics.checkParameterIsNotNull(view, "v");
        Intrinsics.checkParameterIsNotNull(motionEvent, "ev");
        this.addMovement(motionEvent);
        final float n = motionEvent.getRawX() - this.touchDown.x;
        final float n2 = motionEvent.getRawY() - this.touchDown.y;
        final int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action == 2) {
                    if (!this.movedEnough && (float)Math.hypot(n, n2) > this.touchSlop && !this.performedLongClick) {
                        this.movedEnough = true;
                        this.handler.removeCallbacksAndMessages((Object)null);
                    }
                    if (this.movedEnough) {
                        final PointF viewPositionOnTouchDown = this.viewPositionOnTouchDown;
                        this.onMove(view, motionEvent, viewPositionOnTouchDown.x, viewPositionOnTouchDown.y, n, n2);
                    }
                }
            }
            else {
                if (this.movedEnough) {
                    this.velocityTracker.computeCurrentVelocity(1000);
                    final PointF viewPositionOnTouchDown2 = this.viewPositionOnTouchDown;
                    final float x = viewPositionOnTouchDown2.x;
                    final float y = viewPositionOnTouchDown2.y;
                    final VelocityTracker velocityTracker = this.velocityTracker;
                    Intrinsics.checkExpressionValueIsNotNull(velocityTracker, "velocityTracker");
                    final float xVelocity = velocityTracker.getXVelocity();
                    final VelocityTracker velocityTracker2 = this.velocityTracker;
                    Intrinsics.checkExpressionValueIsNotNull(velocityTracker2, "velocityTracker");
                    this.onUp(view, motionEvent, x, y, n, n2, xVelocity, velocityTracker2.getYVelocity());
                }
                else if (!this.performedLongClick) {
                    view.performClick();
                }
                else {
                    this.handler.removeCallbacksAndMessages((Object)null);
                }
                this.velocityTracker.clear();
                this.movedEnough = false;
            }
        }
        else {
            if (!this.onDown(view, motionEvent)) {
                return false;
            }
            final ViewConfiguration value = ViewConfiguration.get(view.getContext());
            Intrinsics.checkExpressionValueIsNotNull(value, "ViewConfiguration.get(v.context)");
            this.touchSlop = value.getScaledTouchSlop();
            this.touchDown.set(motionEvent.getRawX(), motionEvent.getRawY());
            this.viewPositionOnTouchDown.set(view.getTranslationX(), view.getTranslationY());
            this.performedLongClick = false;
            this.handler.postDelayed((Runnable)new RelativeTouchListener$onTouch.RelativeTouchListener$onTouch$1(this, view), (long)ViewConfiguration.getLongPressTimeout());
        }
        return true;
    }
    
    public abstract void onUp(final View p0, final MotionEvent p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);
}
