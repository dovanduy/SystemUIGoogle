// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.content.res.Resources;
import android.graphics.Bitmap$Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.UserHandle;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Path;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.RectF;
import android.graphics.Bitmap;
import android.graphics.DrawFilter;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.content.Context;
import android.graphics.Canvas;

public class BaseIconFactory implements AutoCloseable
{
    static final boolean ATLEAST_OREO;
    private boolean mBadgeOnLeft;
    private final Canvas mCanvas;
    private final ColorExtractor mColorExtractor;
    protected final Context mContext;
    private boolean mDisableColorExtractor;
    protected final int mFillResIconDpi;
    protected final int mIconBitmapSize;
    private IconNormalizer mNormalizer;
    private final Rect mOldBounds;
    private final PackageManager mPm;
    private ShadowGenerator mShadowGenerator;
    private final boolean mShapeDetection;
    private int mWrapperBackgroundColor;
    private Drawable mWrapperIcon;
    
    static {
        ATLEAST_OREO = (Build$VERSION.SDK_INT >= 26);
    }
    
    protected BaseIconFactory(final Context context, final int n, final int n2) {
        this(context, n, n2, false);
    }
    
    protected BaseIconFactory(Context applicationContext, final int mFillResIconDpi, final int mIconBitmapSize, final boolean mShapeDetection) {
        this.mOldBounds = new Rect();
        this.mBadgeOnLeft = false;
        this.mWrapperBackgroundColor = -1;
        applicationContext = applicationContext.getApplicationContext();
        this.mContext = applicationContext;
        this.mShapeDetection = mShapeDetection;
        this.mFillResIconDpi = mFillResIconDpi;
        this.mIconBitmapSize = mIconBitmapSize;
        this.mPm = applicationContext.getPackageManager();
        this.mColorExtractor = new ColorExtractor();
        (this.mCanvas = new Canvas()).setDrawFilter((DrawFilter)new PaintFlagsDrawFilter(4, 2));
        this.clear();
    }
    
    private Bitmap createIconBitmap(final Drawable drawable, final float n) {
        return this.createIconBitmap(drawable, n, this.mIconBitmapSize);
    }
    
    private int extractColor(final Bitmap bitmap) {
        int dominantColorByHue;
        if (this.mDisableColorExtractor) {
            dominantColorByHue = 0;
        }
        else {
            dominantColorByHue = this.mColorExtractor.findDominantColorByHue(bitmap);
        }
        return dominantColorByHue;
    }
    
    public static int getBadgeSizeForIconSize(final int n) {
        return (int)(n * 0.444f);
    }
    
