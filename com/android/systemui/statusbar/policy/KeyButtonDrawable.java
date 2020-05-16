// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.animation.ArgbEvaluator;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff$Mode;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable$ConstantState;
import android.graphics.Rect;
import android.graphics.MaskFilter;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter$Blur;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import android.content.res.Resources;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.FloatProperty;
import android.graphics.drawable.Drawable;

public class KeyButtonDrawable extends Drawable
{
    public static final FloatProperty<KeyButtonDrawable> KEY_DRAWABLE_ROTATE;
    public static final FloatProperty<KeyButtonDrawable> KEY_DRAWABLE_TRANSLATE_Y;
    private AnimatedVectorDrawable mAnimatedDrawable;
    private final Paint mIconPaint;
    private final Paint mShadowPaint;
    private final ShadowDrawableState mState;
    
    static {
        KEY_DRAWABLE_ROTATE = new FloatProperty<KeyButtonDrawable>() {
            public Float get(final KeyButtonDrawable keyButtonDrawable) {
                return keyButtonDrawable.getRotation();
            }
            
            public void setValue(final KeyButtonDrawable keyButtonDrawable, final float rotation) {
                keyButtonDrawable.setRotation(rotation);
            }
        };
        KEY_DRAWABLE_TRANSLATE_Y = new FloatProperty<KeyButtonDrawable>() {
            public Float get(final KeyButtonDrawable keyButtonDrawable) {
                return keyButtonDrawable.getTranslationY();
            }
            
            public void setValue(final KeyButtonDrawable keyButtonDrawable, final float translationY) {
                keyButtonDrawable.setTranslationY(translationY);
            }
        };
    }
    
    public KeyButtonDrawable(final Drawable drawable, final int n, final int n2, final boolean b, final Color color) {
        this(drawable, new ShadowDrawableState(n, n2, drawable instanceof AnimatedVectorDrawable, b, color));
    }
    
    private KeyButtonDrawable(final Drawable drawable, final ShadowDrawableState mState) {
        this.mIconPaint = new Paint(3);
        this.mShadowPaint = new Paint(3);
        this.mState = mState;
        if (drawable != null) {
            mState.mBaseHeight = drawable.getIntrinsicHeight();
            this.mState.mBaseWidth = drawable.getIntrinsicWidth();
            this.mState.mChangingConfigurations = drawable.getChangingConfigurations();
            this.mState.mChildState = drawable.getConstantState();
        }
        if (this.canAnimate()) {
            this.setDrawableBounds((Drawable)(this.mAnimatedDrawable = (AnimatedVectorDrawable)this.mState.mChildState.newDrawable().mutate()));
        }
    }
    
    public static KeyButtonDrawable create(final Context context, final int n, final int n2, final int n3, final boolean b, final Color color) {
        final Resources resources = context.getResources();
        final boolean b2 = resources.getConfiguration().getLayoutDirection() == 1;
        final Drawable drawable = context.getDrawable(n3);
        final KeyButtonDrawable keyButtonDrawable = new KeyButtonDrawable(drawable, n, n2, b2 && drawable.isAutoMirrored(), color);
        if (b) {
            keyButtonDrawable.setShadowProperties(resources.getDimensionPixelSize(R$dimen.nav_key_button_shadow_offset_x), resources.getDimensionPixelSize(R$dimen.nav_key_button_shadow_offset_y), resources.getDimensionPixelSize(R$dimen.nav_key_button_shadow_radius), context.getColor(R$color.nav_key_button_shadow_color));
        }
        return keyButtonDrawable;
    }
    
    public static KeyButtonDrawable create(final Context context, final int n, final boolean b) {
        return create(context, n, b, null);
    }
    
