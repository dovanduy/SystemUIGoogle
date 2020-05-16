// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.graphics.ColorFilter;
import android.view.ViewConfiguration;
import android.graphics.Rect;
import com.android.systemui.Interpolators;
import android.animation.ObjectAnimator;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import android.view.RenderNodeAnimator;
import android.graphics.Canvas;
import android.graphics.RecordingCanvas;
import java.util.Collection;
import com.android.systemui.R$dimen;
import android.content.Context;
import java.util.ArrayList;
import android.view.View;
import android.animation.Animator;
import java.util.HashSet;
import android.graphics.Paint;
import android.view.animation.Interpolator;
import android.os.Handler;
import android.graphics.CanvasProperty;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.Drawable;

public class KeyButtonRipple extends Drawable
{
    private final AnimatorListenerAdapter mAnimatorListener;
    private CanvasProperty<Float> mBottomProp;
    private boolean mDark;
    private boolean mDelayTouchFeedback;
    private boolean mDrawingHardwareGlow;
    private float mGlowAlpha;
    private float mGlowScale;
    private final Handler mHandler;
    private final Interpolator mInterpolator;
    private boolean mLastDark;
    private CanvasProperty<Float> mLeftProp;
    private int mMaxWidth;
    private CanvasProperty<Paint> mPaintProp;
    private boolean mPressed;
    private CanvasProperty<Float> mRightProp;
    private Paint mRipplePaint;
    private final HashSet<Animator> mRunningAnimations;
    private CanvasProperty<Float> mRxProp;
    private CanvasProperty<Float> mRyProp;
    private boolean mSupportHardware;
    private final View mTargetView;
    private final ArrayList<Animator> mTmpArray;
    private CanvasProperty<Float> mTopProp;
    private Type mType;
    private boolean mVisible;
    
