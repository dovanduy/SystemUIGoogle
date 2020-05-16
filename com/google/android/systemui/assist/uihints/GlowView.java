// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import java.util.Collection;
import java.util.Arrays;
import com.android.systemui.R$id;
import android.graphics.drawable.Drawable;
import java.util.Iterator;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.graphics.Matrix$ScaleToFit;
import java.util.function.Consumer;
import android.content.res.Resources$Theme;
import com.android.systemui.R$drawable;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;
import java.util.ArrayList;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.android.systemui.assist.ui.EdgeLight;
import android.widget.ImageView;
import android.widget.FrameLayout;

public final class GlowView extends FrameLayout
{
    private ImageView mBlueGlow;
    private BlurProvider mBlurProvider;
    private int mBlurRadius;
    private EdgeLight[] mEdgeLights;
    private RectF mGlowImageCropRegion;
    private final Matrix mGlowImageMatrix;
    private ArrayList<ImageView> mGlowImageViews;
    private float mGlowWidthRatio;
    private ImageView mGreenGlow;
    private int mMinimumHeightPx;
    private final Paint mPaint;
    private ImageView mRedGlow;
    private int mTranslationY;
    private ImageView mYellowGlow;
    
    public GlowView(final Context context) {
        this(context, null);
    }
    
    public GlowView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public GlowView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public GlowView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mGlowImageCropRegion = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        this.mGlowImageMatrix = new Matrix();
        this.mBlurRadius = 0;
        this.mPaint = new Paint();
        this.mBlurProvider = new BlurProvider(context, context.getResources().getDrawable(R$drawable.glow_vector, (Resources$Theme)null));
    }
    
    private int getGlowHeight() {
        return (int)Math.ceil(this.getGlowWidth() * this.getGlowImageAspectRatio());
    }
    
    private float getGlowImageAspectRatio() {
        if (this.mGlowImageCropRegion.width() == 0.0f) {
            return 0.0f;
        }
        return this.mGlowImageCropRegion.height() / this.mGlowImageCropRegion.width();
    }
    
    private int getGlowWidth() {
        return (int)Math.ceil(this.mGlowWidthRatio * this.getWidth());
    }
    
    private void setBlurredImageOnViews(final int mBlurRadius) {
        this.mBlurRadius = mBlurRadius;
        final BlurProvider.BlurResult value = this.mBlurProvider.get(mBlurRadius);
        this.mGlowImageCropRegion = value.cropRegion;
        this.updateGlowImageMatrix();
        this.mGlowImageViews.forEach(new _$$Lambda$GlowView$O_7UE3OJn3jgHUZXGo46cwtdzlU(this, value));
    }
    
    private void updateGlowImageMatrix() {
        this.mGlowImageMatrix.setRectToRect(this.mGlowImageCropRegion, new RectF(0.0f, 0.0f, (float)this.getGlowWidth(), (float)this.getGlowHeight()), Matrix$ScaleToFit.FILL);
    }
    
    private void updateGlowSizes() {
        final int glowWidth = this.getGlowWidth();
        final int glowHeight = this.getGlowHeight();
        this.updateGlowImageMatrix();
        for (final ImageView imageView : this.mGlowImageViews) {
            final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)imageView.getLayoutParams();
            layoutParams.width = glowWidth;
            layoutParams.height = glowHeight;
            imageView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            imageView.setImageMatrix(this.mGlowImageMatrix);
        }
        this.distributeEvenly();
    }
    
    public void clearCaches() {
        final Iterator<ImageView> iterator = this.mGlowImageViews.iterator();
        while (iterator.hasNext()) {
            iterator.next().setImageDrawable((Drawable)null);
        }
        this.mBlurProvider.clearCaches();
    }
    
    public void distributeEvenly() {
        final float n = (float)DisplayUtils.getCornerRadiusBottom(super.mContext);
        final float n2 = (float)this.getWidth();
        final float n3 = n / n2;
        final float n4 = this.mGlowWidthRatio / 2.0f;
        final float n5 = 0.96f * n4;
        final float n6 = n3 - n4;
        final float n7 = n6 + n5;
        final float n8 = n7 + n5;
        this.mBlueGlow.setX(n6 * n2);
        this.mRedGlow.setX(n7 * n2);
        this.mYellowGlow.setX(n8 * n2);
        this.mGreenGlow.setX(n2 * (n5 + n8));
    }
    
    public int getBlurRadius() {
        return this.mBlurRadius;
    }
    
    public float getGlowWidthRatio() {
        return this.mGlowWidthRatio;
    }
    
    protected void onFinishInflate() {
        this.mBlueGlow = (ImageView)this.findViewById(R$id.blue_glow);
        this.mRedGlow = (ImageView)this.findViewById(R$id.red_glow);
        this.mYellowGlow = (ImageView)this.findViewById(R$id.yellow_glow);
        this.mGreenGlow = (ImageView)this.findViewById(R$id.green_glow);
        this.mGlowImageViews = new ArrayList<ImageView>(Arrays.asList(this.mBlueGlow, this.mRedGlow, this.mYellowGlow, this.mGreenGlow));
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        this.post((Runnable)new _$$Lambda$GlowView$RsiuHu2LuX9WTQjnKF5U72DDI3M(this));
    }
    
    public void setBlurRadius(final int blurredImageOnViews) {
        if (this.mBlurRadius == blurredImageOnViews) {
            return;
        }
        this.setBlurredImageOnViews(blurredImageOnViews);
    }
    
    public void setGlowWidthRatio(final float mGlowWidthRatio) {
        if (this.mGlowWidthRatio == mGlowWidthRatio) {
            return;
        }
        this.mGlowWidthRatio = mGlowWidthRatio;
        this.updateGlowSizes();
        this.distributeEvenly();
    }
    
    public void setGlowsBlendMode(final PorterDuff$Mode porterDuff$Mode) {
        this.mPaint.setXfermode((Xfermode)new PorterDuffXfermode(porterDuff$Mode));
        final Iterator<ImageView> iterator = this.mGlowImageViews.iterator();
        while (iterator.hasNext()) {
            iterator.next().setLayerPaint(this.mPaint);
        }
    }
    
    public void setGlowsY(final int mTranslationY, final int mMinimumHeightPx, final EdgeLight[] mEdgeLights) {
        this.mTranslationY = mTranslationY;
        this.mMinimumHeightPx = mMinimumHeightPx;
        this.mEdgeLights = mEdgeLights;
        final int n = 0;
        final int n2 = 0;
        int i = n;
        if (mEdgeLights != null) {
            i = n;
            if (mEdgeLights.length == 4) {
                final float length = mEdgeLights[0].getLength();
                final float length2 = mEdgeLights[1].getLength();
                final float length3 = mEdgeLights[2].getLength();
                final float length4 = mEdgeLights[3].getLength();
                for (int j = n2; j < 4; ++j) {
                    this.mGlowImageViews.get(j).setTranslationY((float)(this.getHeight() - ((int)(mEdgeLights[j].getLength() * ((mTranslationY - mMinimumHeightPx) * 4) / (length + length2 + length3 + length4)) + mMinimumHeightPx)));
                }
                return;
            }
        }
        while (i < 4) {
            this.mGlowImageViews.get(i).setTranslationY((float)(this.getHeight() - mTranslationY));
            ++i;
        }
    }
    
    public void setVisibility(final int visibility) {
        final int visibility2 = this.getVisibility();
        if (visibility2 == visibility) {
            return;
        }
        super.setVisibility(visibility);
        if (visibility2 == 8) {
            this.setBlurredImageOnViews(this.mBlurRadius);
        }
    }
}