    private Drawable normalizeAndWrapToAdaptiveIcon(final Drawable drawable, final boolean b, final RectF rectF, final float[] array) {
        if (drawable == null) {
            return null;
        }
        float n;
        Drawable drawable2;
        if (b && BaseIconFactory.ATLEAST_OREO) {
            if (this.mWrapperIcon == null) {
                this.mWrapperIcon = this.mContext.getDrawable(R$drawable.adaptive_icon_drawable_wrapper).mutate();
            }
            final AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable)this.mWrapperIcon;
            adaptiveIconDrawable.setBounds(0, 0, 1, 1);
            final boolean[] array2 = { false };
            final float scale = n = this.getNormalizer().getScale(drawable, rectF, adaptiveIconDrawable.getIconMask(), array2);
            drawable2 = drawable;
            if (!(drawable instanceof AdaptiveIconDrawable)) {
                n = scale;
                drawable2 = drawable;
                if (!array2[0]) {
                    final FixedScaleDrawable fixedScaleDrawable = (FixedScaleDrawable)adaptiveIconDrawable.getForeground();
                    fixedScaleDrawable.setDrawable(drawable);
                    fixedScaleDrawable.setScale(scale);
                    n = this.getNormalizer().getScale((Drawable)adaptiveIconDrawable, rectF, null, null);
                    ((ColorDrawable)adaptiveIconDrawable.getBackground()).setColor(this.mWrapperBackgroundColor);
                    drawable2 = (Drawable)adaptiveIconDrawable;
                }
            }
        }
        else {
            n = this.getNormalizer().getScale(drawable, rectF, null, null);
            drawable2 = drawable;
        }
        array[0] = n;
        return drawable2;
    }
    
    public void badgeWithDrawable(final Bitmap bitmap, final Drawable drawable) {
        this.mCanvas.setBitmap(bitmap);
        this.badgeWithDrawable(this.mCanvas, drawable);
        this.mCanvas.setBitmap((Bitmap)null);
    }
    
    public void badgeWithDrawable(final Canvas canvas, final Drawable drawable) {
        final int badgeSizeForIconSize = getBadgeSizeForIconSize(this.mIconBitmapSize);
        if (this.mBadgeOnLeft) {
            final int mIconBitmapSize = this.mIconBitmapSize;
            drawable.setBounds(0, mIconBitmapSize - badgeSizeForIconSize, badgeSizeForIconSize, mIconBitmapSize);
        }
        else {
            final int mIconBitmapSize2 = this.mIconBitmapSize;
            drawable.setBounds(mIconBitmapSize2 - badgeSizeForIconSize, mIconBitmapSize2 - badgeSizeForIconSize, mIconBitmapSize2, mIconBitmapSize2);
        }
        drawable.draw(canvas);
    }
    
    protected void clear() {
        this.mWrapperBackgroundColor = -1;
        this.mDisableColorExtractor = false;
        this.mBadgeOnLeft = false;
    }
    
    @Override
    public void close() {
        this.clear();
    }
    
    public BitmapInfo createBadgedIconBitmap(final Drawable drawable, final UserHandle userHandle, final boolean b) {
        return this.createBadgedIconBitmap(drawable, userHandle, b, false, null);
    }
    
    public BitmapInfo createBadgedIconBitmap(Drawable userBadgedIcon, final UserHandle userHandle, final boolean b, final boolean b2, final float[] array) {
        float[] array2 = array;
        if (array == null) {
            array2 = new float[] { 0.0f };
        }
        final Drawable normalizeAndWrapToAdaptiveIcon = this.normalizeAndWrapToAdaptiveIcon(userBadgedIcon, b, null, array2);
        final Bitmap iconBitmap = this.createIconBitmap(normalizeAndWrapToAdaptiveIcon, array2[0]);
        if (BaseIconFactory.ATLEAST_OREO && normalizeAndWrapToAdaptiveIcon instanceof AdaptiveIconDrawable) {
            this.mCanvas.setBitmap(iconBitmap);
            this.getShadowGenerator().recreateIcon(Bitmap.createBitmap(iconBitmap), this.mCanvas);
            this.mCanvas.setBitmap((Bitmap)null);
        }
        if (b2) {
            this.badgeWithDrawable(iconBitmap, this.mContext.getDrawable(R$drawable.ic_instant_app_badge));
        }
        Bitmap bitmap = iconBitmap;
        if (userHandle != null) {
            userBadgedIcon = this.mPm.getUserBadgedIcon((Drawable)new FixedSizeBitmapDrawable(iconBitmap), userHandle);
            if (userBadgedIcon instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable)userBadgedIcon).getBitmap();
            }
            else {
                bitmap = this.createIconBitmap(userBadgedIcon, 1.0f);
            }
        }
        final int color = this.extractColor(bitmap);
        BitmapInfo bitmapInfo;
        if (normalizeAndWrapToAdaptiveIcon instanceof BitmapInfo.Extender) {
            bitmapInfo = ((BitmapInfo.Extender)normalizeAndWrapToAdaptiveIcon).getExtendedInfo(bitmap, color, this);
        }
        else {
            bitmapInfo = BitmapInfo.of(bitmap, color);
        }
        return bitmapInfo;
    }
    
    public Bitmap createIconBitmap(final Drawable drawable, final float n, int n2) {
        final Bitmap bitmap = Bitmap.createBitmap(n2, n2, Bitmap$Config.ARGB_8888);
        if (drawable == null) {
            return bitmap;
        }
        this.mCanvas.setBitmap(bitmap);
        this.mOldBounds.set(drawable.getBounds());
        if (BaseIconFactory.ATLEAST_OREO && drawable instanceof AdaptiveIconDrawable) {
            final float n3 = (float)n2;
            final int max = Math.max((int)Math.ceil(0.010416667f * n3), Math.round(n3 * (1.0f - n) / 2.0f));
            n2 -= max;
            drawable.setBounds(max, max, n2, n2);
            drawable.draw(this.mCanvas);
        }
        else {
            if (drawable instanceof BitmapDrawable) {
                final BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
                final Bitmap bitmap2 = bitmapDrawable.getBitmap();
                if (bitmap != null && bitmap2.getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(this.mContext.getResources().getDisplayMetrics());
                }
            }
            final int intrinsicWidth = drawable.getIntrinsicWidth();
            final int intrinsicHeight = drawable.getIntrinsicHeight();
            int n5 = 0;
            int n6 = 0;
            Label_0235: {
                if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                    final float n4 = intrinsicWidth / (float)intrinsicHeight;
                    if (intrinsicWidth > intrinsicHeight) {
                        n5 = (int)(n2 / n4);
                        n6 = n2;
                        break Label_0235;
                    }
                    if (intrinsicHeight > intrinsicWidth) {
                        n6 = (int)(n2 * n4);
                        n5 = n2;
                        break Label_0235;
                    }
                }
                n6 = (n5 = n2);
            }
            final int n7 = (n2 - n6) / 2;
            final int n8 = (n2 - n5) / 2;
            drawable.setBounds(n7, n8, n6 + n7, n5 + n8);
            this.mCanvas.save();
            final Canvas mCanvas = this.mCanvas;
            final float n9 = (float)(n2 / 2);
            mCanvas.scale(n, n, n9, n9);
            drawable.draw(this.mCanvas);
            this.mCanvas.restore();
        }
        drawable.setBounds(this.mOldBounds);
        this.mCanvas.setBitmap((Bitmap)null);
        return bitmap;
    }
    
    public BitmapInfo createIconBitmap(final Bitmap bitmap) {
        if (this.mIconBitmapSize == bitmap.getWidth()) {
            final Bitmap iconBitmap = bitmap;
            if (this.mIconBitmapSize == bitmap.getHeight()) {
                return BitmapInfo.of(iconBitmap, this.extractColor(iconBitmap));
            }
        }
        final Bitmap iconBitmap = this.createIconBitmap((Drawable)new BitmapDrawable(this.mContext.getResources(), bitmap), 1.0f);
        return BitmapInfo.of(iconBitmap, this.extractColor(iconBitmap));
    }
    
    public IconNormalizer getNormalizer() {
        if (this.mNormalizer == null) {
            this.mNormalizer = new IconNormalizer(this.mContext, this.mIconBitmapSize, this.mShapeDetection);
        }
        return this.mNormalizer;
    }
    
    public ShadowGenerator getShadowGenerator() {
        if (this.mShadowGenerator == null) {
            this.mShadowGenerator = new ShadowGenerator(this.mIconBitmapSize);
        }
        return this.mShadowGenerator;
    }
    
    private static class FixedSizeBitmapDrawable extends BitmapDrawable
    {
        public FixedSizeBitmapDrawable(final Bitmap bitmap) {
            super((Resources)null, bitmap);
        }
        
        public int getIntrinsicHeight() {
            return this.getBitmap().getWidth();
        }
        
        public int getIntrinsicWidth() {
            return this.getBitmap().getWidth();
        }
    }
}