    public KeyButtonRipple(final Context context, final View mTargetView) {
        this.mGlowAlpha = 0.0f;
        this.mGlowScale = 1.0f;
        this.mInterpolator = (Interpolator)new LogInterpolator();
        this.mHandler = new Handler();
        this.mRunningAnimations = new HashSet<Animator>();
        this.mTmpArray = new ArrayList<Animator>();
        this.mType = Type.ROUNDED_RECT;
        this.mAnimatorListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator o) {
                KeyButtonRipple.this.mRunningAnimations.remove(o);
                if (KeyButtonRipple.this.mRunningAnimations.isEmpty() && !KeyButtonRipple.this.mPressed) {
                    KeyButtonRipple.this.mVisible = false;
                    KeyButtonRipple.this.mDrawingHardwareGlow = false;
                    KeyButtonRipple.this.invalidateSelf();
                }
            }
        };
        this.mMaxWidth = context.getResources().getDimensionPixelSize(R$dimen.key_button_ripple_max_width);
        this.mTargetView = mTargetView;
    }
    
    private void cancelAnimations() {
        int i = 0;
        this.mVisible = false;
        this.mTmpArray.addAll(this.mRunningAnimations);
        while (i < this.mTmpArray.size()) {
            this.mTmpArray.get(i).cancel();
            ++i;
        }
        this.mTmpArray.clear();
        this.mRunningAnimations.clear();
        this.mHandler.removeCallbacksAndMessages((Object)null);
    }
    
    private void drawHardware(final RecordingCanvas recordingCanvas) {
        if (this.mDrawingHardwareGlow) {
            if (this.mType == Type.ROUNDED_RECT) {
                recordingCanvas.drawRoundRect((CanvasProperty)this.mLeftProp, (CanvasProperty)this.mTopProp, (CanvasProperty)this.mRightProp, (CanvasProperty)this.mBottomProp, (CanvasProperty)this.mRxProp, (CanvasProperty)this.mRyProp, (CanvasProperty)this.mPaintProp);
            }
            else {
                recordingCanvas.drawCircle(CanvasProperty.createFloat((float)(this.getBounds().width() / 2)), CanvasProperty.createFloat((float)(this.getBounds().height() / 2)), CanvasProperty.createFloat(Math.min(this.getBounds().width(), this.getBounds().height()) * 1.0f / 2.0f), (CanvasProperty)this.mPaintProp);
            }
        }
    }
    
    private void drawSoftware(final Canvas canvas) {
        if (this.mGlowAlpha > 0.0f) {
            final Paint ripplePaint = this.getRipplePaint();
            ripplePaint.setAlpha((int)(this.mGlowAlpha * 255.0f));
            final float n = (float)this.getBounds().width();
            final float n2 = (float)this.getBounds().height();
            final boolean b = n > n2;
            float b2 = this.getRippleSize() * this.mGlowScale * 0.5f;
            final float n3 = n * 0.5f;
            final float n4 = n2 * 0.5f;
            float a;
            if (b) {
                a = b2;
            }
            else {
                a = n3;
            }
            if (b) {
                b2 = n4;
            }
            float n5;
            if (b) {
                n5 = n4;
            }
            else {
                n5 = n3;
            }
            if (this.mType == Type.ROUNDED_RECT) {
                canvas.drawRoundRect(n3 - a, n4 - b2, a + n3, n4 + b2, n5, n5, ripplePaint);
            }
            else {
                canvas.save();
                canvas.translate(n3, n4);
                final float min = Math.min(a, b2);
                final float n6 = -min;
                canvas.drawOval(n6, n6, min, min, ripplePaint);
                canvas.restore();
            }
        }
    }
    
    private void enterHardware() {
        this.cancelAnimations();
        this.mVisible = true;
        this.mDrawingHardwareGlow = true;
        this.setExtendStart((CanvasProperty<Float>)CanvasProperty.createFloat((float)(this.getExtendSize() / 2)));
        final RenderNodeAnimator e = new RenderNodeAnimator((CanvasProperty)this.getExtendStart(), this.getExtendSize() / 2 - this.getRippleSize() * 1.35f / 2.0f);
        e.setDuration(350L);
        e.setInterpolator((TimeInterpolator)this.mInterpolator);
        e.addListener((Animator$AnimatorListener)this.mAnimatorListener);
        e.setTarget(this.mTargetView);
        this.setExtendEnd((CanvasProperty<Float>)CanvasProperty.createFloat((float)(this.getExtendSize() / 2)));
        final RenderNodeAnimator e2 = new RenderNodeAnimator((CanvasProperty)this.getExtendEnd(), this.getExtendSize() / 2 + this.getRippleSize() * 1.35f / 2.0f);
        e2.setDuration(350L);
        e2.setInterpolator((TimeInterpolator)this.mInterpolator);
        e2.addListener((Animator$AnimatorListener)this.mAnimatorListener);
        e2.setTarget(this.mTargetView);
        if (this.isHorizontal()) {
            this.mTopProp = (CanvasProperty<Float>)CanvasProperty.createFloat(0.0f);
            this.mBottomProp = (CanvasProperty<Float>)CanvasProperty.createFloat((float)this.getBounds().height());
            this.mRxProp = (CanvasProperty<Float>)CanvasProperty.createFloat((float)(this.getBounds().height() / 2));
            this.mRyProp = (CanvasProperty<Float>)CanvasProperty.createFloat((float)(this.getBounds().height() / 2));
        }
        else {
            this.mLeftProp = (CanvasProperty<Float>)CanvasProperty.createFloat(0.0f);
            this.mRightProp = (CanvasProperty<Float>)CanvasProperty.createFloat((float)this.getBounds().width());
            this.mRxProp = (CanvasProperty<Float>)CanvasProperty.createFloat((float)(this.getBounds().width() / 2));
            this.mRyProp = (CanvasProperty<Float>)CanvasProperty.createFloat((float)(this.getBounds().width() / 2));
        }
        this.mGlowScale = 1.35f;
        this.mGlowAlpha = this.getMaxGlowAlpha();
        (this.mRipplePaint = this.getRipplePaint()).setAlpha((int)(this.mGlowAlpha * 255.0f));
        this.mPaintProp = (CanvasProperty<Paint>)CanvasProperty.createPaint(this.mRipplePaint);
        e.start();
        e2.start();
        this.mRunningAnimations.add((Animator)e);
        this.mRunningAnimations.add((Animator)e2);
        this.invalidateSelf();
        if (this.mDelayTouchFeedback && !this.mPressed) {
            this.exitHardware();
        }
    }
    
    private void enterSoftware() {
        this.cancelAnimations();
        this.mVisible = true;
        this.mGlowAlpha = this.getMaxGlowAlpha();
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)this, "glowScale", new float[] { 0.0f, 1.35f });
        ofFloat.setInterpolator((TimeInterpolator)this.mInterpolator);
        ofFloat.setDuration(350L);
        ofFloat.addListener((Animator$AnimatorListener)this.mAnimatorListener);
        ofFloat.start();
        this.mRunningAnimations.add((Animator)ofFloat);
        if (this.mDelayTouchFeedback && !this.mPressed) {
            this.exitSoftware();
        }
    }
    
    private void exitHardware() {
        this.mPaintProp = (CanvasProperty<Paint>)CanvasProperty.createPaint(this.getRipplePaint());
        final RenderNodeAnimator e = new RenderNodeAnimator((CanvasProperty)this.mPaintProp, 1, 0.0f);
        e.setDuration(450L);
        e.setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT);
        e.addListener((Animator$AnimatorListener)this.mAnimatorListener);
        e.setTarget(this.mTargetView);
        e.start();
        this.mRunningAnimations.add((Animator)e);
        this.invalidateSelf();
    }
    
    private void exitSoftware() {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)this, "glowAlpha", new float[] { this.mGlowAlpha, 0.0f });
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT);
        ofFloat.setDuration(450L);
        ofFloat.addListener((Animator$AnimatorListener)this.mAnimatorListener);
        ofFloat.start();
        this.mRunningAnimations.add((Animator)ofFloat);
    }
    
    private CanvasProperty<Float> getExtendEnd() {
        CanvasProperty<Float> canvasProperty;
        if (this.isHorizontal()) {
            canvasProperty = this.mRightProp;
        }
        else {
            canvasProperty = this.mBottomProp;
        }
        return canvasProperty;
    }
    
    private int getExtendSize() {
        final boolean horizontal = this.isHorizontal();
        final Rect bounds = this.getBounds();
        int n;
        if (horizontal) {
            n = bounds.width();
        }
        else {
            n = bounds.height();
        }
        return n;
    }
    
    private CanvasProperty<Float> getExtendStart() {
        CanvasProperty<Float> canvasProperty;
        if (this.isHorizontal()) {
            canvasProperty = this.mLeftProp;
        }
        else {
            canvasProperty = this.mTopProp;
        }
        return canvasProperty;
    }
    
    private float getMaxGlowAlpha() {
        float n;
        if (this.mLastDark) {
            n = 0.1f;
        }
        else {
            n = 0.2f;
        }
        return n;
    }
    
    private Paint getRipplePaint() {
        if (this.mRipplePaint == null) {
            (this.mRipplePaint = new Paint()).setAntiAlias(true);
            final Paint mRipplePaint = this.mRipplePaint;
            int color;
            if (this.mLastDark) {
                color = -16777216;
            }
            else {
                color = -1;
            }
            mRipplePaint.setColor(color);
        }
        return this.mRipplePaint;
    }
    
    private int getRippleSize() {
        int a;
        if (this.isHorizontal()) {
            a = this.getBounds().width();
        }
        else {
            a = this.getBounds().height();
        }
        return Math.min(a, this.mMaxWidth);
    }
    
    private boolean isHorizontal() {
        return this.getBounds().width() > this.getBounds().height();
    }
    
    private void setExtendEnd(final CanvasProperty<Float> canvasProperty) {
        if (this.isHorizontal()) {
            this.mRightProp = canvasProperty;
        }
        else {
            this.mBottomProp = canvasProperty;
        }
    }
    
    private void setExtendStart(final CanvasProperty<Float> canvasProperty) {
        if (this.isHorizontal()) {
            this.mLeftProp = canvasProperty;
        }
        else {
            this.mTopProp = canvasProperty;
        }
    }
    
    private void setPressedHardware(final boolean b) {
        if (b) {
            if (this.mDelayTouchFeedback) {
                if (this.mRunningAnimations.isEmpty()) {
                    this.mHandler.removeCallbacksAndMessages((Object)null);
                    this.mHandler.postDelayed((Runnable)new _$$Lambda$KeyButtonRipple$Xl4rWJU_4TFxkXeTg6i8PM566MQ(this), (long)ViewConfiguration.getTapTimeout());
                }
                else if (this.mVisible) {
                    this.enterHardware();
                }
            }
            else {
                this.enterHardware();
            }
        }
        else {
            this.exitHardware();
        }
    }
    
    private void setPressedSoftware(final boolean b) {
        if (b) {
            if (this.mDelayTouchFeedback) {
                if (this.mRunningAnimations.isEmpty()) {
                    this.mHandler.removeCallbacksAndMessages((Object)null);
                    this.mHandler.postDelayed((Runnable)new _$$Lambda$KeyButtonRipple$_NjSlP8uc8G3rFUDxQkVsRHA4H4(this), (long)ViewConfiguration.getTapTimeout());
                }
                else if (this.mVisible) {
                    this.enterSoftware();
                }
            }
            else {
                this.enterSoftware();
            }
        }
        else {
            this.exitSoftware();
        }
    }
    
    public void abortDelayedRipple() {
        this.mHandler.removeCallbacksAndMessages((Object)null);
    }
    
    public void draw(final Canvas canvas) {
        final boolean hardwareAccelerated = canvas.isHardwareAccelerated();
        this.mSupportHardware = hardwareAccelerated;
        if (hardwareAccelerated) {
            this.drawHardware((RecordingCanvas)canvas);
        }
        else {
            this.drawSoftware(canvas);
        }
    }
    
    public float getGlowAlpha() {
        return this.mGlowAlpha;
    }
    
    public float getGlowScale() {
        return this.mGlowScale;
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public boolean hasFocusStateSpecified() {
        return true;
    }
    
    public boolean isStateful() {
        return true;
    }
    
    public void jumpToCurrentState() {
        this.cancelAnimations();
    }
    
    protected boolean onStateChange(final int[] array) {
        int i = 0;
        while (true) {
            while (i < array.length) {
                if (array[i] == 16842919) {
                    final boolean b = true;
                    if (b != this.mPressed) {
                        this.setPressed(b);
                        this.mPressed = b;
                        return true;
                    }
                    return false;
                }
                else {
                    ++i;
                }
            }
            final boolean b = false;
            continue;
        }
    }
    
    public void setAlpha(final int n) {
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
    }
    
    public void setDarkIntensity(final float n) {
        this.mDark = (n >= 0.5f);
    }
    
    public void setDelayTouchFeedback(final boolean mDelayTouchFeedback) {
        this.mDelayTouchFeedback = mDelayTouchFeedback;
    }
    
    public void setGlowAlpha(final float mGlowAlpha) {
        this.mGlowAlpha = mGlowAlpha;
        this.invalidateSelf();
    }
    
    public void setGlowScale(final float mGlowScale) {
        this.mGlowScale = mGlowScale;
        this.invalidateSelf();
    }
    
    public void setPressed(final boolean b) {
        final boolean mDark = this.mDark;
        if (mDark != this.mLastDark && b) {
            this.mRipplePaint = null;
            this.mLastDark = mDark;
        }
        if (this.mSupportHardware) {
            this.setPressedHardware(b);
        }
        else {
            this.setPressedSoftware(b);
        }
    }
    
    public void setType(final Type mType) {
        this.mType = mType;
    }
    
    private static final class LogInterpolator implements Interpolator
    {
        public float getInterpolation(final float n) {
            return 1.0f - (float)Math.pow(400.0, -n * 1.4);
        }
    }
    
    public enum Type
    {
        OVAL, 
        ROUNDED_RECT;
    }
}
