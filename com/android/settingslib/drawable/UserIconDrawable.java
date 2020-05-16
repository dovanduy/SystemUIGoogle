// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.drawable;

import android.graphics.BitmapShader;
import android.graphics.Shader$TileMode;
import android.graphics.Shader;
import android.app.admin.DevicePolicyManager;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Matrix$ScaleToFit;
import android.graphics.RectF;
import android.graphics.Bitmap$Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable$ConstantState;
import android.graphics.Rect;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Canvas;
import android.graphics.Paint$Style;
import android.content.Context;
import android.graphics.PorterDuff$Mode;
import android.graphics.Matrix;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable$Callback;
import android.graphics.drawable.Drawable;

public class UserIconDrawable extends Drawable implements Drawable$Callback
{
    private Drawable mBadge;
    private float mBadgeMargin;
    private float mBadgeRadius;
    private Bitmap mBitmap;
    private Paint mClearPaint;
    private float mDisplayRadius;
    private ColorStateList mFrameColor;
    private float mFramePadding;
    private Paint mFramePaint;
    private float mFrameWidth;
    private final Matrix mIconMatrix;
    private final Paint mIconPaint;
    private float mIntrinsicRadius;
    private boolean mInvalidated;
    private float mPadding;
    private final Paint mPaint;
    private int mSize;
    private ColorStateList mTintColor;
    private PorterDuff$Mode mTintMode;
    private Drawable mUserDrawable;
    private Bitmap mUserIcon;
    
    public UserIconDrawable() {
        this(0);
    }
    
    public UserIconDrawable(final int intrinsicSize) {
        this.mIconPaint = new Paint();
        this.mPaint = new Paint();
        this.mIconMatrix = new Matrix();
        this.mPadding = 0.0f;
        this.mSize = 0;
        this.mInvalidated = true;
        this.mTintColor = null;
        this.mTintMode = PorterDuff$Mode.SRC_ATOP;
        this.mFrameColor = null;
        this.mIconPaint.setAntiAlias(true);
        this.mIconPaint.setFilterBitmap(true);
        this.mPaint.setFilterBitmap(true);
        this.mPaint.setAntiAlias(true);
        if (intrinsicSize > 0) {
            this.setBounds(0, 0, intrinsicSize, intrinsicSize);
            this.setIntrinsicSize(intrinsicSize);
        }
        this.setIcon(null);
    }
    
    private static Drawable getDrawableForDisplayDensity(final Context context, final int n) {
        return context.getResources().getDrawableForDensity(n, context.getResources().getDisplayMetrics().densityDpi, context.getTheme());
    }
    
    private void initFramePaint() {
        if (this.mFramePaint == null) {
            (this.mFramePaint = new Paint()).setStyle(Paint$Style.STROKE);
            this.mFramePaint.setAntiAlias(true);
        }
    }
    
    private void rebake() {
        this.mInvalidated = false;
        if (this.mBitmap != null) {
            if (this.mUserDrawable != null || this.mUserIcon != null) {
                final Canvas canvas = new Canvas(this.mBitmap);
                canvas.drawColor(0, PorterDuff$Mode.CLEAR);
                final Drawable mUserDrawable = this.mUserDrawable;
                if (mUserDrawable != null) {
                    mUserDrawable.draw(canvas);
                }
                else if (this.mUserIcon != null) {
                    final int save = canvas.save();
                    canvas.concat(this.mIconMatrix);
                    canvas.drawCircle(this.mUserIcon.getWidth() * 0.5f, this.mUserIcon.getHeight() * 0.5f, this.mIntrinsicRadius, this.mIconPaint);
                    canvas.restoreToCount(save);
                }
                final ColorStateList mFrameColor = this.mFrameColor;
                if (mFrameColor != null) {
                    this.mFramePaint.setColor(mFrameColor.getColorForState(this.getState(), 0));
                }
                final float mFrameWidth = this.mFrameWidth;
                if (this.mFramePadding + mFrameWidth > 0.001f) {
                    canvas.drawCircle(this.getBounds().exactCenterX(), this.getBounds().exactCenterY(), this.mDisplayRadius - this.mPadding - mFrameWidth * 0.5f, this.mFramePaint);
                }
                if (this.mBadge != null) {
                    final float mBadgeRadius = this.mBadgeRadius;
                    if (mBadgeRadius > 0.001f) {
                        final float n = mBadgeRadius * 2.0f;
                        final float n2 = this.mBitmap.getHeight() - n;
                        final float n3 = this.mBitmap.getWidth() - n;
                        this.mBadge.setBounds((int)n3, (int)n2, (int)(n3 + n), (int)(n + n2));
                        final float n4 = (float)this.mBadge.getBounds().width();
                        final float mBadgeMargin = this.mBadgeMargin;
                        final float mBadgeRadius2 = this.mBadgeRadius;
                        canvas.drawCircle(n3 + mBadgeRadius2, n2 + mBadgeRadius2, n4 * 0.5f + mBadgeMargin, this.mClearPaint);
                        this.mBadge.draw(canvas);
                    }
                }
            }
        }
    }
    