    public static KeyButtonDrawable create(final Context context, final int n, final boolean b, final Color color) {
        return create((Context)new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.lightIconTheme)), (Context)new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.darkIconTheme)), n, b, color);
    }
    
    public static KeyButtonDrawable create(final Context context, final Context context2, final int n, final boolean b, final Color color) {
        return create(context, Utils.getColorAttrDefaultColor(context, R$attr.singleToneColor), Utils.getColorAttrDefaultColor(context2, R$attr.singleToneColor), n, b, color);
    }
    
    private void regenerateBitmapIconCache() {
        final int intrinsicWidth = this.getIntrinsicWidth();
        final int intrinsicHeight = this.getIntrinsicHeight();
        final Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final Drawable mutate = this.mState.mChildState.newDrawable().mutate();
        this.setDrawableBounds(mutate);
        canvas.save();
        if (this.mState.mHorizontalFlip) {
            canvas.scale(-1.0f, 1.0f, intrinsicWidth * 0.5f, intrinsicHeight * 0.5f);
        }
        mutate.draw(canvas);
        canvas.restore();
        Bitmap copy = bitmap;
        if (this.mState.mIsHardwareBitmap) {
            copy = bitmap.copy(Bitmap$Config.HARDWARE, false);
        }
        this.mState.mLastDrawnIcon = copy;
    }
    
    private void regenerateBitmapShadowCache() {
        final ShadowDrawableState mState = this.mState;
        if (mState.mShadowSize == 0) {
            mState.mLastDrawnIcon = null;
            return;
        }
        final int intrinsicWidth = this.getIntrinsicWidth();
        final int intrinsicHeight = this.getIntrinsicHeight();
        final Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final Drawable mutate = this.mState.mChildState.newDrawable().mutate();
        this.setDrawableBounds(mutate);
        canvas.save();
        if (this.mState.mHorizontalFlip) {
            canvas.scale(-1.0f, 1.0f, intrinsicWidth * 0.5f, intrinsicHeight * 0.5f);
        }
        mutate.draw(canvas);
        canvas.restore();
        final Paint paint = new Paint(3);
        paint.setMaskFilter((MaskFilter)new BlurMaskFilter((float)this.mState.mShadowSize, BlurMaskFilter$Blur.NORMAL));
        final int[] array = new int[2];
        final Bitmap alpha = bitmap.extractAlpha(paint, array);
        paint.setMaskFilter((MaskFilter)null);
        bitmap.eraseColor(0);
        canvas.drawBitmap(alpha, (float)array[0], (float)array[1], paint);
        Bitmap copy = bitmap;
        if (this.mState.mIsHardwareBitmap) {
            copy = bitmap.copy(Bitmap$Config.HARDWARE, false);
        }
        this.mState.mLastDrawnShadow = copy;
    }
    
    private void setDrawableBounds(final Drawable drawable) {
        final ShadowDrawableState mState = this.mState;
        final int n = mState.mShadowSize + Math.abs(mState.mShadowOffsetX);
        final ShadowDrawableState mState2 = this.mState;
        final int n2 = mState2.mShadowSize + Math.abs(mState2.mShadowOffsetY);
        drawable.setBounds(n, n2, this.getIntrinsicWidth() - n, this.getIntrinsicHeight() - n2);
    }
    
    private void updateShadowAlpha() {
        final int alpha = Color.alpha(this.mState.mShadowColor);
        final Paint mShadowPaint = this.mShadowPaint;
        final float n = (float)alpha;
        final ShadowDrawableState mState = this.mState;
        mShadowPaint.setAlpha(Math.round(n * (mState.mAlpha / 255.0f) * (1.0f - mState.mDarkIntensity)));
    }
    
    public boolean canAnimate() {
        return this.mState.mSupportsAnimation;
    }
    
    public boolean canApplyTheme() {
        return this.mState.canApplyTheme();
    }
    
    public void clearAnimationCallbacks() {
        final AnimatedVectorDrawable mAnimatedDrawable = this.mAnimatedDrawable;
        if (mAnimatedDrawable != null) {
            mAnimatedDrawable.clearAnimationCallbacks();
        }
    }
    
    public void draw(final Canvas canvas) {
        final Rect bounds = this.getBounds();
        if (bounds.isEmpty()) {
            return;
        }
        final AnimatedVectorDrawable mAnimatedDrawable = this.mAnimatedDrawable;
        if (mAnimatedDrawable != null) {
            mAnimatedDrawable.draw(canvas);
        }
        else {
            final boolean b = this.mState.mIsHardwareBitmap != canvas.isHardwareAccelerated();
            if (b) {
                this.mState.mIsHardwareBitmap = canvas.isHardwareAccelerated();
            }
            if (this.mState.mLastDrawnIcon == null || b) {
                this.regenerateBitmapIconCache();
            }
            canvas.save();
            final ShadowDrawableState mState = this.mState;
            canvas.translate(mState.mTranslationX, mState.mTranslationY);
            canvas.rotate(this.mState.mRotateDegrees, (float)(this.getIntrinsicWidth() / 2), (float)(this.getIntrinsicHeight() / 2));
            final ShadowDrawableState mState2 = this.mState;
            if (mState2.mShadowSize > 0) {
                if (mState2.mLastDrawnShadow == null || b) {
                    this.regenerateBitmapShadowCache();
                }
                final double n = (float)(this.mState.mRotateDegrees * 3.141592653589793 / 180.0);
                final double sin = Math.sin(n);
                final double n2 = this.mState.mShadowOffsetY;
                final double cos = Math.cos(n);
                final ShadowDrawableState mState3 = this.mState;
                final float n3 = (float)(sin * n2 + cos * mState3.mShadowOffsetX);
                final float mTranslationX = mState3.mTranslationX;
                final double cos2 = Math.cos(n);
                final double n4 = this.mState.mShadowOffsetY;
                final double sin2 = Math.sin(n);
                final ShadowDrawableState mState4 = this.mState;
                canvas.drawBitmap(mState4.mLastDrawnShadow, n3 - mTranslationX, (float)(cos2 * n4 - sin2 * mState4.mShadowOffsetX) - mState4.mTranslationY, this.mShadowPaint);
            }
            canvas.drawBitmap(this.mState.mLastDrawnIcon, (Rect)null, bounds, this.mIconPaint);
            canvas.restore();
        }
    }
    
    public Drawable$ConstantState getConstantState() {
        return this.mState;
    }
    
    public float getDarkIntensity() {
        return this.mState.mDarkIntensity;
    }
    
    int getDrawableBackgroundColor() {
        return this.mState.mOvalBackgroundColor.toArgb();
    }
    
    public int getIntrinsicHeight() {
        final ShadowDrawableState mState = this.mState;
        return mState.mBaseHeight + (mState.mShadowSize + Math.abs(mState.mShadowOffsetY)) * 2;
    }
    
    public int getIntrinsicWidth() {
        final ShadowDrawableState mState = this.mState;
        return mState.mBaseWidth + (mState.mShadowSize + Math.abs(mState.mShadowOffsetX)) * 2;
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public float getRotation() {
        return this.mState.mRotateDegrees;
    }
    
    public float getTranslationY() {
        return this.mState.mTranslationY;
    }
    
    boolean hasOvalBg() {
        return this.mState.mOvalBackgroundColor != null;
    }
    
    public void resetAnimation() {
        final AnimatedVectorDrawable mAnimatedDrawable = this.mAnimatedDrawable;
        if (mAnimatedDrawable != null) {
            mAnimatedDrawable.reset();
        }
    }
    
    public void setAlpha(final int n) {
        this.mState.mAlpha = n;
        this.mIconPaint.setAlpha(n);
        this.updateShadowAlpha();
        this.invalidateSelf();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mIconPaint.setColorFilter(colorFilter);
        if (this.mAnimatedDrawable != null) {
            if (this.hasOvalBg()) {
                this.mAnimatedDrawable.setColorFilter((ColorFilter)new PorterDuffColorFilter(this.mState.mLightColor, PorterDuff$Mode.SRC_IN));
            }
            else {
                this.mAnimatedDrawable.setColorFilter(colorFilter);
            }
        }
        this.invalidateSelf();
    }
    
    public void setDarkIntensity(final float mDarkIntensity) {
        this.mState.mDarkIntensity = mDarkIntensity;
        final int intValue = (int)ArgbEvaluator.getInstance().evaluate(mDarkIntensity, (Object)this.mState.mLightColor, (Object)this.mState.mDarkColor);
        this.updateShadowAlpha();
        this.setColorFilter((ColorFilter)new PorterDuffColorFilter(intValue, PorterDuff$Mode.SRC_ATOP));
    }
    
    public void setRotation(final float mRotateDegrees) {
        if (this.canAnimate()) {
            return;
        }
        final ShadowDrawableState mState = this.mState;
        if (mState.mRotateDegrees != mRotateDegrees) {
            mState.mRotateDegrees = mRotateDegrees;
            this.invalidateSelf();
        }
    }
    
    public void setShadowProperties(final int mShadowOffsetX, final int mShadowOffsetY, final int mShadowSize, final int mShadowColor) {
        if (this.canAnimate()) {
            return;
        }
        final ShadowDrawableState mState = this.mState;
        if (mState.mShadowOffsetX != mShadowOffsetX || mState.mShadowOffsetY != mShadowOffsetY || mState.mShadowSize != mShadowSize || mState.mShadowColor != mShadowColor) {
            final ShadowDrawableState mState2 = this.mState;
            mState2.mShadowOffsetX = mShadowOffsetX;
            mState2.mShadowOffsetY = mShadowOffsetY;
            mState2.mShadowSize = mShadowSize;
            mState2.mShadowColor = mShadowColor;
            this.mShadowPaint.setColorFilter((ColorFilter)new PorterDuffColorFilter(this.mState.mShadowColor, PorterDuff$Mode.SRC_ATOP));
            this.updateShadowAlpha();
            this.invalidateSelf();
        }
    }
    
    public void setTranslation(final float mTranslationX, final float mTranslationY) {
        final ShadowDrawableState mState = this.mState;
        if (mState.mTranslationX != mTranslationX || mState.mTranslationY != mTranslationY) {
            final ShadowDrawableState mState2 = this.mState;
            mState2.mTranslationX = mTranslationX;
            mState2.mTranslationY = mTranslationY;
            this.invalidateSelf();
        }
    }
    
    public void setTranslationY(final float n) {
        this.setTranslation(this.mState.mTranslationX, n);
    }
    
    public void startAnimation() {
        final AnimatedVectorDrawable mAnimatedDrawable = this.mAnimatedDrawable;
        if (mAnimatedDrawable != null) {
            mAnimatedDrawable.start();
        }
    }
    
    private static class ShadowDrawableState extends Drawable$ConstantState
    {
        int mAlpha;
        int mBaseHeight;
        int mBaseWidth;
        int mChangingConfigurations;
        Drawable$ConstantState mChildState;
        final int mDarkColor;
        float mDarkIntensity;
        boolean mHorizontalFlip;
        boolean mIsHardwareBitmap;
        Bitmap mLastDrawnIcon;
        Bitmap mLastDrawnShadow;
        final int mLightColor;
        final Color mOvalBackgroundColor;
        float mRotateDegrees;
        int mShadowColor;
        int mShadowOffsetX;
        int mShadowOffsetY;
        int mShadowSize;
        final boolean mSupportsAnimation;
        float mTranslationX;
        float mTranslationY;
        
        public ShadowDrawableState(final int mLightColor, final int mDarkColor, final boolean mSupportsAnimation, final boolean mHorizontalFlip, final Color mOvalBackgroundColor) {
            this.mLightColor = mLightColor;
            this.mDarkColor = mDarkColor;
            this.mSupportsAnimation = mSupportsAnimation;
            this.mAlpha = 255;
            this.mHorizontalFlip = mHorizontalFlip;
            this.mOvalBackgroundColor = mOvalBackgroundColor;
        }
        
        public boolean canApplyTheme() {
            return true;
        }
        
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
        
        public Drawable newDrawable() {
            return new KeyButtonDrawable(null, this, null);
        }
    }
}