    private boolean shouldUpdateColorFilter(final int n, final PorterDuff$Mode porterDuff$Mode) {
        final ColorFilter colorFilter = this.mPaint.getColorFilter();
        final boolean b = colorFilter instanceof PorterDuffColorFilter;
        boolean b3;
        final boolean b2 = b3 = true;
        if (b) {
            final PorterDuffColorFilter porterDuffColorFilter = (PorterDuffColorFilter)colorFilter;
            final int color = porterDuffColorFilter.getColor();
            final PorterDuff$Mode mode = porterDuffColorFilter.getMode();
            b3 = b2;
            if (color == n) {
                b3 = (mode != porterDuff$Mode && b2);
            }
        }
        return b3;
    }
    
    public UserIconDrawable bake() {
        if (this.mSize > 0) {
            final int mSize = this.mSize;
            this.onBoundsChange(new Rect(0, 0, mSize, mSize));
            this.rebake();
            this.mFrameColor = null;
            this.mFramePaint = null;
            this.mClearPaint = null;
            final Drawable mUserDrawable = this.mUserDrawable;
            if (mUserDrawable != null) {
                mUserDrawable.setCallback((Drawable$Callback)null);
                this.mUserDrawable = null;
            }
            else {
                final Bitmap mUserIcon = this.mUserIcon;
                if (mUserIcon != null) {
                    mUserIcon.recycle();
                    this.mUserIcon = null;
                }
            }
            return this;
        }
        throw new IllegalStateException("Baking requires an explicit intrinsic size");
    }
    
    public void draw(final Canvas canvas) {
        if (this.mInvalidated) {
            this.rebake();
        }
        if (this.mBitmap != null) {
            final ColorStateList mTintColor = this.mTintColor;
            if (mTintColor == null) {
                this.mPaint.setColorFilter((ColorFilter)null);
            }
            else {
                final int colorForState = mTintColor.getColorForState(this.getState(), this.mTintColor.getDefaultColor());
                if (this.shouldUpdateColorFilter(colorForState, this.mTintMode)) {
                    this.mPaint.setColorFilter((ColorFilter)new PorterDuffColorFilter(colorForState, this.mTintMode));
                }
            }
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
        }
    }
    
    public Drawable$ConstantState getConstantState() {
        return new BitmapDrawable(this.mBitmap).getConstantState();
    }
    
    public int getIntrinsicHeight() {
        return this.getIntrinsicWidth();
    }
    
    public int getIntrinsicWidth() {
        int mSize;
        if ((mSize = this.mSize) <= 0) {
            mSize = (int)this.mIntrinsicRadius * 2;
        }
        return mSize;
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public void invalidateDrawable(final Drawable drawable) {
        this.invalidateSelf();
    }
    
    public void invalidateSelf() {
        super.invalidateSelf();
        this.mInvalidated = true;
    }
    
    public boolean isStateful() {
        final ColorStateList mFrameColor = this.mFrameColor;
        return mFrameColor != null && mFrameColor.isStateful();
    }
    
    protected void onBoundsChange(final Rect rect) {
        if (!rect.isEmpty()) {
            if (this.mUserIcon != null || this.mUserDrawable != null) {
                final float mDisplayRadius = Math.min(rect.width(), rect.height()) * 0.5f;
                final int n = (int)(mDisplayRadius * 2.0f);
                if (this.mBitmap == null || n != (int)(this.mDisplayRadius * 2.0f)) {
                    this.mDisplayRadius = mDisplayRadius;
                    final Bitmap mBitmap = this.mBitmap;
                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                    this.mBitmap = Bitmap.createBitmap(n, n, Bitmap$Config.ARGB_8888);
                }
                final float mDisplayRadius2 = Math.min(rect.width(), rect.height()) * 0.5f;
                this.mDisplayRadius = mDisplayRadius2;
                final float n2 = mDisplayRadius2 - this.mFrameWidth - this.mFramePadding - this.mPadding;
                final RectF rectF = new RectF(rect.exactCenterX() - n2, rect.exactCenterY() - n2, rect.exactCenterX() + n2, rect.exactCenterY() + n2);
                if (this.mUserDrawable != null) {
                    final Rect bounds = new Rect();
                    rectF.round(bounds);
                    this.mIntrinsicRadius = Math.min(this.mUserDrawable.getIntrinsicWidth(), this.mUserDrawable.getIntrinsicHeight()) * 0.5f;
                    this.mUserDrawable.setBounds(bounds);
                }
                else {
                    final Bitmap mUserIcon = this.mUserIcon;
                    if (mUserIcon != null) {
                        final float a = mUserIcon.getWidth() * 0.5f;
                        final float b = this.mUserIcon.getHeight() * 0.5f;
                        this.mIntrinsicRadius = Math.min(a, b);
                        final float mIntrinsicRadius = this.mIntrinsicRadius;
                        this.mIconMatrix.setRectToRect(new RectF(a - mIntrinsicRadius, b - mIntrinsicRadius, a + mIntrinsicRadius, b + mIntrinsicRadius), rectF, Matrix$ScaleToFit.FILL);
                    }
                }
                this.invalidateSelf();
            }
        }
    }
    
    public void scheduleDrawable(final Drawable drawable, final Runnable runnable, final long n) {
        this.scheduleSelf(runnable, n);
    }
    
    public void setAlpha(final int alpha) {
        this.mPaint.setAlpha(alpha);
        super.invalidateSelf();
    }
    
    public UserIconDrawable setBadge(final Drawable mBadge) {
        this.mBadge = mBadge;
        if (mBadge != null) {
            if (this.mClearPaint == null) {
                (this.mClearPaint = new Paint()).setAntiAlias(true);
                this.mClearPaint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.CLEAR));
                this.mClearPaint.setStyle(Paint$Style.FILL);
            }
            this.onBoundsChange(this.getBounds());
        }
        else {
            this.invalidateSelf();
        }
        return this;
    }
    
    public UserIconDrawable setBadgeIfManagedUser(final Context context, int n) {
        Drawable drawableForDisplayDensity = null;
        Label_0048: {
            if (n != -10000) {
                if (((DevicePolicyManager)context.getSystemService((Class)DevicePolicyManager.class)).getProfileOwnerAsUser(n) != null) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    drawableForDisplayDensity = getDrawableForDisplayDensity(context, 17302376);
                    break Label_0048;
                }
            }
            drawableForDisplayDensity = null;
        }
        this.setBadge(drawableForDisplayDensity);
        return this;
    }
    
    public void setBadgeMargin(final float mBadgeMargin) {
        this.mBadgeMargin = mBadgeMargin;
        this.onBoundsChange(this.getBounds());
    }
    
    public void setBadgeRadius(final float mBadgeRadius) {
        this.mBadgeRadius = mBadgeRadius;
        this.onBoundsChange(this.getBounds());
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
    }
    
    public void setFrameColor(final ColorStateList mFrameColor) {
        this.initFramePaint();
        this.mFrameColor = mFrameColor;
        this.invalidateSelf();
    }
    
    public void setFramePadding(final float mFramePadding) {
        this.initFramePaint();
        this.mFramePadding = mFramePadding;
        this.onBoundsChange(this.getBounds());
    }
    
    public void setFrameWidth(final float n) {
        this.initFramePaint();
        this.mFrameWidth = n;
        this.mFramePaint.setStrokeWidth(n);
        this.onBoundsChange(this.getBounds());
    }
    
    public UserIconDrawable setIcon(final Bitmap mUserIcon) {
        final Drawable mUserDrawable = this.mUserDrawable;
        if (mUserDrawable != null) {
            mUserDrawable.setCallback((Drawable$Callback)null);
            this.mUserDrawable = null;
        }
        if ((this.mUserIcon = mUserIcon) == null) {
            this.mIconPaint.setShader((Shader)null);
            this.mBitmap = null;
        }
        else {
            final Paint mIconPaint = this.mIconPaint;
            final Shader$TileMode clamp = Shader$TileMode.CLAMP;
            mIconPaint.setShader((Shader)new BitmapShader(mUserIcon, clamp, clamp));
        }
        this.onBoundsChange(this.getBounds());
        return this;
    }
    
    public UserIconDrawable setIconDrawable(final Drawable mUserDrawable) {
        final Drawable mUserDrawable2 = this.mUserDrawable;
        if (mUserDrawable2 != null) {
            mUserDrawable2.setCallback((Drawable$Callback)null);
        }
        this.mUserIcon = null;
        if ((this.mUserDrawable = mUserDrawable) == null) {
            this.mBitmap = null;
        }
        else {
            mUserDrawable.setCallback((Drawable$Callback)this);
        }
        this.onBoundsChange(this.getBounds());
        return this;
    }
    
    public void setIntrinsicSize(final int mSize) {
        this.mSize = mSize;
    }
    
    public void setPadding(final float mPadding) {
        this.mPadding = mPadding;
        this.onBoundsChange(this.getBounds());
    }
    
    public void setTintList(final ColorStateList mTintColor) {
        this.mTintColor = mTintColor;
        super.invalidateSelf();
    }
    
    public void setTintMode(final PorterDuff$Mode mTintMode) {
        this.mTintMode = mTintMode;
        super.invalidateSelf();
    }
    
    public void unscheduleDrawable(final Drawable drawable, final Runnable runnable) {
        this.unscheduleSelf(runnable);
    }
}
